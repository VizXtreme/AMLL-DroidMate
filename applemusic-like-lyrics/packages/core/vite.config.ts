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
			output: {
				manualChunks: undefined, // 禁用代码分割，确保单一 CSS 输出
				inlineDynamicImports: true, // 内联动态导入
				minifyInternalExports: false, // 禁用内部导出的压缩
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
