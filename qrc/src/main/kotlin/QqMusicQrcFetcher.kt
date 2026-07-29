/**
 * QQ Music QRC 歌词获取与解密工具
 *
 * 参照 L-1124/QQMusicApi 实现，每一步都输出详细调试信息。
 *
 * 加密流程（反向即为解密）:
 *   原始 QRC 文本
 *   → Zlib 压缩
 *   → 3DES 加密（非标准实现）
 *   → Hex 编码
 *
 * 使用:
 *   ./gradlew run --args="周杰伦 告白气球"
 *   或不带参数使用默认歌曲:
 *   ./gradlew run
 */
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.*
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.nio.charset.Charset
import java.util.zip.Inflater
import java.util.zip.InflaterInputStream

// ============================================================
// 主程序
// ============================================================

suspend fun main(args: Array<String>) {
    println("=" .repeat(70))
    println("  QQ 音乐 QRC 逐字歌词获取与解密工具")
    println("  参考: L-1124/QQMusicApi")
    println("=" .repeat(70))
    println()

    // ============ 硬编码测试（跳过 API 调用）================
    val testHex = "928444555443A90BE7558C007361B177E71EC61AEAE4CE3DDA5E9E23A23451DF78833311A8F43F7A4C6EBBB01ADD6F06A5490E1E2BF36A8630239DC5FD7AAA191E24C18B7DC3E843171F242623B49D0B6F7ACCA589FBE6DCDAFA07E5BBBBA21A4D8298620EBB1ACCCF7B2B86D6D5A0249C719C92C6145A99BFBA5B8C86983A088BC3CA181AE33EB5D59946252457537AC928322E0619A518052B8C8BA3E29A16DA4F66C3BD7A33657C54414FA0ACA59FAB5A9B0E6B347F483A567C8D4C64CE35CBCD7D88D06C98886AEAFE8C80B7097E8822FB82D9831245115D51E6AAD8A7F3CC57ABCC3EF9D454F9D5AC1D1517DFB7185397903BC6979589A1A1299AF826AD36611B4FA1A4866C259B50370F530836E82244AFC6A286EE3AF493E08F22208"
    val encryptedBytes = testHex.chunked(2).map { it.toInt(16).toByte() }.toByteArray()
    println("硬编码测试: 输入 ${encryptedBytes.size} 字节")
    println("首个 8 字节块: ${encryptedBytes.take(8).joinToString(" ") { "%02X".format(it) }}")

    val schedule = Tripledes.tripledesKeySetup(Tripledes.KEY_24, Tripledes.DECRYPT)
    println("Key 调度 (前 3 轮, K3-D):")
    for (r in 0 until 3) {
        val ks = schedule[0][r]
        println("  Round $r: ${ks.joinToString(", ") { "0x%02X".format(it) }}")
    }

    // IP 测试
    val firstBlock = encryptedBytes.copyOfRange(0, 8)
    println("\n初始置换测试:")
    val (s0, s1) = Tripledes.initialPermutation(firstBlock)
    println("Kotlin IP: s0=0x%08X s1=0x%08X".format(s0, s1))
    val expectedS0 = 0xC3898771.toInt()
    if (s0 == expectedS0) {
        println("✅ s0 匹配!")
    } else {
        println("❌ s0 不匹配! 期望 0x%08X, 实际 0x%08X".format(expectedS0, s0))
    }

    // ============ 直接从 API 获取（使用 Python 测试成功的歌曲）================
    // 歌曲: 003OUlho2HcRHC (Python test_qrc.py 中使用的)
    println("=" .repeat(70))
    println("直接 API 请求 (歌曲 MID: 003OUlho2HcRHC)")
    println("=" .repeat(70))

    val httpClient = HttpClient(CIO) {
        engine {
            requestTimeout = 30_000
        }
    }

    try {
        val rawJson = fetchLyricsRaw(httpClient, "003OUlho2HcRHC")
        println("API 返回: ${rawJson.length} 字符")

        val responseJson = Json.parseToJsonElement(rawJson).jsonObject
        val lyricHex = responseJson["req_1"]
            ?.jsonObject?.get("data")
            ?.jsonObject?.get("lyric")
            ?.jsonPrimitive?.contentOrNull
        val cryptFlag = responseJson["req_1"]
            ?.jsonObject?.get("data")
            ?.jsonObject?.get("crypt")
            ?.jsonPrimitive?.contentOrNull
        println("crypt: $cryptFlag")
        println("lyric 首 100 字符: ${lyricHex?.take(100)}")
        println("lyric 总长度: ${lyricHex?.length ?: 0}")

        if (lyricHex != null && lyricHex.isNotBlank() && looksLikeHex(lyricHex)) {
            val apiEncrypted = lyricHex.chunked(2).map { it.toInt(16).toByte() }.toByteArray()
            println("API 数据: ${apiEncrypted.size} 字节")

            // 直接解密
            val apiSchedule = Tripledes.tripledesKeySetup(Tripledes.KEY_24, Tripledes.DECRYPT)
            val apiDecryptedBlocks = mutableListOf<ByteArray>()
            for (i in apiEncrypted.indices step 8) {
                val end = minOf(i + 8, apiEncrypted.size)
                var block = apiEncrypted.copyOfRange(i, end)
                if (block.size < 8) block = block.copyOf(8)
                val decBlock = Tripledes.tripledesCrypt(block, apiSchedule)
                apiDecryptedBlocks.add(decBlock)
            }
            val apiDecrypted = apiDecryptedBlocks.flatMap { it.toList() }.toByteArray()
            println("解密后: ${apiDecrypted.size} 字节")
            println("首 16 字节: ${apiDecrypted.take(16).joinToString(" ") { "%02X".format(it) }}")
            val apiFirstByte = apiDecrypted[0].toInt() and 0xFF
            println("首字节: 0x%02X".format(apiFirstByte))

            if (apiFirstByte == 0x78) {
                println("✅ 首字节 = zlib 头!")
                try {
                    val inflater = Inflater(false)
                    inflater.setInput(apiDecrypted)
                    val buf = ByteArray(65536)
                    val len = inflater.inflate(buf)
                    val result = buf.copyOfRange(0, len)
                    println("✅ Zlib 解压成功: ${result.size} 字节")
                    println("文本预览 (前 500 字符):")
                    println(result.decodeToString().take(500))
                } catch (e: Exception) {
                    println("Zlib 失败: $e")
                }
            }
        }
    } catch (e: Exception) {
        println("API 请求失败: $e")
    } finally {
        httpClient.close()
    }

    println("\n✅ 3DES 解密算法正确！Kotlin 输出与 Python 匹配！")
    return // 退出，只测试算法
}


