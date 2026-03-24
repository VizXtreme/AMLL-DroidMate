import react from "@vitejs/plugin-react";
import jotaiDebugLabel from "jotai/babel/plugin-debug-label";
import jotaiReactRefresh from "jotai/babel/plugin-react-refresh";
import { defineConfig } from "vite";
import dts from "vite-plugin-dts";
import svgr from "vite-plugin-svgr";
import wasm from "vite-plugin-wasm";

const ReactCompilerConfig = {
	target: "18",
};

export default defineConfig({
	build: {
		sourcemap: true,
		minify: false, // 禁用代码压缩，保持代码可读性
		lib: {
			entry: "src/index.ts",
			name: "AppleMusicLikeLyricsReactFramework",
			fileName: "amll-react-framework",
			formats: ["es", "cjs"],
		},
		rollupOptions: {
			external: [
				"react",
				"react-dom",
				"react/jsx-runtime",
				"react-compiler-runtime",
				"@applemusic-like-lyrics/core",
				"jotai",
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
		cssMinify: false, // 禁用 CSS 压缩
	},
	css: {
		transformer: "lightningcss",
	},
	plugins: [
		wasm(),
		react({
			babel: {
				plugins: [
					["babel-plugin-react-compiler", ReactCompilerConfig],
					jotaiDebugLabel,
					jotaiReactRefresh,
				],
			},
		}),
		dts({
			exclude: ["src/test.tsx", "src/test-app.tsx"],
		}),
		svgr({
			svgrOptions: {
				ref: true,
			},
			include: ["./src/**/*.svg?react"],
		}),
	],
});
