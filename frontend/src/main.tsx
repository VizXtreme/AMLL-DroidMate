import React, { useEffect, useRef, useState } from 'react'
import { createRoot } from 'react-dom/client'
import { LyricPlayer, BackgroundRender } from '@applemusic-like-lyrics/react'
import type { LyricPlayerRef } from '@applemusic-like-lyrics/react'
import '@applemusic-like-lyrics/react-full/style.css'
import './styles.css'
import { useAtom, useSetAtom } from 'jotai'
import {
  musicLyricLinesAtom,
  musicPlayingPositionAtom,
  musicCoverAtom,
  musicPlayingAtom,
  lowFreqVolumeAtom,
  isLyricPageOpenedAtom
} from '@applemusic-like-lyrics/react-full'

// --- existing constants unchanged ---
const PLAYER_BACKGROUND = 'transparent'
const SEEK_THRESHOLD_MS = 900
const SEEK_HOLD_MS = 180
const DEFAULT_FONT_STACK = '"SF Pro Display", "PingFang SC", system-ui, -apple-system, "Segoe UI", sans-serif'
const DYNAMIC_FONT_STYLE_ID = 'amll-dynamic-font-face-style'

interface QualityProfile {
  alignAnchor: string
  alignPosition: number
  enableSpring: boolean
  enableScale: boolean
  enableBlur: boolean
  hidePassedLines: boolean
  wordFadeWidth: number
  linePosYSpringParams: {
    mass: number
    damping: number
    stiffness: number
  }
  lineScaleSpringParams: {
    mass: number
    damping: number
    stiffness: number
  }
}

const QUALITY_PROFILE: QualityProfile = {
  alignAnchor: 'center',
  alignPosition: 0.382,
  enableSpring: true,
  enableScale: true,
  enableBlur: true,
  hidePassedLines: false,
  wordFadeWidth: 0.5,
  linePosYSpringParams: {
    mass: 0.9,
    damping: 15,
    stiffness: 90,
  },
  lineScaleSpringParams: {
    mass: 2,
    damping: 25,
    stiffness: 100,
  },
}

const LITE_PROFILE: QualityProfile = {
  ...QUALITY_PROFILE,
  enableSpring: false,
  enableBlur: false,
  wordFadeWidth: 0.15,
}

interface BackgroundProfile {
  renderer: string
  fps: number
  flowSpeed: number
  renderScale: number
  staticMode: boolean
  lowFreqVolume: number
  hasLyric: boolean
}

const DEFAULT_BG_PROFILE: BackgroundProfile = {
  renderer: 'pixi',
  fps: 60,
  flowSpeed: 2.2,
  renderScale: 0.8,
  staticMode: false,
  lowFreqVolume: 1,
  hasLyric: true,
}

const TOUCH_BG_BLUR_CLASS = 'amll-touch-unblur'
const PAUSE_STYLE_CLASS = 'amll-paused'
const PLAYING_CLASS = 'playing'

// --- shared state & globals (moved to top to avoid TDZ errors) ---
interface BlurState {
  enabled: boolean
  timeoutId: ReturnType<typeof setTimeout> | null
  TIMEOUT_MS: number
}

interface TouchState {
  startX: number
  startY: number
  startTime: number
  isMoved: boolean
}

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
    words?: Array<{
      word?: string
      startTime?: number
      endTime?: number
    }>
    text?: string
    translatedLyric?: string
    romanLyric?: string
    startTime?: number
    endTime?: number
    isBG?: boolean
    isDuet?: boolean
  }>
}

interface FontFileItem {
  familyName: string
  uri: string
}

interface LineClickEvent {
  lineIndex?: number
  line?: {
    getLine?: () => { startTime?: number }
    startTime?: number
  }
  startTime?: number
}

// 禁用 SVG 相关的错误捕获，避免 Script error. 干扰
let svgErrorSuppressed = false

let state: {
  lyricLines: LyricLine[]
  currentTime: number
  isSeeking: boolean
  blur: BlurState
  touch: TouchState
} = {
  lyricLines: [],
  currentTime: 0,
  isSeeking: false,
  blur: {
    enabled: true,
    timeoutId: null,
    TIMEOUT_MS: 5000,
  },
  touch: {
    startX: 0,
    startY: 0,
    startTime: 0,
    isMoved: false,
  },
}

// Playback pause state is driven externally (e.g. from Kotlin).
let __playerPaused = false

const UNBLUR_STYLE_ID = 'amll-unblur-style'

function ensureUnblurStyle() {
  try {
    if (document.getElementById(UNBLUR_STYLE_ID)) return
    const s = document.createElement('style')
    s.id = UNBLUR_STYLE_ID
    s.textContent = `[class*="_lyricLine_"] .amll-line-unblur, [class*="_lyricLine_"].amll-line-unblur {
  filter: none !important;
}

/* Background-lyric lines should be hidden when not active (avoid blurred ghost text) */
[class*="_lyricBgLine_"]:not([class*="_active_"]) {
  opacity: 0 !important;
  visibility: hidden !important;
  pointer-events: none !important;
}`
    if (document.head) {
      document.head.appendChild(s)
    }
  } catch (error) {
    logToAndroid(`ensureUnblurStyle error: ${(error as Error)?.message || error}`, 'error')
  }
}

function applyPauseStyle() {
  const el = player?.getElement?.()
  if (!el) return
  el.classList.add(PAUSE_STYLE_CLASS)
  el.classList.remove(PLAYING_CLASS)
}

function cancelPauseStyle() {
  const el = player?.getElement?.()
  if (!el) return
  el.classList.remove(PAUSE_STYLE_CLASS)
  el.classList.add(PLAYING_CLASS)
}

function setPaused(paused: boolean) {
  const shouldPause = Boolean(paused)
  if (shouldPause === __playerPaused) return

  __playerPaused = shouldPause

  if (shouldPause) {
    applyPauseStyle()
    callPlayer('pause')
  } else {
    cancelPauseStyle()
    callPlayer('resume')
  }
}

let player: any = null
let rafId: number | null = null
let lastFrameTime = -1
let backgroundRender: any = null
let lastAlbumArt = ''
let currentProfile: QualityProfile = { ...QUALITY_PROFILE }
let currentBackgroundProfile: BackgroundProfile = { ...DEFAULT_BG_PROFILE }
let lastIncomingTime: number | null = null
let seekUntilTs = 0

