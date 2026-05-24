/**
 * AMLL React 前端主入口
 * 
 * 这个文件是嵌入到 Android 应用中的 Web 歌词界面的入口点。
 * 它负责 UI 初始化、背景渲染以及与 Android 原生代码的桥接通信。
 * 
 * 主要功能：
 * - 初始化 AMLL Core 渲染器
 * - 处理播放器实例和背景效果
 * - 暴露全局 API 供 Android 调用
 * - 处理 UI 补丁和布局适配
 */
import * as AMLLCore from '@applemusic-like-lyrics/core'
import '@applemusic-like-lyrics/core/style.css'
import './styles.css'
import { logToAndroid } from './utils/bridge_utils'
import {
  LyricLine,
  LyricsPayload,
  processLyricsPayload
} from './utils/lyricProcessor'

// Ensure core module is accessible on window in all bundling scenarios
;(window as any).AMLLCore = AMLLCore
console.log('[AMLL] core assigned to window.AMLLCore')

let playerInstance: any = null
let backgroundRender: any = null
let lastAlbumArt = ''
let albumArtRetryCount = 0
const MAX_ALBUM_ART_RETRIES = 3

let pendingLyricOptions: any = {}

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
 */
function applyAMLLPatch() {
  logToAndroid('AMLL CSS patch handled via styles.css', 'info')
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

    window.__amll = window.__amll || {}
    window.__amll.player = playerInstance
    window.__amll.backgroundRender = backgroundRender
  } catch (error) {
    logToAndroid(`attachCoreInstances error: ${(error as Error).message}`, 'error')
  }
}

/**
 * 调试 / 备用渲染器
 */
