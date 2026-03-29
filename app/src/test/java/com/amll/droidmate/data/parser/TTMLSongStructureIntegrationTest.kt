package com.amll.droidmate.data.parser

import com.amll.droidmate.data.repository.LyricsRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

/**
 * TTML 歌曲结构解析集成测试
 * 验证从 TTML 解析到 UnifiedLyricsParser 的完整流程
 */
class TTMLSongStructureIntegrationTest {

    @Test
    fun testFullPipeline_with_div_attribute() {
        // 测试完整的解析流程，从 TTML 到 UnifiedLyricsParser
        val ttml = """<?xml version="1.0" encoding="UTF-8"?>
            <tt xmlns="http://www.w3.org/ns/ttml" xmlns:itunes="http://www.itunes.com/dtds/podcast">
                <head>
                    <metadata/>
                </head>
                <body>
                    <div begin="02:08.614" end="02:25.561" itunes:song-part="Bridge">
                        <p begin="02:08.614" end="02:15.000">Bridge lyrics part 1</p>
                        <p begin="02:15.000" end="02:25.561">Bridge lyrics part 2</p>
                    </div>
                    <div begin="00:00.000" end="00:30.000" itunes:song-part="Verse 1">
                        <p begin="00:00.000" end="00:15.000">Verse 1 part 1</p>
                        <p begin="00:15.000" end="00:30.000">Verse 1 part 2</p>
                    </div>
                </body>
            </tt>"""
        
        // 使用 LyricsRepository.parseTTML（它会调用 UnifiedLyricsParser）
        val result = LyricsRepository.parseTTML(ttml)
        
        assertNotNull("Parsed result should not be null", result)
        assertNotNull("Metadata should not be null", result?.metadata)
        assertNotNull("SongStructures should not be null", result?.metadata?.songStructures)
        assertEquals("Should have 2 structures", 2, result?.metadata?.songStructures?.size)
        
        // 验证第一个结构（按时间排序后应该是 Verse 1）
        val structures = result?.metadata?.songStructures?.sortedBy { it.startTime }
        val verse = structures!![0]
        assertEquals("Label should be Verse 1", "Verse 1", verse.label)
        assertEquals("Start time should be 0ms", 0L, verse.startTime)
        assertEquals("End time should be 30000ms", 30000L, verse.endTime)
        
        // 验证第二个结构（Bridge）
        val bridge = structures[1]
        assertEquals("Label should be Bridge", "Bridge", bridge.label)
        assertEquals("Start time should be 128614ms", 128614L, bridge.startTime)
        assertEquals("End time should be 145561ms", 145561L, bridge.endTime)
    }

    @Test
    fun testFullPipeline_with_head_metadata() {
        // 测试从 head/metadata 中的 itunes:songPart 元素解析
        val ttml = """<?xml version="1.0" encoding="UTF-8"?>
            <tt xmlns="http://www.w3.org/ns/ttml" xmlns:itunes="http://www.itunes.com/dtds/podcast">
                <head>
                    <metadata>
                        <itunes:songPart itunes:label="Intro" itunes:start-time="00:00.000" itunes:duration="00:10.000"/>
                        <itunes:songPart itunes:label="Verse" itunes:start-time="00:10.000" itunes:duration="00:30.000"/>
                        <itunes:songPart itunes:label="Chorus" itunes:start-time="00:40.000" itunes:duration="00:30.000"/>
                    </metadata>
                </head>
                <body>
                    <div>
                        <p begin="00:00.000" end="01:10.000">Lyrics here</p>
                    </div>
                </body>
            </tt>"""
        
        val result = LyricsRepository.parseTTML(ttml)
        
        assertNotNull("Parsed result should not be null", result)
        val structures = result?.metadata?.songStructures
        assertNotNull("SongStructures should not be null", structures)
        assertEquals("Should have 3 structures", 3, structures?.size)
        
        val intro = structures!![0]
        assertEquals("Label should be Intro", "Intro", intro.label)
        assertEquals("Type should be INTRO", "前奏", intro.type.displayName)
        
        val verse = structures[1]
        assertEquals("Label should be Verse", "Verse", verse.label)
        assertEquals("Type should be VERSE", "主歌", verse.type.displayName)
        
        val chorus = structures[2]
        assertEquals("Label should be Chorus", "Chorus", chorus.label)
        assertEquals("Type should be CHORUS", "副歌", chorus.type.displayName)
    }

    @Test
    fun testFullPipeline_preserves_structure_through_unified_parser() {
        // 验证 UnifiedLyricsParser 不会覆盖或丢失 TTML 元数据中的结构信息
        val ttml = """<?xml version="1.0" encoding="UTF-8"?>
            <tt xmlns="http://www.w3.org/ns/ttml" xmlns:itunes="http://www.itunes.com/dtds/podcast">
                <head>
                    <metadata>
                        <itunes:songPart itunes:label="Custom Section" itunes:start-time="00:00.000" itunes:duration="00:20.000"/>
                    </metadata>
                </head>
                <body>
                    <div>
                        <p begin="00:00.000" end="00:20.000">Test lyrics</p>
                    </div>
                </body>
            </tt>"""
        
        // 使用 UnifiedLyricsParser.parse（processMetadata=true）
        val result = UnifiedLyricsParser.parse(ttml, processMetadata = true)
        
        assertNotNull("Parsed result should not be null", result)
        val structures = result!!.metadata.songStructures
        assertNotNull("SongStructures should not be null", structures)
        assertEquals("Should have 1 structure", 1, structures!!.size)
        
        val customSection = structures!![0]
        assertEquals("Label should be Custom Section", "Custom Section", customSection.label)
        assertEquals("Start time should be 0ms", 0L, customSection.startTime)
        assertEquals("End time should be 20000ms", 20000L, customSection.endTime)
        
        // 验证没有触发 fallback 的自动推断逻辑
        // （自动推断会将未知类型标记为 VERSE，但这里应该保留原始标签）
        assertEquals("Type should be UNKNOWN (not inferred as VERSE)", "未知", customSection.type.displayName)
    }
}
