package dev.amll.droidmate.data.converter

import io.github.zeehan2005.scoremuse.global.LyricLine
import io.github.zeehan2005.scoremuse.global.UnifiedLyrics
import io.github.zeehan2005.scoremuse.data.parser.global.TimestampUtils
import io.github.zeehan2005.scoremuse.data.parser.global.UnifiedLyricsParser
import io.github.zeehan2005.scoremuse.global.LyricsMetadata
import timber.log.Timber
import java.util.Locale

/**
 * TTML 杞崲鍣?- 灏嗘瓕璇嶈浆鎹负 TTML 鏍煎紡
 * 
 * 杩欎釜瀵硅薄鎻愪緵浜嗕竴绯诲垪鏂规硶锛岀敤浜庡湪鍐呴儴姝岃瘝妯″瀷鍜?TTML (Timed Text Markup Language)
 * 鏍囧噯鏍煎紡涔嬮棿杩涜杞崲銆俆TML 鏄竴绉嶅熀浜?XML 鐨勫瓧骞曟牸寮忥紝骞挎硾鐢ㄤ簬瑙嗛鍜岄煶涔愯涓氥€?
 * 
 * 涓昏鍔熻兘锛?
 * - 瀵煎嚭锛氬皢搴旂敤鍐呴儴鐨勬瓕璇嶆暟鎹浆鎹负鏍囧噯 TTML 鏍煎紡
 * - 瀵煎叆锛氳В鏋愬閮?TTML 鏂囦欢涓哄唴閮ㄦ暟鎹粨鏋?
 * - XML 杞箟锛氶槻姝㈢壒娈婂瓧绗︾牬鍧?XML 缁撴瀯
 * - 鍏冩暟鎹繚鐣欙細淇濇寔姝屾洸淇℃伅銆佺粨鏋勬爣璁扮瓑
 * 
 * 娉ㄦ剰锛氭湰杞崲鍣ㄧ殑鎵€鏈夋柟娉曞潎閫氳繃鍙嶅皠璋冪敤锛屽洜姝ら渶瑕佷繚鐣?@Suppress("unused")
 * 杩欎簺鏂规硶鍦ㄨ繍琛屾椂琚姩鎬佽皟鐢紝鐢ㄤ簬姝岃瘝鏍煎紡鐨勫鍏?瀵煎嚭鍔熻兘
 */
object TTMLConverter {

    /**
     * 杞箟 XML 鐗规畩瀛楃
     * 
     * XML 涓湁 5 涓壒娈婂瓧绗﹀繀椤昏繘琛岃浆涔夛紝鍚﹀垯浼氱牬鍧忔枃妗ｇ粨鏋勶細
     * - & (鍜屽彿) 鈫?&amp;  猸?蹇呴』鏈€鍏堣浆涔夛紝鍚﹀垯浼氬鑷村弻閲嶈浆涔?
     * - < (灏忎簬鍙? 鈫?&lt;
     * - > (澶т簬鍙? 鈫?&gt;
     * - " (鍙屽紩鍙? 鈫?&quot;
     * - ' (鍗曞紩鍙? 鈫?&apos;
     * 
     * 渚嬪姝岃瘝涓殑 "A&B" 濡傛灉涓嶈浆涔夛紝浼氳 XML 瑙ｆ瀽鍣ㄨ璁や负鏄疄浣撳紩鐢ㄣ€?
     * 
     * @param text 鍘熷鏂囨湰
     * @return 杞箟鍚庣殑瀹夊叏鏂囨湰
     */
    private fun escapeXml(text: String?): String? {
        return text
            ?.replace("&", "&amp;")   // 蹇呴』绗竴涓浆涔夛紝閬垮厤鍙岄噸杞箟
            ?.replace("<", "&lt;")
            ?.replace(">", "&gt;")
            ?.replace("\"", "&quot;")
            ?.replace("'", "&apos;")
    }

