/**
 * 构建后处理脚本：将 CSS 嵌入到 Android assets 的 index.html 中
 * 
 * 这个脚本的作用是将构建生成的 frontend.css 文件内容直接嵌入到 HTML 文件中，
 * 这样可以减少 HTTP 请求数量，提高加载速度。
 * 
 * 工作流程：
 * 1. 读取构建生成的 frontend.css 文件
 * 2. 读取 Android assets 目录中的 index.html
 * 3. 将 <link> 标签替换为内联的 <style> 标签
 * 4. 写回修改后的 HTML 文件
 */
const fs = require('fs');
const path = require('path');

// ==================== 路径定义 ====================
// dist 目录：Vite 构建产物输出位置
const distDir = path.join(__dirname, '..', 'dist');
// Android assets 目录：Android 应用的静态资源目录
const androidAssetsDir = path.join(__dirname, '../../app/src/main/assets/amll');
// Android index.html 完整路径
const androidIndexPath = path.join(androidAssetsDir, 'index.html');
// CSS 文件路径
const cssPath = path.join(distDir, 'frontend.css');

console.log('📖 Embedding CSS into Android assets index.html...');

// ==================== 文件存在性检查 ====================
// 检查 Android assets 的 index.html 是否存在
if (!fs.existsSync(androidIndexPath)) {
  console.error('❌ Android assets index.html not found!');
  process.exit(1);
}

// 检查 frontend.css 是否存在
if (!fs.existsSync(cssPath)) {
  console.error('❌ frontend.css not found!');
  process.exit(1);
}

// ==================== 读取文件内容 ====================
// 读取 CSS 文件内容
const cssContent = fs.readFileSync(cssPath, 'utf-8');
// 读取 HTML 文件内容
let androidHtmlContent = fs.readFileSync(androidIndexPath, 'utf-8');

// ==================== 替换 <link> 标签为内联 <style> ====================
// 原始的 <link> 标签引用外部 CSS 文件
const linkTag = '<link rel="stylesheet" href="./frontend.css" />';
// 新的内联 <style> 标签，包含完整的 CSS 内容
const styleTag = `<style>\n${cssContent}</style>`;

// 执行替换操作
androidHtmlContent = androidHtmlContent.replace(linkTag, styleTag);

// ==================== 写回文件 ====================
// 将修改后的 HTML 内容写入 Android assets
fs.writeFileSync(androidIndexPath, androidHtmlContent);

console.log('✅ CSS embedded successfully into Android assets!');
console.log(`   CSS size: ${(cssContent.length / 1024).toFixed(2)} KB`);
console.log(`   File updated: app/src/main/assets/amll/index.html`);
