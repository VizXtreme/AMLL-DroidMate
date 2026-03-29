package com.amll.droidmate.data.parser

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class TTMLSongStructureParserTest {

    @Test
    fun parseSongStructures_from_div_attribute_with_kebab_case() {
        // 测试在 div 标签上使用 itunes:song-part 属性（连字符格式）
        val ttml = """<?xml version="1.0" encoding="UTF-8"?>
            <tt xmlns="http://www.w3.org/ns/ttml" xmlns:itunes="http://www.itunes.com/dtds/podcast">
                <head>
                    <metadata/>
                </head>
                <body>
                    <div begin="02:08.614" end="02:25.561" itunes:song-part="Bridge">
                        <p begin="02:08.614" end="02:15.000">Test lyrics 1</p>
                        <p begin="02:15.000" end="02:25.561">Test lyrics 2</p>
                    </div>
                </body>
            </tt>"""
        
        val result = TTMLParser.parse(ttml)
        
        assertNotNull("Metadata should not be null", result.metadata)
        assertNotNull("SongStructures should not be null", result.metadata.songStructures)
        assertEquals("Should have 1 structure", 1, result.metadata.songStructures?.size)
        
        val structure = result.metadata.songStructures!!.first()
        assertEquals("Label should be Bridge", "Bridge", structure.label)
        assertEquals("Start time should be 128614ms", 128614L, structure.startTime)
        assertEquals("End time should be 145561ms", 145561L, structure.endTime)
    }

    @Test
    fun parseSongStructures_from_div_attribute_with_camel_case() {
        // 测试在 div 标签上使用 itunes:songPart 属性（驼峰格式）
        val ttml = """<?xml version="1.0" encoding="UTF-8"?>
            <tt xmlns="http://www.w3.org/ns/ttml" xmlns:itunes="http://www.itunes.com/dtds/podcast">
                <head>
                    <metadata/>
                </head>
                <body>
                    <div begin="00:30.000" end="01:00.000" itunes:songPart="Chorus">
                        <p begin="00:30.000" end="00:45.000">Chorus part 1</p>
                        <p begin="00:45.000" end="01:00.000">Chorus part 2</p>
                    </div>
                </body>
            </tt>"""
        
        val result = TTMLParser.parse(ttml)
        
        assertNotNull("Metadata should not be null", result.metadata)
        assertNotNull("SongStructures should not be null", result.metadata.songStructures)
        assertEquals("Should have 1 structure", 1, result.metadata.songStructures?.size)
        
        val structure = result.metadata.songStructures!!.first()
        assertEquals("Label should be Chorus", "Chorus", structure.label)
        assertEquals("Start time should be 30000ms", 30000L, structure.startTime)
        assertEquals("End time should be 60000ms", 60000L, structure.endTime)
    }

    @Test
    fun parseSongStructures_from_p_attribute() {
        // 测试在 p 标签上使用 itunes:song-part 属性
        val ttml = """<?xml version="1.0" encoding="UTF-8"?>
            <tt xmlns="http://www.w3.org/ns/ttml" xmlns:itunes="http://www.itunes.com/dtds/podcast">
                <head>
                    <metadata/>
                </head>
                <body>
                    <div>
                        <p begin="00:00.000" end="00:30.000" itunes:song-part="Verse 1">Verse lyrics</p>
                        <p begin="00:30.000" end="01:00.000" itunes:song-part="Chorus">Chorus lyrics</p>
                    </div>
                </body>
            </tt>"""
        
        val result = TTMLParser.parse(ttml)
        
        assertNotNull("Metadata should not be null", result.metadata)
        assertNotNull("SongStructures should not be null", result.metadata.songStructures)
        assertEquals("Should have 2 structures", 2, result.metadata.songStructures?.size)
        
        val verseStructure = result.metadata.songStructures!![0]
        assertEquals("Label should be Verse 1", "Verse 1", verseStructure.label)
        assertEquals("Start time should be 0ms", 0L, verseStructure.startTime)
        assertEquals("End time should be 30000ms", 30000L, verseStructure.endTime)
        
        val chorusStructure = result.metadata.songStructures!![1]
        assertEquals("Label should be Chorus", "Chorus", chorusStructure.label)
        assertEquals("Start time should be 30000ms", 30000L, chorusStructure.startTime)
        assertEquals("End time should be 60000ms", 60000L, chorusStructure.endTime)
    }

    @Test
    fun parseSongStructures_from_head_metadata_element() {
        // 测试从 head/metadata 中的 itunes:songPart 元素解析
        val ttml = """<?xml version="1.0" encoding="UTF-8"?>
            <tt xmlns="http://www.w3.org/ns/ttml" xmlns:itunes="http://www.itunes.com/dtds/podcast">
                <head>
                    <metadata>
                        <itunes:songPart itunes:label="Intro" itunes:start-time="00:00.000" itunes:duration="00:15.000"/>
                        <itunes:songPart itunes:label="Verse 1" itunes:start-time="00:15.000" itunes:duration="00:30.000"/>
                        <itunes:songPart itunes:label="Chorus" itunes:start-time="00:45.000" itunes:duration="00:30.000"/>
                    </metadata>
                </head>
                <body>
                    <div>
                        <p begin="00:00.000" end="01:15.000">Lyrics</p>
                    </div>
                </body>
            </tt>"""
        
        val result = TTMLParser.parse(ttml)
        
        assertNotNull("Metadata should not be null", result.metadata)
        assertNotNull("SongStructures should not be null", result.metadata.songStructures)
        assertEquals("Should have 3 structures", 3, result.metadata.songStructures?.size)
        
        val intro = result.metadata.songStructures!![0]
        assertEquals("Label should be Intro", "Intro", intro.label)
        assertEquals("Start time should be 0ms", 0L, intro.startTime)
        assertEquals("End time should be 15000ms", 15000L, intro.endTime)
        
        val verse = result.metadata.songStructures!![1]
        assertEquals("Label should be Verse 1", "Verse 1", verse.label)
        assertEquals("Start time should be 15000ms", 15000L, verse.startTime)
        assertEquals("End time should be 45000ms", 45000L, verse.endTime)
        
        val chorus = result.metadata.songStructures!![2]
        assertEquals("Label should be Chorus", "Chorus", chorus.label)
        assertEquals("Start time should be 45000ms", 45000L, chorus.startTime)
        assertEquals("End time should be 75000ms", 75000L, chorus.endTime)
    }

    @Test
    fun parseSongStructures_mixed_formats() {
        // 测试同时包含 head 元素和 body 属性的情况（优先使用 head 中的定义）
        val ttml = """<?xml version="1.0" encoding="UTF-8"?>
            <tt xmlns="http://www.w3.org/ns/ttml" xmlns:itunes="http://www.itunes.com/dtds/podcast">
                <head>
                    <metadata>
                        <itunes:songPart itunes:label="Intro" itunes:start-time="00:00.000" itunes:duration="00:10.000"/>
                    </metadata>
                </head>
                <body>
                    <div begin="00:10.000" end="00:40.000" itunes:song-part="Verse">
                        <p begin="00:10.000" end="00:40.000">Verse lyrics</p>
                    </div>
                </body>
            </tt>"""
        
        val result = TTMLParser.parse(ttml)
        
        assertNotNull("Metadata should not be null", result.metadata)
        assertNotNull("SongStructures should not be null", result.metadata.songStructures)
        // 应该同时包含 head 和 body 中定义的结构
        assertEquals("Should have 2 structures", 2, result.metadata.songStructures?.size)
    }

    @Test
    fun parseSongStructures_type_mapping() {
        // 测试不同类型标签的映射
        val testCases = listOf(
            "Verse" to "主歌",
            "Chorus" to "副歌",
            "Bridge" to "桥段",
            "Pre-Chorus" to "预副歌",
            "Intro" to "前奏",
            "Interlude" to "间奏",
            "Outro" to "尾奏"
        )
        
        for ((label, expectedType) in testCases) {
            val ttml = """<?xml version="1.0" encoding="UTF-8"?>
                <tt xmlns="http://www.w3.org/ns/ttml" xmlns:itunes="http://www.itunes.com/dtds/podcast">
                    <head>
                        <metadata/>
                    </head>
                    <body>
                        <div begin="00:00.000" end="00:30.000" itunes:song-part="$label">
                            <p begin="00:00.000" end="00:30.000">Lyrics</p>
                        </div>
                    </body>
                </tt>"""
            
            val result = TTMLParser.parse(ttml)
            val structure = result.metadata.songStructures!!.first()
            
            assertEquals("Type for $label should be $expectedType", expectedType, structure.type.displayName)
        }
    }
}
