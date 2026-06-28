# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Token 节省策略

**本项目的 CLAUDE.md 已较大，优先复用当前上下文中已有的信息，而非重读文件。** 遵循以下原则：

- **简单操作说步骤** — 如果某个操作逻辑简单但需要大量 token（如批量搜索/读取多个小文件），直接描述步骤让用户自行处理，不要实际执行。
- **不 Read 刚写过的文件** — Edit/Write 失败会报错，成功即确认，无需再 Read 验证。
- **精准搜索** — 优先用 `Grep` 定位内容，而非 `Glob` + 逐个 `Read`。有明确目标时先试精确路径或关键词。
- **不读大全文** — 用 `limit`/`offset` 分段读取，或先用 `Grep` 定位行号再局部读取。
- **不重复已知** — 已从上下文或工具结果获得的信息不再二次确认。
- **不自动编译** — 不得运行任何编译命令，除非用户明确要求。

## Build & Run

**不要编译。** 用户自行编译，只需说明编译命令即可。只有用户明确要求时才运行编译。

```bash
# Build the full project (frontend assets + Android APK)
./gradlew assembleDebug

# Build only frontend assets (npm install + vite build → app/src/main/assets/amll/)
./gradlew buildFrontend

# Install debug APK to connected device
./gradlew installDebug

# Run unit tests
./gradlew test

# Run connected Android instrumentation tests
./gradlew connectedAndroidTest

# Frontend dev server (for browser-based development)
cd frontend && npm run dev        # Vite dev server on port 5173
```

The `buildFrontend` Gradle task (defined in `app/build.gradle.kts`) runs automatically before `preBuild`. It executes `npm install` then `npm run build` in the `frontend/` directory, then syncs `frontend/dist/` to `app/src/main/assets/amll/`. The frontend is built as an ES module library (`amll.bundle.js`) loaded by `index.html` inside a WebView.

## Architecture

This is **AMLL DroidMate** — an Android lyrics display app that shows Apple Music-style animated lyrics overlaid on any music source. It detects currently playing music via Android's `MediaSession` / notification listener, fetches lyrics from multiple online sources, and renders them with fluid animations in a WebView using the [`@applemusic-like-lyrics/core`](https://github.com/amll-dev/applemusic-like-lyrics) library.

### Two namespaces

| Namespace | Role |
|---|---|
| `io.github.zeehan2005.scoremuse` | ScoreMuse base: media detection, lyrics fetching/parsing, settings, theme, services, UI |
| `dev.amll.droidmate` | AMLL-specific: WebView lyrics view (`AMLLLyricsView`), TTML converter, WASM parser, AMLL settings |

### Key data flow

```
Music app playing → MediaInfoService (MediaSession callback) → MainViewModel
  → LyricsRepository.fetchLyricsAuto() (parallel search across QQ/Netease/Kugou/AMLL-DB)
  → UnifiedLyricsParser → TTMLConverter → UnifiedLyrics (cached via LyricsCacheRepository)
  → MainScreen → AMLLLyricsView (Compose WebView wrapper)
  → evaluateJavascript("window.updateLyrics(...)") → @applemusic-like-lyrics/core renders
```

### Core components

