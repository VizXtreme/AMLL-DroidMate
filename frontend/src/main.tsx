/**
 * AMLL React 前端主入口
 * 
 * 这个文件是嵌入到 Android 应用中的 Web 歌词界面的入口点。
 * 它使用 React 和 @applemusic-like-lyrics 库来渲染华丽的歌词效果。
 * 
 * 主要功能：
 * - 与 Android 原生代码通过 WebView 桥接通信
 * - 接收音乐信息和歌词数据
 * - 渲染逐字高亮的歌词
 * - 支持背景视觉效果（音频可视化）
 * - 处理用户交互（点击、滑动等）
 */
// Switched to core-only implementation (no React)
import * as AMLLCore from '@applemusic-like-lyrics/core'
import '@applemusic-like-lyrics/core/style.css'
// Ensure core module is accessible on window in all bundling scenarios
;(window as any).AMLLCore = AMLLCore

// Android 特定的适配配置
// 使用透明背景以便与 Android 原生 UI 融合

interface WordEntry { word: string; startTime: number; endTime: number }

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

let playerInstance: any = null
let backgroundRender: any = null
let lastAlbumArt = ''
let albumArtRetryCount = 0
const MAX_ALBUM_ART_RETRIES = 3

let pendingLyricOptions: any = {}

let enableSpringValue = true
let enableScaleValue = true
let enableBlurValue = true
let hidePassedLinesValue = false
let wordFadeWidthValue = 0.5
let fpsValue = 60

declare global {
  interface Window {
    __amll?: any
    updateLyrics?: (payload: LyricsPayload) => void
    updateTime?: (timeMs: number) => void
    updateAlbumArt?: (uri: string) => Promise<void>
    setPaused?: (paused: boolean) => void
    configureLyricMotion?: (options: any) => void
    configureBackgroundEffect?: (options: any) => void
    setRenderMode?: (mode: string) => void
    setLyricPlayerImplementation?: (implementation: string) => void
    setLyricSizePreset?: (preset: string) => void
    setEnableTranslationLine?: (enabled: boolean) => void
    setEnableRomanLine?: (enabled: boolean) => void
    setEnableSwapTransRomanLine?: (enabled: boolean) => void
    setAdvanceLyricDynamicLyricTime?: (enabled: boolean) => void
    rebuildLyricsDom?: (reason?: string) => boolean
    Android?: {
      log?: (message: string, level: string) => void
      isPlaying?: () => boolean
      onLineClick?: (index: number, startTime: number) => void
    }
  }
}

/**
 * 应用 AMLL 库的补丁
 *
 * 由于 AMLL 库在某些情况下存在 mask-image 相关的问题，
 * 这个函数会在页面加载后立即应用 CSS 补丁，确保渐变效果正常显示。
 *
 * 主要修复：
 * - 为 CSS 变量设置安全默认值
 * - 防止 width 验证问题
 * - 确保蒙版效果正常工作
 */
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

function attachElementToRoot(root: HTMLElement, el: HTMLElement, zIndex: string) {
  el.style.position = el.style.position || 'absolute'
  el.style.inset = el.style.inset || '0'
  el.style.width = el.style.width || '100%'
  el.style.height = el.style.height || '100%'
  el.style.pointerEvents = el.style.pointerEvents || 'none'
  el.style.zIndex = el.style.zIndex || zIndex
  if (el.parentElement !== root) {
    root.appendChild(el)
  }
}

function createBackgroundRenderer(core: any, root: HTMLElement) {
  const Background = core.BackgroundRender
  if (!Background?.new) {
    logToAndroid('BackgroundRender factory not found on core', 'debug')
    return null
  }

  const rendererCandidates = [
    core.MeshGradientRenderer,
    core.PixiRenderer,
  ].filter(Boolean)

  for (const RendererCtor of rendererCandidates) {
    try {
      const instance = Background.new(RendererCtor)
      const element = instance.getElement()
      attachElementToRoot(root, element, '-1')
      logToAndroid(`Created BackgroundRender with ${RendererCtor?.name || 'renderer'}`, 'info')
      return instance
    } catch (e) {
      logToAndroid(`BackgroundRender init failed with ${RendererCtor?.name || 'renderer'}: ${(e as Error).message}`, 'warn')
    }
  }

  return null
}

