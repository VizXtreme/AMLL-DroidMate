import { defineConfig } from 'vite'
import { resolve } from 'node:path'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],

  base: './',
  define: {
    global: 'globalThis',
    'process.env.NODE_ENV': JSON.stringify('production'),
  },
  build: {
    outDir: 'dist',
    emptyOutDir: true,
    sourcemap: false,
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
        // 确保 IIFE 格式正确导出
        exports: 'named',
      },
    },
  },
  resolve: {
    // 强制去重，确保运行时只有一个 React / React DOM 副本
    dedupe: ['react', 'react-dom', 'jotai'],
  },
  server: {
    port: 5173,
  },
})