- **`MainActivity`** — Entry point. Initializes Timber logging, sets up edge-to-edge display, observes dynamic theme from album art, hosts `MainScreen`.
- **`MainViewModel`** — Central state manager. Holds `nowPlayingMusic`, `lyrics`, `songStructures`, `isLoading` as `StateFlow`s. Coordinates media listening, lyrics fetching, caching, and notification display.
- **`AMLLLyricsView`** — Composable wrapper around a native `WebView`. Injects lyrics JSON, playback time, album art, and motion configs via `evaluateJavascript`. Uses `WebViewAssetLoader` for secure local asset loading. Throttles time updates to 100ms intervals.
- **`LyricsRepository`** — Multi-source lyrics search/fetch. Parallel search across QQ Music (QRC/LRC), Netease (YRC/LRC), Kugou (KRC), and AMLL TTML DB. Includes sophisticated title/artist matching with Levenshtein distance, accent normalization, and version-keyword detection. All results are normalized to TTML via `TTMLConverter`.
- **`UnifiedLyricsParser`** — Format-detecting parser that dispatches to format-specific parsers (LRC, Enhanced LRC, QRC, KRC, YRC, TTML). All formats produce `UnifiedLyrics` (common data model) with `LyricLine`s containing text, translation, transliteration, per-word timing, and structural markers (isBG, isDuet, agent).
- **`ServiceLocator`** — Manual DI container providing singleton `HttpClient`, `LyricsRepository`, `LyricsCacheRepository`, and `WasmLyricParser`.
- **`DynamicThemeManager`** — Global observable `ColorScheme` extracted from album art by `AlbumColorExtractor` using AndroidX Palette.
- **`MediaInfoService`** — Polls `MediaController` for current playback info (title, artist, album art URI, position, play state).
- **`MediaListenerService`** — System `NotificationListenerService` for detecting which music app is playing.
- **`LyricNotificationManager`** — Persistent notification showing current lyric line, with optional lock screen display.
- **`LyricsCacheRepository`** — Local cache using SharedPreferences-backed key-value storage (song title + artist → TTML XML).

### Frontend (`frontend/`)

- **`src/main.tsx`** — Entry point. Initializes `DomLyricPlayer` from `@applemusic-like-lyrics/core`. Exposes `window.updateLyrics`, `window.updateTime`, `window.updateAlbumArt`, `window.setPaused`, `window.configureLyricMotion`, etc. for the Android bridge. Runs a `requestAnimationFrame` tick loop.
- **Vite config** — Library mode build (`amll.bundle.js`), CSS inlined into JS bundle, WASM + top-level-await plugins. Built output goes to `dist/` then Gradle copies to `app/src/main/assets/amll/`.

### Data model

`UnifiedLyrics` is the universal lyrics representation, containing `LyricsMetadata` (title, artist, album, language, duration, source, `SongStructure` list) and `List<LyricLine>`. Each `LyricLine` has start/end time, text, optional translation/transliteration, per-word timing (`List<LyricWord>`), and flags (`isBG`, `isDuet`, `agent`). `SongStructure` marks sections (verse, chorus, bridge, intro, outro, etc.) identified from TTML metadata or inferred from lyric patterns.

## Logging conventions

This project uses **Timber** with strict conventions defined in `LOGGING_GUIDELINES.md`:

- `Timber.wtf()` — Fatal errors that crash the app
- `Timber.e()` — Functional errors (network failures, parse errors)
- `Timber.w()` — Non-critical issues, fallbacks triggered
- `Timber.i()` — Phase completions, state transitions, reasonable edge cases
- `Timber.d()` — Internal processing steps (max 10 lines per log; excess → `Timber.v()`)
- `Timber.v()` — Polling/no-change checks, long content overflow from debug

Every log line **must** begin with a `[ModuleName]` tag (e.g., `[WebSocket]`, `[LyricsMatcher]`, `[AMLLLyrics]`). Exception parameters always go last: `Timber.e("[Module] message", e)`.

**Do not** include explanatory/educational content in logs (no "可能原因:", "请检查:", etc.).

## Important notes

- The project uses **Aliyun Maven mirrors** for all dependencies (configured in `settings.gradle.kts`).
- `local.properties` is gitignored — it contains SDK paths. Android Studio generates it automatically.
- Debug signing config is committed (keystore at root `sign.jks`). This is intentional for a solo developer project.
- `android.useAndroidX=true` and `android.enableJetifier=false` in `gradle.properties`.
- AGP 9.2.1 with Kotlin 2.3.21 and Compose compiler plugin. Configuration cache is enabled.
- Min SDK 26 (Android 8.0), target/compile SDK 37.
- The README notes this is "100% HITL Vibe coding" (human-in-the-loop AI-assisted development).