// Core-based non-React application

const PLAYER_BACKGROUND = 'transparent'
const demoAlbumArt = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNDAwIiBoZWlnaHQ9IjQwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB3aWR0aD0iMTAwJSIgaGVpZ2h0PSIxMDAlIiBmaWxsPSJyZ2JhKDAsMCwwLDAuMSkiLz48L3N2Zz4='

let currentTime = 0
let lyricLines: LyricLine[] = []
let albumUri = demoAlbumArt
let isPlaying = true
let hasPlaybackState = false

function attachCoreInstances(container: HTMLElement) {
  try {
    const Core: any = (AMLLCore as any) || (window as any).AMLLCore
    if (!Core) {
      logToAndroid('AMLL core module not found at runtime', 'warn')
      return
    }

    // Try common export names for DOM lyric player
    const DomLyricPlayer = Core.DomLyricPlayer || Core.DOMLyricPlayer || Core.DomLyricPlayerClass || Core.LyricPlayer
    if (DomLyricPlayer) {
      try {
        playerInstance = new DomLyricPlayer({ container, album: albumUri })
        logToAndroid('Created DomLyricPlayer from core', 'info')
      } catch (e) {
        logToAndroid(`Failed to instantiate DomLyricPlayer: ${(e as Error).message}`, 'error')
      }
    } else {
      logToAndroid('DomLyricPlayer constructor not found on core', 'debug')
    }

    backgroundRender = createBackgroundRenderer(Core, container)

    // Expose __amll references
    window.__amll = window.__amll || {}
    window.__amll.player = playerInstance
    window.__amll.backgroundRender = backgroundRender
  } catch (error) {
    logToAndroid(`attachCoreInstances error: ${(error as Error).message}`, 'error')
  }
}

/**
 * Debug / fallback renderer
 *
 * When the bundled AMLL core does not create visible DOM nodes (for example the
 * renderer is canvas-based or failed to instantiate), this helper will create
 * a lightweight textual representation under #app so Chrome/WebTools can
 * inspect the lyrics during development and debugging.
 */
function renderFallbackLyrics(root: HTMLElement | null, lines: LyricLine[]) {
  try {
    if (!root) return
    let container = document.getElementById('amll-debug-lyrics') as HTMLElement | null
    if (!container) {
      container = document.createElement('div')
      container.id = 'amll-debug-lyrics'
      // keep pointer-events none so it won't intercept touches in production
      container.style.pointerEvents = 'none'
      container.style.position = 'absolute'
      container.style.left = '0'
      container.style.top = '0'
      container.style.width = '100%'
      container.style.zIndex = '9999'
      root.appendChild(container)
    }

    // Clear and render simple lines
    container.innerHTML = ''
    lines.forEach((line, idx) => {
      const el = document.createElement('div')
      el.className = 'amll-debug-line'
      const text = (line.words && line.words.length > 0)
        ? line.words.map(w => w.word).join(' ')
        : (line.translatedLyric || line.romanLyric || '')
      el.textContent = text || `[line ${idx}]`
      el.setAttribute('data-start', String(line.startTime))
      el.setAttribute('data-end', String(line.endTime))
      container!.appendChild(el)
    })
  } catch (err) {
    logToAndroid(`renderFallbackLyrics error: ${(err as Error).message}`, 'error')
  }
}