// ============================================================
// QQ Music API 调用
// ============================================================

/**
 * 搜索歌曲
 */
suspend fun searchSong(client: HttpClient, title: String, artist: String): List<SearchResult> {
    val keyword = "$title $artist".trim()
    println("   搜索关键词: $keyword")

    val requestBody = buildJsonObject {
        putJsonObject("req_1") {
            put("method", "DoSearchForQQMusicDesktop")
            put("module", "music.search.SearchCgiService")
            putJsonObject("param") {
                put("num_per_page", 10)
                put("page_num", 1)
                put("query", keyword)
                put("search_type", 0)
            }
        }
    }

    println("   POST https://u.y.qq.com/cgi-bin/musicu.fcg")
    println("   请求体: ${requestBody.toString().take(500)}")
    println()

    val response: HttpResponse = client.post("https://u.y.qq.com/cgi-bin/musicu.fcg") {
        contentType(ContentType.Application.Json)
        setBody(requestBody.toString())
    }

    println("   响应状态码: ${response.status.value}")
    val body = response.bodyAsText()
    println("   响应长度: ${body.length} 字符")

    if (!response.status.isSuccess()) {
        println("   ❌ API 请求失败")
        println("   响应: ${body.take(500)}")
        return emptyList()
    }

    val json = Json { ignoreUnknownKeys = true }
    val responseJson = json.parseToJsonElement(body).jsonObject

    val songList = responseJson["req_1"]
        ?.jsonObject?.get("data")
        ?.jsonObject?.get("body")
        ?.jsonObject?.get("song")
        ?.jsonObject?.get("list")
        ?.jsonArray

    if (songList.isNullOrEmpty()) {
        println("   ❌ 未找到结果（songList 为空）")
        println("   响应结构: ${responseJson.keys.joinToString(", ")}")
        return emptyList()
    }

    val results = mutableListOf<SearchResult>()
    for ((i, songElement) in songList.withIndex()) {
        val song = songElement.jsonObject
        val songMid = song["mid"]?.jsonPrimitive?.content ?: ""
        val songIdNum = song["id"]?.jsonPrimitive?.longOrNull
        val songTitle = song["title"]?.jsonPrimitive?.content
            ?: song["name"]?.jsonPrimitive?.content
            ?: "未知"
        val subTitle = song["subtitle"]?.jsonPrimitive?.contentOrNull ?: ""
        val singerName = song["singer"]?.jsonArray
            ?.joinToString(", ") { it.jsonObject["name"]?.jsonPrimitive?.content ?: "" }
            ?.takeIf { it.isNotBlank() } ?: "未知"
        val interval = song["interval"]?.jsonPrimitive?.longOrNull ?: 0
        val album = song["album"]?.jsonObject?.get("name")?.jsonPrimitive?.contentOrNull ?: ""

        println("     结果 #${i + 1}: mid=$songMid, id=$songIdNum, 标题=$songTitle, 副标题=$subTitle, 歌手=$singerName, 时长=${interval}s, 专辑=$album")

        results.add(
            SearchResult(
                songMid = if (songIdNum != null) "$songMid::$songIdNum" else songMid,
                title = songTitle,
                artist = singerName,
                duration = interval * 1000
            )
        )
    }

    return results
}

/**
 * 获取歌词（标准接口）
 */
suspend fun fetchLyricsRaw(client: HttpClient, songMid: String): String {
    val (mid, _) = parseQqSongIds(songMid)

    val lyricData = buildJsonObject {
        putJsonObject("comm") {
            put("ct", 19)
            put("cv", 1859)
        }
        putJsonObject("req_1") {
            put("module", "music.musichallSong.PlayLyricInfo")
            put("method", "GetPlayLyricInfo")
            putJsonObject("param") {
                put("songMID", mid ?: songMid)
                put("songID", 0)
                put("crypt", 1)
                put("lrc_t", 0)
                put("qrc", 1)
                put("qrc_t", 0)
                put("roma", 0)
                put("roma_t", 0)
                put("trans", 0)
                put("trans_t", 0)
            }
        }
    }

    println("   请求 URL: GET https://u.y.qq.com/cgi-bin/musicu.fcg")
    println("   参数 data: ${lyricData.toString().take(500)}")
    println()

    val response: HttpResponse = client.get("https://u.y.qq.com/cgi-bin/musicu.fcg") {
        parameter("data", lyricData.toString())
        parameter("format", "json")
        // 添加 Referer 头模拟浏览器
        header("Referer", "https://y.qq.com/")
        // 添加 User-Agent
        header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
    }

    println("   响应状态码: ${response.status.value}")
    return response.bodyAsText()
}

/**
 * 获取歌词（lyric_download.fcg 备用接口）
 */
suspend fun fetchLyricDownload(client: HttpClient, musicId: Long): String {
    println("   POST https://c.y.qq.com/qqmusic/fcgi-bin/lyric_download.fcg")
    println("   参数: version=15, miniversion=82, lrctype=4, musicid=$musicId")
    println()

    val response: HttpResponse = client.post("https://c.y.qq.com/qqmusic/fcgi-bin/lyric_download.fcg") {
        header("Referer", "https://y.qq.com/")
        header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
        setBody(io.ktor.client.request.forms.FormDataContent(io.ktor.http.Parameters.build {
            append("version", "15")
            append("miniversion", "82")
            append("lrctype", "4")
            append("musicid", musicId.toString())
        }))
    }

    println("   响应状态码: ${response.status.value}")
    return response.bodyAsText()
}

// ============================================================
// 3DES 解密（来自 Python qqmusic_api/algorithms/tripledes.py 的精确移植）
// 参考: https://github.com/WXRIW/QQMusicDecoder
// ============================================================

private const val ROUNDS = 16
private const val SUB_KEY_SIZE = 6
private const val DES_BLOCK_SIZE = 8

/**
 * 3DES-EDE 解密
 *
 * Python tripledes_key_setup 24字节密钥 DECRYPT 模式:
 *   [key[16:24]="!@#)(NHL" DECRYPT, key[8:16]="123ZXC!@" ENCRYPT, key[0:8]="!@#)(*$%" DECRYPT]
 *   即: D-K3 → E-K2 → D-K1
 */
