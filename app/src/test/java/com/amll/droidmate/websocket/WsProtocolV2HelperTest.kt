package com.amll.droidmate.websocket

import org.junit.Test
import org.junit.Assert.*

/**
 * WsProtocolV2Helper 单元测试
 * 用于验证生成的 JSON 消息格式是否符合 V2 协议规范
 */
class WsProtocolV2HelperTest {
    
    @Test
    fun testCreateSetMusicUpdate() {
        val json = WsProtocolV2Helper.createSetMusicUpdate(
            musicId = "123",
            musicName = "Test Song",
            albumName = "Test Album",
            artists = listOf(WsProtocolV2Helper.Artist("1", "Test Artist")),
            duration = 180000
        )
        
        println("SetMusic JSON: $json")
        
        // 验证生成的 JSON 包含必要的字段
        assertTrue(json.contains("\"type\":\"state\""))
        assertTrue(json.contains("\"update\":\"setMusic\""))
        assertTrue(json.contains("\"musicId\":\"123\""))
        assertTrue(json.contains("\"musicName\":\"Test Song\""))
        assertTrue(json.contains("\"albumName\":\"Test Album\""))
        assertTrue(json.contains("\"artists\""))
        assertTrue(json.contains("\"duration\":180000"))
        
        // 验证不应该存在的嵌套结构
        assertFalse(json.contains("\"musicInfo\":"))
    }
    
    @Test
    fun testCreateTTMLLyricUpdate() {
        val ttmlContent = """<?xml version="1.0" encoding="UTF-8"?>
<ttml xmlns="http://www.w3.org/ns/ttml">
  <body>
    <div>
      <p begin="00:00:00.000" end="00:00:03.000">测试歌词</p>
    </div>
  </body>
</ttml>"""
        
        val json = WsProtocolV2Helper.createTTMLLyricUpdate(ttmlContent)
        
        println("Lyric JSON: $json")
        
        // 验证生成的 JSON 包含必要的字段
        assertTrue(json.contains("\"type\":\"state\""))
        assertTrue(json.contains("\"update\":\"setLyric\""))
        assertTrue(json.contains("\"format\":\"ttml\""))
        assertTrue(json.contains("\"data\":"))
        
        // 验证 TTML 内容被正确编码
        assertTrue(json.contains("<?xml"))
    }
    
    @Test
    fun testCreateAlbumArtUpdate() {
        val base64DataUrl = "data:image/jpeg;base64,/9j/4AAQSkZJRg=="
        
        val json = WsProtocolV2Helper.createAlbumArtUpdate(base64DataUrl)
        
        println("AlbumArt JSON: $json")
        
        // 验证生成的 JSON 包含必要的字段
        assertTrue(json.contains("\"type\":\"state\""))
        assertTrue(json.contains("\"update\":\"setCover\""))
        assertTrue(json.contains("\"source\":\"data\""))
        assertTrue(json.contains("\"image\":"))
        assertTrue(json.contains("\"mimeType\":\"image/jpeg\""))
        assertTrue(json.contains("\"data\":\"/9j/4AAQSkZJRg==\""))
        
        // 验证 image 是嵌套对象而不是扁平化字段
        assertTrue(json.contains("\"image\":{\"mimeType\""))
    }
    
    @Test
    fun testCreateProgressUpdate() {
        val json = WsProtocolV2Helper.createProgressUpdate(12345L)
        
        println("Progress JSON: $json")
        
        // 验证生成的 JSON 包含必要的字段
        assertTrue(json.contains("\"type\":\"state\""))
        assertTrue(json.contains("\"update\":\"progress\""))
        assertTrue(json.contains("\"progress\":12345"))
    }
    
    @Test
    fun testCreatePausedUpdate() {
        val json = WsProtocolV2Helper.createPausedUpdate()
        
        println("Paused JSON: $json")
        
        // 验证生成的 JSON 包含必要的字段
        assertTrue(json.contains("\"type\":\"state\""))
        assertTrue(json.contains("\"update\":\"paused\""))
    }
    
    @Test
    fun testCreateResumedUpdate() {
        val json = WsProtocolV2Helper.createResumedUpdate()
        
        println("Resumed JSON: $json")
        
        // 验证生成的 JSON 包含必要的字段
        assertTrue(json.contains("\"type\":\"state\""))
        assertTrue(json.contains("\"update\":\"resumed\""))
    }
}