function forceRebuildLyricsDom(reason: string = 'manual refresh') {
  try {
    const p = (window.__amll && window.__amll.player) || playerInstance
    if (!p) return false

    const lineObjects = Array.isArray(p.currentLyricLineObjects) ? p.currentLyricLineObjects : []
    if (lineObjects.length === 0) {
      if (typeof p.setLyricLines === 'function' && lyricLines.length > 0) {
        p.setLyricLines(lyricLines, currentTime)
        if (typeof p.update === 'function') {
          p.update(currentTime)
        }
        logToAndroid(`forceRebuildLyricsDom: rebuilt via setLyricLines (${reason})`, 'debug')
        return true
      }
      return false
    }

    let rebuiltCount = 0
    for (const line of lineObjects) {
      if (!line) continue

      if (typeof line.rebuildElement === 'function') {
        line.rebuildElement()
        rebuiltCount++
      }

      if (typeof line.markMaskImageDirty === 'function') {
        line.markMaskImageDirty(reason)
      } else if (typeof line.updateMaskImageSync === 'function') {
        line.updateMaskImageSync()
      } else if (typeof line.rebuildStyle === 'function') {
        line.rebuildStyle()
      }
    }

    if (typeof p.update === 'function') {
      p.update(currentTime)
    }

    logToAndroid(`forceRebuildLyricsDom: rebuilt=${rebuiltCount} (${reason})`, 'debug')
    return rebuiltCount > 0
  } catch (err) {
    logToAndroid(`forceRebuildLyricsDom error: ${(err as Error).message}`, 'error')
    return false
  }
}

// Initialize app when DOM is ready. Use explicit readiness check so that
// if the bundle is executed after DOMContentLoaded (common in WebView / local
// file loads), initialization still runs — otherwise #app may never get
// created/mounted and look empty in DevTools.
function initAMLL() {
  try {
    document.documentElement.style.background = 'transparent'
    document.body.style.background = 'transparent'

    applyAMLLPatch()

    const root = document.getElementById('app') || document.createElement('div')
    if (!document.getElementById('app')) {
      root.id = 'app'
      document.body.appendChild(root)
    }
    root.style.position = 'relative'
    root.style.width = '100%'
    root.style.height = '100vh'

    attachCoreInstances(root as HTMLElement)

    // Fallback: if the core created a DOM element for the player but the
    // element is not attached into our `root` (some renderers create an
    // element but don't append it), append it and give it safe sizing so it
    // becomes visible in WebView / DevTools. This helps when the core
    // renderer is DOM-based but uses different attachment semantics.
    try {
      const p = (window.__amll && window.__amll.player) || playerInstance
      const el = p?.element || p?.rootElement || null
      if (el && el instanceof HTMLElement) {
        if (el.parentElement !== root) {
          try {
            // Make sure element has reasonable layout rules so it isn't 0x0
            el.style.position = el.style.position || 'relative'
            el.style.width = el.style.width || '100%'
            el.style.height = el.style.height || '100%'
            // Ensure it's appended to our root so DevTools shows content
            root.appendChild(el)
            logToAndroid('Appended player.element to #app as fallback', 'debug')
          } catch (e) {
            logToAndroid(`Failed to append player.element fallback: ${(e as Error).message}`, 'error')
          }
        }
      }
    } catch (e) {
      logToAndroid(`player append fallback error: ${(e as Error).message}`, 'error')
    }

    if (lyricLines.length > 0) {
      requestAnimationFrame(() => {
        forceRebuildLyricsDom('initAMLL')
      })
    }

    logToAndroid('AMLL core WebView initialized', 'info')
  } catch (error) {
    logToAndroid(`Initialization error: ${(error as Error).message}`, 'error')
  }
}

// If document is still loading, wait for DOMContentLoaded; otherwise run now.
if (document.readyState === 'loading') {
  window.addEventListener('DOMContentLoaded', initAMLL)
} else {
  // DOM already parsed — schedule init on next tick to match event timing
  setTimeout(initAMLL, 0)
}

