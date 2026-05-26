/**
 * AMLL 前端主入口 (Main Entry Point)
 *
 * 这个文件是嵌入到 Android WebView 中的 Web 歌词界面的核心逻辑。
 * 它充当了 @applemusic-like-lyrics/core (核心渲染库) 与 Android 原生代码之间的桥梁。
 *
 * 主要职责：
 * 1. 初始化渲染引擎 (LyricPlayer) 和背景效果 (BackgroundRender)。
 * 2. 暴露全局 API (挂载到 window)，供 Android 端通过 `evaluateJavascript` 调用。
 * 3. 管理歌词状态、播放进度、专辑封面以及 UI 配置。
 * 4. 处理布局适配和针对 WebView 环境的性能优化。
 */

import * as AMLLCore from '@applemusic-like-lyrics/core'
import '@applemusic-like-lyrics/core/style.css'
import '../styles.css'
import { logToAndroid } from './utils/bridge_utils'

// --- 全局类型声明 (Global Type Declarations) ---
// 声明挂载在 window 对象上的 API，以便 Android 端和 TypeScript 类型检查使用
declare global {
  interface Window {
    __amll?: any // 暴露给调试用的内部实例
    updateLyrics?: (payload: LyricsPayload) => void // 更新歌词数据
    updateTime?: (timeMs: number) => void // 更新当前播放时间
    updateAlbumArt?: (uri: string) => Promise<void> // 更新专辑封面
    setPaused?: (paused: boolean) => void // 设置播放/暂停状态
    configureLyricMotion?: (options: any) => void // 配置歌词滚动/动画效果
    configureBackgroundEffect?: (options: any) => void // 配置背景渲染参数
    configureLyricBackground?: (options: any) => void // 配置底色或渲染器切换
    setRenderMode?: (mode: string) => void // 设置渲染模式（如流式、静态）
    setLyricPlayerImplementation?: (implementation: string) => void // 切换播放器实现
    setLyricSizePreset?: (preset: string) => void // 设置歌词字体大小预设
    setEnableTranslationLine?: (enabled: boolean) => void // 启用/禁用翻译行
    setEnableRomanLine?: (enabled: boolean) => void // 启用/禁用罗马音行
    setEnableSwapTransRomanLine?: (enabled: boolean) => void // 交换翻译和罗马音的位置
    setAdvanceLyricDynamicLyricTime?: (enabled: boolean) => void // 启用歌词提前量优化
    rebuildLyricsDom?: (reason?: string) => boolean // 强制重构歌词 DOM（处理布局异常）

    // Android 原生通过 JavascriptInterface 注入的对象
    Android?: {
      log?: (message: string, level: string) => void // 向原生发送日志
      isPlaying?: () => boolean // 查询原生播放状态
      onLineClick?: (index: number, startTime: number) => void // 歌词行点击回调
    }
    AMLLCore: typeof AMLLCore
  }
}

// --- 内部状态管理 (Internal State Management) ---
const state = {
  player: null as any, // LyricPlayer 实例
  background: null as any, // BackgroundRender 实例
  currentTime: -1, // 当前毫秒级播放时间
  lyricLines: [] as LyricLine[], // 当前加载的歌词行数据
  // 默认占位图 (SVG Base64)
  albumUri: '',
  lastAlbumArt: '', // 上一次设置的封面 URI，用于去重
  isPlaying: false, // 播放状态缓存
  hasPlaybackState: false, // 是否已接收过播放状态
  pendingLyricOptions: {} as Record<string, any>, // 待应用的配置项
}

// 将 Core 挂载到 window 确保全局可用（方便 HMR 或调试）
;(window as any).AMLLCore = AMLLCore

// --- 工具函数 (Utility Functions) ---

/**
 * 统一日志输出，如果 Android 接口可用则发送给原生，否则输出到控制台
 */
const log = (msg: string, level: 'info' | 'debug' | 'warn' | 'error' = 'info') => logToAndroid(msg, level)

/**
 * 设置 CSS 全局变量，用于响应式修改样式
 */
const setCSSVar = (name: string, value: string | number | boolean) => {
  const val = typeof value === 'boolean' ? (value ? '1' : '0') : String(value)
  document.documentElement.style.setProperty(name, val)
}

/**
 * 将 HTML 元素挂载到根节点，并应用基础全屏样式
 */