// 初始化全局状态
function initGlobalState() {
  if (typeof window !== 'undefined') {
    if (!window.__amll) {
      window.__amll = {} as AMLLGlobal
    }
    Object.assign(window.__amll, {
      get player() { return player }, set player(v: any) { player = v },
      get rafId() { return rafId }, set rafId(v: number | null) { rafId = v },
      get lastFrameTime() { return lastFrameTime }, set lastFrameTime(v: number) { lastFrameTime = v },
      get backgroundRender() { return backgroundRender }, set backgroundRender(v: any) { backgroundRender = v },
      get lastAlbumArt() { return lastAlbumArt }, set lastAlbumArt(v: string) { lastAlbumArt = v },
      get currentProfile() { return currentProfile }, set currentProfile(v: QualityProfile) { currentProfile = v },
      get currentBackgroundProfile() { return currentBackgroundProfile }, set currentBackgroundProfile(v: BackgroundProfile) { currentBackgroundProfile = v },
      get state() { return state }, set state(v: typeof state) { state = v },
    })
  }
}

interface AMLLGlobal {
  player: any
  rafId: number | null
  lastFrameTime: number
  backgroundRender: any
  lastAlbumArt: string
  currentProfile: QualityProfile
  currentBackgroundProfile: BackgroundProfile
  state: typeof state
}

declare global {
  interface Window {
    __amll?: AMLLGlobal
    setPaused?: (paused: boolean) => void
    toWordEntries?: (line: any) => WordEntry[]
    updateLyrics?: (payload: LyricsPayload) => void
    updateAlbumArt?: (uri: string) => Promise<void>
    updateTime?: (timeMs: number) => void
    configureLyricMotion?: (options: Partial<QualityProfile>) => void
    setBlurEnabled?: (enabled: boolean) => void
    setBlurTimeout?: (timeMs: number) => void
    setBackgroundRenderer?: (mode: string) => void
    updateLowFreqVolume?: (value: number) => void
    configureBackgroundEffect?: (options: Partial<BackgroundProfile>) => void
    logFromKotlin?: (message: string) => void
    setFontSettings?: (
      fontFamily: string,
      activeFontFamilyNames?: string[],
      fontFiles?: FontFileItem[]
    ) => void
    setRenderMode?: (mode: string) => void
    Android?: {
      log?: (message: string, level: string) => void
      isPlaying?: () => boolean
      onLineClick?: (index: number, startTime: number) => void
    }
  }
}

function amllGet(name: string): any {
  return window.__amll ? window.__amll[name] : undefined
}

function amllSet(name: string, value: any) {
  if (!window.__amll) window.__amll = {}
  window.__amll[name] = value
  return value
}

function logToAndroid(message: string, level: string = 'debug') {
  if (typeof window !== 'undefined') {
    if (typeof window.Android !== 'undefined' && typeof window.Android.log === 'function') {
      try {
        window.Android.log(message, level)
      } catch (e) {
        console.log(`[ANDROID_LOG_ERROR] ${message}`)
      }
    } else {
      console.log(`[${level.toUpperCase()}] ${message}`)
    }
  }
}

function stripLeadingBgBracket(text: string): string {
  return String(text ?? '').replace(/^\s*[\(（]\s*/, '')
}

function stripTrailingBgBracket(text: string): string {
  return String(text ?? '').replace(/\s*[\)）]\s*$/, '')
}

function toWordEntries(line: any): WordEntry[] {
  if (Array.isArray(line?.words) && line.words.length > 0) {
    const mapped = line.words.map((word: any) => ({
      word: String(word?.word ?? ''),
      startTime: Number(word?.startTime ?? line?.startTime ?? 0),
      endTime: Number(word?.endTime ?? line?.endTime ?? line?.startTime ?? 0),
    }))

    const normalized = mapped.map((word: WordEntry) => {
      const startTime = Number.isFinite(word.startTime) ? word.startTime : 0
      const endTime = Number.isFinite(word.endTime) ? word.endTime : startTime
      return {
        ...word,
        startTime,
        endTime: Math.max(startTime, endTime),
      }
    })

    if (line?.isBG && normalized.length > 0) {
      logToAndroid(`Processing background lyrics with ${normalized.length} words`, 'debug')
      
      const firstWord = normalized[0]
      const originalFirst = firstWord.word
      firstWord.word = stripLeadingBgBracket(firstWord.word)
      if (firstWord.word !== originalFirst) {
        logToAndroid(`Removed leading bracket from first word: "${originalFirst}" -> "${firstWord.word}"`, 'debug')
      } else {
        logToAndroid(`First word unchanged after bracket strip: "${originalFirst}"`, 'debug')
      }

      const lastWord = normalized[normalized.length - 1]
      const originalLast = lastWord.word
      lastWord.word = stripTrailingBgBracket(lastWord.word)
      if (lastWord.word !== originalLast) {
        logToAndroid(`Removed trailing bracket from last word: "${originalLast}" -> "${lastWord.word}"`, 'debug')
      } else {
        logToAndroid(`Last word unchanged after bracket strip: "${originalLast}"`, 'debug')
      }

      const afterText = normalized.map((w) => w.word).join('')
      logToAndroid(`BG words after strip: "${afterText}"`, 'debug')

      for (let i = normalized.length - 1; i >= 0; i -= 1) {
        if (String(normalized[i].word ?? '').length === 0) {
          normalized.splice(i, 1)
        }
      }

      if (normalized.length === 0) {
        normalized.push({
          word: ' ',
          startTime: Number(line?.startTime ?? 0),
          endTime: Number(line?.endTime ?? line?.startTime ?? 0),
        })
      }
    }

    return normalized
  }

  const lineText = String(line?.text ?? '').trim()
  return [
    {
      word: lineText.length > 0 ? lineText : ' ',
      startTime: Number(line?.startTime ?? 0),
      endTime: Number(line?.endTime ?? line?.startTime ?? 0),
    },
  ]
}