// Global API implementations (mirror previous behavior but call core instance methods when available)
window.updateLyrics = function (payload: LyricsPayload) {
  try {
    const rawLines = Array.isArray(payload?.lines) ? payload.lines : []
    const normalized = normalizeLyricLines(rawLines)
    lyricLines = normalized
    logToAndroid(`updateLyrics: ${normalized.length} lines`, 'debug')

    if (playerInstance) {
      // Try common method names
      if (playerInstance.setLyricLines) {
        playerInstance.setLyricLines(normalized)
      } else if (playerInstance.setLyrics) {
        playerInstance.setLyrics(normalized)
      } else if (playerInstance.updateLyrics) {
        playerInstance.updateLyrics(normalized)
      } else {
        logToAndroid('playerInstance does not expose setLyricLines/setLyrics/updateLyrics', 'warn')
        // Render fallback debug DOM so the #app element isn't empty in DevTools
        const root = document.getElementById('app')
        renderFallbackLyrics(root, normalized)
      }

      requestAnimationFrame(() => {
        forceRebuildLyricsDom('updateLyrics')
      })
    } else {
      // No player instance at all -> render fallback so the #app element isn't empty
      const root = document.getElementById('app')
      renderFallbackLyrics(root, normalized)
    }
  } catch (e) {
    logToAndroid(`updateLyrics error: ${(e as Error).message}`, 'error')
  }
}

window.updateTime = function (timeMs: number) {
  try {
    const t = Number(timeMs)
    if (hasPlaybackState && !isPlaying) {
      logToAndroid(`updateTime skipped while paused: ${Math.trunc(t)}`, 'debug')
      return
    }
    currentTime = t
    if (playerInstance) {
      if (playerInstance.setCurrentTime) {
        playerInstance.setCurrentTime(Math.trunc(t), false)
      } else if (playerInstance.seek) {
        playerInstance.seek(Math.trunc(t))
      } else {
        logToAndroid('playerInstance has no setCurrentTime/seek', 'debug')
      }

      if (typeof playerInstance.update === 'function') {
        playerInstance.update(Math.trunc(t))
      }
    }
  } catch (e) {
    logToAndroid(`updateTime error: ${(e as Error).message}`, 'error')
  }
}

window.updateAlbumArt = async function (uri: string) {
  try {
    const isValidUri = uri && typeof uri === 'string' && uri.trim().length > 0
    if (!isValidUri) {
      logToAndroid('updateAlbumArt: invalid uri, using placeholder', 'warn')
      albumUri = demoAlbumArt
      lastAlbumArt = ''
      return
    }

    let finalUri = uri
    if (uri.startsWith('file:')) {
      try {
        const response = await fetch(uri)
        if (!response.ok) throw new Error(`HTTP ${response.status}`)
        const blob = await response.blob()
        const reader = new FileReader()
        finalUri = await new Promise<string>((resolve, reject) => {
          reader.onload = () => resolve(reader.result as string)
          reader.onerror = () => reject(reader.error)
          reader.readAsDataURL(blob)
        })
      } catch (err) {
        logToAndroid(`Failed to load file URI: ${(err as Error).message}`, 'error')
        albumUri = demoAlbumArt
        lastAlbumArt = ''
        return
      }
    }

    if (lastAlbumArt === uri) {
      logToAndroid('Album art unchanged, skipping', 'debug')
      return
    }

    albumUri = finalUri || demoAlbumArt
    lastAlbumArt = uri
    albumArtRetryCount = 0

    if (backgroundRender && backgroundRender.setAlbum) {
      try {
        await backgroundRender.setAlbum(albumUri)
        logToAndroid('BackgroundRender setAlbum success', 'debug')
      } catch (err) {
        logToAndroid(`BackgroundRender.setAlbum error: ${(err as Error).message}`, 'error')
        albumArtRetryCount++
        if (albumArtRetryCount < MAX_ALBUM_ART_RETRIES) {
          setTimeout(() => window.updateAlbumArt?.(uri), 500 * albumArtRetryCount)
        }
      }
    }
  } catch (e) {
    logToAndroid(`updateAlbumArt error: ${(e as Error).message}`, 'error')
  }
}

window.setPaused = function (paused: boolean) {
  isPlaying = !paused
  hasPlaybackState = true
  logToAndroid(`Playback ${paused ? 'paused' : 'resumed'}`, 'debug')
  try {
    if (!playerInstance) return
    if (paused && typeof playerInstance.pause === 'function') {
      playerInstance.pause()
    } else if (!paused && typeof playerInstance.resume === 'function') {
      playerInstance.resume()
    } else if (!paused && typeof playerInstance.play === 'function') {
      playerInstance.play()
    }

    if (!paused && typeof playerInstance.update === 'function') {
      playerInstance.update(Math.trunc(currentTime))
    }
  } catch (e) {
    logToAndroid(`setPaused error: ${(e as Error).message}`, 'error')
  }
}