function attachElementToRoot(root: HTMLElement, el: HTMLElement, zIndex: string) {
  Object.assign(el.style, {
    position: 'absolute',
    inset: '0',
    width: '100%',
    height: '100%',
    pointerEvents: 'none', // 默认不拦截点击事件
    zIndex
  })
  if (el.parentElement !== root) {
    root.appendChild(el)
  }
}

// --- 核心初始化逻辑 (Core Initialization) ---

/**
 * 创建背景渲染器
 * 尝试按优先级初始化不同的渲染引擎 (如 MeshGradient 或 Pixi)
 */
function createBackgroundRenderer(core: any, root: HTMLElement, selectedRenderer?: string) {
  const Background = core.BackgroundRender
  if (!Background?.new) {
    log('BackgroundRender factory not found on core', 'debug')
    return null
  }

  // 定义可用的候选渲染器映射
  const allCandidates: Record<string, any> = {
    'mesh': core.MeshGradientRenderer,
    'pixi': core.PixiRenderer
  }

  // 如果显式选定了存在的渲染器，则只尝试那一个；否则使用默认的优先级列表
  const rendererCandidates = (selectedRenderer && allCandidates[selectedRenderer])
    ? [allCandidates[selectedRenderer]]
    : [core.MeshGradientRenderer, core.PixiRenderer].filter(Boolean)

  for (const RendererCtor of rendererCandidates) {
    try {
      const instance = Background.new(RendererCtor)
      const element = instance.getElement()
      attachElementToRoot(root, element, '-1') // 放在最底层
      log(`Created BackgroundRender with ${RendererCtor?.name || 'renderer'}`, 'info')
      return instance
    } catch (e) {
      log(`BackgroundRender init failed with ${RendererCtor?.name || 'renderer'}: ${(e as Error).message}`, 'warn')
    }
  }
  return null
}




/**
 * 强制重构歌词 DOM 结构
 * 解决 WebView 在后台恢复或尺寸改变时可能出现的遮罩渲染错误或布局偏移
 */
function forceRebuildLyricsDom(reason: string = 'manual refresh'): boolean {
  try {
    const p = state.player
    if (!p) return false

    const lineObjects = Array.isArray(p.currentLyricLineObjects) ? p.currentLyricLineObjects : []

    // 如果当前没有行对象，尝试重新注入歌词
    if (lineObjects.length === 0) {
      if (typeof p.setLyricLines === 'function' && state.lyricLines.length > 0) {
        p.setLyricLines(state.lyricLines, state.currentTime)
        p.update?.(state.currentTime)
        log(`forceRebuildLyricsDom: full rebuild (${reason})`, 'debug')
        return true
      }
      return false
    }

    // 遍历所有行，调用重构方法
    lineObjects.forEach(line => {
      if (!line) return
      line.rebuildElement?.()
      // 更新遮罩图以确保逐词动画效果正确
      if (line.markMaskImageDirty) {
        line.markMaskImageDirty(reason)
      } else if (line.updateMaskImageSync) {
        line.updateMaskImageSync()
      } else {
        line.rebuildStyle?.()
      }
    })

    p.update?.(state.currentTime)
    log(`forceRebuildLyricsDom: rebuilt ${lineObjects.length} lines (${reason})`, 'debug')
    return true
  } catch (err) {
    log(`forceRebuildLyricsDom error: ${(err as Error).message}`, 'error')
    return false
  }
}

/**
 * 初始化 AMLL 核心环境
 */
function initAMLL() {
  try {
    // 设置页面背景透明，以便 Android 底层背景可见
    document.documentElement.style.background = 'transparent'
    document.body.style.background = 'transparent'

    const root = document.getElementById('app') || document.createElement('div')
    if (!document.getElementById('app')) {
      root.id = 'app'
      document.body.appendChild(root)
    }
    Object.assign(root.style, { position: 'relative', width: '100%', height: '100vh' })

    const Core = AMLLCore as any
    // 获取播放器类（支持不同版本的命名习惯）
    const DomLyricPlayer = Core.DomLyricPlayer || Core.DOMLyricPlayer || Core.LyricPlayer

    if (DomLyricPlayer) {
      try {
        state.player = new DomLyricPlayer({
          container: root,
          album: state.albumUri,
          // 可以在此处添加更多初始化参数
        })
        log('Created DomLyricPlayer', 'info')
      } catch (e) {
        log(`Failed to instantiate DomLyricPlayer: ${(e as Error).message}`, 'error')
      }
    }

    // 默认不主动创建背景渲染器，由 Android 端通过 configureLyricBackground 按需初始化
    // 这可以避免在已经有原生背景的情况下产生冗余的 Canvas 节点
    // state.background = createBackgroundRenderer(Core, root as HTMLElement)

    // 暴露内部实例以便调试
    window.__amll = { player: state.player, backgroundRender: state.background }

    // 如果初始化时已有歌词，立即渲染
    if (state.lyricLines.length > 0) {
      requestAnimationFrame(() => forceRebuildLyricsDom('initAMLL'))
    }

    log('AMLL core WebView initialized', 'info')
  } catch (error) {
    log(`Initialization error: ${(error as Error).message}`, 'error')
  }
}

