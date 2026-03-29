package com.amll.droidmate.data.converter

import com.amll.droidmate.domain.model.LyricLine
import com.amll.droidmate.domain.model.TTMLLyrics
import com.amll.droidmate.domain.model.TTMLMetadata
import com.amll.droidmate.domain.model.SongStructure
import com.amll.droidmate.domain.model.SongStructureType
import org.junit.Assert.*
import org.junit.Test

/**
 * 测试 TTMLConverter 是否正确保存和恢复歌曲结构
 */
class TTMLConverterSongStructureTest {
    
    @Test
    fun testToTTMLString_preservesSongStructures() {
        // 创建包含歌曲结构的 TTMLLyrics 对象
        val structures = listOf(
            SongStructure("Intro", 0L, 10000L, SongStructureType.INTRO),
            SongStructure("Verse 1", 10000L, 30000L, SongStructureType.VERSE),
            SongStructure("Chorus", 30000L, 50000L, SongStructureType.CHORUS)
        )
        
        val lyrics = TTMLLyrics(
            metadata = TTMLMetadata(
                title = "Test Song",
                artist = "Test Artist",
                songStructures = structures
            ),
            lines = listOf(
                LyricLine(0L, 5000L, "Line 1"),
                LyricLine(10000L, 15000L, "Line 2")
            )
        )
        
        // 转换为 TTML 字符串
        val ttmlString = TTMLConverter.toTTMLString(lyrics, formatted = true)
        
        // 验证输出的 TTML 包含歌曲结构信息
        assertTrue("TTML should contain song-structure meta", 
            ttmlString.contains("name=\"song-structure\""))
        assertTrue("TTML should contain Intro structure", 
            ttmlString.contains("\"label\":\"Intro\""))
        assertTrue("TTML should contain Verse 1 structure", 
            ttmlString.contains("\"label\":\"Verse 1\""))
        assertTrue("TTML should contain Chorus structure", 
            ttmlString.contains("\"label\":\"Chorus\""))
        
        println("Generated TTML:\n$ttmlString")
    }
    
    @Test
    fun testFromLyrics_withSongStructures() {
        // 创建包含歌曲结构的 TTML 字符串
        val ttmlWithStructures = """<?xml version="1.0" encoding="UTF-8"?>
            <tt xmlns="http://www.w3.org/ns/ttml" xmlns:amll="http://www.example.com/ns/amll">
                <head>
                    <metadata>
                        <amll:meta key="title" value="Test Song" />
                        <amll:meta key="artist" value="Test Artist" />
                        <amll:meta name="song-structure" content='[{"label":"Intro","start":0,"end":10000,"type":"intro"},{"label":"Verse","start":10000,"end":30000,"type":"verse"}]' />
                    </metadata>
                </head>
                <body>
                    <div>
                        <p begin="00:00.000" end="00:05.000">Test lyrics</p>
                    </div>
                </body>
            </tt>"""
        
        // 使用 UnifiedLyricsParser 解析
        val result = com.amll.droidmate.data.parser.UnifiedLyricsParser.parse(ttmlWithStructures, processMetadata = true)
        
        assertNotNull("Parsed result should not be null", result)
        
        val structures = result!!.metadata.songStructures
        assertNotNull("SongStructures should not be null", structures)
        assertEquals("Should have 2 structures", 2, structures!!.size)
        
        // 验证第一个结构
        val intro = structures[0]
        assertEquals("Label should be Intro", "Intro", intro.label)
        assertEquals("Start time should be 0ms", 0L, intro.startTime)
        assertEquals("End time should be 10000ms", 10000L, intro.endTime)
        assertEquals("Type should be INTRO", SongStructureType.INTRO, intro.type)
        
        // 验证第二个结构
        val verse = structures[1]
        assertEquals("Label should be Verse", "Verse", verse.label)
        assertEquals("Start time should be 10000ms", 10000L, verse.startTime)
        assertEquals("End time should be 30000ms", 30000L, verse.endTime)
        assertEquals("Type should be VERSE", SongStructureType.VERSE, verse.type)
    }
}
