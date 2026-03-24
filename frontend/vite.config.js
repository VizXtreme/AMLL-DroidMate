import { defineConfig } from 'vite'
import { resolve } from 'node:path'
import react from '@vitejs/plugin-react'
import fs from 'fs'

export default defineConfig({
  plugins: [
    react(),
    {
      name: 'fix-react-refs',
      closeBundle() {
        // 构建完成后自动修复 bundle 中的 React 引用
        const bundlePath = resolve(__dirname, 'dist/amll.bundle.js')
        if (fs.existsSync(bundlePath)) {
          let content = fs.readFileSync(bundlePath, 'utf8')
          // 将 AutoLyricLayout 和 PrebuiltLyricPlayer 的 React.forwardRef 替换为 reactExports.forwardRef
          content = content.replace(
            /var AutoLyricLayout = React\.forwardRef/g,
            'var AutoLyricLayout = reactExports.forwardRef'
          )
          content = content.replace(
            /var PrebuiltLyricPlayer = React\$1\.forwardRef/g,
            'var PrebuiltLyricPlayer = reactExports.forwardRef'
          )
          fs.writeFileSync(bundlePath, content, 'utf8')
          console.log('✓ Fixed React references in amll.bundle.js')
        }
      },
    },
  ],

  base: './',
  define: {
    global: 'globalThis',
    'process.env.NODE_ENV': JSON.stringify('development'),
  },
  build: {
    outDir: 'dist',
    emptyOutDir: true,
    sourcemap: true, // 启用 source map 便于调试
    cssCodeSplit: false,
    minify: false, // 禁用代码压缩，保持代码可读性
    lib: {
      entry: resolve(__dirname, 'src/main.tsx'),
      name: 'AMLLBundle',
      formats: ['iife'],
      fileName: () => 'amll.bundle.js',
    },
    rollupOptions: {
      output: {
        indent: '  ', // 使用 2 空格缩进
        compact: false, // 不紧凑排列
        minifyInternalExports: false, // 禁用内部导出的压缩
      },
    },
    // 完全禁用压缩相关的优化
    target: 'esnext',
    polyfillModulePreload: false,
  },
  // 禁用 Vite 的预打包优化，避免依赖被压缩
  optimizeDeps: {
    noDiscovery: true,
    include: ['react', 'react-dom'],
  },
  resolve: {
    // 强制去重，确保运行时只有一个 React / React DOM 副本
    dedupe: ['react', 'react-dom', 'jotai'],
  },
  server: {
    port: 5173,
  },
})