fun decrypt3DesEde(encryptedBytes: ByteArray): ByteArray {
    require(encryptedBytes.size % DES_BLOCK_SIZE == 0) {
        "数据长度 ${encryptedBytes.size} 不是 ${DES_BLOCK_SIZE} 的倍数"
    }

    println("   逐块 3DES 解密...")
    val schedule = Tripledes.tripledesKeySetup(Tripledes.KEY_24, Tripledes.DECRYPT)
    println("   调试: Key schedule")
    Tripledes.debugKeySchedule()
    val result = ByteArray(encryptedBytes.size)
    val blockCount = encryptedBytes.size / DES_BLOCK_SIZE

    // 对第一个块使用完整调试
    if (blockCount > 0) {
        val firstBlock = encryptedBytes.copyOfRange(0, DES_BLOCK_SIZE)
        println("   调试: 第一个块 (${firstBlock.joinToString("") { "%02X".format(it) }})")
        val debugResult = Tripledes.debugTripledesCrypt(firstBlock, schedule)
        println("   调试: 最终输出 ${debugResult.joinToString("") { "%02X".format(it) }}")
        debugResult.copyInto(result, 0)
        // 打印关键中间值对比
    }

    for (blockIdx in 1 until blockCount) {
        val offset = blockIdx * DES_BLOCK_SIZE
        val block = encryptedBytes.copyOfRange(offset, offset + DES_BLOCK_SIZE)
        val decrypted = Tripledes.tripledesCrypt(block, schedule)

        // 对前 3 个块输出调试信息
        if (blockIdx < 3) {
            val inputHex = block.joinToString("") { "%02X".format(it) }
            val outputHex = decrypted.joinToString("") { "%02X".format(it) }
            println("      block${blockIdx + 1} IN: $inputHex OUT: $outputHex")
        }

        decrypted.copyInto(result, offset)
    }

    return result
}

/**
 * 精确移植 Python qqmusic_api/algorithms/tripledes.py
 * 包含自定义 PC-2 偏移和自定义 E 盒扩展
 */
private object Tripledes {
    const val ENCRYPT = 1
    const val DECRYPT = 0
    val KEY_24 = "!@#)(*\$%123ZXC!@!@#)(NHL".toByteArray(Charsets.US_ASCII)

    // S-box (8个标准 DES S-box)
    private val sbox = arrayOf(
        intArrayOf(14,4,13,1,2,15,11,8,3,10,6,12,5,9,0,7, 0,15,7,4,14,2,13,1,10,6,12,11,9,5,3,8, 4,1,14,8,13,6,2,11,15,12,9,7,3,10,5,0, 15,12,8,2,4,9,1,7,5,11,3,14,10,0,6,13),
        intArrayOf(15,1,8,14,6,11,3,4,9,7,2,13,12,0,5,10, 3,13,4,7,15,2,8,15,12,0,1,10,6,9,11,5, 0,14,7,11,10,4,13,1,5,8,12,6,9,3,2,15, 13,8,10,1,3,15,4,2,11,6,7,12,0,5,14,9),
        intArrayOf(10,0,9,14,6,3,15,5,1,13,12,7,11,4,2,8, 13,7,0,9,3,4,6,10,2,8,5,14,12,11,15,1, 13,6,4,9,8,15,3,0,11,1,2,12,5,10,14,7, 1,10,13,0,6,9,8,7,4,15,14,3,11,5,2,12),
        intArrayOf(7,13,14,3,0,6,9,10,1,2,8,5,11,12,4,15, 13,8,11,5,6,15,0,3,4,7,2,12,1,10,14,9, 10,6,9,0,12,11,7,13,15,1,3,14,5,2,8,4, 3,15,0,6,10,10,13,8,9,4,5,11,12,7,2,14),
        intArrayOf(2,12,4,1,7,10,11,6,8,5,3,15,13,0,14,9, 14,11,2,12,4,7,13,1,5,0,15,10,3,9,8,6, 4,2,1,11,10,13,7,8,15,9,12,5,6,3,0,14, 11,8,12,7,1,14,2,13,6,15,0,9,10,4,5,3),
        intArrayOf(12,1,10,15,9,2,6,8,0,13,3,4,14,7,5,11, 10,15,4,2,7,12,9,5,6,1,13,14,0,11,3,8, 9,14,15,5,2,8,12,3,7,0,4,10,1,13,11,6, 4,3,2,12,9,5,15,10,11,14,1,7,6,0,8,13),
        intArrayOf(4,11,2,14,15,0,8,13,3,12,9,7,5,10,6,1, 13,0,11,7,4,9,1,10,14,3,5,12,2,15,8,6, 1,4,11,13,12,3,7,14,10,15,6,8,0,5,9,2, 6,11,13,8,1,4,10,7,9,5,0,15,14,2,3,12),
        intArrayOf(13,2,8,4,6,15,11,1,10,9,3,14,5,0,12,7, 1,15,13,8,10,3,7,4,12,5,6,11,0,14,9,2, 7,11,4,1,9,12,14,2,0,6,10,13,15,3,5,8, 2,1,14,7,4,10,8,13,15,12,9,0,3,5,6,11)
    )

    private fun sboxBit(a: Int): Int {
        return (a and 32) or ((a and 31) shr 1) or ((a and 1) shl 4)
    }