// --- 启动初始化进程 ---
if (document.readyState === 'loading') {
  window.addEventListener('DOMContentLoaded', initAMLL)
} else {
  setTimeout(initAMLL, 0)
}

// --- 全局 API 实现 (供 Android 端调用) ---

/**
 * 更新歌词数据
 * @param payload 包含原始歌词文本或结构化歌词的对象
 */
window.updateLyrics = async (payload: LyricsPayload) => {
  try {
    // 预处理歌词（解析 LRC/TTML 等）
    const normalized = await processLyricsPayload(payload)
    state.lyricLines = normalized
    log(`updateLyrics: ${normalized.length} lines`, 'debug')

    const p = state.player
    if (p) {
      const setter = p.setLyricLines || p.setLyrics || p.updateLyrics
      if (setter) {
        setter.call(p, normalized)
        // 数据更新后请求下一帧重构 DOM
        requestAnimationFrame(() => forceRebuildLyricsDom('updateLyrics'))
      } else {
        log('playerInstance does not expose lyric setter', 'warn')
      }
    }
  } catch (e) {
    log(`updateLyrics error: ${(e as Error).message}`, 'error')
  }
}

/**
 * 同步播放时间
 * @param timeMs 当前播放位置（毫秒）
 */
window.updateTime = (timeMs: number) => {
  // 性能优化：如果处于暂停状态，且已经同步过状态，则忽略细微的时间波动
  if (state.hasPlaybackState && !state.isPlaying) return

  const t = Math.trunc(timeMs)
  if (state.currentTime === t) return
  state.currentTime = t

  const p = state.player
  if (!p) return

  try {
    // 更新播放器时间
    if (p.setCurrentTime) {
      p.setCurrentTime(t, false)
    } else if (p.seek) {
      p.seek(t)
    }
    // 触发渲染更新
    p.update?.(t)
  } catch (e) {
    log(`updateTime error: ${(e as Error).message}`, 'error')
  }
}

/**
 * 更新专辑封面
 * 支持 URL 或 base64，会自动处理 file:// 协议的本地文件
 */
window.updateAlbumArt = async (uri: string) => {
  if (!uri || uri.trim().length === 0) {
    state.albumUri = state.lastAlbumArt = ''
    return
  }

  if (state.lastAlbumArt === uri) return
  state.lastAlbumArt = uri

  try {
    let finalUri = uri
    // 如果是本地文件协议，尝试转换成 Data URL 以规避 WebView 的跨域限制
    if (uri.startsWith('file:')) {
      const response = await fetch(uri)
      const blob = await response.blob()
      finalUri = await new Promise((resolve, reject) => {
        const reader = new FileReader()
        reader.onload = () => resolve(reader.result as string)
        reader.onerror = () => reject(reader.error)
        reader.readAsDataURL(blob)
      })
    }

    state.albumUri = finalUri

    // 通知背景渲染器更新封面
    if (state.background?.setAlbum) {
      try {
        await state.background.setAlbum(state.albumUri)
      } catch (err) {
        log(`setAlbum error: ${(err as Error).message}. `, 'warn')
      }
    }
  } catch (e) {
    log(`updateAlbumArt error: ${(e as Error).message}`, 'error')
  }
}

/**
 * 控制播放/暂停
 */
window.setPaused = (paused: boolean) => {
  state.isPlaying = !paused
  state.hasPlaybackState = true
  const p = state.player
  if (!p) return

  try {
    if (paused) {
      p.pause?.()
    } else {
      ;(p.resume || p.play)?.call(p)
    }
  } catch (e) {
    log(`setPaused error: ${(e as Error).message}`, 'error')
  }
}

/**
 * 配置歌词动态效果
 * 包含弹性滚动、缩放、模糊等高级参数
 */
