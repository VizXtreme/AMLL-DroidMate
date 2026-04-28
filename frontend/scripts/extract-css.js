#!/usr/bin/env node

/**
 * 构建后处理脚本：从 JS bundle 中提取 CSS 并合并到 frontend.css
 * 
 * **问题背景**：
 * Vite 在 IIFE 模式下使用 cssCodeSplit: false 时，会通过 <style> 标签将 CSS 注入到 JS bundle 中。
 * 这导致 CSS 被包裹在 JavaScript 代码里，不利于维护和调试。
 * 
 * **解决方案**：
 * 这个脚本会：
 * 1. 解析 JS bundle 文件
 * 2. 提取其中的 CSS 内容
 * 3. 将 CSS 保存到单独的 frontend.css 文件
 * 4. 与现有的 CSS 合并
 * 
 * **正则匹配原理**：
 * Vite 生成的代码格式：var __vite_style__ = document.createElement("style"); __vite_style__.textContent = "...";
 * 我们使用正则表达式捕获引号内的 CSS 内容
 */

const fs = require('fs');
const path = require('path');

// ==================== 路径定义 ====================
// dist 目录：构建产物输出位置
const distDir = path.join(__dirname, '..', 'dist');
// JS bundle 文件路径（Vite 打包后的主文件）
const bundlePath = path.join(distDir, 'amll.bundle.js');
// CSS 文件路径（要保存到的目标文件）
const cssPath = path.join(distDir, 'frontend.css');

console.log('🔍 Extracting CSS from bundle...');

// ==================== 读取 JS bundle ====================
const bundleContent = fs.readFileSync(bundlePath, 'utf-8');

// ==================== 正则匹配提取 CSS ====================
// 匹配 Vite 生成的 CSS 注入代码：
// var __vite_style__ = document.createElement("style"); __vite_style__.textContent = "...";
const cssMatch = bundleContent.match(/var __vite_style__ = document\.createElement\("style\"); [\s\S]*?__vite_style__\.textContent = "([\s\S]*?)";/);

if (cssMatch && cssMatch[1]) {
  let cssContent = cssMatch[1];
  
  // ==================== 转义字符还原 ====================
  // 由于 CSS 被包裹在 JS 字符串中，需要还原转义字符：
  // \n → 换行符
  // \" → 双引号
  // \\ → 反斜杠
  cssContent = cssContent
    .replace(/\\n/g, '\n')
    .replace(/\\"/g, '"')
    .replace(/\\\\/g, '\\');
  
  // ==================== 读取现有 CSS 文件 ====================
  let existingCss = '';
  if (fs.existsSync(cssPath)) {
    existingCss = fs.readFileSync(cssPath, 'utf-8');
    console.log('✓ Found existing frontend.css');
  }
  
  // ==================== 合并 CSS ====================
  // 将提取的 CSS 追加到现有 CSS 后面
  const mergedCss = existingCss + '\n\n/* === AMLL Core Styles (auto-extracted) === */\n' + cssContent;
  
  // ==================== 写入文件 ====================
  fs.writeFileSync(cssPath, mergedCss);
  console.log('✅ CSS extracted and merged successfully!');
  console.log(`   Total size: ${(mergedCss.length / 1024).toFixed(2)} KB`);
} else {
  // 如果没有找到 CSS，说明 CSS 是动态注入的，这是正常现象
  console.warn('⚠️  No CSS found in bundle. This is expected if CSS is injected dynamically.');
  console.warn('   The styles should still work at runtime.');
}