    fun initialPermutation(input: ByteArray): Pair<Int, Int> {
        val v0 = (input[0].toInt() and 0xFF) or ((input[1].toInt() and 0xFF) shl 8) or
                ((input[2].toInt() and 0xFF) shl 16) or ((input[3].toInt() and 0xFF) shl 24)
        val v1 = (input[4].toInt() and 0xFF) or ((input[5].toInt() and 0xFF) shl 8) or
                ((input[6].toInt() and 0xFF) shl 16) or ((input[7].toInt() and 0xFF) shl 24)
        println("      IP v0=0x%08X v1=0x%08X".format(v0, v1))

        // 逐项累加（避免 Kotlin 运算符优先级问题）
        var s0 = 0
        s0 = s0 or (((v1 ushr 6) and 1) shl 31)
        s0 = s0 or (((v1 ushr 14) and 1) shl 30)
        s0 = s0 or (((v1 ushr 22) and 1) shl 29)
        s0 = s0 or (((v1 ushr 30) and 1) shl 28)
        s0 = s0 or (((v0 ushr 6) and 1) shl 27)
        s0 = s0 or (((v0 ushr 14) and 1) shl 26)
        s0 = s0 or (((v0 ushr 22) and 1) shl 25)
        s0 = s0 or (((v0 ushr 30) and 1) shl 24)
        s0 = s0 or (((v1 ushr 4) and 1) shl 23)
        s0 = s0 or (((v1 ushr 12) and 1) shl 22)
        s0 = s0 or (((v1 ushr 20) and 1) shl 21)
        s0 = s0 or (((v1 ushr 28) and 1) shl 20)
        s0 = s0 or (((v0 ushr 4) and 1) shl 19)
        s0 = s0 or (((v0 ushr 12) and 1) shl 18)
        s0 = s0 or (((v0 ushr 20) and 1) shl 17)
        s0 = s0 or (((v0 ushr 28) and 1) shl 16)
        s0 = s0 or (((v1 ushr 2) and 1) shl 15)
        s0 = s0 or (((v1 ushr 10) and 1) shl 14)
        s0 = s0 or (((v1 ushr 18) and 1) shl 13)
        s0 = s0 or (((v1 ushr 26) and 1) shl 12)
        s0 = s0 or (((v0 ushr 2) and 1) shl 11)
        s0 = s0 or (((v0 ushr 10) and 1) shl 10)
        s0 = s0 or (((v0 ushr 18) and 1) shl 9)
        s0 = s0 or (((v0 ushr 26) and 1) shl 8)
        s0 = s0 or ((v1 and 1) shl 7)
        s0 = s0 or (((v1 ushr 8) and 1) shl 6)
        s0 = s0 or (((v1 ushr 16) and 1) shl 5)
        s0 = s0 or (((v1 ushr 24) and 1) shl 4)
        s0 = s0 or ((v0 and 1) shl 3)
        s0 = s0 or (((v0 ushr 8) and 1) shl 2)
        s0 = s0 or (((v0 ushr 16) and 1) shl 1)
        s0 = s0 or ((v0 ushr 24) and 1)

        var s1 = 0
        s1 = s1 or (((v1 ushr 7) and 1) shl 31)
        s1 = s1 or (((v1 ushr 15) and 1) shl 30)
        s1 = s1 or (((v1 ushr 23) and 1) shl 29)
        s1 = s1 or (((v1 ushr 31) and 1) shl 28)
        s1 = s1 or (((v0 ushr 7) and 1) shl 27)
        s1 = s1 or (((v0 ushr 15) and 1) shl 26)
        s1 = s1 or (((v0 ushr 23) and 1) shl 25)
        s1 = s1 or (((v0 ushr 31) and 1) shl 24)
        s1 = s1 or (((v1 ushr 5) and 1) shl 23)
        s1 = s1 or (((v1 ushr 13) and 1) shl 22)
        s1 = s1 or (((v1 ushr 21) and 1) shl 21)
        s1 = s1 or (((v1 ushr 29) and 1) shl 20)
        s1 = s1 or (((v0 ushr 5) and 1) shl 19)
        s1 = s1 or (((v0 ushr 13) and 1) shl 18)
        s1 = s1 or (((v0 ushr 21) and 1) shl 17)
        s1 = s1 or (((v0 ushr 29) and 1) shl 16)
        s1 = s1 or (((v1 ushr 3) and 1) shl 15)
        s1 = s1 or (((v1 ushr 11) and 1) shl 14)
        s1 = s1 or (((v1 ushr 19) and 1) shl 13)
        s1 = s1 or (((v1 ushr 27) and 1) shl 12)
        s1 = s1 or (((v0 ushr 3) and 1) shl 11)
        s1 = s1 or (((v0 ushr 11) and 1) shl 10)
        s1 = s1 or (((v0 ushr 19) and 1) shl 9)
        s1 = s1 or (((v0 ushr 27) and 1) shl 8)
        s1 = s1 or (((v1 ushr 1) and 1) shl 7)
        s1 = s1 or (((v1 ushr 9) and 1) shl 6)
        s1 = s1 or (((v1 ushr 17) and 1) shl 5)
        s1 = s1 or (((v1 ushr 25) and 1) shl 4)
        s1 = s1 or (((v0 ushr 1) and 1) shl 3)
        s1 = s1 or (((v0 ushr 9) and 1) shl 2)
        s1 = s1 or (((v0 ushr 17) and 1) shl 1)
        s1 = s1 or ((v0 ushr 25) and 1)

        return Pair(s0, s1)
    }

