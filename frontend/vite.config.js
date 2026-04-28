/**
 * Vite 构建配置文件
 * 
 * Vite 是一个现代的前端构建工具，提供快速的开发服务器和优化的生产构建。
 * 这个配置文件定义了如何打包 AMLL React 前端应用，以便嵌入到 Android WebView 中使用。
 * 
 * 主要配置项：
 * - React 插件支持 JSX/TSX
 * - IIFE 格式输出（适合直接在 HTML 中使用）
 * - CSS 内联处理
 * - Source Map 生成（便于调试）
 */
import { defineConfig } from 'vite'
import { resolve } from 'node:path'
import react from '@vitejs/plugin-react'

// 使用 defineConfig 定义配置，获得更好的 TypeScript 类型提示
export default defineConfig({
  // ==================== 插件配置 ====================
  // React 插件：支持 JSX/TSX 语法转换
  plugins: [react()],
  // ==================== 基础路径 ====================
  // 设置资源引用的基础路径为相对路径 './'
  // 这样在 Android WebView 中加载时可以正确找到资源
  base: './',
  // ==================== 全局变量定义 ====================
  // 定义全局常量，确保在不同环境下的兼容性
  define: {
    global: 'globalThis',  // 将 global 映射到 globalThis（标准的全局对象）
    'process.env.NODE_ENV': JSON.stringify('development'),  // 设置 Node 环境变量为开发模式
  },
  // ==================== 构建设置 ====================
  build: {
    outDir: 'dist',  // 构建产物输出目录
    emptyOutDir: true,  // 构建前清空输出目录
    sourcemap: true,  // 生成 Source Map 文件（便于调试）
    cssCodeSplit: false,  // 不拆分 CSS 文件，保持单一 CSS 文件
    minify: false,  // 不压缩代码（保留可读性，便于调试）
    
    // 库模式配置：打包为独立的库文件
    lib: {
      entry: resolve(__dirname, 'src/main.tsx'),  // 入口文件
      name: 'AMLLBundle',  // 全局变量名（在 HTML 中通过 window.AMLLBundle 访问）
      formats: ['iife'],  // 输出格式：IIFE（立即执行函数表达式）
      fileName: () => 'amll.bundle.js',  // 输出文件名（固定为 amll.bundle.js）
    },
  },
  // ==================== 模块解析配置 ====================
  resolve: {
    // 强制去重，确保运行时只有一个 React / React DOM 副本
    // 这很重要，因为多个 React 副本会导致运行时错误
    dedupe: ['react', 'react-dom', 'jotai'],
  },
  // ==================== 开发服务器配置 ====================
  server: {
    port: 5173,  // 开发服务器监听的端口号
  },
})
