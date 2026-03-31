#!/usr/bin/env node

/**
 * Copy built AMLL files to Android assets directory
 * This bypasses the complex npm dependency issues
 */

import { copyFileSync, mkdirSync, existsSync, rmSync } from 'fs'
import { resolve, dirname } from 'path'
import { fileURLToPath } from 'url'

const __filename = fileURLToPath(import.meta.url)
const __dirname = dirname(__filename)

const projectRoot = resolve(__dirname, '..')
// Try to use frontend/dist first (built with IIFE), fallback to AMLL dist
const frontendDistDir = resolve(projectRoot, 'frontend/dist')
const amllDistDir = resolve(projectRoot, 'applemusic-like-lyrics/packages/react-full/dist')
const useFrontendDist = existsSync(frontendDistDir)
const sourceDistDir = useFrontendDist ? frontendDistDir : amllDistDir
const assetsDir = resolve(projectRoot, 'app/src/main/assets/amll')

console.log('Copying AMLL build artifacts...')
console.log(`From: ${sourceDistDir} ${useFrontendDist ? '(frontend/dist with IIFE)' : '(AMLL dist)'}`)
console.log(`To: ${assetsDir}`)

try {
  // Clean assets directory first
  if (existsSync(assetsDir)) {
    console.log('Cleaning existing assets...')
    rmSync(assetsDir, { recursive: true })
  }
  
  mkdirSync(assetsDir, { recursive: true })
  
  // Copy CSS
  const cssFile = resolve(sourceDistDir, useFrontendDist ? 'frontend.css' : 'amll-react-framework.css')
  if (existsSync(cssFile)) {
    copyFileSync(cssFile, resolve(assetsDir, 'frontend.css'))
    console.log('✓ Copied frontend.css')
  } else {
    console.error('✗ CSS file not found:', cssFile)
  }
  
  // IMPORTANT: Override with amll-core.css for complete lyric styles
  const amllCoreCss = resolve(projectRoot, 'applemusic-like-lyrics/packages/core/dist/amll-core.css')
  if (existsSync(amllCoreCss)) {
    copyFileSync(amllCoreCss, resolve(assetsDir, 'frontend.css'))
    console.log('✓ Overridden with amll-core.css (complete lyric styles)')
  } else {
    console.warn('⚠ amll-core.css not found, using default CSS')
  }
  
  // Copy JS bundle
  const jsFile = resolve(sourceDistDir, 'amll.bundle.js')
  if (existsSync(jsFile)) {
    copyFileSync(jsFile, resolve(assetsDir, 'amll.bundle.js'))
    console.log('✓ Copied amll.bundle.js')
  } else {
    console.error('✗ JS file not found:', jsFile)
  }
  
  // Copy source map
  const mapFile = resolve(sourceDistDir, 'amll.bundle.js.map')
  if (existsSync(mapFile)) {
    copyFileSync(mapFile, resolve(assetsDir, 'amll.bundle.js.map'))
    console.log('✓ Copied amll.bundle.js.map')
  }
  
  // Copy index.html from frontend/
  const indexHtmlSrc = resolve(projectRoot, 'frontend/index.html')
  const indexHtmlDest = resolve(assetsDir, 'index.html')
  if (existsSync(indexHtmlSrc)) {
    copyFileSync(indexHtmlSrc, indexHtmlDest)
    console.log('✓ Copied index.html')
  } else {
    console.error('✗ index.html not found:', indexHtmlSrc)
  }
  
  console.log('\n✅ AMLL artifacts copied successfully!')
} catch (error) {
  console.error('❌ Error copying files:', error.message)
  process.exit(1)
}
