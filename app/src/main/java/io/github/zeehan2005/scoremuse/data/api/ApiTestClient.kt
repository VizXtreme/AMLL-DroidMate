package io.github.zeehan2005.scoremuse.data.api

import io.github.zeehan2005.scoremuse.data.get.netease.NeteaseEapiCrypto
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import kotlinx.serialization.json.*
import kotlin.time.measureTimedValue

data class ApiTestResult(
    val responseBody: String = "",
    val statusCode: Int? = null,
    val durationMs: Long = 0L,
    val requestUrl: String = "",
    val requestMethod: String = "",
    val errorMessage: String? = null
)

class ApiTestClient(private val httpClient: HttpClient) {

    suspend fun qqSearch(keyword: String): ApiTestResult {
        val body = buildJsonObject {
            putJsonObject("req_1") {
                put("method", "DoSearchForQQMusicDesktop")
                put("module", "music.search.SearchCgiService")
                putJsonObject("param") {
                    put("num_per_page", 5)
                    put("page_num", 1)
                    put("query", keyword)
                    put("search_type", 0)
                }
            }
        }
        return post("https://u.y.qq.com/cgi-bin/musicu.fcg", body.toString())
    }

    suspend fun qqGetLyrics(songMid: String): ApiTestResult {
        val lyricData = buildJsonObject {
            putJsonObject("comm") { put("ct", 19); put("cv", 1859) }
            putJsonObject("req_1") {
                put("module", "music.musichallSong.PlayLyricInfo")
                put("method", "GetPlayLyricInfo")
                putJsonObject("param") { put("songMID", songMid); put("songID", 0) }
            }
        }
        return get("https://u.y.qq.com/cgi-bin/musicu.fcg") {
            parameter("data", lyricData.toString())
            parameter("format", "json")
        }
    }

    suspend fun qqLyricDownload(musicId: String): ApiTestResult {
        return postForm("https://c.y.qq.com/qqmusic/fcgi-bin/lyric_download.fcg") {
            header("Referer", "https://y.qq.com/")
            setBody(FormDataContent(Parameters.build {
                append("version", "15"); append("miniversion", "82")
                append("lrctype", "4"); append("musicid", musicId)
            }))
        }
    }

    suspend fun neteaseSearch(keyword: String): ApiTestResult {
        val payload = buildJsonObject {
            put("s", keyword); put("type", "1"); put("limit", "5")
            put("offset", "0"); put("total", "true")
        }
        val encrypted = NeteaseEapiCrypto.prepareEapiParams("/api/cloudsearch/pc", payload.toString())
        return postForm("https://interface.music.163.com/eapi/cloudsearch/pc") {
            setBody(FormDataContent(Parameters.build { append("params", encrypted) }))
        }
    }

    suspend fun neteaseGetLyrics(songId: String): ApiTestResult {
        val payload = buildJsonObject {
            put("id", songId); put("cp", "false"); put("lv", "0"); put("kv", "0")
            put("tv", "0"); put("rv", "0"); put("yv", "0"); put("ytv", "0"); put("yrv", "0")
            put("csrf_token", "")
        }
        val encrypted = NeteaseEapiCrypto.prepareEapiParams("/api/song/lyric/v1", payload.toString())
        return postForm("https://interface3.music.163.com/eapi/song/lyric/v1") {
            setBody(FormDataContent(Parameters.build { append("params", encrypted) }))
        }
    }

    suspend fun kugouSearch(keyword: String): ApiTestResult {
        return get("http://mobilecdn.kugou.com/api/v3/search/song") {
            parameter("keyword", keyword); parameter("page", "1")
            parameter("pagesize", "5"); parameter("showtype", "1")
        }
    }

    suspend fun kugouLyricSearch(hash: String): ApiTestResult {
        return get("https://lyrics.kugou.com/search") {
            parameter("ver", "1"); parameter("man", "yes"); parameter("client", "pc")
            parameter("keyword", ""); parameter("hash", hash)
        }
    }

    suspend fun kugouLyricDownload(id: String, accesskey: String): ApiTestResult {
        return get("https://lyrics.kugou.com/download") {
            parameter("ver", "1"); parameter("client", "pc")
            parameter("id", id); parameter("accesskey", accesskey)
            parameter("fmt", "krc"); parameter("charset", "utf8")
        }
    }