window.configureLyricMotion = function (options: any) {
  logToAndroid(`configureLyricMotion: ${JSON.stringify(options)}`, 'debug')
  pendingLyricOptions = { ...pendingLyricOptions, ...options }
  try {
    if (!playerInstance) return
    const lp = playerInstance
    if (options.enableSpring !== undefined && lp.setLinePosYSpringParams) {
      lp.setLinePosYSpringParams(options.enableSpring ? { mass: 0.9, damping: 15, stiffness: 90 } : { mass: 1.0, damping: 30, stiffness: 50 })
    }
    if (options.enableScale !== undefined && lp.setLineScaleSpringParams) {
      lp.setLineScaleSpringParams(options.enableScale ? { mass: 2, damping: 25, stiffness: 100 } : { mass: 1.0, damping: 30, stiffness: 50 })
    }
    if (options.enableBlur !== undefined && lp.setEnableBlur) lp.setEnableBlur(options.enableBlur)
    if (options.hidePassedLines !== undefined && lp.setHidePassedLines) lp.setHidePassedLines(options.hidePassedLines)
    if (options.wordFadeWidth !== undefined && lp.setWordFadeWidth) lp.setWordFadeWidth(options.wordFadeWidth)
  } catch (e) {
    logToAndroid(`configureLyricMotion error: ${(e as Error).message}`, 'error')
  }
}

window.configureBackgroundEffect = function (options: any) {
  logToAndroid(`configureBackgroundEffect: ${JSON.stringify(options)}`, 'debug')
  try {
    if (!backgroundRender) return
    if (options.flowSpeed !== undefined && backgroundRender.setFlowSpeed) backgroundRender.setFlowSpeed(options.flowSpeed)
    if (options.renderScale !== undefined && backgroundRender.setRenderScale) backgroundRender.setRenderScale(options.renderScale)
    if (options.lowFreqVolume !== undefined && backgroundRender.setLowFreqVolume) backgroundRender.setLowFreqVolume(options.lowFreqVolume)
    if (options.fps !== undefined && backgroundRender.setFPS) backgroundRender.setFPS(options.fps)
    if (options.staticMode !== undefined && backgroundRender.setStaticMode) backgroundRender.setStaticMode(options.staticMode)
  } catch (e) {
    logToAndroid(`configureBackgroundEffect error: ${(e as Error).message}`, 'error')
  }
}

window.setRenderMode = function (mode: string) {
  logToAndroid(`setRenderMode: ${mode}`, 'debug')
}

window.setLyricPlayerImplementation = function (implementation: string) {
  logToAndroid(`setLyricPlayerImplementation: ${implementation}`, 'debug')
}

window.setLyricSizePreset = function (preset: string) {
  logToAndroid(`setLyricSizePreset: ${preset}`, 'debug')
  document.documentElement.style.setProperty('--amll-lp-font-size-preset', preset)
}

window.setEnableTranslationLine = function (enabled: boolean) {
  document.documentElement.style.setProperty('--amll-show-translation', enabled ? '1' : '0')
}

window.setEnableRomanLine = function (enabled: boolean) {
  document.documentElement.style.setProperty('--amll-show-roman', enabled ? '1' : '0')
}

window.setEnableSwapTransRomanLine = function (enabled: boolean) {
  document.documentElement.style.setProperty('--amll-swap-trans-roman', enabled ? '1' : '0')
}

window.setAdvanceLyricDynamicLyricTime = function (enabled: boolean) {
  document.documentElement.style.setProperty('--amll-advance-dynamic-time', enabled ? '1' : '0')
  pendingLyricOptions = { ...pendingLyricOptions, advanceDynamicTime: enabled }
}

window.rebuildLyricsDom = function (reason: string = 'manual refresh') {
  return forceRebuildLyricsDom(reason)
}


// End of file - core-only implementation does not export React App