function normalizeLyricLines(lines: any[]): LyricLine[] {
  if (!Array.isArray(lines)) return []

  return lines.map((line) => {
    const words = toWordEntries(line)
    const wordStart = words.length > 0 ? words[0].startTime : Number(line?.startTime ?? 0)
    const wordEnd = words.length > 0 ? words[words.length - 1].endTime : Number(line?.endTime ?? wordStart)
    const startTime = Number(line?.startTime ?? wordStart)
    const endTime = Number(line?.endTime ?? wordEnd)

    const result: LyricLine = {
      words,
      translatedLyric: String(line?.translatedLyric ?? ''),
      romanLyric: String(line?.romanLyric ?? ''),
      startTime: Number.isFinite(startTime) ? startTime : 0,
      endTime: Number.isFinite(endTime) ? endTime : 0,
      isBG: !!line?.isBG,
      isDuet: !!line?.isDuet,
    }

    if (Number.isFinite(result.startTime) && Number.isFinite(result.endTime)) {
      return result
    }

    return {
      words: toWordEntries(line),
      translatedLyric: String(line?.translatedLyric ?? ''),
      romanLyric: String(line?.romanLyric ?? ''),
      startTime: Number.isFinite(result.startTime) ? result.startTime : 0,
      endTime: Number.isFinite(result.endTime) ? result.endTime : 0,
      isBG: !!line?.isBG,
      isDuet: !!line?.isDuet,
    }
  })
}

function callBackground(methodName: string, ...args: any[]) {
  logToAndroid(`callBackground(${methodName}) called`, 'debug')
}

function applyBackgroundProfile(profile: Partial<BackgroundProfile>) {
  currentBackgroundProfile = {
    ...currentBackgroundProfile,
    ...profile,
  }
  logToAndroid(`applyBackgroundProfile called with: ${JSON.stringify(profile)}`, 'debug')
  
  // 实际应用配置到 backgroundRender
  if (backgroundRender) {
    try {
      if (profile.flowSpeed !== undefined && typeof backgroundRender.setFlowSpeed === 'function') {
        backgroundRender.setFlowSpeed(profile.flowSpeed)
      }
      if (profile.renderScale !== undefined && typeof backgroundRender.setRenderScale === 'function') {
        backgroundRender.setRenderScale(profile.renderScale)
      }
      if (profile.lowFreqVolume !== undefined && typeof backgroundRender.setLowFreqVolume === 'function') {
        backgroundRender.setLowFreqVolume(profile.lowFreqVolume)
      }
    } catch (error) {
      logToAndroid(`applyBackgroundProfile error: ${(error as Error)?.message || error}`, 'error')
    }
  }
}

function rebuildBackgroundRender() {
  logToAndroid('rebuildBackgroundRender called', 'debug')
  
  // 如果已经有 backgroundRender，先销毁
  if (backgroundRender) {
    try {
      backgroundRender.dispose()
      backgroundRender = null
      logToAndroid('Previous backgroundRender disposed', 'debug')
    } catch (error) {
      logToAndroid(`Dispose backgroundRender error: ${(error as Error)?.message || error}`, 'error')
    }
  }
  
  // 重新创建背景渲染器
  try {
    const bgElement = document.querySelector('.amll-background-render')
    if (bgElement && lastAlbumArt) {
      logToAndroid('Creating new BackgroundRender instance', 'debug')
      // BackgroundRender 会通过 React 组件自动创建
    }
  } catch (error) {
    logToAndroid(`rebuildBackgroundRender error: ${(error as Error)?.message || error}`, 'error')
  }
}

function callPlayer(methodName: string, ...args: any[]) {
  logToAndroid(`callPlayer(${methodName}) called with args: ${JSON.stringify(args)}`, 'debug')
  
  // 实际调用 player 对象的方法
  if (player && typeof player[methodName] === 'function') {
    try {
      player[methodName](...args)
    } catch (error) {
      logToAndroid(`callPlayer ${methodName} error: ${(error as Error)?.message || error}`, 'error')
    }
  }
}

function applyMotionProfile(profile: QualityProfile) {
  currentProfile = { ...profile }
  if (window.__amll) window.__amll.currentProfile = currentProfile
  logToAndroid(`applyMotionProfile called with: ${JSON.stringify(profile)}`, 'debug')
}

function resetBlurTimeout() {
  if (state.blur.timeoutId !== null) {
    clearTimeout(state.blur.timeoutId)
  }
  
  state.blur.timeoutId = setTimeout(() => {
    if (player && state.blur.enabled === false) {
      callPlayer('setEnableBlur', true)
      state.blur.enabled = true
      player.getElement?.().classList.remove(TOUCH_BG_BLUR_CLASS)

      cancelPauseStyle()
      logToAndroid('Blur restored after 5s inactivity', 'info')

      document.querySelectorAll('.amll-line-unblur').forEach(el => el.classList.remove('amll-line-unblur'))
    }
    state.blur.timeoutId = null
  }, state.blur.TIMEOUT_MS)
}

function handleTouchStart(e: TouchEvent) {
  const touch = e?.touches?.[0]
  state.touch.startX = touch?.clientX ?? 0
  state.touch.startY = touch?.clientY ?? 0
  state.touch.startTime = Date.now()
  state.touch.isMoved = false

  if (player && state.blur.enabled === true) {
    callPlayer('setEnableBlur', false)
    state.blur.enabled = false
    player.getElement?.().classList.add(TOUCH_BG_BLUR_CLASS)
    logToAndroid('Blur disabled on touch, keep BG blurred', 'info')
  }

  applyPauseStyle()

  try {
    const x = touch?.clientX ?? state.touch.startX
    const y = touch?.clientY ?? state.touch.startY
    const el = document.elementFromPoint(x, y)
    const lineEl = el?.closest ? el.closest('[class*="_lyricLine_"]') : null
    if (lineEl) {
      lineEl.classList.add('amll-line-unblur')
    }
  } catch (_ignored) {}

  resetBlurTimeout()
}

function handleTouchMove(e: TouchEvent) {
  const moveX = Math.abs((e?.touches?.[0]?.clientX ?? 0) - state.touch.startX)
  const moveY = Math.abs((e?.touches?.[0]?.clientY ?? 0) - state.touch.startY)
  
  if (moveX > 10 || moveY > 10) {
    state.touch.isMoved = true
  }

  if (state.blur.timeoutId !== null) {
    clearTimeout(state.blur.timeoutId)
  }
  resetBlurTimeout()
}