    suspend fun amllDbSearch(keyword: String): ApiTestResult {
        val body = buildJsonObject { put("query", keyword); put("type", "all") }
        return post("https://amlldb.bikonoo.com/api/search-lyrics", body.toString())
    }

    private val amllEndpoints = listOf(
        "https://amll-ttml-db.stevexmh.net/{platform}/{rawId}",                              // 主站
        "https://amlldb.bikonoo.com/{folder}/{id}.ttml",                                     // 镜像 1
        "https://amll.mirror.dimeta.top/api/db/{folder}/{id}.ttml",                          // 镜像 2
        "https://raw.githubusercontent.com/amll-dev/amll-ttml-db/refs/heads/main/{folder}/{id}.ttml" // GitHub Raw
    )

    suspend fun amllGetLyrics(songId: String, mirrorIndex: Int = -1): ApiTestResult {
        val parts = songId.split(":", limit = 2)
        val platform = if (parts.size == 2) parts[0].lowercase() else "ncm"
        val rawId = if (parts.size == 2) parts[1] else songId
        val folder = if (platform.endsWith("-lyrics")) platform else "${platform}-lyrics"
        val id = rawId.removeSuffix(".ttml")  // 模板已自带 .ttml，这里不再追加
        val urls = amllEndpoints.map { tpl ->
            tpl.replace("{platform}", platform).replace("{rawId}", rawId)
                .replace("{folder}", folder).replace("{id}", id)
        }
        if (mirrorIndex in urls.indices) {
            return get(urls[mirrorIndex])
        }
        for (url in urls) {
            val r = get(url)
            if (r.statusCode != null && r.responseBody.isNotBlank()) return r
        }
        return ApiTestResult(requestUrl = urls.first(), errorMessage = "all endpoints returned empty")
    }

    /** 返回所有 AMLL TTML 端点的格式化标签 */
    fun getAmllEndpointLabels(): List<String> = amllEndpoints.map { tpl ->
        val name = when {
            tpl.contains("stevexmh") -> "主站"
            tpl.contains("bikonoo") -> "镜像 1"
            tpl.contains("dimeta") -> "镜像 2"
            else -> "GitHub Raw"
        }
        "AMLL TTML - $name"
    }

    // ─── HTTP executors ───

    private suspend fun get(url: String, block: HttpRequestBuilder.() -> Unit = {}): ApiTestResult {
        val (resp, dur) = measureTimedValue { runCatching { httpClient.get(url, block) } }
        return resp.fold({ r ->
            ApiTestResult(
                responseBody = runCatching { r.body<String>() }.getOrDefault(""),
                statusCode = r.status.value,
                durationMs = dur.inWholeMilliseconds,
                requestUrl = url, requestMethod = "GET"
            )
        }, { e ->
            ApiTestResult(requestUrl = url, errorMessage = e.message, durationMs = dur.inWholeMilliseconds)
        })
    }

    private suspend fun post(url: String, body: String = ""): ApiTestResult {
        val (resp, dur) = measureTimedValue { runCatching {
            httpClient.post(url) { contentType(ContentType.Application.Json); if (body.isNotEmpty()) setBody(body) }
        }}
        return resp.fold({ r ->
            ApiTestResult(
                responseBody = runCatching { r.body<String>() }.getOrDefault(""),
                statusCode = r.status.value,
                durationMs = dur.inWholeMilliseconds,
                requestUrl = url, requestMethod = "POST"
            )
        }, { e ->
            ApiTestResult(requestUrl = url, errorMessage = e.message, durationMs = dur.inWholeMilliseconds)
        })
    }

    private suspend fun postForm(url: String, block: HttpRequestBuilder.() -> Unit = {}): ApiTestResult {
        val (resp, dur) = measureTimedValue { runCatching { httpClient.post(url, block) } }
        return resp.fold({ r ->
            ApiTestResult(
                responseBody = runCatching { r.body<String>() }.getOrDefault(""),
                statusCode = r.status.value,
                durationMs = dur.inWholeMilliseconds,
                requestUrl = url, requestMethod = "POST"
            )
        }, { e ->
            ApiTestResult(requestUrl = url, errorMessage = e.message, durationMs = dur.inWholeMilliseconds)
        })
    }
}