function renderFallbackLyrics(root: HTMLElement | null, lines: LyricLine[]) {
  try {
    if (!root) return
    let container = document.getElementById('amll-debug-lyrics') as HTMLElement | null
    if (!container) {
      container = document.createElement('div')
      container.id = 'amll-debug-lyrics'
      container.style.pointerEvents = 'none'
      container.style.position = 'absolute'
      container.style.left = '0'
      container.style.top = '0'
      container.style.width = '100%'
      container.style.zIndex = '9999'
      root.appendChild(container)
    }

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

    try {
      const p = (window.__amll && window.__amll.player) || playerInstance
      const el = p?.element || p?.rootElement || null
      if (el && el instanceof HTMLElement) {
        if (el.parentElement !== root) {
          el.style.position = el.style.position || 'relative'
          el.style.width = el.style.width || '100%'
          el.style.height = el.style.height || '100%'
          root.appendChild(el)
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

if (document.readyState === 'loading') {
  window.addEventListener('DOMContentLoaded', initAMLL)
} else {
  setTimeout(initAMLL, 0)
}

// Global API implementations
window.updateLyrics = async function (payload: LyricsPayload) {
  try {
    const normalized = await processLyricsPayload(payload)
    lyricLines = normalized
    logToAndroid(`updateLyrics: ${normalized.length} lines`, 'debug')

    if (playerInstance) {
      if (playerInstance.setLyricLines) {
        playerInstance.setLyricLines(normalized)
      } else if (playerInstance.setLyrics) {
        playerInstance.setLyrics(normalized)
      } else if (playerInstance.updateLyrics) {
        playerInstance.updateLyrics(normalized)
      } else {
        logToAndroid('playerInstance does not expose setLyricLines', 'warn')
        renderFallbackLyrics(document.getElementById('app'), normalized)
      }

      requestAnimationFrame(() => {
        forceRebuildLyricsDom('updateLyrics')
      })
    } else {
      renderFallbackLyrics(document.getElementById('app'), normalized)
    }
  } catch (e) {
    logToAndroid(`updateLyrics error: ${(e as Error).message}`, 'error')
  }
}

window.updateTime = function (timeMs: number) {
  try {
    const t = Number(timeMs)
    if (hasPlaybackState && !isPlaying) return
    currentTime = t
    if (playerInstance) {
      if (playerInstance.setCurrentTime) {
        playerInstance.setCurrentTime(Math.trunc(t), false)
      } else if (playerInstance.seek) {
        playerInstance.seek(Math.trunc(t))
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
      albumUri = demoAlbumArt
      lastAlbumArt = ''
      return
    }

    let finalUri = uri
    if (uri.startsWith('file:')) {
      try {
        const response = await fetch(uri)
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

    if (lastAlbumArt === uri) return

    albumUri = finalUri || demoAlbumArt
    lastAlbumArt = uri
    albumArtRetryCount = 0

    if (backgroundRender && backgroundRender.setAlbum) {
      try {
        await backgroundRender.setAlbum(albumUri)
      } catch (err) {
        logToAndroid(`setAlbum error: ${(err as Error).message}`, 'error')
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
  try {
    if (!playerInstance) return
    if (paused && typeof playerInstance.pause === 'function') {
      playerInstance.pause()
    } else if (!paused && typeof playerInstance.resume === 'function') {
      playerInstance.resume()
    } else if (!paused && typeof playerInstance.play === 'function') {
      playerInstance.play()
    }
  } catch (e) {
    logToAndroid(`setPaused error: ${(e as Error).message}`, 'error')
  }
}

window.configureLyricMotion = function (options: any) {
  pendingLyricOptions = { ...pendingLyricOptions, ...options }
  try {
    if (!playerInstance) return
    const lp = playerInstance
    if (options.springPosY && lp.setLinePosYSpringParams) lp.setLinePosYSpringParams(options.springPosY)
    if (options.enableSpring !== undefined && lp.setEnableSpring) lp.setEnableSpring(options.enableSpring)
    if (options.springScale && lp.setLineScaleSpringParams) lp.setLineScaleSpringParams(options.springScale)
    if (options.enableScale !== undefined && lp.setEnableScale) lp.setEnableScale(options.enableScale)
    if (options.enableBlur !== undefined && lp.setEnableBlur) lp.setEnableBlur(options.enableBlur)
    if (options.hidePassedLines !== undefined && lp.setHidePassedLines) lp.setHidePassedLines(options.hidePassedLines)
    if (options.wordFadeWidth !== undefined && lp.setWordFadeWidth) lp.setWordFadeWidth(options.wordFadeWidth)
    if (lp.calcLayout) lp.calcLayout()
  } catch (e) {
    logToAndroid(`configureLyricMotion error: ${(e as Error).message}`, 'error')
  }
}

window.configureBackgroundEffect = function (options: any) {
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

window.configureLyricBackground = function (options: any) {
  try {
    const renderer = options.renderer
    const bgElement = backgroundRender?.getElement?.()
    if (bgElement) {
      bgElement.style.display = (renderer === 'css-bg') ? 'none' : 'block'
    }

    if (backgroundRender) {
      if (options.fps !== undefined && backgroundRender.setFPS) backgroundRender.setFPS(options.fps)
      if (options.renderScale !== undefined && backgroundRender.setRenderScale) backgroundRender.setRenderScale(options.renderScale)
      if (options.staticMode !== undefined && backgroundRender.setStaticMode) backgroundRender.setStaticMode(options.staticMode)
    }

    if (renderer === 'css-bg' && options.cssProperty) {
      document.body.style.background = options.cssProperty
    } else {
      document.body.style.background = 'transparent'
    }
  } catch (e) {
    logToAndroid(`configureLyricBackground error: ${(e as Error).message}`, 'error')
  }
}

window.setRenderMode = function (mode: string) {
  logToAndroid(`setRenderMode: ${mode}`, 'debug')
}

window.setLyricPlayerImplementation = function (implementation: string) {
  logToAndroid(`setLyricPlayerImplementation: ${implementation}`, 'debug')
}

window.setLyricSizePreset = function (preset: string) {
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
