import React, { useEffect, useRef } from 'react'
import { createRoot } from 'react-dom/client'
import { LyricPlayer, BackgroundRender } from '@applemusic-like-lyrics/react'
import type { LyricPlayerRef } from '@applemusic-like-lyrics/react'
import '@applemusic-like-lyrics/react-full/style.css'
import '@applemusic-like-lyrics/core/style.css'
import { useAtom, useSetAtom } from 'jotai'
import {
  musicLyricLinesAtom,
  musicPlayingPositionAtom,
  musicCoverAtom,
  musicPlayingAtom,
  lowFreqVolumeAtom,
} from '@applemusic-like-lyrics/react-full'

// Minimal Android-specific adaptations
const PLAYER_BACKGROUND = 'transparent'
const demoAlbumArt = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNDAwIiBoZWlnaHQ9IjQwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB3aWR0aD0iMTAwJSIgaGVpZ2h0PSIxMDAlIiBmaWxsPSJyZ2JhKDAsMCwwLDAuMSkiLz48L3N2Zz4='

interface WordEntry {
  word: string
  startTime: number
  endTime: number
}

interface LyricLine {
  words: WordEntry[]
  translatedLyric: string
  romanLyric: string
  startTime: number
  endTime: number
  isBG: boolean
  isDuet: boolean
}

interface LyricsPayload {
  lines?: Array<{
    words?: Array<{ word?: string; startTime?: number; endTime?: number }>
    text?: string
    translatedLyric?: string
    romanLyric?: string
    startTime?: number
    endTime?: number
    isBG?: boolean
    isDuet?: boolean
  }>
}

let player: LyricPlayerRef | null = null
let backgroundRender: any = null
let lastAlbumArt = ''
let albumArtRetryCount = 0
const MAX_ALBUM_ART_RETRIES = 3

// Global state for Android bridge
interface AMLLGlobal {
  player: any
  backgroundRender: any
  state: any
}

// 🔧 下游覆盖：修正 generateFadeGradient width 验证问题
// 修正 AMLL 加载后立即应用补丁
function applyAMLLPatch() {
  logToAndroid('Applying AMLL patch for generateFadeGradient', 'info')
  
  // 方法 1：通过 CSS 变量设置安全值
  const style = document.createElement('style')
  style.textContent = `
    /* 确保 mask-image 相关 CSS 变量始终有安全默认值 */
    :root {
      --bright-mask-alpha: 1.0;
      --dark-mask-alpha: 0.2;
    }
  `
  document.head.appendChild(style)
  
  logToAndroid('AMLL patch applied successfully', 'debug')
}

declare global {
  interface Window {
    __amll?: AMLLGlobal
    updateLyrics?: (payload: LyricsPayload) => void
    updateTime?: (timeMs: number) => void
    updateAlbumArt?: (uri: string) => Promise<void>
    setPaused?: (paused: boolean) => void
    configureLyricMotion?: (options: any) => void
    configureBackgroundEffect?: (options: any) => void
    setRenderMode?: (mode: string) => void
    Android?: {
      log?: (message: string, level: string) => void
      isPlaying?: () => boolean
      onLineClick?: (index: number, startTime: number) => void
    }
  }
}

function logToAndroid(message: string, level: string = 'debug') {
  if (window.Android?.log) {
    try {
      window.Android.log(message, level)
    } catch (e) {
      console.log(`[ANDROID] ${message}`)
    }
  } else {
    console.log(`[${level.toUpperCase()}] ${message}`)
  }
}

