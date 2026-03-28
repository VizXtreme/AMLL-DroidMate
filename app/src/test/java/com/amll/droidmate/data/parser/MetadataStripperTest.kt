package com.amll.droidmate.data.parser

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * MetadataStripper 单元测试
 */
class MetadataStripperTest {
    
    @Test
    fun testIsMetadataLine_lrcFormat() {
        // LRC 标准元数据
        assertTrue(MetadataStripper.isMetadataLine("[ti:Song Title]"))
        assertTrue(MetadataStripper.isMetadataLine("[ar:Artist]"))
        assertTrue(MetadataStripper.isMetadataLine("[al:Album]"))
        assertTrue(MetadataStripper.isMetadataLine("[by:Lyricist]"))
        assertTrue(MetadataStripper.isMetadataLine("[offset:100]"))
        assertTrue(MetadataStripper.isMetadataLine("[length:03:45]"))
        
        // 大小写不敏感
        assertTrue(MetadataStripper.isMetadataLine("[TI:Song]"))
        assertTrue(MetadataStripper.isMetadataLine("[AR:Artist]"))
    }
    
    @Test
    fun testIsMetadataLine_keywords() {
        // 制作人员信息
        assertTrue(MetadataStripper.isMetadataLine("作词：张三"))
        assertTrue(MetadataStripper.isMetadataLine("作曲：李四"))
        assertTrue(MetadataStripper.isMetadataLine("编曲：王五"))
        assertTrue(MetadataStripper.isMetadataLine("演唱：歌手"))
        assertTrue(MetadataStripper.isMetadataLine("歌手：姓名"))
        assertTrue(MetadataStripper.isMetadataLine("专辑：专辑名"))
        
        // 英文关键词
        assertTrue(MetadataStripper.isMetadataLine("Lyrics by Someone"))
        assertTrue(MetadataStripper.isMetadataLine("Composer: John Doe"))
        assertTrue(MetadataStripper.isMetadataLine("Producer: Jane Smith"))
    }
    
    @Test
    fun testIsMetadataLine_copyright() {
        // 版权声明
        assertTrue(MetadataStripper.isMetadataLine("版权所有 侵权必究"))
        assertTrue(MetadataStripper.isMetadataLine("未经许可不得使用"))
        assertTrue(MetadataStripper.isMetadataLine("【未经授权 禁止使用】"))
    }
    
    @Test
    fun testIsMetadataLine_companyNames() {
        // 公司名称
        assertTrue(MetadataStripper.isMetadataLine("ABC Music Limited"))
        assertTrue(MetadataStripper.isMetadataLine("XYZ Productions Inc."))
        assertTrue(MetadataStripper.isMetadataLine("Music Group LLC"))
    }
    
    @Test
    fun testIsMetadataLine_notMetadata() {
        // 普通歌词行不是元数据
        assertFalse(MetadataStripper.isMetadataLine("这是歌词内容"))
        assertFalse(MetadataStripper.isMetadataLine("This is a lyric line"))
        assertFalse(MetadataStripper.isMetadataLine(""))
        assertFalse(MetadataStripper.isMetadataLine("   "))
    }
    
    @Test
    fun testIsMetadataLine_edgeCases() {
        // 边界情况
        assertTrue(MetadataStripper.isMetadataLine("  [ti:Song]  ")) // 带空格
        assertTrue(MetadataStripper.isMetadataLine("[ti:Song]:")) // 多余冒号
        assertFalse(MetadataStripper.isMetadataLine("tishi: not metadata")) // 相似但不是
    }
}