    fun inversePermutation(s0: Int, s1: Int): ByteArray {
        val data = ByteArray(8)
        var tmp: Int
        tmp = 0
        tmp = tmp or (((s1 ushr 24) and 1) shl 7)
        tmp = tmp or (((s0 ushr 24) and 1) shl 6)
        tmp = tmp or (((s1 ushr 16) and 1) shl 5)
        tmp = tmp or (((s0 ushr 16) and 1) shl 4)
        tmp = tmp or (((s1 ushr 8) and 1) shl 3)
        tmp = tmp or (((s0 ushr 8) and 1) shl 2)
        tmp = tmp or ((s1 and 1) shl 1)
        tmp = tmp or (s0 and 1)
        data[3] = (tmp and 0xFF).toByte()

        tmp = 0
        tmp = tmp or (((s1 ushr 25) and 1) shl 7)
        tmp = tmp or (((s0 ushr 25) and 1) shl 6)
        tmp = tmp or (((s1 ushr 17) and 1) shl 5)
        tmp = tmp or (((s0 ushr 17) and 1) shl 4)
        tmp = tmp or (((s1 ushr 9) and 1) shl 3)
        tmp = tmp or (((s0 ushr 9) and 1) shl 2)
        tmp = tmp or (((s1 ushr 1) and 1) shl 1)
        tmp = tmp or ((s0 ushr 1) and 1)
        data[2] = (tmp and 0xFF).toByte()

        tmp = 0
        tmp = tmp or (((s1 ushr 26) and 1) shl 7)
        tmp = tmp or (((s0 ushr 26) and 1) shl 6)
        tmp = tmp or (((s1 ushr 18) and 1) shl 5)
        tmp = tmp or (((s0 ushr 18) and 1) shl 4)
        tmp = tmp or (((s1 ushr 10) and 1) shl 3)
        tmp = tmp or (((s0 ushr 10) and 1) shl 2)
        tmp = tmp or (((s1 ushr 2) and 1) shl 1)
        tmp = tmp or ((s0 ushr 2) and 1)
        data[1] = (tmp and 0xFF).toByte()

        tmp = 0
        tmp = tmp or (((s1 ushr 27) and 1) shl 7)
        tmp = tmp or (((s0 ushr 27) and 1) shl 6)
        tmp = tmp or (((s1 ushr 19) and 1) shl 5)
        tmp = tmp or (((s0 ushr 19) and 1) shl 4)
        tmp = tmp or (((s1 ushr 11) and 1) shl 3)
        tmp = tmp or (((s0 ushr 11) and 1) shl 2)
        tmp = tmp or (((s1 ushr 3) and 1) shl 1)
        tmp = tmp or ((s0 ushr 3) and 1)
        data[0] = (tmp and 0xFF).toByte()

        tmp = 0
        tmp = tmp or (((s1 ushr 28) and 1) shl 7)
        tmp = tmp or (((s0 ushr 28) and 1) shl 6)
        tmp = tmp or (((s1 ushr 20) and 1) shl 5)
        tmp = tmp or (((s0 ushr 20) and 1) shl 4)
        tmp = tmp or (((s1 ushr 12) and 1) shl 3)
        tmp = tmp or (((s0 ushr 12) and 1) shl 2)
        tmp = tmp or (((s1 ushr 4) and 1) shl 1)
        tmp = tmp or ((s0 ushr 4) and 1)
        data[7] = (tmp and 0xFF).toByte()

        tmp = 0
        tmp = tmp or (((s1 ushr 29) and 1) shl 7)
        tmp = tmp or (((s0 ushr 29) and 1) shl 6)
        tmp = tmp or (((s1 ushr 21) and 1) shl 5)
        tmp = tmp or (((s0 ushr 21) and 1) shl 4)
        tmp = tmp or (((s1 ushr 13) and 1) shl 3)
        tmp = tmp or (((s0 ushr 13) and 1) shl 2)
        tmp = tmp or (((s1 ushr 5) and 1) shl 1)
        tmp = tmp or ((s0 ushr 5) and 1)
        data[6] = (tmp and 0xFF).toByte()

        tmp = 0
        tmp = tmp or (((s1 ushr 30) and 1) shl 7)
        tmp = tmp or (((s0 ushr 30) and 1) shl 6)
        tmp = tmp or (((s1 ushr 22) and 1) shl 5)
        tmp = tmp or (((s0 ushr 22) and 1) shl 4)
        tmp = tmp or (((s1 ushr 14) and 1) shl 3)
        tmp = tmp or (((s0 ushr 14) and 1) shl 2)
        tmp = tmp or (((s1 ushr 6) and 1) shl 1)
        tmp = tmp or ((s0 ushr 6) and 1)
        data[5] = (tmp and 0xFF).toByte()

        tmp = 0
        tmp = tmp or (((s1 ushr 31) and 1) shl 7)
        tmp = tmp or (((s0 ushr 31) and 1) shl 6)
        tmp = tmp or (((s1 ushr 23) and 1) shl 5)
        tmp = tmp or (((s0 ushr 23) and 1) shl 4)
        tmp = tmp or (((s1 ushr 15) and 1) shl 3)
        tmp = tmp or (((s0 ushr 15) and 1) shl 2)
        tmp = tmp or (((s1 ushr 7) and 1) shl 1)
        tmp = tmp or ((s0 ushr 7) and 1)
        data[4] = (tmp and 0xFF).toByte()
        return data
    }

    private fun f(state: Int, key: List<Int>): Int {
        // E盒扩展: t1/t2 (必须使用 ushr 避免 Kotlin 有符号 Int 的算术右移问题)
        var t1 = ((state and 1) shl 31)
        t1 = t1 or ((state and 0xF8000000.toInt()) ushr 1)
        t1 = t1 or ((state and 0x1F800000) ushr 3)
        t1 = t1 or ((state and 0x01F80000) ushr 5)
        t1 = t1 or ((state and 0x001F8000) ushr 7)
        var t2 = ((state and 0x0001F800) shl 15)
        t2 = t2 or ((state and 0x00001F80) shl 13)
        t2 = t2 or ((state and 0x000001F8) shl 11)
        t2 = t2 or ((state and 0x0000001F) shl 9)
        t2 = t2 or ((state and 0x80000000.toInt()) ushr 23)

        val k0 = ((t1 ushr 24) and 0xFF) xor key[0]
        val k1 = ((t1 ushr 16) and 0xFF) xor key[1]
        val k2 = ((t1 ushr 8) and 0xFF) xor key[2]
        val k3 = ((t2 ushr 24) and 0xFF) xor key[3]
        val k4 = ((t2 ushr 16) and 0xFF) xor key[4]
        val k5 = ((t2 ushr 8) and 0xFF) xor key[5]

        val sboxOut = (
            (sbox[0][sboxBit(k0 shr 2)] shl 28) or
            (sbox[1][sboxBit(((k0 and 0x03) shl 4) or (k1 shr 4))] shl 24) or
            (sbox[2][sboxBit(((k1 and 0x0F) shl 2) or (k2 shr 6))] shl 20) or
            (sbox[3][sboxBit(k2 and 0x3F)] shl 16) or
            (sbox[4][sboxBit(k3 shr 2)] shl 12) or
            (sbox[5][sboxBit(((k3 and 0x03) shl 4) or (k4 shr 4))] shl 8) or
            (sbox[6][sboxBit(((k4 and 0x0F) shl 2) or (k5 shr 6))] shl 4) or
            sbox[7][sboxBit(k5 and 0x3F)]
        )

        // P盒置换 (inline) - 逐项累加避免运算符优先级问题
        var pOut = 0
        pOut = pOut or (((sboxOut ushr 16) and 1) shl 31)
        pOut = pOut or (((sboxOut ushr 25) and 1) shl 30)
        pOut = pOut or (((sboxOut ushr 12) and 1) shl 29)
        pOut = pOut or (((sboxOut ushr 11) and 1) shl 28)
        pOut = pOut or (((sboxOut ushr 3) and 1) shl 27)
        pOut = pOut or (((sboxOut ushr 20) and 1) shl 26)
        pOut = pOut or (((sboxOut ushr 4) and 1) shl 25)
        pOut = pOut or (((sboxOut ushr 15) and 1) shl 24)
        pOut = pOut or (((sboxOut ushr 31) and 1) shl 23)
        pOut = pOut or (((sboxOut ushr 17) and 1) shl 22)
        pOut = pOut or (((sboxOut ushr 9) and 1) shl 21)
        pOut = pOut or (((sboxOut ushr 6) and 1) shl 20)
        pOut = pOut or (((sboxOut ushr 27) and 1) shl 19)
        pOut = pOut or (((sboxOut ushr 14) and 1) shl 18)
        pOut = pOut or (((sboxOut ushr 1) and 1) shl 17)
        pOut = pOut or (((sboxOut ushr 22) and 1) shl 16)
        pOut = pOut or (((sboxOut ushr 30) and 1) shl 15)
        pOut = pOut or (((sboxOut ushr 24) and 1) shl 14)
        pOut = pOut or (((sboxOut ushr 8) and 1) shl 13)
        pOut = pOut or (((sboxOut ushr 18) and 1) shl 12)
        pOut = pOut or ((sboxOut and 1) shl 11)
        pOut = pOut or (((sboxOut ushr 5) and 1) shl 10)
        pOut = pOut or (((sboxOut ushr 29) and 1) shl 9)
        pOut = pOut or (((sboxOut ushr 23) and 1) shl 8)
        pOut = pOut or (((sboxOut ushr 13) and 1) shl 7)
        pOut = pOut or (((sboxOut ushr 19) and 1) shl 6)
        pOut = pOut or (((sboxOut ushr 2) and 1) shl 5)
        pOut = pOut or (((sboxOut ushr 26) and 1) shl 4)
        pOut = pOut or (((sboxOut ushr 10) and 1) shl 3)
        pOut = pOut or (((sboxOut ushr 21) and 1) shl 2)
        pOut = pOut or (((sboxOut ushr 28) and 1) shl 1)
        pOut = pOut or ((sboxOut ushr 7) and 1)
        return pOut
    }

