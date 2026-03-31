const fs = require('fs');
const path = require('path');

const distDir = path.join(__dirname, '..', 'dist');
const androidAssetsDir = path.join(__dirname, '../../app/src/main/assets/amll');
const androidIndexPath = path.join(androidAssetsDir, 'index.html');
const cssPath = path.join(distDir, 'frontend.css');

console.log('📖 Embedding CSS into Android assets index.html...');

// 检查文件是否存在
if (!fs.existsSync(androidIndexPath)) {
  console.error('❌ Android assets index.html not found!');
  process.exit(1);
}

if (!fs.existsSync(cssPath)) {
  console.error('❌ frontend.css not found!');
  process.exit(1);
}

const cssContent = fs.readFileSync(cssPath, 'utf-8');
let androidHtmlContent = fs.readFileSync(androidIndexPath, 'utf-8');

// Replace <link rel="stylesheet" href="./frontend.css" /> with inline <style>
const linkTag = '<link rel="stylesheet" href="./frontend.css" />';
const styleTag = `<style>\n${cssContent}</style>`;

androidHtmlContent = androidHtmlContent.replace(linkTag, styleTag);

fs.writeFileSync(androidIndexPath, androidHtmlContent);

console.log('✅ CSS embedded successfully into Android assets!');
console.log(`   CSS size: ${(cssContent.length / 1024).toFixed(2)} KB`);
console.log(`   File updated: app/src/main/assets/amll/index.html`);