    /**
     * 灏嗘瓕璇嶈鍒楄〃杞崲涓?TTML 瀛楃涓?
     * 
     * 杩欐槸 TTML 瀵煎嚭鐨勬牳蹇冩柟娉曘€傚畠浼氬皢鍐呴儴鐨勬瓕璇嶆暟鎹粨鏋勮浆鎹负鏍囧噯鐨?TTML XML 鏍煎紡銆?
     * 
     * 鐢熸垚鐨?TTML 鍖呭惈锛?
     * - XML 澹版槑鍜屽懡鍚嶇┖闂村畾涔?
     * - 鍏冩暟鎹尯鍩燂紙鏍囬銆佽壓鏈绛夛級
     * - 姝岃瘝涓讳綋鍖哄煙锛堝寘鍚椂闂存埑鐨勯€愯姝岃瘝锛?
     * - 鍙€夌殑鏍煎紡鍖栵紙缂╄繘鍜屾崲琛岋紝渚夸簬闃呰锛?
     * 
     * 猸?淇鍏抽敭锛氭墍鏈夊瓧绗︿覆鍊煎繀椤昏繘琛?XML 杞箟锛岄槻姝㈣В鏋愬け璐?
     * 
     * @param lyrics 瑕佽浆鎹㈢殑姝岃瘝瀵硅薄
     * @param formatted 鏄惁鏍煎紡鍖栬緭鍑猴紙娣诲姞缂╄繘鍜屾崲琛岋級
     * @return TTML 鏍煎紡鐨?XML 瀛楃涓?
     */
    fun toTTMLString(
        lyrics: UnifiedLyrics,
        formatted: Boolean = false
    ): String {
        val sb = StringBuilder()
        
        // XML 澶?
        sb.append("""<?xml version="1.0" encoding="UTF-8"?>""")
        if (formatted) sb.append("\n")
        
        /** TTML 鏍稿績 */
        val indent = if (formatted) "  " else ""
        val lineBreak = if (formatted) "\n" else ""
        
        sb.append("""<tt xmlns="http://www.w3.org/ns/ttml"""")
        sb.append(""" xmlns:ttm="http://www.w3.org/ns/ttml#metadata"""")
        sb.append(""" xmlns:itunes="http://music.apple.com/lyric-ttml-internal"""")
        sb.append(""" xmlns:amll="http://www.example.com/ns/amll"""")
        sb.append(""" xml:lang="ja"""")
        sb.append(""" itunes:timing="Word">$lineBreak""")
        
        // Head
        sb.append("${indent}<head>$lineBreak")
        
        /** 鉁?濡傛灉鏈夊師濮?XML metadata锛岀洿鎺ヤ娇鐢ㄥ畠锛堜繚鐣欐墍鏈夋湭浣跨敤鐨?XML 淇℃伅锛?*/
        val rawXmlMetadata = lyrics.metadata.rawXmlMetadata
        if (!rawXmlMetadata.isNullOrBlank()) {
            Timber.d("[TTMLConverter] Using preserved raw XML metadata for serialization")
            sb.append("${indent}${indent}$rawXmlMetadata$lineBreak")
        } else {
            // 鍚﹀垯锛岄噸鏂版瀯寤?metadata锛堝悜鍚庡吋瀹癸級
            sb.append("${indent}${indent}<metadata>")
            if (formatted) sb.append("\n")
            
            // Metadata
            with(lyrics.metadata) {
                // 鉁?娣诲姞涓诲敱 agent 瀹氫箟锛堝繀椤绘斁鍦ㄥ叾浠?meta 涔嬪墠锛岃 Rust 瑙ｆ瀽鍣ㄦ纭瘑鍒級
                sb.append("""${indent}${indent}${indent}<ttm:agent type="person" xml:id="v1" />""")
                if (formatted) sb.append("\n")
                
                /** 妫€鏌ユ槸鍚︽湁瀵瑰敱姝岃瘝锛屽鏋滄湁鍒欐坊鍔?v2 agent 瀹氫箟 */
                val hasDuet = lyrics.lines.any { it.isDuet }
                if (hasDuet) {
                    sb.append("""${indent}${indent}${indent}<ttm:agent type="other" xml:id="v2" />""")
                    if (formatted) sb.append("\n")
                }
                
                sb.append("""${indent}${indent}${indent}<amll:meta key="title" value="${escapeXml(title)}" />""")
                if (formatted) sb.append("\n")
                sb.append("""${indent}${indent}${indent}<amll:meta key="artist" value="${escapeXml(artist)}" />""")
                if (formatted) sb.append("\n")
                album?.let {
                    sb.append("""${indent}${indent}${indent}<amll:meta key="album" value="${escapeXml(album)}" />""")
                    if (formatted) sb.append("\n")
                }
                sb.append("""${indent}${indent}${indent}<amll:meta key="language" value="${escapeXml(language)}" />""")
                if (formatted) sb.append("\n")
                sb.append("""${indent}${indent}${indent}<amll:meta key="source" value="${escapeXml(source)}" />""")
                if (formatted) sb.append("\n")
            }
            
            sb.append("""${indent}${indent}</metadata>$lineBreak""")
        }
        

        /** Generate <iTunesMetadata><transliterations> per-word romanization blocks */
        val hasTransliterationWords = lyrics.lines.any { !it.transliterationWords.isNullOrEmpty() }
        if (hasTransliterationWords) {
            sb.append("""${indent}${indent}<iTunesMetadata xmlns="http://music.apple.com/lyric-ttml-internal">$lineBreak""")
            sb.append("""${indent}${indent}${indent}<transliterations>$lineBreak""")
            sb.append("""${indent}${indent}${indent}${indent}<transliteration xml:lang="ja-Latn">$lineBreak""")
            lyrics.lines.forEachIndexed { index, line ->
                if (line.transliterationWords.isNullOrEmpty()) return@forEachIndexed
                val lineNum = "L${index + 1}"
                sb.append("""${indent}${indent}${indent}${indent}${indent}<text for="$lineNum">$lineBreak""")
                line.transliterationWords.forEachIndexed { wIndex, word ->
                    val wBegin = TimestampUtils.fromMillis(word.startTime)
                    val wEnd = TimestampUtils.fromMillis(word.endTime)
                    val wText = word.word.trim()
                    if (wText.isEmpty()) return@forEachIndexed
                    if (wIndex > 0) sb.append(" ")
                    sb.append("""${indent}${indent}${indent}${indent}${indent}${indent}<span begin="$wBegin" end="$wEnd" xmlns="http://www.w3.org/ns/ttml">${escapeXml(wText)}</span>""")
                    if (formatted) sb.append("\n")
                }
                sb.append("""${indent}${indent}${indent}${indent}${indent}</text>$lineBreak""")
            }
            sb.append("""${indent}${indent}${indent}${indent}</transliteration>$lineBreak""")
            sb.append("""${indent}${indent}${indent}</transliterations>$lineBreak""")
            sb.append("""${indent}${indent}</iTunesMetadata>$lineBreak""")
        }

        sb.append("""${indent}</head>$lineBreak""")
        
        /** Body */
        val duration = TimestampUtils.fromMillis(lyrics.lines.lastOrNull()?.endTime ?: 0L)
        sb.append("""${indent}<body dur="$duration">$lineBreak""")

        /** 鉁?灏?songStructures 搴忓垪鍖栦负 <div itunes:songPart="...">锛岀‘淇濈紦瀛樺啀瑙ｆ瀽鏃惰兘鎭㈠缁撴瀯銆?
         *  鍙繚鐣?鐪熷疄"鐨勬钀芥爣璁帮紙鏉ヨ嚜 TTML 鍘熸暟鎹殑 songPart锛夛紝鎺掗櫎锛?
         *  - 鎺ㄦ柇鐨勯棿濂?鍓嶅/灏惧锛圛NTRO_INST銆両NTERLUDE銆丱UTRO_INST锛?
         *  - SongStructureParser 鍥為€€鎺ㄦ柇鐨?"娈佃惤 X" 鏍囩
         */
        val structures = lyrics.metadata.songStructures
        if (!structures.isNullOrEmpty()) {
            val realStructures = structures.filter { structure ->
                structure.label != "Interlude" &&
                !structure.label.matches(Regex("^娈佃惤\\s*\\d+$"))
            }

            if (realStructures.isNotEmpty()) {
                val addedLines = mutableSetOf<Int>()

                for (structure in realStructures) {
                    val startTimeAttr = TimestampUtils.fromMillis(structure.startTime)
                    val endTimeAttr = TimestampUtils.fromMillis(structure.endTime)

                    sb.append("""${indent}${indent}<div itunes:songPart="${escapeXml(structure.label)}" begin="$startTimeAttr" end="$endTimeAttr">$lineBreak""")

                    // 娣诲姞璇ョ粨鏋勬椂闂磋寖鍥村唴鐨勬瓕璇嶈
                    for ((index, line) in lyrics.lines.withIndex()) {
                        if (index in addedLines) continue
                        if (line.startTime in structure.startTime until structure.endTime) {
                            appendLyricLine(sb, line, index, indent, formatted)
                            addedLines.add(index)
                        }
                    }

                    sb.append("""${indent}${indent}</div>$lineBreak""")
                }

                // 娌℃湁琚换浣曠粨鏋勮鐩栫殑鍓╀綑姝岃瘝琛?
                if (addedLines.size < lyrics.lines.size) {
                    sb.append("""${indent}${indent}<div>$lineBreak""")
                    for ((index, line) in lyrics.lines.withIndex()) {
                        if (index !in addedLines) {
                            appendLyricLine(sb, line, index, indent, formatted)
                        }
                    }
                    sb.append("""${indent}${indent}</div>$lineBreak""")
                }
            } else {
                // 鍙湁鎺ㄦ柇缁撴瀯锛堥棿濂忕瓑锛夛紝娌℃湁鐪熷疄娈佃惤鏍囪 鈫?浣跨敤鍗曚釜 div
                sb.append("""${indent}${indent}<div>$lineBreak""")
                lyrics.lines.forEachIndexed { index, line ->
                    appendLyricLine(sb, line, index, indent, formatted)
                }
                sb.append("""${indent}${indent}</div>$lineBreak""")
            }
        } else {
            // 娌℃湁缁撴瀯淇℃伅锛屼娇鐢ㄥ崟涓?div 鍖呭惈鎵€鏈夋瓕璇?
            sb.append("""${indent}${indent}<div>$lineBreak""")
            lyrics.lines.forEachIndexed { index, line ->
                appendLyricLine(sb, line, index, indent, formatted)
            }
            sb.append("""${indent}${indent}</div>$lineBreak""")
        }
        
        // 鍏抽棴 body 鍜?tt 鏍囩
        sb.append("""${indent}</body>$lineBreak""")
        sb.append("</tt>")
        
        return sb.toString()
    }
    
    /**
     * 杈呭姪鏂规硶锛氳拷鍔犳瓕璇嶈鍒?StringBuilder
     */
    private fun appendLyricLine(
        sb: StringBuilder,
        line: LyricLine,
        lineIndex: Int,
        indent: String,
        formatted: Boolean
    ) {
        val begin = TimestampUtils.fromMillis(line.startTime)
        val end = TimestampUtils.fromMillis(line.endTime)
        val lineNum = "L${lineIndex + 1}"
                
        /** 鉁?浼樺厛浣跨敤 agent 瀛楁锛屽鏋滄病鏈夊垯鏍规嵁 isDuet 鎺ㄦ柇*/
        val agentValue = line.agent ?: if (line.isDuet) "v2" else "v1"
        val agentAttr = if (agentValue.isNotEmpty()) " ttm:agent=\"$agentValue\"" else ""
                
        sb.append("""${indent}${indent}${indent}<p begin="$begin" end="$end" itunes:key="$lineNum"$agentAttr>""")
        if (formatted) sb.append("\n")

        val lineContentIndent = "${indent}${indent}${indent}"
        val lineWrapPrefix = if (line.isBG) "<span ttm:role=\"x-bg\">" else ""
        val lineWrapSuffix = if (line.isBG) "</span>" else ""

        if (line.isBG) {
            sb.append("""$lineContentIndent$lineWrapPrefix""")
            if (formatted) sb.append("\n")
        }

        val spanIndent = if (line.isBG) {
            "${indent}${indent}${indent}${indent}"
        } else {
            lineContentIndent
        }
            
        // Main lyrics - 濡傛灉鏈?words 鏁扮粍鍒欓€愯瘝杈撳嚭锛屽惁鍒欐暣琛岃緭鍑?
        if (line.words.isNotEmpty()) {
            // 璀︾ず鍚庝汉锛?p>/<span> 鍐呯┖鏍兼槸鍙姝岃瘝璇箟锛屼笉鑳藉璇嶆枃鏈仛 trim銆?
            // 杩欓噷鏈€澶氫粎娓呯悊鎹㈣鎺у埗瀛楃锛岄伩鍏嶅鍑哄悗鎶?"a b" 鍙樻垚 "ab"銆?
            line.words.forEachIndexed inner@{ wordIndex, word ->
                val wordBegin = TimestampUtils.fromMillis(word.startTime)
                val wordEnd = TimestampUtils.fromMillis(word.endTime)

                val spanText = word.word
                    .replace("\r", "")
                    .replace("\n", "")

                if (spanText.isEmpty()) {
                    // 淇濈暀绌虹櫧璇嶈妭鐐圭殑鏈€灏忓垎闅旇涔夛紝閬垮厤璇嶉棿琚畬鍏ㄧ矘杩炪€?
                    if (!formatted && wordIndex < line.words.lastIndex) {
                        sb.append(" ")
                    }
                    return@inner
                }

                sb.append("""$spanIndent<span begin="$wordBegin" end="$wordEnd">${escapeXml(spanText)}</span>""")

                if (formatted) sb.append("\n")
            }
        } else {
            // 鏁磋杈撳嚭
            sb.append("""$spanIndent<span begin="$begin" end="$end">${escapeXml(line.text)}</span>""")
            if (formatted) sb.append("\n")
        }

        if (line.isBG) {
            // BG 琛岀殑缈昏瘧涓庨煶璇戝簲浣滀负 x-bg 鐨勫瓙鑺傜偣锛岄伩鍏嶄簩娆¤В鏋愭椂琚綋浣滀富姝岃瘝缈昏瘧銆?
            line.translation?.let {
                sb.append("""$spanIndent<span ttm:role="x-translation" xml:lang="zh-CN">${escapeXml(it)}</span>""")
                if (formatted) sb.append("\n")
            }

            line.transliteration?.let {
                sb.append("""$spanIndent<span ttm:role="x-roman" xml:lang="ja-Latn">${escapeXml(it)}</span>""")
                if (formatted) sb.append("\n")
            }

            sb.append("""$lineContentIndent$lineWrapSuffix""")
            if (formatted) sb.append("\n")
        } else {
            // Translation if available
            line.translation?.let {
                sb.append("""$lineContentIndent<span ttm:role="x-translation" xml:lang="zh-CN">${escapeXml(it)}</span>""")
                if (formatted) sb.append("\n")
            }

            // Transliteration if available
            line.transliteration?.let {
                sb.append("""$lineContentIndent<span ttm:role="x-roman" xml:lang="ja-Latn">${escapeXml(it)}</span>""")
                if (formatted) sb.append("\n")
            }
        }
        
        sb.append("""${indent}${indent}</p>""")
        if (formatted) sb.append("\n")
    }

    /**
     * 灏嗘瓕璇嶈鍒楄〃杞崲涓哄畬鏁寸殑 TTML 瀵硅薄
     */
    fun fromLyricLines(
        lines: List<LyricLine>,
        title: String = "Unknown",
        artist: String = "Unknown",
        album: String? = null
    ): UnifiedLyrics {
        return UnifiedLyrics(
            metadata = LyricsMetadata(
                title = title,
                artist = artist,
                album = album,
                duration = lines.lastOrNull()?.endTime ?: 0L,
                source = "DroidMate"
            ),
            lines = lines.sortedBy { it.startTime }
        )
    }

    /**
     * 鏍煎紡鍖栨椂闂翠负 TTML 鏍煎紡
     * 鏍煎紡锛歮m:ss.msms
     * @deprecated 浣跨敤 TimestampUtils.fromMillis() 浠ｆ浛
     */
    @Deprecated("Use TimestampUtils.fromMillis()", ReplaceWith("TimestampUtils.fromMillis(millis, TimestampUtils.Format.MM_SS_MS)"))
    fun formatTime(millis: Long): String {
        val totalSeconds = millis / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        val ms = millis % 1000
            
        return String.format(Locale.ROOT, "%02d:%02d.%03d", minutes, seconds, ms)
    }

    /**
     * 灏嗘椂闂村瓧绗︿覆杞崲涓烘绉?
     * 鏀寔锛歮m:ss.mmm 鎴?mm:ss
     * @deprecated 浣跨敤 TimestampUtils.toMillis() 浠ｆ浛
     */
    @Deprecated("Use TimestampUtils.toMillis()", ReplaceWith("TimestampUtils.toMillis(timeStr)"))
    fun timeToMillis(timeStr: String): Long {
        return try {
            val parts = timeStr.split(":")
            if (parts.size != 2) return 0L
    
            val minutes = parts[0].toLongOrNull() ?: return 0L
            val secParts = parts[1].split(".")
            val seconds = secParts[0].toLongOrNull() ?: return 0L
            val millis = if (secParts.size > 1) {
                secParts[1].padEnd(3, '0').take(3).toLongOrNull() ?: 0L
            } else {
                0L
            }
    
            (minutes * 60 + seconds) * 1000 + millis
        } catch (_: Exception) {
            0L
        }
    }

    /**
     * 浠庡绉嶆牸寮忚В鏋愭瓕璇嶅埌 TTML锛堜娇鐢?Unilyric 瑙勫垯锛?
     * 鏀寔锛歀RC, Enhanced LRC, QRC, KRC, YRC
     * 
     * @param content 姝岃瘝鍐呭
     * @param title 姝屾洸鏍囬锛堝彲閫夛級
     * @param artist 鑹烘湳瀹讹紙鍙€夛級
     * @param album 涓撹緫锛堝彲閫夛級
     * @param processMetadata 鏄惁澶勭悊鍏冩暟鎹紙榛樿绂佺敤锛岄槻姝㈠己琛屽鐞嗗鑷寸炕璇?闊宠瘧閿欎綅锛?
     * @return UnifiedLyrics 瀵硅薄锛屽鏋滆В鏋愬け璐ュ垯杩斿洖 null
     */
    fun fromLyrics(
        content: String,
        title: String = "Unknown",
        artist: String = "Unknown",
        album: String? = null,
        processMetadata: Boolean = false
    ): UnifiedLyrics? {
        return try {
            UnifiedLyricsParser.parse(
                content = content,
                title = title,
                artist = artist,
                album = album,
                processMetadata = processMetadata
            )
        } catch (e: Exception) {
            Timber.e("[TTMLConverter] Error parsing lyrics using Unilyric rules $e")
            null
        }
    }
    
    /**
     * 浠?LRC 鏍煎紡杞崲鍒?TTML锛堜繚鐣欑敤浜庡悜鍚庡吋瀹癸級
     * @deprecated 浣跨敤 fromLyrics() 浠ｆ浛锛屽畠鏀寔鏇村鏍煎紡
     */
    @Deprecated(
        message = "Use fromLyrics() instead for better format support",
        replaceWith = ReplaceWith("fromLyrics(lrcContent)")
    )
    fun fromLRC(lrcContent: String): UnifiedLyrics? {
        return fromLyrics(lrcContent)
    }
}

