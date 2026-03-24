import React, { useEffect, useRef, useState } from 'react'
import { createRoot } from 'react-dom/client'
import { PrebuiltLyricPlayer } from '@applemusic-like-lyrics/react-full'
import { BackgroundRender } from '@applemusic-like-lyrics/react'
import '../../../applemusic-like-lyrics/packages/react-full/dist/amll-react-framework.css'
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

const QUALITY_PROFILE = {
  alignAnchor: 'center',
  // Align based on the active line's vertical center.
  // Target the “upper golden-ratio” point (≈38.2%) of the visible area.
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

const LITE_PROFILE = {
  ...QUALITY_PROFILE,
  enableSpring: false,
  enableBlur: false,
  wordFadeWidth: 0.15,
}

const DEFAULT_BG_PROFILE = {
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
let state = {
  lyricLines: [],
  currentTime: 0,
  isSeeking: false,
  blur: {
    enabled: true,
    timeoutId: null,
    TIMEOUT_MS: 5000, // 5秒
  },
  touch: {
    startX: 0,
    startY: 0,
    startTime: 0,
    isMoved: false,
  },
}

// Playback pause state is driven externally (e.g. from Kotlin).
// We do not automatically pause based on time updates.
let __playerPaused = false

// insert a small stylesheet rule that lets us quickly un‑blur a single
// line by adding the `amll-line-unblur` class. the core library already
// applies per-line blur but this gives us a way to override a touched
// line without turning off blur for the entire player.
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
    logToAndroid(`ensureUnblurStyle error: ${error?.message || error}`, 'error')
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

function setPaused(paused) {
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

window.setPaused = setPaused

let player = null
let rafId = null
let lastFrameTime = -1
let backgroundRender = null
let lastAlbumArt = ''
let currentProfile = { ...QUALITY_PROFILE }
let currentBackgroundProfile = { ...DEFAULT_BG_PROFILE }
let lastIncomingTime = null
let seekUntilTs = 0

// mirror important state on window so callbacks in the bundle can access them
window.__amll = window.__amll || {}
Object.assign(window.__amll, {
  get player() { return player }, set player(v) { player = v },
  get rafId() { return rafId }, set rafId(v) { rafId = v },
  get lastFrameTime() { return lastFrameTime }, set lastFrameTime(v) { lastFrameTime = v },
  get backgroundRender() { return backgroundRender }, set backgroundRender(v) { backgroundRender = v },
  get lastAlbumArt() { return lastAlbumArt }, set lastAlbumArt(v) { lastAlbumArt = v },
  get currentProfile() { return currentProfile }, set currentProfile(v) { currentProfile = v },
  get currentBackgroundProfile() { return currentBackgroundProfile }, set currentBackgroundProfile(v) { currentBackgroundProfile = v },
  get state() { return state }, set state(v) { state = v },
})

function amllGet(name){return window.__amll ? window.__amll[name] : undefined}

// helper to write to the shared global state object
function amllSet(name, value) {
  if (!window.__amll) window.__amll = {}
  window.__amll[name] = value
  return value
}


function logToAndroid(message, level = 'debug') {
  if (typeof Android !== 'undefined' && Android?.log) {
    Android.log(message, level)
  } else {
    console.log(`[${level.toUpperCase()}] ${message}`)
  }
}

function stripLeadingBgBracket(text) {
  return String(text ?? '').replace(/^\s*[\(（]\s*/, '')
}

function stripTrailingBgBracket(text) {
  return String(text ?? '').replace(/\s*[\)）]\s*$/, '')
}

function toWordEntries(line) {
  if (Array.isArray(line?.words) && line.words.length > 0) {
    const mapped = line.words.map((word) => ({
      word: String(word?.word ?? ''),
      startTime: Number(word?.startTime ?? line?.startTime ?? 0),
      endTime: Number(word?.endTime ?? line?.endTime ?? line?.startTime ?? 0),
    }))

    const normalized = mapped.map((word) => {
      const startTime = Number.isFinite(word.startTime) ? word.startTime : 0
      const endTime = Number.isFinite(word.endTime) ? word.endTime : startTime
      return {
        ...word,
        startTime,
        endTime: Math.max(startTime, endTime),
      }
    })

    // 背景歌词：去除第一个词开头的'('和最后一个词结尾的')'
    if (line?.isBG && normalized.length > 0) {
      logToAndroid(`Processing background lyrics with ${normalized.length} words`, 'debug')
      
      // 去除第一个词的开头括号
      const firstWord = normalized[0]
      const originalFirst = firstWord.word
      firstWord.word = stripLeadingBgBracket(firstWord.word)
      if (firstWord.word !== originalFirst) {
        logToAndroid(`Removed leading bracket from first word: "${originalFirst}" -> "${firstWord.word}"`, 'debug')
      } else {
        logToAndroid(`First word unchanged after bracket strip: "${originalFirst}"`, 'debug')
      }

      // 去除最后一个词的结尾括号
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

// ensure function visible globally for older ff references
window.toWordEntries = toWordEntries

// normalizeLyricLines originally lived in main.js; the React entrypoint
// didn't include it, resulting in a runtime reference error when
// updateLyrics invoked it.  Define it here so bundler will package it.
function normalizeLyricLines(lines) {
  if (!Array.isArray(lines)) return []

  return lines.map((line) => {
    const words = toWordEntries(line)
    const wordStart = words.length > 0 ? words[0].startTime : Number(line?.startTime ?? 0)
    const wordEnd = words.length > 0 ? words[words.length - 1].endTime : Number(line?.endTime ?? wordStart)
    const startTime = Number(line?.startTime ?? wordStart)
    const endTime = Number(line?.endTime ?? wordEnd)

    const result = {
      words,
      translatedLyric: String(line?.translatedLyric ?? ''),
      romanLyric: String(line?.romanLyric ?? ''),
      startTime: Number.isFinite(startTime) ? startTime : 0,
      endTime: Number.isFinite(endTime) ? endTime : 0,
      isBG: !!line?.isBG,
      isDuet: !!line?.isDuet,
    }

    // fallback to word-level timings if the computed values are invalid
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

function callBackground(methodName, ...args) {
  // React 组件版本的 BackgroundRender 不需要直接调用方法
  // 我们通过 props 来传递配置
  logToAndroid(`callBackground(${methodName}) called`, 'debug')
}

// getBackgroundRendererCtor function is no longer needed as we're using React components

function applyBackgroundProfile(profile) {
  currentBackgroundProfile = {
    ...currentBackgroundProfile,
    ...profile,
  }
  // React 组件版本的 BackgroundRender 不需要直接调用方法
  // 我们通过 props 来传递配置
  logToAndroid(`applyBackgroundProfile called with: ${JSON.stringify(profile)}`, 'debug')
}

function rebuildBackgroundRender() {
  // React 组件版本的 BackgroundRender 不需要重建
  // 组件会根据 props 的变化自动更新
  logToAndroid('rebuildBackgroundRender called', 'debug')
}

function callPlayer(methodName, ...args) {
  // React 组件版本的 LyricPlayer 不需要直接调用方法
  // 我们通过 props 来传递配置
  logToAndroid(`callPlayer(${methodName}) called with args: ${JSON.stringify(args)}`, 'debug')
}

function applyMotionProfile(profile) {
  currentProfile = { ...profile }
  if (window.__amll) window.__amll.currentProfile = currentProfile
  // React 组件版本的 LyricPlayer 不需要直接调用方法
  // 我们通过 props 来传递配置
  logToAndroid(`applyMotionProfile called with: ${JSON.stringify(profile)}`, 'debug')
}

function resetBlurTimeout() {
  // 清除旧的计时器
  if (state.blur.timeoutId !== null) {
    clearTimeout(state.blur.timeoutId)
  }
  
  // 设置新的计时器，5秒后恢复模糊
  state.blur.timeoutId = setTimeout(() => {
    if (player && state.blur.enabled === false) {
      callPlayer('setEnableBlur', true)
      state.blur.enabled = true
      player.getElement?.().classList.remove(TOUCH_BG_BLUR_CLASS)

      cancelPauseStyle()
      logToAndroid('Blur restored after 5s inactivity', 'info')

      // also cleanup any lingering line-specific overrides
      document.querySelectorAll('.amll-line-unblur').forEach(el => el.classList.remove('amll-line-unblur'))
    }
    state.blur.timeoutId = null
  }, state.blur.TIMEOUT_MS)
}

function handleTouchStart(e) {
  // 记录触摸位置和时间
  const touch = e?.touches?.[0]
  state.touch.startX = touch?.clientX ?? 0
  state.touch.startY = touch?.clientY ?? 0
  state.touch.startTime = Date.now()
  state.touch.isMoved = false

  // 保持主歌词原有体验：触摸时取消模糊
  if (player && state.blur.enabled === true) {
    callPlayer('setEnableBlur', false)
    state.blur.enabled = false
    player.getElement?.().classList.add(TOUCH_BG_BLUR_CLASS)
    logToAndroid('Blur disabled on touch, keep BG blurred', 'info')
  }

  // Apply the same visual “paused” style that shows background lyrics.
  // NOTE: we don't actually pause the player here; keeping the player "playing" allows
  // mask-size updates to continue while the touch blur style is active.
  applyPauseStyle()

  // also unblur just the line under the finger so that the user can
  // tap a lyric and see it clearly without removing blur from every line.
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

function handleTouchMove(e) {
  // 检测是否有显著移动 (大于10像素)
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

function handleTouchEnd(e) {
  const touchDuration = Date.now() - state.touch.startTime
  
  // 如果是快速短按（<300ms）且没有明显移动，视为点击
  if (!state.touch.isMoved && touchDuration < 300) {
    const x = e?.changedTouches?.[0]?.clientX ?? state.touch.startX
    const y = e?.changedTouches?.[0]?.clientY ?? state.touch.startY
    
    logToAndroid(`Tap detected at coordinates (${x}, ${y}), duration=${touchDuration}ms`, 'info')
    
    // 模拟点击事件
    try {
      const element = document.elementFromPoint(x, y)
      if (element) {
        logToAndroid(`Clicked element: ${element.tagName}, class=${element.className}`, 'info')
        
        // 尝试在其最近的歌词行容器上触发点击
        let lyricLine = element.closest('._lyricLine_1vq69_6, ._lyricLine_1ygrf_6')
        if (!lyricLine) {
          lyricLine = element.closest('[class*="lyric"]')
        }
        
        if (lyricLine) {
          logToAndroid('Found lyric line element', 'info')
          lyricLine.click?.()
          
          // 如果无法通过该方法触发，尝试手动分发click事件
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
      logToAndroid(`${error?.message || error}`, 'error')
    }
  }

  // (触摸结束后不清理样式，保持 touch pause/blur 状态)
}

function markSeeking(now) {
  state.isSeeking = true
  seekUntilTs = now + SEEK_HOLD_MS
  callPlayer('setIsSeeking', true)
}

function updateSeekingStateFromTime(now, nextTimeMs) {
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

function settleSeekingIfNeeded(now) {
  if (!state.isSeeking) return
  if (now < seekUntilTs) return
  state.isSeeking = false
  callPlayer('setIsSeeking', false)
}

function applyPlayerStyle(element) {
  element.style.width = '100%'
  // let the container size itself vertically instead of forcing 100%
  // height; the overridden calcLayout function will update the height
  // to the total lyric length so that the page can scroll naturally.
  element.style.height = 'auto'
  element.style.background = PLAYER_BACKGROUND
  // avoid blend mode which may cancel out lyrics against album art
  element.style.mixBlendMode = 'normal'
  element.style.color = '#f5f7ff'
  element.style.setProperty('--amll-lp-font-family', `var(--amll-user-font-family, ${DEFAULT_FONT_STACK})`)
  element.style.fontFamily = 'var(--amll-lp-font-family)'
  element.style.fontWeight = '700'
  element.style.setProperty('--amll-lp-color', '#f5f7ff')
  element.style.setProperty('--amll-lp-bg-color', 'rgba(0, 0, 0, 0.28)')
  element.style.setProperty('--amll-lp-hover-bg-color', 'rgba(255, 255, 255, 0.12)')
  element.style.setProperty('--amll-lp-font-size', 'clamp(30px, 4vh, 36px)')

  // 触摸导致全局去模糊时，仅对背景歌词加回固定模糊，主歌词保持清晰
  element.style.setProperty('--amll-touch-bg-blur', '10px')
}

function escapeCssString(value) {
  return String(value ?? '').replace(/\\/g, '\\\\').replace(/"/g, '\\"')
}

function setFontSettings(fontFamily, activeFontFamilyNames = [], fontFiles = []) {
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

    // Playback pause/resume is controlled externally (e.g. by the host app).
    // Kotlin can provide play state via Android.isPlaying(), so we sync here.
    if (typeof Android !== 'undefined' && typeof Android.isPlaying === 'function') {
      try {
        setPaused(!Boolean(Android.isPlaying()))
      } catch (_err) {
        // ignore
      }
    }

    // React 组件版本的 LyricPlayer 不需要手动调用 update
    // 组件会根据 props 的变化自动更新
  } catch (error) {
    logToAndroid(`update loop error: ${error?.message || error}`, 'error')
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

// mountPlayer function is no longer needed as we're using React components

window.setRenderMode = function (mode) {
  const normalizedMode = String(mode ?? '').toLowerCase()
  if (normalizedMode === 'dom-lite') {
    applyMotionProfile(LITE_PROFILE)
    logToAndroid('setRenderMode(dom-lite) -> lite profile applied', 'info')
    return
  }

  // always apply a known-quality profile instead of relying on potentially
  // uninitialized currentProfile variable (which might not exist yet when
  // this function is invoked early in initialization)
  applyMotionProfile(QUALITY_PROFILE)
  logToAndroid(`setRenderMode(${mode}) -> quality profile applied`, 'info')
}

window.updateLyrics = function (lyricsPayload) {
  try {
    const rawLines = Array.isArray(lyricsPayload?.lines) ? lyricsPayload.lines : []
    
    // 调试：检查接收到的背景歌词原始数据
    const bgLines = rawLines.filter(line => line?.isBG)
    if (bgLines.length > 0) {
      logToAndroid(`Received ${bgLines.length} BG lines from backend`, 'debug')
      bgLines.slice(0, 3).forEach((line, idx) => {
        logToAndroid(`Raw BG line ${idx}: text="${line?.text}" translation="${line?.translatedLyric}" words=${line?.words?.length || 0}`, 'debug')
      })
    }
    
    state.lyricLines = normalizeLyricLines(rawLines)

    // Debug: inspect normalized results
    if (state.lyricLines.length > 0) {
      state.lyricLines.slice(0, 3).forEach((ln, idx) => {
        const txt = ln.words.map(w => w.word).join('')
        logToAndroid(`normalized line ${idx}: text="${txt}" len=${ln.words.length}`, 'debug')
      })
    } else {
      logToAndroid('normalizeLyricLines produced 0 lines', 'warn')
    }
    logToAndroid(`lyricsPayload lines count=${rawLines.length}`, 'debug')

    // fallback when no lines at all
    if (state.lyricLines.length === 0) {
      logToAndroid('injecting placeholder lyric because none provided', 'debug')
      state.lyricLines = [
        { words: [{word:'Demo',startTime:0,endTime:2000}],translatedLyric:'',romanLyric:'',startTime:0,endTime:2000,isBG:false,isDuet:false }
      ]
    }

    if (player) {
      const currentTimeToUse = Math.trunc(state.currentTime)
      logToAndroid(`Updating lyrics with currentTime=${currentTimeToUse}ms`, 'info')
      callPlayer('setLyricLines', state.lyricLines, currentTimeToUse)
      callPlayer('setCurrentTime', currentTimeToUse, true)
      callPlayer('update', 0)
      logToAndroid(`Updated player with ${state.lyricLines.length} lines`, 'info')
    }
    if (backgroundRender) {
      applyBackgroundProfile({ hasLyric: state.lyricLines.length > 0 })
    }

    logToAndroid(`Updated lyrics (${state.lyricLines.length} lines)`, 'info')
  } catch (error) {
    logToAndroid(`updateLyrics error: ${error?.message || error}`, 'error')
  }
}

window.updateAlbumArt = async function (albumUri) {
  try {
    const uri = String(albumUri ?? '').trim()
    if (uri.length === 0 || uri === amllGet('lastAlbumArt')) return

    lastAlbumArt = uri
    amllSet('lastAlbumArt', uri)
    logToAndroid('Background album art updated', 'info')
    // React 组件版本的 BackgroundRender 会通过 props 自动更新
  } catch (error) {
    logToAndroid(`updateAlbumArt error: ${error?.message || error}`, 'error')
  }
}

window.updateTime = function (timeMs) {
  const now = performance.now()
  const parsedTime = Number(timeMs)
  const st = amllGet('state') || state
  st.currentTime = Number.isFinite(parsedTime) ? parsedTime : 0
  updateSeekingStateFromTime(now, st.currentTime)
  
  // 立即更新播放器状态，减少歌词行激活延迟
  if (player) {
    const currentTime = Math.trunc(st.currentTime)
    callPlayer('setCurrentTime', currentTime, state.isSeeking)
    callPlayer('update', 0)
  }
}

window.configureLyricMotion = function (options) {
  if (!options || typeof options !== 'object') return
  const merged = {
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

window.setBlurEnabled = function (enabled) {
  const shouldEnable = Boolean(enabled)
  if (player && state.blur.enabled !== shouldEnable) {
    callPlayer('setEnableBlur', shouldEnable)
    state.blur.enabled = shouldEnable
    logToAndroid(`setBlurEnabled(${shouldEnable})`, 'info')
    
    // 仅在启用模糊时清除计时器，禁用时不清除
    if (shouldEnable && state.blur.timeoutId !== null) {
      clearTimeout(state.blur.timeoutId)
      state.blur.timeoutId = null
    }
  }
}

window.setBlurTimeout = function (timeMs) {
  const ms = Number(timeMs)
  if (Number.isFinite(ms) && ms > 0) {
    state.blur.TIMEOUT_MS = ms
    logToAndroid(`Blur timeout set to ${ms}ms`, 'info')
  }
}

window.setBackgroundRenderer = function (mode) {
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

window.updateLowFreqVolume = function (value) {
  const parsed = Number(value)
  const clamped = Number.isFinite(parsed) ? Math.max(0, Math.min(1, parsed)) : 1
  applyBackgroundProfile({ lowFreqVolume: clamped })
}

window.configureBackgroundEffect = function (options) {
  if (!options || typeof options !== 'object') return

  const base = amllGet('currentBackgroundProfile') || currentBackgroundProfile
  const next = {
    ...base,
    ...options,
  }
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

window.logFromKotlin = function (message) {
  logToAndroid(message, 'debug')
}

// capture uncaught errors in JS and forward to Android logcat
window.onerror = function (msg, src, line, col, err) {
  logToAndroid(`Uncaught JS: ${msg} at ${src}:${line}:${col} ${err?err.stack:''}`, 'error')
}

// monkey patch document.createElement to handle SVG data URLs
const originalCreateElement = document.createElement;
document.createElement = function(tagName, options) {
  if (typeof tagName === 'string' && tagName.startsWith('data:image/svg+xml')) {
    // If tagName is an SVG data URL, create a div instead
    const div = originalCreateElement.call(document, 'div', options);
    div.style.display = 'none';
    return div;
  }
  return originalCreateElement.call(document, tagName, options);
};

// monkey patch document.createElementNS to handle SVG data URLs
const originalCreateElementNS = document.createElementNS;
document.createElementNS = function(namespaceURI, qualifiedName, options) {
  if (typeof qualifiedName === 'string' && qualifiedName.startsWith('data:image/svg+xml')) {
    // If qualifiedName is an SVG data URL, create a div instead
    const div = originalCreateElementNS.call(document, namespaceURI, 'div', options);
    div.style.display = 'none';
    return div;
  }
  return originalCreateElementNS.call(document, namespaceURI, qualifiedName, options);
};

window.setFontSettings = setFontSettings

function App() {
  const playerRef = useRef(null)
  const audioRef = useRef(null)
  const [lyricLines, setLyricLines] = useAtom(musicLyricLinesAtom)
  const [currentTime, setCurrentTime] = useAtom(musicPlayingPositionAtom)
  const [albumUri, setAlbumUri] = useAtom(musicCoverAtom)
  const setIsPlaying = useSetAtom(musicPlayingAtom)
  const setLowFreqVolume = useSetAtom(lowFreqVolumeAtom)
  const setIsLyricPageOpened = useSetAtom(isLyricPageOpenedAtom)
  const demoAlbumArt = 'https://example.com/your-album-art.png'
  const demoAudioSrc = '' // 填写你的音频文件链接。例如: 'https://example.com/music.mp3'

  useEffect(() => {
    // 初始化全局状态
    window.__amll = window.__amll || {}
    Object.assign(window.__amll, {
      player: null,
      state: state,
      lastAlbumArt: '',
      currentProfile: { ...QUALITY_PROFILE }
    })

    // 检查 Android 接口是否可用
    if (typeof Android !== 'undefined' && Android?.log) {
      if (typeof Android.onLineClick === 'function') {
        logToAndroid('Android.onLineClick interface is ready', 'info')
      } else {
        logToAndroid('WARNING: Android.onLineClick interface NOT found', 'warn')
      }
    } else {
      logToAndroid('WARNING: Android interface NOT available', 'warn')
    }

    // 启动动画循环
    startAnimationLoop()

    // 覆盖 updateLyrics 函数，使用 Jotai 状态
    window.updateLyrics = function (lyricsPayload) {
      try {
        const rawLines = Array.isArray(lyricsPayload?.lines) ? lyricsPayload.lines : []
        
        // 调试：检查接收到的背景歌词原始数据
        const bgLines = rawLines.filter(line => line?.isBG)
        if (bgLines.length > 0) {
          logToAndroid(`Received ${bgLines.length} BG lines from backend`, 'debug')
          bgLines.slice(0, 3).forEach((line, idx) => {
            logToAndroid(`Raw BG line ${idx}: text="${line?.text}" translation="${line?.translatedLyric}" words=${line?.words?.length || 0}`, 'debug')
          })
        }
        
        const normalizedLines = normalizeLyricLines(rawLines)

        // Debug: inspect normalized results
        if (normalizedLines.length > 0) {
          normalizedLines.slice(0, 3).forEach((ln, idx) => {
            const txt = ln.words.map(w => w.word).join('')
            logToAndroid(`normalized line ${idx}: text="${txt}" len=${ln.words.length}`, 'debug')
          })
        } else {
          logToAndroid('normalizeLyricLines produced 0 lines', 'warn')
        }
        logToAndroid(`lyricsPayload lines count=${rawLines.length}`, 'debug')

        // fallback when no lines at all
        if (normalizedLines.length === 0) {
          logToAndroid('injecting placeholder lyric because none provided', 'debug')
          setLyricLines([
            { words: [{word:'Demo',startTime:0,endTime:2000}],translatedLyric:'',romanLyric:'',startTime:0,endTime:2000,isBG:false,isDuet:false }
          ])
        } else {
          setLyricLines(normalizedLines)
        }

        logToAndroid(`Updated lyrics (${normalizedLines.length} lines)`, 'info')
      } catch (error) {
        logToAndroid(`updateLyrics error: ${error?.message || error}`, 'error')
      }
    }

    // 覆盖 updateAlbumArt 函数，使用 Jotai 状态
    window.updateAlbumArt = async function (albumUri) {
      try {
        const uri = String(albumUri ?? '').trim()
        if (uri.length === 0 || uri === amllGet('lastAlbumArt')) return

        lastAlbumArt = uri
        amllSet('lastAlbumArt', uri)
        setAlbumUri(uri)
        logToAndroid('Background album art updated', 'info')
      } catch (error) {
        logToAndroid(`updateAlbumArt error: ${error?.message || error}`, 'error')
      }
    }

    // 覆盖 updateTime 函数，使用 Jotai 状态
    window.updateTime = function (timeMs) {
  const now = performance.now()
  const parsedTime = Number(timeMs)
  const st = amllGet('state') || state
  st.currentTime = Number.isFinite(parsedTime) ? parsedTime : 0
  setCurrentTime(st.currentTime)
  updateSeekingStateFromTime(now, st.currentTime)
}

    // 初始化其他状态
    setIsPlaying(true)
    setLowFreqVolume(1)
    setIsLyricPageOpened(true)

    // development-only: if no Android bridge, inject a sample lyric to verify layout
    if (typeof Android === 'undefined') {
      logToAndroid('no Android object, inserting demo lyric', 'debug')
      window.updateLyrics({
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
    }

    return () => {
      // 清理
      if (rafId != null) {
        window.cancelAnimationFrame(rafId)
        rafId = null
      }
    }
  }, [setLyricLines, setCurrentTime, setAlbumUri, setIsPlaying, setLowFreqVolume, setIsLyricPageOpened])

  // 音频时间同步
  useEffect(() => {
    const audio = audioRef.current
    if (!audio) return

    const onTimeUpdate = () => {
      const nextTime = Math.trunc(audio.currentTime * 1000)
      setCurrentTime(nextTime)
      if (typeof window.updateTime === 'function') {
        window.updateTime(nextTime)
      }
    }

    audio.addEventListener('timeupdate', onTimeUpdate)
    return () => {
      audio.removeEventListener('timeupdate', onTimeUpdate)
    }
  }, [setCurrentTime])

  const handleLineClick = (event) => {
    try {
      const lineIndex = Number(event?.lineIndex ?? -1)
      const line = event?.line
      let startTime = 0
      
      // 尝试多种方式获取 startTime
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
      
      if (typeof Android !== 'undefined' && Android?.onLineClick) {
        Android.onLineClick(lineIndex, startTime)
        logToAndroid(`Called Android.onLineClick(${lineIndex}, ${startTime})`, 'info')
      } else {
        logToAndroid('Android.onLineClick not available', 'error')
      }

      if (audioRef.current && Number.isFinite(startTime)) {
        audioRef.current.currentTime = startTime / 1000
      }
    } catch (error) {
      logToAndroid(`line-click handler exception: ${error?.message || error}`, 'error')
    }
  }

  return (
    <div id="app" style={{ position: 'relative', width: '100%', height: '100vh' }}>
      <BackgroundRender
        src={albumUri || demoAlbumArt}
        style={{ position: 'absolute', inset: 0, zIndex: 0 }}
        onError={(err) => logToAndroid(`BackgroundRender error: ${err}`, 'error')}
      />

      <PrebuiltLyricPlayer
        ref={playerRef}
        lyricLines={lyricLines}
        currentTime={currentTime}
        onLineClick={handleLineClick}
        style={{
          position: 'absolute',
          inset: 0,
          zIndex: 1,
          width: '100%',
          height: '100%',
          background: PLAYER_BACKGROUND
        }}
      />

      <audio
        ref={audioRef}
        src={demoAudioSrc}
        controls
        style={{ position: 'absolute', bottom: 10, left: 10, zIndex: 2, width: 'calc(100% - 20px)' }}
      />
    </div>
  )
}

window.addEventListener('DOMContentLoaded', () => {
  try {
    document.documentElement.style.background = 'transparent'
    document.body.style.background = 'transparent'
    
    const root = document.getElementById('app') || document.createElement('div')
    if (!document.getElementById('app')) {
      root.id = 'app'
      if (document.body) {
        document.body.appendChild(root)
      }
    }
    
    // 使用 React 18 的渲染方式
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
    logToAndroid(`DOMContentLoaded error: ${error?.message || error}`, 'error')
  }
})

window.addEventListener('beforeunload', () => {
  // 清除blur计时器
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