function handleTouchEnd(e: TouchEvent) {
  const touchDuration = Date.now() - state.touch.startTime
  
  if (!state.touch.isMoved && touchDuration < 300) {
    const x = e?.changedTouches?.[0]?.clientX ?? state.touch.startX
    const y = e?.changedTouches?.[0]?.clientY ?? state.touch.startY
    
    logToAndroid(`Tap detected at coordinates (${x}, ${y}), duration=${touchDuration}ms`, 'info')
    
    try {
      const element = document.elementFromPoint(x, y)
      if (element) {
        logToAndroid(`Clicked element: ${element.tagName}, class=${element.className}`, 'info')
        
        let lyricLine = element.closest('._lyricLine_1vq69_6, ._lyricLine_1ygrf_6')
        if (!lyricLine) {
          lyricLine = element.closest('[class*="lyric"]')
        }
        
        if (lyricLine) {
          logToAndroid('Found lyric line element', 'info')
          lyricLine.click?.()
          
          const clickEvent = new MouseEvent('click', {
            bubbles: true,
            cancelable: true,
            view: window
          })
          lyricLine.dispatchEvent(clickEvent)
          logToAndroid('Dispatched click event', 'info')
        }
      }
    } catch (error) {
      logToAndroid(`${(error as Error)?.message || error}`, 'error')
    }
  }
}

function markSeeking(now: number) {
  state.isSeeking = true
  seekUntilTs = now + SEEK_HOLD_MS
  callPlayer('setIsSeeking', true)
}

function updateSeekingStateFromTime(now: number, nextTimeMs: number) {
  if (!Number.isFinite(nextTimeMs)) return

  if (lastIncomingTime == null) {
    lastIncomingTime = nextTimeMs
    return
  }

  const diff = Math.abs(nextTimeMs - lastIncomingTime)
  lastIncomingTime = nextTimeMs
  if (diff >= SEEK_THRESHOLD_MS) {
    markSeeking(now)
  }
}

function settleSeekingIfNeeded(now: number) {
  if (!state.isSeeking) return
  if (now < seekUntilTs) return
  state.isSeeking = false
  callPlayer('setIsSeeking', false)
}

function applyPlayerStyle(element: HTMLElement) {
  element.style.width = '100%'
  element.style.height = 'auto'
  element.style.background = PLAYER_BACKGROUND
  element.style.mixBlendMode = 'normal'
  element.style.color = '#f5f7ff'
  element.style.setProperty('--amll-lp-font-family', `var(--amll-user-font-family, ${DEFAULT_FONT_STACK})`)
  element.style.fontFamily = 'var(--amll-lp-font-family)'
  element.style.fontWeight = '700'
  element.style.setProperty('--amll-lp-color', '#f5f7ff')
  element.style.setProperty('--amll-lp-bg-color', 'rgba(0, 0, 0, 0.28)')
  element.style.setProperty('--amll-lp-hover-bg-color', 'rgba(255, 255, 255, 0.12)')
  element.style.setProperty('--amll-lp-font-size', 'clamp(30px, 4vh, 36px)')
  element.style.setProperty('--amll-touch-bg-blur', '10px')
}

