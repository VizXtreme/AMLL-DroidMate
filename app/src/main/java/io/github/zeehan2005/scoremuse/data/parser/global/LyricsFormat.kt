package io.github.zeehan2005.scoremuse.data.parser.global

/**
 * 歌词格式枚举
 *
 * 定义了系统支持的所有歌词格式类型。
 * 每种格式都有其特定的文件扩展名和显示名称。
 *
 * 格式检测逻辑：
 * 1. 首先检查特殊标记（如 XML 标签、特定元数据）
 * 2. 然后检查时间戳格式特征
 * 3. 最后降级到纯文本
 *
 * 参考：https://github.com/apoint123/Unilyric/tree/main/lyrics_helper_rs
 */
enum class LyricsFormat(val extension: String, val displayName: String) {
        /** 标准 LRC 格式 - 最常见的歌词格式，只精确到行 */
        LRC("lrc", "LRC"),

        /** 增强型 LRC 格式（支持逐字时间戳） - 在传统 LRC 基础上添加了每个字的精确时间 */
        ENHANCED_LRC("lrc", "Enhanced LRC"),

        /** 酷狗音乐 KRC 格式（逐字时间戳） - 酷狗音乐专有格式，包含丰富的元数据 */
        KRC("krc", "KRC"),

        /** 网易云音乐 YRC 格式（逐字时间戳） - 网易云音乐专有格式 */
        YRC("yrc", "YRC"),

        /** ScoreMuse 自定义 XML 格式 - 基于 XML 的歌词格式，支持丰富的元数据和结构 */
        SCOREMUSE_XML("xml", "ScoreMuse XML"),

        /** Apple Music 式 TTML 格式 - 基于 XML 的标准字幕格式，支持复杂样式 */
        TTML("ttml", "TTML"),

        /** 纯文本格式 - 没有时间戳的普通文本 */
        PLAIN_TEXT("txt", "Plain Text");

    companion object {
        /**
         * 根据文件扩展名或内容特征检测格式
         *
         * 这个方法能够智能识别各种歌词格式，即使文件扩展名不正确或缺失。
         * 它通过分析内容的特征（如特定的标签、时间戳格式等）来判断格式类型。
         *
         * 注意：此方法被标记为 @Suppress("unused") 是因为它通过反射调用
         * 在运行时的歌词格式自动检测功能中使用
         *
         * @param content 歌词文件的完整内容
         * @return 检测到的歌词格式类型
         */
        fun detect(content: String): LyricsFormat {
            /** 预处理：移除 BOM（字节顺序标记）和首尾空格
            // Some lyrics sources may include a leading BOM (U+FEFF) which breaks regex-based format detection.
            // Normalize by trimming whitespace and stripping a leading BOM so format detection works consistently.*/
            val trimmed = content.trim().trimStart('﻿')

            // ========== ScoreMuse XML 格式检测 ==========
            // ScoreMuse XML 是基于 XML 的自定义格式，以 <scoremuse 标签开头
            if (trimmed.contains("<scoremuse", ignoreCase = true)) {
                return SCOREMUSE_XML
            }

            // ========== TTML 格式检测 ==========
            // TTML 是基于 XML 的字幕格式，通常以 <?xml 或 <tt 开头
            if (trimmed.startsWith("<?xml") || trimmed.startsWith("<tt")) {
                return TTML
            }

            // ========== 网易云 YRC 格式检测 ==========
            // YRC: 元数据 JSON 行或 [start,duration] + (start,duration,0)
            // 特征：以 {"t": 开头的 JSON 行，或者同时包含 [毫秒，毫秒] 和 (毫秒，毫秒，0) 格式
            if (trimmed.lines().any { it.trim().startsWith("{\"t\":") } ||
                (Regex("""^\[\d+,\d+]""", RegexOption.MULTILINE).containsMatchIn(trimmed) &&
                    Regex("""\(\d+,\d+,0\)""").containsMatchIn(trimmed))) {
                return YRC
            }

            // ========== 酷狗 KRC 格式检测 ==========
            // KRC: 两种识别方式。
            // 1. 带 metadata: [language:], [id:], [hash:] 等，也包括常见的 [kana:] 行
            // 2. 不带 metadata 但有特征：[毫秒，毫秒]<毫秒，毫秒，0>（酷狗解密后的格式）
            if (trimmed.lines().any {
                it.startsWith("[language:") ||
                it.startsWith("[id:") ||
                it.startsWith("[hash:") ||
                it.startsWith("[kana:")
            } ||
            (Regex("""^\[\d{4,},\d+]""", RegexOption.MULTILINE).containsMatchIn(trimmed) &&
                Regex("""<\d+,\d+,0>""").containsMatchIn(trimmed))) {
                return KRC
            }

            // ========== 增强型 LRC 格式检测 ==========
            // Enhanced LRC - 逐字时间戳 <mm:ss.ms>word
            // 特征：每个字前面都有时间戳，例如 <00:12.34>你<00:12.56>好
            if (Regex("""<\d{2}:\d{2}\.\d{2,3}>""").containsMatchIn(trimmed)) {
                return ENHANCED_LRC
            }

            // ========== 标准 LRC 格式检测 ==========
            // 标准 LRC - 只有行级时间戳 [mm:ss.ms]
            if (Regex("""\[\d{1,2}:\d{1,2}(?:[.:]\d{1,3})?]""").containsMatchIn(trimmed)) {
                return LRC
            }

            // ========== 默认情况 ==========
            // 如果以上都不是，则认为是纯文本格式
            return PLAIN_TEXT
        }

    }
}