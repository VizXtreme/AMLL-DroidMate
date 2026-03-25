const fs = require('fs');
const path = require('path');

const distDir = path.join(__dirname, '..', 'dist');
const indexPath = path.join(distDir, 'index.html');
const cssPath = path.join(distDir, 'frontend.css');

console.log('📖 Embedding CSS into index.html...');

const cssContent = fs.readFileSync(cssPath, 'utf-8');
let htmlContent = fs.readFileSync(indexPath, 'utf-8');

// Replace <link rel="stylesheet" href="./frontend.css" /> with inline <style>
const linkTag = '<link rel="stylesheet" href="./frontend.css" />';
const styleTag = `<style>\n${cssContent}</style>`;

htmlContent = htmlContent.replace(linkTag, styleTag);

fs.writeFileSync(indexPath, htmlContent);
console.log('✅ CSS embedded successfully!');
console.log(`   CSS size: ${(cssContent.length / 1024).toFixed(2)} KB`);
