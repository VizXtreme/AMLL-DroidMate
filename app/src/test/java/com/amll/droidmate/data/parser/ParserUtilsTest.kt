package com.amll.droidmate.data.parser

import com.amll.droidmate.domain.model.LyricLine
import com.amll.droidmate.domain.model.LyricWord
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * ParserUtils 单元测试
 */
class ParserUtilsTest {
    
    @Test
    fun testNormalizeTextWhitespace() {
        // 标准清理
        assertEquals("hello world", normalizeTextWhitespace("  hello   world  "))
        assertEquals("test", normalizeTextWhitespace("\t\ntest\n\t"))
        
        // QQ 音乐翻译字段（双斜杠）
        assertEquals("translation", normalizeTextWhitespace("//   translation"))
        assertEquals("text", normalizeTextWhitespace("////text"))
        
        // 空字符串
        assertEquals("", normalizeTextWhitespace(""))
        assertEquals("", normalizeTextWhitespace("   "))
    }
    
    @Test
    fun testProcessSyllableText() {
        val words = mutableListOf<LyricWord>()
        
        // 普通文本
        val result1 = processSyllableText("hello", words)
        assertEquals("hello" to false, result1)
        
        // 带前导空格
        words.add(LyricWord("word", 0, 100))
        val result2 = processSyllableText(" next", words)
        assertEquals("next" to false, result2)
        // 验证最后一个单词后面加了空格
        assertTrue(words.last().word.endsWith(" "))
        
        // 带尾随空格
        val result3 = processSyllableText("text ", words)
        assertEquals("text" to true, result3)
        
        // 空文本
        assertNull(processSyllableText("", words))
        assertNull(processSyllableText("   ", words))
    }
    
    @Test
    fun testCleanLyricText() {
        // 标准模式（trim 并压缩空格）
        assertEquals("hello world", cleanLyricText("  hello   world  "))
        
        // 保留空格模式
        assertEquals("hello   world", cleanLyricText("hello   world", preserveSpaces = true))
        assertEquals("hello\nworld", cleanLyricText("hello\nworld", preserveSpaces = true))
        
        // 移除双斜杠
        assertEquals("text", cleanLyricText("//text", removeLeadingSlashes = true))
        assertEquals("//text", cleanLyricText("//text", removeLeadingSlashes = false))
    }
    
    @Test
    fun testCleanBackgroundText() {
        // 括号包裹的文本
        assertEquals("background", cleanBackgroundText("(background)"))
        assertEquals("bg text", cleanBackgroundText("(bg text)"))
        
        // 非括号包裹的文本
        assertEquals("normal text", cleanBackgroundText("normal text"))
        assertEquals("", cleanBackgroundText(""))
    }
    
    @Test
    fun testFindNextLineStartTime() {
        val lines = listOf(
            "[00:12.50] First line",
            "Invalid line",
            "",
            "[00:15.00] Second line",
            "[00:17.500] Third line"
        )
        
        val regex = Regex("""\[(\d{1,2}):(\d{1,2})(?:[.:](\d{1,3}))?]""")
        
        // 找到下一行时间
        assertEquals(15000L, findNextLineStartTime(lines, 0, regex))
        assertEquals(17500L, findNextLineStartTime(lines, 3, regex))
        
        // 找不到返回 null
        assertNull(findNextLineStartTime(lines, 4, regex))
    }
    
    @Test
    fun testMergeLyricLines() {
        val mainLines = listOf(
            LyricLine(startTime = 1000, endTime = 2000, text = "Line 1", words = emptyList()),
            LyricLine(startTime = 3000, endTime = 4000, text = "Line 2", words = emptyList()),
            LyricLine(startTime = 5000, endTime = 6000, text = "Line 3", words = emptyList())
        )
        
        val translationLines = listOf(
            LyricLine(startTime = 1000, endTime = 2000, text = "翻译 1", words = emptyList()),
            LyricLine(startTime = 3000, endTime = 4000, text = "翻译 2", words = emptyList())
        )
        
        val merged = mergeLyricLines(mainLines, translationLines, null)
        
        assertEquals(3, merged.size)
        assertEquals("翻译 1", merged[0].translation)
        assertEquals("翻译 2", merged[1].translation)
        assertNull(merged[2].translation) // 没有对应的翻译
    }
}
