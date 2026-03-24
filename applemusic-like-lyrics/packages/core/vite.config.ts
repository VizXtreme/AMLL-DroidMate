import { defineConfig } from "vite";
import dts from "vite-plugin-dts";
import wasm from "vite-plugin-wasm";
import path from "path";

export default defineConfig({
	build: {
		sourcemap: true,
		minify: false, // 禁用代码压缩，保持代码可读性
		lib: {
			entry: "src/index.ts",
			name: "AppleMusicLikeLyricsCore",
			fileName: "amll-core",
			formats: ["es"],
		},
		cssMinify: false, // 禁用 CSS 压缩
		rollupOptions: {
			external: [
				"@pixi/display",
				"@pixi/app",
				"@pixi/filter-blur",
				"@pixi/filter-color-matrix",
				"@pixi/filter-bulge-pinch",
				"@pixi/core",
				"@pixi/sprite",
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
	resolve: {
		alias: {
			"@applemusic-like-lyrics/lyric": path.resolve(__dirname, "../lyric/pkg"),
			"@applemusic-like-lyrics/ttml": path.resolve(__dirname, "../ttml/src"),
		},
	},
	plugins: [
		wasm(),
		dts({
			exclude: ["src/test.ts"],
		}),
	],
});
