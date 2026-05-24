/**
 * AMLL 歌词处理器
 *
 * 负责歌词的解析、格式化和归一化。
 * 支持从多种格式（LRC, YRC, QRC, KRC, TTML等）解析为统一的内部格式。
 */
import { logToAndroid } from './bridge_utils'

// ==================== 类型定义 ====================

export interface WordEntry {
  word: string
  startTime: number
  endTime: number
}

export interface LyricLine {
  words: WordEntry[]
  translatedLyric: string
  romanLyric: string
  startTime: number
  endTime: number
  isBG: boolean
  isDuet: boolean
}

export interface LyricsPayload {
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
  raw?: string
  format?: string
}

// ==================== 状态管理 ====================

let lyricParserModule: any = null

/**
 * 懒加载 WASM 歌词解析器
 */
export async function getLyricParser() {
  if (!lyricParserModule) {
    try {
      console.log('[AMLL] loading wasm lyric parser...')
      lyricParserModule = await import('@applemusic-like-lyrics/lyric')
      console.log('[AMLL] wasm lyric parser loaded successfully')
    } catch (e) {
      logToAndroid(`Wasm load failed: ${(e as Error).message}`, 'error')
      throw e
    }
  }
  return lyricParserModule
}

// ==================== 转换工具 ====================

/**
 * 将 WASM 解析器输出的歌词行转换为内部 LyricLine 格式
 */
export function convertAmllLyricToLyricLine(amllLines: any[]): LyricLine[] {
  return amllLines.map(line => {
    const startTime = line.startTime ?? (line.words?.[0]?.startTime ?? 0)
    const endTime = line.endTime ?? (line.words?.[line.words.length - 1]?.endTime ?? startTime)

    return {
      startTime: Number(startTime),
      endTime: Number(endTime),
      words: (line.words || []).map((w: any) => ({
        word: String(w.word || ''),
        startTime: Number(w.startTime ?? startTime),
        endTime: Number(w.endTime ?? endTime)
      })),
      translatedLyric: String(line.translatedLyric || line.translation || ''),
      romanLyric: String(line.romanLyric || line.roman || line.transliteration || ''),
      isBG: !!line.isBG,
      isDuet: !!line.isDuet
    }
  })
}

/**
 * 归一化预解析的歌词行
 */
export function normalizeLyricLines(lines: any[]): LyricLine[] {
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

/**
 * 处理歌词 Payload
 *
 * 根据 Payload 中的数据（原始文本或预解析行）返回归一化的 LyricLine 数组。
 */
export async function processLyricsPayload(payload: LyricsPayload): Promise<LyricLine[]> {
  try {
    let normalized: LyricLine[] = []

    // 1. 优先尝试解析原始文本
    if (payload.raw) {
      logToAndroid(`processLyrics: parsing raw lyrics (${payload.format})`, 'debug')
      const format = (payload.format || 'lrc').toLowerCase()
      let parsed: any[] = []
      try {
        const parser = await getLyricParser()
        const { parseLrc, parseYrc, parseQrc, parseKrc, parseTtml, parseLys, parseEslrc } = parser

        if (format === 'yrc') parsed = parseYrc(payload.raw)
        else if (format === 'qrc') parsed = parseQrc(payload.raw)
        else if (format === 'krc') parsed = (parseKrc || parseQrc)(payload.raw)
        else if (format === 'ttml') parsed = parseTtml ? parseTtml(payload.raw) : []
        else if (format === 'lys') parsed = parseLys(payload.raw)
        else if (format === 'enhanced_lrc') parsed = parseEslrc(payload.raw)
        else if (format === 'lrc') parsed = parseLrc(payload.raw)
        else {
          logToAndroid(`Parser: format '${format}' not supported by wasm, using pre-parsed lines`, 'debug')
        }

        if (parsed.length > 0) {
          logToAndroid(`Parser success: ${parsed.length} lines`, 'debug')
          normalized = convertAmllLyricToLyricLine(parsed)
        }
      } catch (err) {
        logToAndroid(`Parser error (${format}): ${(err as Error).message}`, 'error')
        console.error(err)
      }
    }

    // 2. 如果原始文本解析失败或不存在，使用预解析的行
    if (normalized.length === 0) {
      const rawLines = Array.isArray(payload?.lines) ? payload.lines : []
      normalized = normalizeLyricLines(rawLines)
    }

    return normalized
  } catch (e) {
    logToAndroid(`processLyrics error: ${(e as Error).message}`, 'error')
    return []
  }
}