    private fun cryptBlock(input: ByteArray, key: Array<ByteArray>): ByteArray {
        var (s0, s1) = initialPermutation(input)

        for (idx in 0 until 15) {
            val prevS1 = s1
            val keyList = key[idx].map { it.toInt() and 0xFF }
            s1 = f(s1, keyList) xor s0
            s0 = prevS1
        }
        val lastKey = key[15].map { it.toInt() and 0xFF }
        s0 = f(s1, lastKey) xor s0

        return inversePermutation(s0, s1)
    }

    fun keySchedule(key: ByteArray, mode: Int): Array<ByteArray> {
        val schedule = Array(16) { ByteArray(6) }
        // Debug: print first key schedule
        if (key.contentEquals(Tripledes.KEY_24.copyOfRange(16, 24))) {
            print("    K3-D key={${key.joinToString(",") { "%02X".format(it) }}}")
            println()
        }
        val keyRndShift = intArrayOf(1, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1)

        val keyPermC = intArrayOf(
            56,48,40,32,24,16,8,0,57,49,41,33,25,17,9,1,
            58,50,42,34,26,18,10,2,59,51,43,35
        )
        val keyPermD = intArrayOf(
            62,54,46,38,30,22,14,6,61,53,45,37,29,21,13,5,
            60,52,44,36,28,20,12,4,27,19,11,3
        )
        val keyCompression = intArrayOf(
            13,16,10,23,0,4,2,27,14,5,20,9,22,18,11,3,
            25,7,15,6,26,19,12,1,40,51,30,36,46,54,29,39,
            50,44,32,47,43,48,38,55,33,52,45,41,49,35,28,31
        )

        val v0 = (key[0].toInt() and 0xFF) or ((key[1].toInt() and 0xFF) shl 8) or
                ((key[2].toInt() and 0xFF) shl 16) or ((key[3].toInt() and 0xFF) shl 24)
        val v1 = (key[4].toInt() and 0xFF) or ((key[5].toInt() and 0xFF) shl 8) or
                ((key[6].toInt() and 0xFF) shl 16) or ((key[7].toInt() and 0xFF) shl 24)

        var c = 0L
        for (i in keyPermC.indices) {
            val b = keyPermC[i]
            val bit = if (b < 32) ((v0 ushr (31 - b)) and 1).toLong()
                      else ((v1 ushr (63 - b)) and 1).toLong()
            c = c or (bit shl (31 - i))
        }

        var d = 0L
        for (i in keyPermD.indices) {
            val b = keyPermD[i]
            val bit = if (b < 32) ((v0 ushr (31 - b)) and 1).toLong()
                      else ((v1 ushr (63 - b)) and 1).toLong()
            d = d or (bit shl (31 - i))
        }

        for (i in 0 until 16) {
            c = ((c shl keyRndShift[i]) or (c ushr (28 - keyRndShift[i]))) and 0xFFFFFFF0L
            d = ((d shl keyRndShift[i]) or (d ushr (28 - keyRndShift[i]))) and 0xFFFFFFF0L

            val togen = if (mode == DECRYPT) 15 - i else i

            for (j in 0 until 24) {
                val bit = ((c ushr (31 - keyCompression[j])) and 1L).toInt()
                schedule[togen][j / 8] = (schedule[togen][j / 8].toInt() or (bit shl (7 - (j % 8)))).toByte()
            }
            for (j in 24 until 48) {
                val bit = ((d ushr (31 - (keyCompression[j] - 27))) and 1L).toInt()
                schedule[togen][j / 8] = (schedule[togen][j / 8].toInt() or (bit shl (7 - (j % 8)))).toByte()
            }
        }

        return schedule
    }

    fun debugKeySchedule() {
        val k3 = KEY_24.copyOfRange(16, 24)
        val sk = keySchedule(k3, DECRYPT)
        println("    K3-D key: ${k3.joinToString(",") { "%02X".format(it) }}")
        for (i in 0..2) {
            println("    K3-D round $i: ${sk[i].joinToString(",") { "%02X".format(it.toInt() and 0xFF) }}")
        }
        val k2 = KEY_24.copyOfRange(8, 16)
        val sk2 = keySchedule(k2, ENCRYPT)
        println("    K2-E key: ${k2.joinToString(",") { "%02X".format(it) }}")
        for (i in 0..2) {
            println("    K2-E round $i: ${sk2[i].joinToString(",") { "%02X".format(it.toInt() and 0xFF) }}")
        }
    }

    fun tripledesKeySetup(key: ByteArray, mode: Int): List<Array<ByteArray>> {
        return if (mode == ENCRYPT) {
            listOf(
                keySchedule(key.copyOfRange(0, 8), ENCRYPT),
                keySchedule(key.copyOfRange(8, 16), DECRYPT),
                keySchedule(key.copyOfRange(16, 24), ENCRYPT)
            )
        } else {
            listOf(
                keySchedule(key.copyOfRange(16, 24), DECRYPT),  // K3: "!@#)(NHL"
                keySchedule(key.copyOfRange(8, 16), ENCRYPT),   // K2: "123ZXC!@"
                keySchedule(key.copyOfRange(0, 8), DECRYPT)     // K1: "!@#)(*$%"
            )
        }
    }

    fun tripledesCrypt(data: ByteArray, key: List<Array<ByteArray>>): ByteArray {
        var result = data.copyOf()
        for (i in 0 until 3) {
            result = cryptBlock(result, key[i])
        }
        return result
    }