window.configureLyricMotion = (options: any) => {
  state.pendingLyricOptions = { ...state.pendingLyricOptions, ...options }
  const lp = state.player
  if (!lp) return

  try {
    const { springPosY, enableSpring, springScale, enableScale, enableBlur, hidePassedLines, wordFadeWidth } = options
    if (springPosY && lp.setLinePosYSpringParams) lp.setLinePosYSpringParams(springPosY)
    if (enableSpring !== undefined && lp.setEnableSpring) lp.setEnableSpring(enableSpring)
    if (springScale && lp.setLineScaleSpringParams) lp.setLineScaleSpringParams(springScale)
    if (enableScale !== undefined && lp.setEnableScale) lp.setEnableScale(enableScale)
    if (enableBlur !== undefined && lp.setEnableBlur) lp.setEnableBlur(enableBlur)
    if (hidePassedLines !== undefined && lp.setHidePassedLines) lp.setHidePassedLines(hidePassedLines)
    if (wordFadeWidth !== undefined && lp.setWordFadeWidth) lp.setWordFadeWidth(wordFadeWidth)
    lp.calcLayout?.() // 重新计算布局
  } catch (e) {
    log(`configureLyricMotion error: ${(e as Error).message}`, 'error')
  }
}

/**
 * 配置背景特效参数
 * 如流动速度、渲染缩放（性能优化）、帧率等
 */
window.configureBackgroundEffect = (options: any) => {
  const bg = state.background
  if (!bg) return
  try {
    if (options.flowSpeed !== undefined) bg.setFlowSpeed?.(options.flowSpeed)
    if (options.renderScale !== undefined) bg.setRenderScale?.(options.renderScale)
    if (options.lowFreqVolume !== undefined) bg.setLowFreqVolume?.(options.lowFreqVolume)
    if (options.fps !== undefined) bg.setFPS?.(options.fps)
    if (options.staticMode !== undefined) bg.setStaticMode?.(options.staticMode)
  } catch (e) {
    log(`configureBackgroundEffect error: ${(e as Error).message}`, 'error')
  }
}

/**
 * 配置歌词底色背景
 * 可以在 Canvas 渲染背景和 CSS 纯色/渐变背景之间切换
 */
window.configureLyricBackground = (options: any) => {
  try {
    const isCssBg = options.renderer === 'css-bg'

    // 如果当前没有背景实例，且没有显式要求关闭(css-bg)，则根据 options.renderer 尝试初始化
    if (!state.background && !isCssBg) {
      state.background = createBackgroundRenderer(AMLLCore, document.getElementById('app')!, options.renderer)
    }

    const bgElement = state.background?.getElement?.()
    if (bgElement) {
      bgElement.style.display = isCssBg ? 'none' : 'block'
    }

    if (state.background) {
      const { fps, renderScale, staticMode } = options
      if (fps !== undefined) state.background.setFPS?.(fps)
      if (renderScale !== undefined) state.background.setRenderScale?.(renderScale)
      if (staticMode !== undefined) state.background.setStaticMode?.(staticMode)
    }

    document.body.style.background = (isCssBg && options.cssProperty) ? options.cssProperty : 'transparent'
  } catch (e) {
    log(`configureLyricBackground error: ${(e as Error).message}`, 'error')
  }
}

// --- CSS 变量设置项 (CSS Variable Setters) ---
window.setLyricSizePreset = (preset: string) => { if (preset !== undefined) setCSSVar('--amll-lp-font-size-preset', preset) }
window.setEnableTranslationLine = (enabled: boolean) => { if (enabled !== undefined) setCSSVar('--amll-show-translation', enabled) }
window.setEnableRomanLine = (enabled: boolean) => { if (enabled !== undefined) setCSSVar('--amll-show-roman', enabled) }
window.setEnableSwapTransRomanLine = (enabled: boolean) => { if (enabled !== undefined) setCSSVar('--amll-swap-trans-roman', enabled) }
window.setAdvanceLyricDynamicLyricTime = (enabled: boolean) => {
  if (enabled !== undefined) {
    setCSSVar('--amll-advance-dynamic-time', enabled)
    state.pendingLyricOptions.advanceDynamicTime = enabled
  }
}

// --- 其他占位或转发接口 ---
window.setRenderMode = (mode: string) => log(`setRenderMode: ${mode}`, 'debug')
window.setLyricPlayerImplementation = (imp: string) => log(`setLyricPlayerImplementation: ${imp}`, 'debug')
window.rebuildLyricsDom = (reason?: string) => forceRebuildLyricsDom(reason)