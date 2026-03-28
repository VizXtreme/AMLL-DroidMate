import React, { useEffect, useRef } from 'react'
import { createRoot } from 'react-dom/client'
import { LyricPlayer, BackgroundRender } from '@applemusic-like-lyrics/react'
import type { LyricPlayerRef } from '@applemusic-like-lyrics/react'
import '@applemusic-like-lyrics/react-full/style.css'
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

// Global state for Android bridge
interface AMLLGlobal {
  player: any
  backgroundRender: any
  state: any
}

// 🔧 下游覆盖：修�?generateFadeGradient �?width 验证问题
// �?AMLL 加载后立即应用补�?
function applyAMLLPatch() {
  logToAndroid('Applying AMLL patch for generateFadeGradient', 'info')
  
  // 方法 1：通过 CSS 变量设置安全�?
  const style = document.createElement('style')
  style.textContent = `
    /* 确保 mask-image 相关 CSS 变量始终有安全默认�?*/
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

    // 关键修复：在替换全局 setter 之前，先检查是否有延迟的数据需要处�?
    const pendingLyrics = globalSetLyricLines
    const pendingTime = globalSetCurrentTime
    const pendingAlbum = globalSetAlbumUri
    
    // Expose global API for Android - 直接绑定�?Jotai �?setter
    ;(window as any).__setLyricLines = setLyricLines
    ;(window as any).__setCurrentTime = setCurrentTime
    ;(window as any).__setAlbumUri = setAlbumUri

    // 如果有延迟的数据，在替换 setter 后立即应�?
    if (pendingLyrics && Array.isArray(pendingLyrics) && pendingLyrics.length > 0) {
      setLyricLines(pendingLyrics)
      logToAndroid(`Applied pending lyrics (${pendingLyrics.length} lines)`, 'info')
    }
    if (pendingTime !== null && typeof pendingTime === 'number') {
      setCurrentTime(pendingTime)
    }
    if (pendingAlbum && typeof pendingAlbum === 'string') {
      setAlbumUri(pendingAlbum)
    }

    // Global API functions
    window.updateLyrics = function (payload: LyricsPayload) {
      try {
        const rawLines = Array.isArray(payload?.lines) ? payload.lines : []
        const normalizedLines = normalizeLyricLines(rawLines)
        
        logToAndroid(`updateLyrics called with ${rawLines.length} raw lines, ${normalizedLines.length} normalized`, 'info')
        
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
          // 调试：打印前几行歌词的详细信�?
          normalizedLines.slice(0, 3).forEach((line, idx) => {
            logToAndroid(`Line ${idx}: text="${line.words.map(w => w.word).join('')}", words=${line.words.length}, startTime=${line.startTime}, endTime=${line.endTime}`, 'info')
            line.words.slice(0, 2).forEach((word, wIdx) => {
              logToAndroid(`  Word ${wIdx}: "${word.word}" ${word.startTime}-${word.endTime}ms`, 'debug')
            })
          })
          
          setLyricLines(normalizedLines)
        }
        
        logToAndroid(`Updated lyrics (${normalizedLines.length} lines)`, 'info')
        
        // 关键修复：在设置歌词后，如果当前已经有时间值，强制 LyricPlayer 立即更新进度
        // 这是因为 Android �?updateTime 可能�?updateLyrics 之前通过 evaluateJavascript 异步发�?
        // 导致 initialLayoutFinished 检查失败而被跳过
        if (playerRef.current?.lyricPlayer && currentTime > 0) {
          logToAndroid(`Force update LyricPlayer time to ${currentTime} after setting lyrics`, 'info')
          playerRef.current.lyricPlayer.setCurrentTime(Math.trunc(currentTime), false)
              
          // 额外修复：触发一�?mask-image 更新，确�?CSS 变量正确初始�?
          // 这是因为 AMLL �?mask-image 生成依赖于布局测量，可能在初始渲染时未完成
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
      if (typeof (window as any).__setCurrentTime === 'function') {
        ;(window as any).__setCurrentTime(parsedTime)
      }
      // playerRef.current.lyricPlayer 才是真正的歌词播放实�?
      if (playerRef.current?.lyricPlayer) {
        playerRef.current.lyricPlayer.setCurrentTime(Math.trunc(parsedTime), false)
      }
    }

    window.updateAlbumArt = async function (uri: string) {
      setAlbumUri(uri || demoAlbumArt)
      lastAlbumArt = uri
      logToAndroid(`Album art updated: ${uri ? 'present' : 'empty'}`, 'debug')
      
      // Also update BackgroundRender directly if available
      if (window.__amll?.backgroundRender) {
        const bgRender = window.__amll.backgroundRender
        if (bgRender.setAlbum) {
          bgRender.setAlbum(uri || '')
          logToAndroid('BackgroundRender album updated directly', 'debug')
        }
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
      logToAndroid(`setRenderMode: ${mode}`, 'info')
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
        alignPosition={0.5}
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
  // 立即挂载全局 API，确�?Android 能随时调�?
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
      
      // 🔧 应用下游补丁
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
