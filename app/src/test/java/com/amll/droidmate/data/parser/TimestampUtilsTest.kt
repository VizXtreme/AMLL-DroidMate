package com.amll.droidmate.data.parser

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * TimestampUtils 单元测试
 */
class TimestampUtilsTest {
    
    @Test
    fun testToMillis_mmss_ms() {
        // 标准 mm:ss.ms 格式
        assertEquals(12345L, TimestampUtils.toMillis("00:12.345"))
        assertEquals(60000L, TimestampUtils.toMillis("01:00.000"))
        assertEquals(125500L, TimestampUtils.toMillis("02:05.500"))
    }
    
    @Test
    fun testToMillis_mmss() {
        // mm:ss 格式（无毫秒）
        assertEquals(12000L, TimestampUtils.toMillis("00:12"))
        assertEquals(60000L, TimestampUtils.toMillis("01:00"))
        assertEquals(125000L, TimestampUtils.toMillis("02:05"))
    }
    
    @Test
    fun testToMillis_hhmmss_ms() {
        // hh:mm:ss.ms 格式
        assertEquals(3661123L, TimestampUtils.toMillis("01:01:01.123"))
        assertEquals(7265500L, TimestampUtils.toMillis("02:01:05.500"))
    }
    
    @Test
    fun testToMillis_pureSeconds() {
        // 纯秒数格式
        assertEquals(12345L, TimestampUtils.toMillis("12.345"))
        assertEquals(60000L, TimestampUtils.toMillis("60"))
        assertEquals(125500L, TimestampUtils.toMillis("125.5"))
    }
    
    @Test
    fun testToMillis_withSuffix() {
        // 带 's' 后缀的格式
        assertEquals(12345L, TimestampUtils.toMillis("12.345s"))
        assertEquals(60000L, TimestampUtils.toMillis("60s"))
    }
    
    @Test
    fun testToMillis_edgeCases() {
        // 边界情况
        assertEquals(0L, TimestampUtils.toMillis(""))
        assertEquals(0L, TimestampUtils.toMillis("invalid"))
        assertEquals(0L, TimestampUtils.toMillis(null as String?))
        assertEquals(500L, TimestampUtils.toMillis("0.5"))
        assertEquals(500L, TimestampUtils.toMillis(".5"))
    }
    
    @Test
    fun testToMillis_millisecondPrecision() {
        // 毫秒精度处理
        assertEquals(1000L, TimestampUtils.toMillis("00:01.1"))      // .1 -> 100ms
        assertEquals(1010L, TimestampUtils.toMillis("00:01.01"))     // .01 -> 10ms
        assertEquals(1001L, TimestampUtils.toMillis("00:01.001"))    // .001 -> 1ms
        assertEquals(1123L, TimestampUtils.toMillis("00:01.1234"))   // .1234 -> 123ms (截断)
    }
    
    @Test
    fun testFromMillis_default() {
        // 默认格式 (AUTO)
        assertEquals("00:12.345", TimestampUtils.fromMillis(12345L))
        assertEquals("01:00.000", TimestampUtils.fromMillis(60000L))
        assertEquals("02:05.500", TimestampUtils.fromMillis(125500L))
    }
    
    @Test
    fun testFromMillis_withHours() {
        // 超过 1 小时时自动使用 hh:mm:ss.ms 格式
        assertEquals("01:01:01.123", TimestampUtils.fromMillis(3661123L))
        assertEquals("02:01:05.500", TimestampUtils.fromMillis(7265500L))
    }
    
    @Test
    fun testFromMillis_explicitFormats() {
        // 明确指定格式
        assertEquals("01:01.123", TimestampUtils.fromMillis(61123L, TimestampUtils.Format.MM_SS_MS))
        assertEquals("00:01:01.123", TimestampUtils.fromMillis(61123L, TimestampUtils.Format.HH_MM_SS_MS))
        
        // AUTO 模式下，小于 1 小时不使用小时格式
        assertEquals("01:01.123", TimestampUtils.fromMillis(61123L, TimestampUtils.Format.AUTO))
    }
    
    @Test
    fun testFromMillis_negative() {
        // 负数处理
        assertEquals("00:00.000", TimestampUtils.fromMillis(-1L))
        assertEquals("00:00.000", TimestampUtils.fromMillis(-1000L))
    }
    
    @Test
    fun testRoundTrip() {
        // 往返转换测试
        val original = "02:05.500"
        val millis = TimestampUtils.toMillis(original)
        val back = TimestampUtils.fromMillis(millis)
        assertEquals(original, back)
    }
    
    @Test
    fun testRoundTripWithHours() {
        // 带小时的往返转换
        val original = "01:02:03.456"
        val millis = TimestampUtils.toMillis(original)
        val back = TimestampUtils.fromMillis(millis, TimestampUtils.Format.HH_MM_SS_MS)
        assertEquals(original, back)
    }
}
