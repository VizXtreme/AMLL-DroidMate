import react from "@vitejs/plugin-react";
import { defineConfig } from "vite";
import dts from "vite-plugin-dts";

export default defineConfig({
	build: {
		sourcemap: true,
		minify: false, // 禁用代码压缩，保持代码可读性
		lib: {
			entry: "src/index.ts",
			name: "AppleMusicLikeLyricsReact",
			fileName: "amll-react",
			formats: ["es"],
		},
		rollupOptions: {
			external: [
				"react",
				"react-dom",
				"react/jsx-runtime",
				"@applemusic-like-lyrics/core",
			],
			output: {
				minifyInternalExports: false, // 禁用内部导出的压缩
				compact: false, // 保持多行格式
				indent: '  ', // 设置缩进为 2 个空格
				generatedCode: {
					constBindings: true, // 使用 const 绑定
					arrowFunctions: false, // 不使用箭头函数
					objectShorthand: false // 不使用对象简写
				}
			}
		},
	},
	plugins: [
		react(),
		dts({
			exclude: ["src/test.tsx", "src/test-app.tsx"],
		}),
	],
});
