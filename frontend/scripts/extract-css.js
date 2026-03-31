#!/usr/bin/env node

/**
 * Post-build script: Extract CSS from JS bundle and merge into frontend.css
 * 
 * Vite in IIFE mode with cssCodeSplit: false injects CSS via a <style> tag.
 * This script extracts that CSS and saves it to a separate file.
 */

const fs = require('fs');
const path = require('path');

const distDir = path.join(__dirname, '..', 'dist');
const bundlePath = path.join(distDir, 'amll.bundle.js');
const cssPath = path.join(distDir, 'frontend.css');

console.log('🔍 Extracting CSS from bundle...');

// Read the bundle
const bundleContent = fs.readFileSync(bundlePath, 'utf-8');

// Find the CSS content in the __vite_style__ tag
// Pattern: var __vite_style__ = document.createElement("style"); __vite_style__.textContent = "...";
const cssMatch = bundleContent.match(/var __vite_style__ = document\.createElement\("style"\);[\s\S]*?__vite_style__\.textContent = "([\s\S]*?)";/);

if (cssMatch && cssMatch[1]) {
  let cssContent = cssMatch[1];
  
  // Unescape the CSS content
  cssContent = cssContent
    .replace(/\\n/g, '\n')
    .replace(/\\"/g, '"')
    .replace(/\\\\/g, '\\');
  
  // Read existing frontend.css (if any)
  let existingCss = '';
  if (fs.existsSync(cssPath)) {
    existingCss = fs.readFileSync(cssPath, 'utf-8');
    console.log('✓ Found existing frontend.css');
  }
  
  // Merge CSS
  const mergedCss = existingCss + '\n\n/* === AMLL Core Styles (auto-extracted) === */\n' + cssContent;
  
  // Write to frontend.css
  fs.writeFileSync(cssPath, mergedCss);
  console.log('✅ CSS extracted and merged successfully!');
  console.log(`   Total size: ${(mergedCss.length / 1024).toFixed(2)} KB`);
} else {
  console.warn('⚠️  No CSS found in bundle. This is expected if CSS is injected dynamically.');
  console.warn('   The styles should still work at runtime.');
}