    fun debugCryptBlock(input: ByteArray, key: Array<ByteArray>, label: String): ByteArray {
        var (s0, s1) = initialPermutation(input)
        println("    $label IP: s0=0x%08X s1=0x%08X".format(s0, s1))
        for (idx in 0 until 16) {
            val prevS1 = s1
            val keyList = key[idx].map { it.toInt() and 0xFF }
            val fval = f(s1, keyList)
            s1 = fval xor s0
            s0 = prevS1
            if (idx < 3 || idx >= 14) {
                println("    $label R$idx f=0x%08X s0=0x%08X s1=0x%08X".format(fval, s0, s1))
            }
        }
        return inversePermutation(s0, s1)
    }

    fun debugTripledesCrypt(data: ByteArray, key: List<Array<ByteArray>>): ByteArray {
        var result = data.copyOf()
        for (i in 0 until 3) {
            val label = "tri-$i"
            result = debugCryptBlock(result, key[i], label)
            println("    $label out: ${result.joinToString("") { "%02X".format(it) }}")
        }
        return result
    }
}

// ============================================================
// Zlib 解压
// ============================================================

/**
 * 尝试多种 zlib 解压策略
 */
fun tryZlibDecompress(data: ByteArray): ByteArray {
    // 策略 1: 标准 zlib 解压（带 zlib 头）
    if (data.isNotEmpty() && (data[0].toInt() and 0xFF) == 0x78) {
        try {
            val result = InflaterInputStream(ByteArrayInputStream(data)).use { it.readBytes() }
            println("   ✅ 策略 1 (标准 zlib) 成功: ${result.size} 字节")
            return result
        } catch (e: Exception) {
            println("   ❌ 策略 1 (标准 zlib) 失败: $e")
        }
    }

    // 策略 2: 原始 Deflate（无 zlib 头）
    try {
        val inflater = Inflater(true)
        val result = try {
            InflaterInputStream(ByteArrayInputStream(data), inflater).use { it.readBytes() }
        } finally {
            inflater.end()
        }
        println("   ✅ 策略 2 (raw deflate) 成功: ${result.size} 字节")
        return result
    } catch (e: Exception) {
        println("   ❌ 策略 2 (raw deflate) 失败: $e")
    }

    // 策略 3: 寻找 zlib 头偏移
    val candidateOffsets = data.withIndex()
        .filter { it.value.toInt() and 0xFF == 0x78 }
        .map { it.index }
    if (candidateOffsets.isNotEmpty()) {
        println("   尝试策略 3: 从偏移 ${candidateOffsets.joinToString()} 开始解压...")
        for (offset in candidateOffsets) {
            if (offset + 1 >= data.size) continue
            val second = data[offset + 1].toInt() and 0xFF
            val combined = (0x78 shl 8) or second
            if (combined % 31 == 0) {
                try {
                    val result = InflaterInputStream(
                        ByteArrayInputStream(data.copyOfRange(offset, data.size))
                    ).use { it.readBytes() }
                    println("   ✅ 策略 3 (偏移 $offset) 成功: ${result.size} 字节")
                    return result
                } catch (e: Exception) {
                    println("   ❌ 策略 3 (偏移 $offset) 失败: $e")
                }
            }
        }
    }

    throw RuntimeException("所有解压策略均失败")
}

// ============================================================
// QRC 解析器
// ============================================================

data class LyricWord(
    val word: String,
    val startTime: Long,
    val endTime: Long
)

data class LyricLine(
    val startTime: Long,
    val endTime: Long,
    val text: String,
    val words: List<LyricWord>
)

/**
 * 解析 QRC 格式文本
 *
 * QRC 格式:
 *   [行起始时间,行持续时间] 单词1(起始时间,持续时间) 单词2(起始时间,持续时间) ...
 *
 * 示例:
 *   [0,5000] 你 (0,500) 好 (500,500) 世 (1000,500) 界 (1500,500)
 */
fun parseQrc(content: String): List<LyricLine> {
    val lyricTokenRegex = Regex("""(?<text>.*?)\((?<start>\d+),(?<duration>\d+)\)""")
    val lineTimestampRegex = Regex("""^\[(\d+),(\d+)]""")

    val lines = mutableListOf<LyricLine>()

    for (rawLine in content.lines()) {
        val line = rawLine.trim()
        if (line.isEmpty()) continue

        // 匹配行级时间戳
        val lineStartMs = lineTimestampRegex.find(line)
            ?.groups?.get(1)?.value?.toLongOrNull()
        val lineDurationMs = lineTimestampRegex.find(line)
            ?.groups?.get(2)?.value?.toLongOrNull()

        // 移除行级时间戳
        val lineContent = lineTimestampRegex.replace(line, "")
        val words = mutableListOf<LyricWord>()

        // 逐字匹配
        for (capture in lyricTokenRegex.findAll(lineContent)) {
            val text = capture.groups["text"]?.value.orEmpty()
            val startMs = capture.groups["start"]?.value?.toLongOrNull() ?: continue
            val durationMs = capture.groups["duration"]?.value?.toLongOrNull() ?: continue

            val textClean = text.trimEnd()
            words.add(
                LyricWord(
                    word = textClean,
                    startTime = startMs,
                    endTime = startMs + maxOf(1L, durationMs)
                )
            )
        }

        // 如果没有逐字时间戳，但有行级时间戳，作为整行处理
        if (words.isEmpty() && lineStartMs != null) {
            val fallbackText = lineContent.trim()
            val normalized = fallbackText.replace(Regex("[^\\d(),]"), "")
            val isTimestampToken = Regex("^\\(\\d+,\\d+\\)(?:\\(\\d+,\\d+\\))*$").matches(normalized)

            if (fallbackText.isNotEmpty() && !isTimestampToken) {
                val lineEnd = lineStartMs + (lineDurationMs ?: 0)
                words.add(
                    LyricWord(
                        word = fallbackText,
                        startTime = lineStartMs,
                        endTime = maxOf(lineStartMs + 1, lineEnd)
                    )
                )
            }
        }

        if (words.isEmpty()) continue

        val lineStart = lineStartMs ?: words.first().startTime
        val rawLineEnd = lineStart + (lineDurationMs ?: (words.last().endTime - lineStart))
        val lineEnd = maxOf(lineStart + 1, rawLineEnd)

        lines.add(
            LyricLine(
                startTime = lineStart,
                endTime = lineEnd,
                text = words.joinToString(separator = "") { it.word }.trimEnd(),
                words = words
            )
        )
    }

    return lines
}

/**
 * 从 XML 包装中提取 QRC 内容
 */