function escapeCssString(value: string): string {
  return String(value ?? '').replace(/\\/g, '\\\\').replace(/"/g, '\\"')
}

function setFontSettings(
  fontFamily: string,
  activeFontFamilyNames: string[] = [],
  fontFiles: FontFileItem[] = []
) {
  const fallbackFamily = String(fontFamily || DEFAULT_FONT_STACK)
  const enabledFamilies = (Array.isArray(activeFontFamilyNames)
    ? activeFontFamilyNames
    : [activeFontFamilyNames]
  )
    .map((name) => String(name || '').trim())
    .filter((name) => name.length > 0)
    .sort((a, b) => a.localeCompare(b, 'en', { sensitivity: 'base' }))

  const effectiveFamily = enabledFamilies.length > 0
    ? `${enabledFamilies.map((name) => `"${name}"`).join(', ')}, ${fallbackFamily}`
    : fallbackFamily

  let styleTag = document.getElementById(DYNAMIC_FONT_STYLE_ID)
  if (!styleTag) {
    styleTag = document.createElement('style')
    styleTag.id = DYNAMIC_FONT_STYLE_ID
    document.head.appendChild(styleTag)
  }

  const css = (Array.isArray(fontFiles) ? fontFiles : [])
    .filter((item) => item && item.familyName && item.uri && !item.uri.startsWith('data:image/svg+xml'))
    .map((item) => `@font-face{font-family:"${escapeCssString(item.familyName)}";src:url("${escapeCssString(item.uri)}");font-display:swap;}`)
    .join('')
  styleTag.textContent = css

  document.documentElement.style.setProperty('--amll-user-font-family', effectiveFamily)
  document.documentElement.style.setProperty('--amll-lp-font-family', 'var(--amll-user-font-family)')

  if (player) {
    const el = player.getElement?.()
    if (el) {
      el.style.setProperty('--amll-lp-font-family', 'var(--amll-user-font-family)')
      el.style.fontFamily = 'var(--amll-lp-font-family)'
    }
  }
}

function animationFrameLoop() {
  try {
    const now = performance.now()
    const delta = lastFrameTime === -1 ? 0 : now - lastFrameTime
    lastFrameTime = now

    settleSeekingIfNeeded(now)

    if (typeof window.Android !== 'undefined' && typeof window.Android.isPlaying === 'function') {
      try {
        setPaused(!Boolean(window.Android.isPlaying()))
      } catch (_err) {
        // ignore
      }
    }
  } catch (error) {
    logToAndroid(`update loop error: ${(error as Error)?.message || error}`, 'error')
  }

  rafId = window.requestAnimationFrame(animationFrameLoop)
}

function startAnimationLoop() {
  if (rafId != null) {
    window.cancelAnimationFrame(rafId)
    rafId = null
  }
  lastFrameTime = -1
  rafId = window.requestAnimationFrame(animationFrameLoop)
}

// 全局 API 设置函数 - 在 DOMContentLoaded 中调用
function setupGlobalAPI() {
  if (typeof window === 'undefined') return
  
  // 挂载所有 API 函数
  window.setRenderMode = function (mode: string) {
    const normalizedMode = String(mode ?? '').toLowerCase()
    if (normalizedMode === 'dom-lite') {
      applyMotionProfile(LITE_PROFILE)
      logToAndroid('setRenderMode(dom-lite) -> lite profile applied', 'info')
      return
    }

    applyMotionProfile(QUALITY_PROFILE)
    logToAndroid(`setRenderMode(${mode}) -> quality profile applied`, 'info')
  }

  ;(window as any).updateLyrics = function (lyricsPayload: LyricsPayload) {
    try {
      // 检查 __setLyricLines 是否已经挂载（需要等待 App 组件初始化）
      if (typeof (window as any).__setLyricLines !== 'function') {
        logToAndroid('updateLyrics called but __setLyricLines not ready yet', 'warn')
        // 将歌词数据暂存，等 __setLyricLines 准备好后再处理
        const pendingPayload = lyricsPayload
        setTimeout(() => {
          if (typeof (window as any).__setLyricLines === 'function') {
            logToAndroid('Retrying updateLyrics with pending payload', 'debug')
            ;(window as any).updateLyrics(pendingPayload)
          } else {
            logToAndroid('__setLyricLines still not available after retry', 'error')
          }
        }, 100)
        return
      }

      const rawLines = Array.isArray(lyricsPayload?.lines) ? lyricsPayload.lines : []
      
      const bgLines = rawLines.filter(line => line?.isBG)
      if (bgLines.length > 0) {
        logToAndroid(`Received ${bgLines.length} BG lines from backend`, 'debug')
        bgLines.slice(0, 3).forEach((line: any, idx: number) => {
          logToAndroid(`Raw BG line ${idx}: text="${line?.text}" translation="${line?.translatedLyric}" words=${line?.words?.length || 0}`, 'debug')
        })
      }
      
      const normalizedLines = normalizeLyricLines(rawLines)

      if (normalizedLines.length > 0) {
        normalizedLines.slice(0, 3).forEach((ln: LyricLine, idx: number) => {
          const txt = ln.words.map(w => w.word).join('')
          logToAndroid(`normalized line ${idx}: text="${txt}" len=${ln.words.length}`, 'debug')
        })
      } else {
        logToAndroid('normalizeLyricLines produced 0 lines', 'warn')
      }
      logToAndroid(`lyricsPayload lines count=${rawLines.length}`, 'debug')

      if (normalizedLines.length === 0) {
        logToAndroid('injecting placeholder lyric because none provided', 'debug')
        ;(window as any).__setLyricLines([
          { words: [{word:'Demo',startTime:0,endTime:2000}],translatedLyric:'',romanLyric:'',startTime:0,endTime:2000,isBG:false,isDuet:false }
        ])
        logToAndroid('DEBUG: Called __setLyricLines with placeholder', 'debug')
      } else {
        logToAndroid(`DEBUG: About to call __setLyricLines with ${normalizedLines.length} lines`, 'debug')
        logToAndroid(`DEBUG: __setLyricLines is ${typeof (window as any).__setLyricLines}`, 'debug')
        ;(window as any).__setLyricLines(normalizedLines)
        logToAndroid('DEBUG: __setLyricLines called successfully', 'debug')
      }

      logToAndroid(`Updated lyrics (${normalizedLines.length} lines)`, 'info')
    } catch (error) {
      logToAndroid(`updateLyrics error: ${(error as Error)?.message || error}`, 'error')
    }
  }

  ;(window as any).updateAlbumArt = async function (albumUri: string) {
    try {
      const uri = String(albumUri ?? '').trim()
      if (uri.length === 0 || uri === amllGet('lastAlbumArt')) return

      lastAlbumArt = uri
      amllSet('lastAlbumArt', uri)
      logToAndroid('Background album art updated', 'info')
    } catch (error) {
      logToAndroid(`updateAlbumArt error: ${(error as Error)?.message || error}`, 'error')
    }
  }

  ;(window as any).updateTime = function (timeMs: number) {
    const now = performance.now()
    const parsedTime = Number(timeMs)
    const st = amllGet('state') || state
    st.currentTime = Number.isFinite(parsedTime) ? parsedTime : 0
    updateSeekingStateFromTime(now, st.currentTime)
    
    // 直接更新 Jotai state，这样 LyricPlayer 组件会收到最新的 currentTime
    // 注意：这个函数在 setupGlobalAPI 中定义，此时 App 组件可能还未初始化
    // 所以通过全局的 __setCurrentTime 来更新
    if (typeof (window as any).__setCurrentTime === 'function') {
      try {
        (window as any).__setCurrentTime(st.currentTime)
      } catch (error) {
        logToAndroid(`__setCurrentTime error: ${(error as Error)?.message || error}`, 'error')
      }
    }
    
    // 同时调用 player 的方法（如果 player 已初始化）
    if (player) {
      const currentTime = Math.trunc(st.currentTime)
      callPlayer('setCurrentTime', currentTime, state.isSeeking)
      callPlayer('update', 0)
    }
  }

  ;(window as any).configureLyricMotion = function (options: Partial<QualityProfile>) {
    if (!options || typeof options !== 'object') return
    const merged: QualityProfile = {
      ...currentProfile,
      ...options,
      linePosYSpringParams: {
        ...currentProfile.linePosYSpringParams,
        ...(options.linePosYSpringParams || {}),
      },
      lineScaleSpringParams: {
        ...currentProfile.lineScaleSpringParams,
        ...(options.lineScaleSpringParams || {}),
      },
    }
    applyMotionProfile(merged)
    logToAndroid('configureLyricMotion applied', 'info')
  }

  ;(window as any).setBlurEnabled = function (enabled: boolean) {
    const shouldEnable = Boolean(enabled)
    if (player && state.blur.enabled !== shouldEnable) {
      callPlayer('setEnableBlur', shouldEnable)
      state.blur.enabled = shouldEnable
      logToAndroid(`setBlurEnabled(${shouldEnable})`, 'info')
      
      if (shouldEnable && state.blur.timeoutId !== null) {
        clearTimeout(state.blur.timeoutId)
        state.blur.timeoutId = null
      }
    }
  }

  ;(window as any).setBlurTimeout = function (timeMs: number) {
    const ms = Number(timeMs)
    if (Number.isFinite(ms) && ms > 0) {
      state.blur.TIMEOUT_MS = ms
      logToAndroid(`Blur timeout set to ${ms}ms`, 'info')
    }
  }

  ;(window as any).setBackgroundRenderer = function (mode: string) {
    const normalized = String(mode ?? '').toLowerCase()
    const renderer = normalized === 'mesh' ? 'mesh' : 'pixi'
    if (renderer === currentBackgroundProfile.renderer && backgroundRender) {
      logToAndroid(`setBackgroundRenderer(${renderer}) skipped (no change)`, 'info')
      return
    }

    currentBackgroundProfile = {
      ...currentBackgroundProfile,
      renderer,
    }
    rebuildBackgroundRender()
    logToAndroid(`setBackgroundRenderer(${renderer}) applied`, 'info')
  }

  ;(window as any).updateLowFreqVolume = function (value: number) {
    const parsed = Number(value)
    const clamped = Number.isFinite(parsed) ? Math.max(0, Math.min(1, parsed)) : 1
    applyBackgroundProfile({ lowFreqVolume: clamped })
  }

  ;(window as any).configureBackgroundEffect = function (options: Partial<BackgroundProfile>) {
    if (!options || typeof options !== 'object') return

    const base = amllGet('currentBackgroundProfile') || currentBackgroundProfile
    const next: BackgroundProfile = {
      ...base,
      ...options,
    } as BackgroundProfile
    if (typeof next.renderer === 'string') {
      next.renderer = next.renderer.toLowerCase() === 'mesh' ? 'mesh' : 'pixi'
    } else {
      next.renderer = base.renderer
    }

    const rendererChanged = next.renderer !== base.renderer
    currentBackgroundProfile = next
    amllSet('currentBackgroundProfile', next)

    if (rendererChanged) {
      rebuildBackgroundRender()
    } else {
      applyBackgroundProfile(currentBackgroundProfile)
    }
    logToAndroid('configureBackgroundEffect applied', 'info')
  }

  ;(window as any).logFromKotlin = function (message: string) {
    logToAndroid(message, 'debug')
  }
}

// 全局错误处理 - 使用正确的类型
const globalErrorHandler = (event: ErrorEvent | Event | string, source?: string, lineno?: number, colno?: number) => {
  try {
    // 如果是字符串（Script error.），直接记录
    if (typeof event === 'string') {
      // 只对纯 "Script error." 进行降级处理，其他都记录
      if (event === 'Script error.') {
        logToAndroid(`Cross-origin script error (expected): ${event}`,'debug');
        return;
      }
      logToAndroid(`Uncaught JS error: ${event}`,'error');
      if (event.includes('SVG') || event.includes('mask')) {
        logToAndroid('Note: This may be related to SVG/mask operations, usually harmless','debug');
      }
      return;
    }
    
    // 检查是否是 ErrorEvent
    const errorEvent = event as ErrorEvent;
    if (errorEvent.message) {
      const msg = errorEvent.message;
      
      // 忽略空消息或纯 Script error.
      if (!msg || msg === 'Script error.') {
        logToAndroid(`Empty/cross-origin error (ignored)`,'debug');
        return;
      }
      
      logToAndroid(`Uncaught JS: ${msg} at ${errorEvent.filename || source || 'unknown'}:${errorEvent.lineno || lineno || 0}:${errorEvent.colno || colno || 0}`,'error');
      if (errorEvent.error) {
        logToAndroid(`Error stack: ${errorEvent.error.stack || errorEvent.error.toString()}`,'error');
      }
      
      // 对 SVG/mask 相关错误添加说明
      if (msg.toLowerCase().includes('svg') || msg.toLowerCase().includes('mask')) {
        logToAndroid('Note: SVG/mask errors are usually cosmetic in WebView environment','debug');
      }
    } else {
      // 处理普通 Event 对象（如资源加载失败）
      const target = (event as any).target || (event as any).srcElement;
      const targetType = target ? target.tagName || 'unknown' : 'none';
      const targetSrc = target ? (target.src || target.href || 'no-src') : 'none';
      const eventType = event.type || 'unknown';
      
      logToAndroid(`Resource load error: type=${eventType}, target=${targetType}, src=${targetSrc}`,'error');
    }
  } catch (e) {
    logToAndroid(`Error handler exception: ${(e as Error).message}`,'error');
  }
};

window.onerror = function(msg, url, line, col, error) {
  // 忽略纯 "Script error." 字符串（通常是跨域脚本的噪音）
  const msgStr = String(msg || '');
  if (msgStr === 'Script error.' && !url && !line && !col) {
    logToAndroid('Ignored cross-origin script error noise','debug');
    return true;
  }
  globalErrorHandler(msg || 'Unknown error', url, line, col);
  return true; // Prevent default browser error handling
};
window.addEventListener('error', globalErrorHandler, true);

// 拦截可能触发 Script error 的 SVG 操作（仅记录，不拦截）
if (typeof document !== 'undefined') {
  try {
    const originalCreateElementNS = document.createElementNS;
    (document as any).createElementNS = function(namespaceURI: string | null, qualifiedName: string, options?: any) {
      // 如果是 SVG 命名空间，添加错误日志
      if (namespaceURI === 'http://www.w3.org/2000/svg') {
        try {
          return originalCreateElementNS.call(document, namespaceURI, qualifiedName, options);
        } catch (e) {
          // 记录但不阻止错误，让上层决定如何处理
          logToAndroid(`SVG createElementNS error (non-blocking): ${(e as Error).message}`,'debug');
          throw e; // 重新抛出错误，不影响正常流程
        }
      }
      return originalCreateElementNS.call(document, namespaceURI, qualifiedName, options);
    };
  } catch (e) {
    // 如果拦截失败，继续正常使用原始方法
    logToAndroid(`Failed to wrap createElementNS: ${(e as Error).message}`,'debug');
  }
}

function App() {
  const playerRef = useRef<LyricPlayerRef>(null)
  const [lyricLines, setLyricLines] = useAtom(musicLyricLinesAtom)
  const [currentTime, setCurrentTime] = useAtom(musicPlayingPositionAtom)
  const [albumUri, setAlbumUri] = useAtom(musicCoverAtom)
  const [musicIsPlaying, setIsPlaying] = useAtom(musicPlayingAtom)
  const setLowFreqVolume = useSetAtom(lowFreqVolumeAtom)
  // 默认占位图（透明背景）
  const demoAlbumArt = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNDAwIiBoZWlnaHQ9IjQwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB3aWR0aD0iMTAwJSIgaGVpZ2h0PSIxMDAlIiBmaWxsPSJyZ2JhKDAsMCwwLDAuMSkiLz48L3N2Zz4='
  
  // 使用 ref 来保存最新的 lyricLines，避免闭包问题
  const lyricLinesRef = useRef(lyricLines)
  useEffect(() => {
    lyricLinesRef.current = lyricLines
  }, [lyricLines])

  // 初始化全局状态和接口
  useEffect(() => {
    initGlobalState()
    
    // 挂载全局 API
    ;(window as any).__setLyricLines = setLyricLines;
    (window as any).__setCurrentTime = setCurrentTime;
    (window as any).__setAlbumUri = setAlbumUri;
    
    // 暴露全局 API 函数
    setupGlobalAPI()
    
    return () => {
      delete (window as any).__setLyricLines;
      delete (window as any).__setCurrentTime;
      delete (window as any).__setAlbumUri;
    };
  }, [setLyricLines, setCurrentTime, setAlbumUri]);

  // 监听 lyricLines 变化
  useEffect(() => {
    if (lyricLines && lyricLines.length > 0) {
      logToAndroid(`Lyrics loaded: ${lyricLines.length} lines`, 'info');
    }
  }, [lyricLines]);

  useEffect(() => {
    window.__amll = window.__amll || {}
    Object.assign(window.__amll, {
      player: null,
      state: state,
      lastAlbumArt: '',
      currentProfile: { ...QUALITY_PROFILE }
    })

    if (typeof window.Android !== 'undefined' && window.Android?.log) {
      if (typeof window.Android.onLineClick === 'function') {
        logToAndroid('Android.onLineClick interface is ready', 'info')
      } else {
        logToAndroid('WARNING: Android.onLineClick interface NOT found', 'warn')
      }
    } else {
      logToAndroid('WARNING: Android interface NOT available', 'warn')
    }

    startAnimationLoop()
    
    // 清理定时器
    return () => {
      if (rafId != null) {
        window.cancelAnimationFrame(rafId)
        rafId = null
      }
    }

    ;(window as any).updateAlbumArt = async function (albumUri: string) {
      try {
        const uri = String(albumUri ?? '').trim()
        if (uri.length === 0 || uri === amllGet('lastAlbumArt')) return

        lastAlbumArt = uri
        amllSet('lastAlbumArt', uri)
        
        // 处理 file:// 协议的 URI，确保 WebView 能正确加载
        const processedUri = uri.startsWith('file://') ? uri : uri
        setAlbumUri(processedUri)
        logToAndroid(`Background album art updated: ${processedUri}`, 'info')
      } catch (error) {
        logToAndroid(`updateAlbumArt error: ${(error as Error)?.message || error}`, 'error')
      }
    }

    ;(window as any).updateTime = function (timeMs: number) {
      const now = performance.now()
      const parsedTime = Number(timeMs)
      const st = amllGet('state') || state
      st.currentTime = Number.isFinite(parsedTime) ? parsedTime : 0
      setCurrentTime(st.currentTime)
      updateSeekingStateFromTime(now, st.currentTime)
    }

    setIsPlaying(true)
    setLowFreqVolume(1)

    // 如果没有 Android 对象或者 Android 没有传递歌词，注入测试歌词
    if (typeof window.Android === 'undefined') {
      logToAndroid('no Android object, inserting demo lyric', 'debug')
      ;(window as any).updateLyrics({
        lines: [{
          words: [
            { word: 'Hello', startTime: 0, endTime: 2000 },
            { word: 'world', startTime: 2000, endTime: 4000 }
          ],
          startTime: 0,
          endTime: 4000,
          translatedLyric: '',
          romanLyric: '',
          isBG: false,
          isDuet: false
        }]
      })
    } else {
      // Android 对象存在，但等待 2 秒后检查是否收到了歌词
      setTimeout(() => {
        if (lyricLines.length === 0) {
          logToAndroid('Android exists but no lyrics received, injecting demo lyric', 'warn')
          ;(window as any).updateLyrics({
            lines: [
              {
                words: [
                  { word: '测试', startTime: 0, endTime: 1500 },
                  { word: '歌词', startTime: 1500, endTime: 3000 }
                ],
                startTime: 0,
                endTime: 3000,
                translatedLyric: '',
                romanLyric: '',
                isBG: false,
                isDuet: false
              },
              {
                words: [
                  { word: '第二行', startTime: 3000, endTime: 4500 },
                  { word: '歌词', startTime: 4500, endTime: 6000 }
                ],
                startTime: 3000,
                endTime: 6000,
                translatedLyric: '',
                romanLyric: '',
                isBG: false,
                isDuet: false
              }
            ]
          })
        } else {
          logToAndroid(`Android sent lyrics: ${lyricLines.length} lines`, 'info')
        }
      }, 2000)
    }

    return () => {
      if (rafId != null) {
        window.cancelAnimationFrame(rafId)
        rafId = null
      }
    }
  }, [setLyricLines, setCurrentTime, setAlbumUri, setIsPlaying, setLowFreqVolume])

  const handleLineClick = (event: LineClickEvent) => {
    try {
      const lineIndex = Number(event?.lineIndex ?? -1)
      const line = event?.line
      let startTime = 0
      
      if (line && typeof line.getLine === 'function') {
        const lineData = line.getLine()
        startTime = Math.trunc(Number(lineData?.startTime ?? 0))
        logToAndroid(`Line ${lineIndex} found via getLine(), startTime=${startTime}ms`, 'debug')
      } else if (line?.startTime !== undefined) {
        startTime = Math.trunc(Number(line.startTime))
        logToAndroid(`Line ${lineIndex} found via direct property, startTime=${startTime}ms`, 'debug')
      } else if (event?.startTime !== undefined) {
        startTime = Math.trunc(Number(event.startTime))
        logToAndroid(`Line ${lineIndex} found via evt.startTime, startTime=${startTime}ms`, 'debug')
      } else {
        logToAndroid(`Line ${lineIndex} clicked but startTime not found, using 0`, 'debug')
      }
      
      if (typeof window.Android !== 'undefined' && typeof window.Android.onLineClick === 'function') {
        try {
          window.Android.onLineClick(lineIndex, startTime)
          logToAndroid(`Called Android.onLineClick(${lineIndex}, ${startTime})`, 'info')
        } catch (e) {
          logToAndroid(`Android.onLineClick error: ${(e as Error)?.message || e}`, 'error')
        }
      } else {
        logToAndroid('Android.onLineClick not available', 'warn')
      }
    } catch (error) {
      logToAndroid(`line-click handler exception: ${(error as Error)?.message || error}`, 'error')
    }
  }

  return (
    <div id="app" style={{ position: 'relative', width: '100%', height: '100vh' }}>
      <BackgroundRender
        src={albumUri || demoAlbumArt}
        style={{ position: 'absolute', inset: 0, zIndex: 0 }}
        onError={(err) => logToAndroid(`BackgroundRender error: ${err}`, 'error')}
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
        onLyricLineClick={(evt) => {
          const lineData = evt.line.getLine();
          const startTime = Math.trunc(Number(lineData?.startTime ?? 0));
          const lineIndex = -1; // 简化处理
          
          if (typeof window.Android !== 'undefined' && typeof window.Android.onLineClick === 'function') {
            try {
              window.Android.onLineClick(lineIndex, startTime);
              logToAndroid(`Called Android.onLineClick(${lineIndex}, ${startTime})`, 'info');
            } catch (e) {
              logToAndroid(`Android.onLineClick error: ${(e as Error)?.message || e}`, 'error');
            }
          }
        }}
        style={{
          position: 'absolute',
          inset: 0,
          zIndex: 1,
          width: '100%',
          height: '100%',
        }}
      />
    </div>
  )
}

if (typeof window !== 'undefined') {
  window.addEventListener('DOMContentLoaded', () => {
    try {
      document.documentElement.style.background = 'transparent'
      document.body.style.background = 'transparent'
      
      // 全局调试函数 - 直接在控制台输出
      ;(window as any).debugAMLL = function() {
        logToAndroid('=== AMLL DEBUG START ===', 'debug');
        const appEl = document.getElementById('app');
        logToAndroid(`App element: ${appEl}`, 'debug');
        
        const allElements = document.querySelectorAll('*');
        logToAndroid(`Total elements: ${allElements.length}`, 'debug');
        
        // 查找所有包含 lyric 的元素
        const lyricElements = document.querySelectorAll('[class*="lyric"], [class*="Lyric"]');
        logToAndroid(`Lyric elements found: ${lyricElements.length}`, 'debug');
        lyricElements.forEach((el, idx) => {
          const style = window.getComputedStyle(el);
          logToAndroid(`  [${idx}] ${el.tagName} ${el.className} font-size:${style.fontSize} height:${style.height}`, 'debug');
        });
        
        // 查找 AMLL 播放器
        const amllPlayer = document.querySelector('.amll-lyric-player');
        logToAndroid(`AMLL Player: ${amllPlayer}`, 'debug');
        if (amllPlayer) {
          const style = window.getComputedStyle(amllPlayer);
          logToAndroid(`  font-size:${style.fontSize}`, 'debug');
          logToAndroid(`  --amll-lp-font-size:${style.getPropertyValue('--amll-lp-font-size')}`, 'debug');
          logToAndroid(`  height:${style.height}`, 'debug');
          logToAndroid(`  width:${style.width}`, 'debug');
        }
        
        logToAndroid('=== AMLL DEBUG END ===', 'debug');
      };
      
      // 3 秒后自动执行调试
      setTimeout(() => {
        logToAndroid('[AUTO] Running AMLL debug after 3s...', 'debug');
        (window as any).debugAMLL();
      }, 3000);
      
      const root = document.getElementById('app') || document.createElement('div')
      if (!document.getElementById('app')) {
        root.id = 'app'
        if (document.body) {
          document.body.appendChild(root)
        }
      }
      
      if (root) {
        createRoot(root).render(<App />)
      }

      const styleTag = document.createElement('style')
      styleTag.id = 'amll-touch-bg-blur-style'
      styleTag.textContent = `
        /* force the player to match viewport height and remove builtin vertical padding */
        .amll-lyric-player {
          padding-top: 0 !important;
          padding-bottom: 0 !important;
          height: 100vh !important;
          max-height: 100vh !important;
          overflow: hidden !important;
        }

        /* touch down: show background lyrics + dim the player, similar to pause */
        .amll-lyric-player.${PAUSE_STYLE_CLASS} {
          opacity: 0.85 !important;
        }

        /* when paused, make main lyrics fade so background lyrics can "expand" under them */
        .amll-lyric-player.${PAUSE_STYLE_CLASS} [class*="lyricLine"]:not([class*="lyricBgLine"]) {
          opacity: 0.85 !important;
        }

        .amll-lyric-player.${PAUSE_STYLE_CLASS} [class*="lyricBgLine"] {
          opacity: 0.85 !important;
          visibility: visible !important;
        }

        /* when touch interaction is ongoing, mimic pause style */
        .amll-lyric-player.${TOUCH_BG_BLUR_CLASS} {
          opacity: 1 !important;
        }

        .amll-lyric-player.${TOUCH_BG_BLUR_CLASS} [class*="lyricBgLine"] {
          opacity: 1 !important;
          visibility: visible !important;
          filter: none !important;
        }

        .amll-lyric-player.${TOUCH_BG_BLUR_CLASS} [class*="lyricBgLine"][class*="active"] {
          filter: none !important;
        }
      `
      if (document.head) {
        document.head.appendChild(styleTag)
      }
    } catch (error) {
      logToAndroid(`DOMContentLoaded error: ${(error as Error)?.message || error}`, 'error')
    }
  })

  window.addEventListener('beforeunload', () => {
    if (state.blur.timeoutId !== null) {
      clearTimeout(state.blur.timeoutId)
      state.blur.timeoutId = null
    }
    
    if (rafId != null) {
      window.cancelAnimationFrame(rafId)
      rafId = null
    }
    if (backgroundRender) {
      backgroundRender.dispose()
      backgroundRender = null
    }

    const styleTag = document.getElementById('amll-touch-bg-blur-style')
    styleTag?.remove()
  })
}