function normalizeLyricLines(lines: any[]): LyricLine[] {
  if (!Array.isArray(lines)) return []
  
  return lines.map((line) => {
    const words = line.words?.map((w: any) => ({
      word: String(w.word ?? ''),
      startTime: Number(w.startTime ?? line.startTime ?? 0),
      endTime: Number(w.endTime ?? line.endTime ?? line.startTime ?? 0),
    })) || []
    
    if (words.length === 0 && line.text) {
      words.push({
        word: line.text,
        startTime: Number(line.startTime ?? 0),
        endTime: Number(line.endTime ?? line.startTime ?? 0),
      })
    }
    
    return {
      words,
      translatedLyric: String(line.translatedLyric ?? ''),
      romanLyric: String(line.romanLyric ?? ''),
      startTime: Number(line.startTime ?? 0),
      endTime: Number(line.endTime ?? 0),
      isBG: !!line.isBG,
      isDuet: !!line.isDuet,
    }
  })
}

function App() {
  const playerRef = useRef<LyricPlayerRef>(null)
  const [lyricLines, setLyricLines] = useAtom(musicLyricLinesAtom)
  const [currentTime, setCurrentTime] = useAtom(musicPlayingPositionAtom)
  const [albumUri, setAlbumUri] = useAtom(musicCoverAtom)
  const [musicIsPlaying, setIsPlaying] = useAtom(musicPlayingAtom)
  const setLowFreqVolume = useSetAtom(lowFreqVolumeAtom)

  // Initialize global state and Android bridge
  useEffect(() => {
    // Connect global setters to Jotai atoms
    if (globalSetLyricLines) {
      globalSetLyricLines = setLyricLines
    }
    if (globalSetCurrentTime) {
      globalSetCurrentTime = setCurrentTime
    }
    if (globalSetAlbumUri) {
      globalSetAlbumUri = setAlbumUri
    }
    
    // Mount global references
    if (window.__amll) {
      window.__amll.player = playerRef.current
      window.__amll.backgroundRender = backgroundRender
    }

    // 🔧 修复：添加 WebView 布局信息调试日志
    logToAndroid(`WebView size: ${window.innerWidth}x${window.innerHeight}, DPR: ${window.devicePixelRatio || 1}`, 'info')
    logToAndroid(`User Agent: ${navigator.userAgent}`, 'debug')
    
    // 检查 CSS 是否正确加载
    const hasStyleElement = !!document.querySelector('style') || !!document.querySelector('link[rel="stylesheet"]')
    logToAndroid(`CSS loaded: ${hasStyleElement}`, 'debug')
    
    // 检查 HTML 结构
    const hasAppElement = !!document.getElementById('app')
    logToAndroid(`App element exists: ${hasAppElement}`, 'debug')

    // 关键修复：在替换全局 setter 之前，先检查是否有延迟的数据需要处理
    const pendingLyrics = globalSetLyricLines
    const pendingTime = globalSetCurrentTime
    const pendingAlbum = globalSetAlbumUri
    
    // Expose global API for Android - 直接绑定到 Jotai atom setter
    ;(window as any).__setLyricLines = setLyricLines
    ;(window as any).__setCurrentTime = setCurrentTime
    ;(window as any).__setAlbumUri = setAlbumUri

    // 如果有延迟的数据，在替换 setter 后立即应用
    if (pendingLyrics && Array.isArray(pendingLyrics) && pendingLyrics.length > 0) {
      logToAndroid(`Applying ${pendingLyrics.length} pending lyric lines`, 'info')
      setLyricLines(pendingLyrics)
    }
    if (pendingTime !== null && typeof pendingTime === 'number') {
      logToAndroid(`Applying pending time: ${pendingTime}ms`, 'debug')
      setCurrentTime(pendingTime)
    }
    if (pendingAlbum && typeof pendingAlbum === 'string') {
      logToAndroid(`Applying pending album art`, 'debug')
      setAlbumUri(pendingAlbum)
    }

    // Global API functions
    window.updateLyrics = function (payload: LyricsPayload) {
      try {
        const rawLines = Array.isArray(payload?.lines) ? payload.lines : []
        const normalizedLines = normalizeLyricLines(rawLines)
        
        logToAndroid(`updateLyrics called with ${rawLines.length} raw lines, ${normalizedLines.length} normalized`, 'debug')
        
        if (normalizedLines.length === 0) {
          // Inject placeholder if no lyrics provided
          setLyricLines([
            { 
              words: [{word:'Demo',startTime:0,endTime:2000}],
              translatedLyric:'',
              romanLyric:'',
              startTime:0,
              endTime:2000,
              isBG:false,
              isDuet:false 
            }
          ])
        } else {
          // 调试：打印前几行歌词的详细信息
          normalizedLines.slice(0, 3).forEach((line, idx) => {
            logToAndroid(`Line ${idx}: text="${line.words.map(w => w.word).join('')}", words=${line.words.length}, startTime=${line.startTime}, endTime=${line.endTime}`, 'debug')
            line.words.slice(0, 2).forEach((word, wIdx) => {
              logToAndroid(`  Word ${wIdx}: "${word.word}" ${word.startTime}-${word.endTime}ms`, 'debug')
            })
          })
          
          setLyricLines(normalizedLines)
        }
        
        logToAndroid(`Updated lyrics (${normalizedLines.length} lines)`, 'debug')
        
        // 关键修复：在设置歌词后，如果当前已经有时间值，强制 LyricPlayer 立即更新进度
        // 这是因为 Android updateTime 可能 updateLyrics 之前通过 evaluateJavascript 异步发送
        // 导致 initialLayoutFinished 检查失败而被跳过
        if (playerRef.current?.lyricPlayer && currentTime > 0) {
          logToAndroid(`Force update LyricPlayer time to ${currentTime} after setting lyrics`, 'info')
          playerRef.current.lyricPlayer.setCurrentTime(Math.trunc(currentTime), false)
              
          // 额外修复：触发一次 mask-image 更新，确保 CSS 变量正确初始化
          // 这是因为 AMLL mask-image 生成依赖于布局测量，可能在初始渲染时未完成
          setTimeout(() => {
            if (playerRef.current?.lyricPlayer) {
              logToAndroid('Triggering mask-image recalculation', 'debug')
              playerRef.current.lyricPlayer.setCurrentTime(Math.trunc(currentTime), true)
            }
          }, 100)
        }
      } catch (error) {
        logToAndroid(`updateLyrics error: ${(error as Error).message}`, 'error')
      }
    }

    window.updateTime = function (timeMs: number) {
      const parsedTime = Number(timeMs)
      // playerRef.current.lyricPlayer 才是真正的歌词播放实例
      if (playerRef.current?.lyricPlayer) {
        playerRef.current.lyricPlayer.setCurrentTime(Math.trunc(parsedTime), false)
      }
    }

    window.updateAlbumArt = async function (uri: string) {
      try {
        // 验证 URI 有效性
        const isValidUri = uri && typeof uri === 'string' && uri.trim().length > 0
        
        if (!isValidUri) {
          logToAndroid('updateAlbumArt: received empty/invalid URI, using placeholder only', 'warn')
          // 仅更新 Jotai state 使用占位图，不污染 BackgroundRender
          setAlbumUri(demoAlbumArt)
          lastAlbumArt = ''
          return
        }
        
        // 检查是否为有效的 data URL 或 http(s) URL
        const isDataUrl = uri.startsWith('data:')
        const isHttpUrl = uri.startsWith('http://') || uri.startsWith('https://')
        const isFileUrl = uri.startsWith('file:')
        
        if (!isDataUrl && !isHttpUrl && !isFileUrl) {
          logToAndroid(`updateAlbumArt: invalid URI format: ${uri.substring(0, 50)}...`, 'error')
          setAlbumUri(demoAlbumArt)
          lastAlbumArt = ''
          return
        }
        
        // 对于 file:// URL，尝试加载并转换为 data URL
        let finalUri = uri
        if (isFileUrl) {
          try {
            logToAndroid('updateAlbumArt: attempting to load file:// URI', 'debug')
            const response = await fetch(uri)
            if (!response.ok) {
              throw new Error(`HTTP ${response.status}`)
            }
            const blob = await response.blob()
            const reader = new FileReader()
            finalUri = await new Promise<string>((resolve, reject) => {
              reader.onload = () => resolve(reader.result as string)
              reader.onerror = () => reject(reader.error)
              reader.readAsDataURL(blob)
            })
            logToAndroid('updateAlbumArt: successfully loaded file:// URI', 'debug')
          } catch (error) {
            logToAndroid(`updateAlbumArt: failed to load file:// URI: ${(error as Error).message}`, 'error')
            // 如果文件加载失败，使用占位图但不更新 BackgroundRender
            setAlbumUri(demoAlbumArt)
            lastAlbumArt = ''
            return
          }
        }
        
        // ✅ 关键改进：检查专辑图是否真的变化了
        const hasAlbumArtChanged = lastAlbumArt !== uri
        
        if (!hasAlbumArtChanged) {
          logToAndroid('updateAlbumArt: album art unchanged, skipping update', 'debug')
          return
        }
        
        // 更新 Jotai state
        setAlbumUri(finalUri || demoAlbumArt)
        lastAlbumArt = uri
        logToAndroid(`Album art CHANGED and updated: ${uri ? 'present' : 'empty'}`, 'info')
        
        // 重置重试计数器
        albumArtRetryCount = 0
        
        // 直接调用 BackgroundRender 的 setAlbum 方法
        if (window.__amll?.backgroundRender) {
          const bgRender = window.__amll.backgroundRender
          if (bgRender.setAlbum) {
            try {
              await bgRender.setAlbum(finalUri || '')
              logToAndroid('BackgroundRender album updated successfully', 'debug')
            } catch (error) {
              logToAndroid(`BackgroundRender.setAlbum error: ${(error as Error).message}`, 'error')
              // 如果设置失败，增加重试计数
              albumArtRetryCount++
              if (albumArtRetryCount < MAX_ALBUM_ART_RETRIES) {
                logToAndroid(`Will retry album art update (${albumArtRetryCount}/${MAX_ALBUM_ART_RETRIES})`, 'warn')
                setTimeout(() => {
                  window.updateAlbumArt?.(uri)
                }, 500 * albumArtRetryCount)
              }
            }
          }
        }
      } catch (error) {
        logToAndroid(`updateAlbumArt error: ${(error as Error).message}`, 'error')
      }
    }

    window.setPaused = function (paused: boolean) {
      setIsPlaying(!paused)
      logToAndroid(`Playback ${paused ? 'paused' : 'resumed'}`, 'debug')
    }

    window.configureLyricMotion = function (options: any) {
      logToAndroid(`configureLyricMotion: ${JSON.stringify(options)}`, 'debug')
      // AMLL Core handles motion configuration internally
    }

    window.configureBackgroundEffect = function (options: any) {
      logToAndroid(`configureBackgroundEffect: ${JSON.stringify(options)}`, 'debug')
      if (backgroundRender && options.flowSpeed !== undefined) {
        backgroundRender.setFlowSpeed?.(options.flowSpeed)
      }
      if (backgroundRender && options.renderScale !== undefined) {
        backgroundRender.setRenderScale?.(options.renderScale)
      }
      if (backgroundRender && options.lowFreqVolume !== undefined) {
        setLowFreqVolume(options.lowFreqVolume)
      }
    }

    window.setRenderMode = function (mode: string) {
      logToAndroid(`setRenderMode: ${mode}`, 'debug')
      // Render mode is handled by AMLL Core
    }

    return () => {
      delete (window as any).__setLyricLines
      delete (window as any).__setCurrentTime
      delete (window as any).__setAlbumUri
    }
  }, [setLyricLines, setCurrentTime, setAlbumUri, setIsPlaying, setLowFreqVolume])

  // Sync playing state with Android
  useEffect(() => {
    // Sync playing state with Android
    if (window.Android?.isPlaying) {
      try {
        const isPlaying = window.Android.isPlaying()
        if (isPlaying !== musicIsPlaying) {
          setIsPlaying(isPlaying)
        }
      } catch (_err) {
        // Ignore
      }
    }
    
    // 🔧 修复：定期输出布局状态用于调试
    const debugInterval = setInterval(() => {
      if (playerRef.current?.lyricPlayer) {
        const currentTime = playerRef.current.lyricPlayer.getCurrentTime()
        const scrollToIndex = (playerRef.current.lyricPlayer as any).scrollToIndex
        logToAndroid(`[DEBUG] LyricPlayer time: ${currentTime}ms, scrollToIndex: ${scrollToIndex}`, 'debug')
      }
    }, 5000) // 每 5 秒输出一次
    
    return () => clearInterval(debugInterval)
  }, [musicIsPlaying, setIsPlaying])

  const handleLineClick = (event: any) => {
    try {
      const lineData = event.line.getLine()
      const startTime = Math.trunc(Number(lineData?.startTime ?? 0))
      const lineIndex = -1
      
      if (window.Android?.onLineClick) {
        window.Android.onLineClick(lineIndex, startTime)
        logToAndroid(`Called Android.onLineClick(${lineIndex}, ${startTime})`, 'info')
      }
    } catch (error) {
      logToAndroid(`line-click handler error: ${(error as Error).message}`, 'error')
    }
  }

  return (
    <div id="app" style={{ position: 'relative', width: '100%', height: '100vh' }}>
      <BackgroundRender
        ref={(ref) => {
          if (ref?.bgRender) {
            backgroundRender = ref.bgRender
            logToAndroid('BackgroundRender instance attached', 'debug')
          }
        }}
        album={albumUri || demoAlbumArt}
        style={{ position: 'absolute', inset: 0, zIndex: 0 }}
      />

      <LyricPlayer
        ref={playerRef}
        lyricLines={lyricLines}
        currentTime={currentTime}
        playing={musicIsPlaying}
        disabled={false}
        enableSpring={true}
        enableBlur={true}
        enableScale={true}
        wordFadeWidth={0.5}
        alignAnchor="center"
        alignPosition={0.35}
        linePosYSpringParams={{ mass: 0.9, damping: 15, stiffness: 90 }}
        lineScaleSpringParams={{ mass: 2, damping: 25, stiffness: 100 }}
        onLyricLineClick={handleLineClick}
        style={{
          position: 'absolute',
          inset: 0,
          zIndex: 1,
          width: '100%',
          height: '100%',
          background: PLAYER_BACKGROUND,
        }}
      />
    </div>
  )
}

// Initialize app
let globalSetLyricLines: any = null
let globalSetCurrentTime: any = null
let globalSetAlbumUri: any = null

if (typeof window !== 'undefined') {
  // 立即挂载全局 API，确保 Android 能随时调用
  ;(window as any).__setLyricLines = (lines: any[]) => {
    globalSetLyricLines = lines
  }
  ;(window as any).__setCurrentTime = (time: number) => {
    globalSetCurrentTime = time
  }
  ;(window as any).__setAlbumUri = (uri: string) => {
    globalSetAlbumUri = uri
  }
  
  window.addEventListener('DOMContentLoaded', () => {
    try {
      document.documentElement.style.background = 'transparent'
      document.body.style.background = 'transparent'
      
      // 🔧 应用 AMLL 核心补丁（mask-image 等必要修复）
      applyAMLLPatch()
      
      const root = document.getElementById('app') || document.createElement('div')
      if (!document.getElementById('app')) {
        root.id = 'app'
        document.body?.appendChild(root)
      }
      
      if (root) {
        createRoot(root).render(<App />)
      }
      
      logToAndroid('AMLL WebView initialized', 'info')
    } catch (error) {
      logToAndroid(`Initialization error: ${(error as Error).message}`, 'error')
    }
  })
}

export default App