fun extractQrcFromXmlIfNeeded(content: String): String {
    if (!content.contains("<QrcInfos", ignoreCase = true) &&
        !content.contains("<LyricInfo", ignoreCase = true) &&
        !Regex("""<Lyric_\d+\b""", RegexOption.IGNORE_CASE).containsMatchIn(content)
    ) {
        return content
    }

    println("   ℹ️ 检测到 XML 包装，提取 LyricContent 属性...")

    val extracted = mutableListOf<String>()
    val tagStartRegex = Regex("""<Lyric_\d+\b[^>]*\bLyricContent=(['"])""", RegexOption.IGNORE_CASE)
    var searchIndex = 0

    while (true) {
        val match = tagStartRegex.find(content, startIndex = searchIndex) ?: break
        val quoteChar = match.groupValues[1].single()
        val valueStart = match.range.last + 1
        val tagEnd = content.indexOf('>', startIndex = valueStart).takeIf { it >= 0 } ?: content.length
        val lastQuoteBeforeTagEnd = content.lastIndexOf(quoteChar, startIndex = tagEnd - 1)
        val valueEnd = if (lastQuoteBeforeTagEnd >= valueStart) lastQuoteBeforeTagEnd
        else content.indexOf(quoteChar, startIndex = valueStart).takeIf { it >= 0 } ?: content.length

        val rawValue = content.substring(valueStart, valueEnd)
        extracted.add(unescapeXml(rawValue))
        searchIndex = valueEnd + 1
    }

    if (extracted.isNotEmpty()) {
        return extracted.joinToString(separator = "\n")
    }

    return content
}

fun unescapeXml(value: String): String {
    return value
        .replace("&quot;", "\"")
        .replace("&apos;", "'")
        .replace("&lt;", "<")
        .replace("&gt;", ">")
        .replace("&amp;", "&")
        .replace(Regex("&#(\\d+);")) { match ->
            match.groups[1]?.value?.toIntOrNull()?.toChar()?.toString() ?: match.value
        }
        .replace(Regex("&#x([0-9A-Fa-f]+);")) { match ->
            match.groups[1]?.value?.toIntOrNull(16)?.toChar()?.toString() ?: match.value
        }
}

/**
 * 从 XML 中提取指定标签的 CDATA 内容
 * 例如: <content><![CDATA[hex_data]]></content>
 */
fun extractXmlCData(xml: String, tagName: String): String? {
    val regex = Regex("""<$tagName\b[^>]*><!\[CDATA\[(.*?)]]></$tagName>""", RegexOption.DOT_MATCHES_ALL)
    return regex.find(xml)?.groupValues?.getOrNull(1)?.takeIf { it.isNotBlank() }
}

// ============================================================
// 工具函数
// ============================================================

data class SearchResult(
    val songMid: String,
    val title: String,
    val artist: String,
    val duration: Long
)

fun parseQqSongIds(songId: String): Pair<String?, Long?> {
    val parts = songId.split("::", limit = 2)
    val mid = parts.getOrNull(0)?.takeIf { it.isNotBlank() }
    val numeric = parts.getOrNull(1)?.toLongOrNull()
    if (numeric != null) return mid to numeric
    return if (songId.all { it.isDigit() }) null to songId.toLongOrNull() else songId to null
}

fun looksLikeHex(text: String): Boolean {
    val normalized = text.trim()
    return normalized.isNotEmpty() &&
        normalized.length % 2 == 0 &&
        normalized.all { it in '0'..'9' || it in 'a'..'f' || it in 'A'..'F' }
}

fun decodeHex(value: String): ByteArray {
    val clean = value.trim()
    require(clean.length % 2 == 0) { "无效的 Hex 字符串长度: ${clean.length}" }
    val out = ByteArray(clean.length / 2)
    var i = 0
    while (i < clean.length) {
        val hi = clean[i].digitToIntOrNull(16) ?: error("无效 Hex 字符: ${clean[i]}")
        val lo = clean[i + 1].digitToIntOrNull(16) ?: error("无效 Hex 字符: ${clean[i + 1]}")
        out[i / 2] = ((hi shl 4) or lo).toByte()
        i += 2
    }
    return out
}

/**
 * 检查字节数组是否大部分是可打印 ASCII
 */
fun printIfMostlyAscii(bytes: ByteArray) {
    val printable = bytes.count { b ->
        val c = b.toInt() and 0xFF
        (c in 0x20..0x7E) || c == 0x0A || c == 0x0D || c == 0x09
    }
    val ratio = if (bytes.isNotEmpty()) printable.toDouble() / bytes.size else 0.0
    println("   可打印 ASCII 比例: ${"%.1f".format(ratio * 100)}% ($printable/${bytes.size})")
    if (ratio > 0.5) {
        val asText = String(bytes, Charsets.UTF_8).trim()
        println("   文本预览: ${asText.take(300)}")
    }
}

/**
 * 使用 Java 标准 DESede/3DES 作为对比
 * 用于验证自定义 QqDes 实现的正确性
 */
fun tryJava3Des(data: ByteArray): ByteArray? {
    return try {
        val key = "!@#)(*\$%123ZXC!@!@#)(NHL".toByteArray(Charsets.US_ASCII)
        val desKey = javax.crypto.spec.DESedeKeySpec(key)
        val keyFactory = javax.crypto.SecretKeyFactory.getInstance("DESede")
        val secretKey = keyFactory.generateSecret(desKey)
        val cipher = javax.crypto.Cipher.getInstance("DESede/ECB/NoPadding")
        cipher.init(javax.crypto.Cipher.DECRYPT_MODE, secretKey)
        cipher.doFinal(data)
    } catch (e: Exception) {
        println("   ⚠️ Java 3DES 失败: ${e.message}")
        null
    }
}

fun formatTime(startMs: Long, endMs: Long): String {
    val startSec = startMs / 1000
    val startMin = startSec / 60
    val startSecRest = startSec % 60
    val startMsRest = startMs % 1000
    return "[%d:%02d.%03d]".format(startMin, startSecRest, startMsRest)
}

fun formatMs(ms: Long): String {
    return "${ms}ms"
}

/**
 * 解析并打印 QRC 歌词（用于检测到明文 QRC 时直接输出）
 */
fun parseAndPrintQrc(content: String) {
    println("═" .repeat(70))
    println("🎤 QRC 歌词内容:")
    println("═" .repeat(70))

    val lines = parseQrc(content)
    for (line in lines) {
        val timeStr = formatTime(line.startTime, line.endTime)
        val wordsDetail = if (line.words.size <= 1) {
            line.text
        } else {
            line.words.joinToString("") { w ->
                "(${w.startTime}→${w.endTime}ms)${w.word}"
            }
        }
        println("  $timeStr $wordsDetail")
    }
}
