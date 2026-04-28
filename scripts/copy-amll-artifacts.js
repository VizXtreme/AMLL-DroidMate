#!/usr/bin/env node

/**
 * ============================================================================
 * AMLL 构建产物复制脚本
 * ============================================================================
 * 
 * **功能说明**：
 * 这个 Node.js 脚本用于将编译完成的 AMLL (Apple Music Like Lyrics) 文件
 * 复制到 Android assets 目录。这是一个简化的复制流程，绕过了复杂的
 * npm 依赖问题。
 * 
 * **主要用途**：
 * 1. 清理旧的 assets 目录
 * 2. 复制 CSS 样式文件（优先使用 amll-core.css）
 * 3. 复制 JavaScript bundle
 * 4. 复制 source map（用于调试）
 * 5. 复制 HTML 入口文件
 * 
 * **优先级策略**：
 * - 优先使用 frontend/dist（IIFE 格式，适合直接在 HTML 中使用）
 * - 回退到 applemusic-like-lyrics/packages/react-full/dist
 * - CSS 强制使用 amll-core.css（包含完整的歌词样式）
 * 
 * **使用方法**：
 * ```bash
 * # 直接执行
 * node scripts/copy-amll-artifacts.js
 * ```
 */

// ==================== 导入必要的模块 ====================
import { copyFileSync, mkdirSync, existsSync, rmSync } from 'fs'
import { resolve, dirname } from 'path'
import { fileURLToPath } from 'url'

// ==================== 路径配置 ====================
// 获取当前文件的绝对路径（ES Module 中需要使用 import.meta.url）
const __filename = fileURLToPath(import.meta.url)
// 获取当前文件所在目录
const __dirname = dirname(__filename)

// 项目根目录（脚本的父目录）
const projectRoot = resolve(__dirname, '..')

// 前端构建产物目录（IIFE 格式，适合 WebView 直接使用）
const frontendDistDir = resolve(projectRoot, 'frontend/dist')

// AMLL 原始构建产物目录（备用方案）
const amllDistDir = resolve(projectRoot, 'applemusic-like-lyrics/packages/react-full/dist')

// 优先使用 frontend/dist，如果不存在则回退到 AMLL dist
const useFrontendDist = existsSync(frontendDistDir)
const sourceDistDir = useFrontendDist ? frontendDistDir : amllDistDir

// Android assets 目标目录
const assetsDir = resolve(projectRoot, 'app/src/main/assets/amll')

// ==================== 开始复制 ====================
console.log('Copying AMLL build artifacts...')
console.log(`From: ${sourceDistDir} ${useFrontendDist ? '(frontend/dist with IIFE)' : '(AMLL dist)'}`)
console.log(`To: ${assetsDir}`)

try {
  // Step 1: 清理旧的 assets 目录
  if (existsSync(assetsDir)) {
    console.log('Cleaning existing assets...')
    rmSync(assetsDir, { recursive: true })  // 递归删除整个目录
  }
  
  // Step 2: 创建新的 assets 目录
  mkdirSync(assetsDir, { recursive: true })  // recursive 确保父目录也存在
  
  // Step 3: 复制 CSS 文件
  const cssFile = resolve(sourceDistDir, useFrontendDist ? 'frontend.css' : 'amll-react-framework.css')
  if (existsSync(cssFile)) {
    copyFileSync(cssFile, resolve(assetsDir, 'frontend.css'))
    console.log('✓ Copied frontend.css')
  } else {
    console.error('✗ CSS file not found:', cssFile)
  }
  
  // Step 4: 【重要】使用 amll-core.css 覆盖，确保完整的歌词样式
  // amll-core.css 包含了所有歌词渲染所需的完整样式，是核心 CSS 文件
  const amllCoreCss = resolve(projectRoot, 'applemusic-like-lyrics/packages/core/dist/amll-core.css')
  if (existsSync(amllCoreCss)) {
    copyFileSync(amllCoreCss, resolve(assetsDir, 'frontend.css'))
    console.log('✓ Overridden with amll-core.css (complete lyric styles)')
  } else {
    console.warn('⚠ amll-core.css not found, using default CSS')
  }
  
  // Step 5: 复制 JavaScript bundle
  const jsFile = resolve(sourceDistDir, 'amll.bundle.js')
  if (existsSync(jsFile)) {
    copyFileSync(jsFile, resolve(assetsDir, 'amll.bundle.js'))
    console.log('✓ Copied amll.bundle.js')
  } else {
    console.error('✗ JS file not found:', jsFile)
  }
  
  // Step 6: 复制 Source Map（用于调试）
  const mapFile = resolve(sourceDistDir, 'amll.bundle.js.map')
  if (existsSync(mapFile)) {
    copyFileSync(mapFile, resolve(assetsDir, 'amll.bundle.js.map'))
    console.log('✓ Copied amll.bundle.js.map')
  }
  // 注意：map 文件是可选的，生产环境可以选择性包含
  
  // Step 7: 复制 HTML 入口文件
  const indexHtmlSrc = resolve(projectRoot, 'frontend/index.html')
  const indexHtmlDest = resolve(assetsDir, 'index.html')
  if (existsSync(indexHtmlSrc)) {
    copyFileSync(indexHtmlSrc, indexHtmlDest)
    console.log('✓ Copied index.html')
  } else {
    console.error('✗ index.html not found:', indexHtmlSrc)
  }
  
  // ==================== 完成！ ====================
  console.log('\n✅ AMLL artifacts copied successfully!')
} catch (error) {
  // 错误处理：输出详细错误信息并退出
  console.error('❌ Error copying files:', error.message)
  process.exit(1)
}
