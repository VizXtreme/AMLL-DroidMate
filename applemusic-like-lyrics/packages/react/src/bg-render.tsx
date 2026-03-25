import {
	type AbstractBaseRenderer,
	type BaseRenderer,
	BackgroundRender as CoreBackgroundRender,
	MeshGradientRenderer,
} from "@applemusic-like-lyrics/core";
import {
	forwardRef,
	type HTMLProps,
	useEffect,
	useImperativeHandle,
	useLayoutEffect,
	useRef,
} from "react";

export {
	BaseRenderer,
	MeshGradientRenderer,
	PixiRenderer,
} from "@applemusic-like-lyrics/core";

/**
 * 背景渲染组件的属性
 */
export interface BackgroundRenderProps {
	/**
	 * 设置背景专辑资源
	 */
	album?: string | HTMLImageElement | HTMLVideoElement;
	/**
	 * 设置专辑资源是否为视频
	 */
	albumIsVideo?: boolean;
	/**
	 * 设置当前背景动画帧率，如果为 `undefined` 则默认为 `30`
	 */
	fps?: number;
	/**
	 * 设置当前播放状态，如果为 `undefined` 则默认为 `true`
	 */
	playing?: boolean;
	/**
	 * 设置当前动画流动速度，如果为 `undefined` 则默认为 `2`
	 */
	flowSpeed?: number;
	/**
	 * 设置背景是否根据“是否有歌词”这个特征调整自身效果，例如有歌词时会变得更加活跃
	 *
	 * 部分渲染器会根据这个特征调整自身效果
	 *
	 * 如果不确定是否需要赋值或无法知晓是否包含歌词，请传入 true 或不做任何处理（默认值为 true）
	 */
	hasLyric?: boolean;
	/**
	 * 设置低频的音量大小，范围在 80hz-120hz 之间为宜，取值范围在 [0.0-1.0] 之间
	 *
	 * 部分渲染器会根据音量大小调整背景效果（例如根据鼓点跳动）
	 *
	 * 如果无法获取到类似的数据，请传入 undefined 或 1.0 作为默认值，或不做任何处理（默认值即 1.0）
	 */
	lowFreqVolume?: number;
	/**
	 * 设置当前渲染缩放比例，如果为 `undefined` 则默认为 `0.5`
	 */
	renderScale?: number;
	/**
	 * 是否启用静态模式，即图片在更换后就会保持静止状态并禁用更新，以节省性能
	 * 默认为 `false`
	 */
	staticMode?: boolean;
	/**
	 * 设置渲染器，如果为 `undefined` 则默认为 `PixiRenderer`
	 * 默认渲染器有可能会随着版本更新而更换
	 */
	renderer?: {
		new (...args: ConstructorParameters<typeof BaseRenderer>): BaseRenderer;
	};
}

/**
 * 背景渲染组件的引用
 */
export interface BackgroundRenderRef {
	/**
	 * 背景渲染实例引用
	 */
	bgRender?: AbstractBaseRenderer;
	/**
	 * 将背景渲染实例的元素包裹起来的 DIV 元素实例
	 */
	wrapperEl: HTMLDivElement | null;
}

/**
 * 流体背景渲染组件，通过提供图片链接可以显示出酷似 Apple Music 的流体背景效果
 */
export const BackgroundRender = forwardRef<
	BackgroundRenderRef,
	HTMLProps<HTMLDivElement> & BackgroundRenderProps
>(
	(
		{
			album,
			albumIsVideo,
			fps,
			playing,
			flowSpeed,
			renderScale,
			staticMode,
			lowFreqVolume,
			hasLyric,
			renderer,
			style,
			...props
		},
		ref,
	) => {
		const coreBGRenderRef = useRef<AbstractBaseRenderer>(null);
		const wrapperRef = useRef<HTMLDivElement>(null);
		const lastRendererRef = useRef<{
			new (canvas: HTMLCanvasElement): BaseRenderer;
		}>(null);
		const curRenderer = renderer ?? MeshGradientRenderer;
		const canvasRef = useRef<HTMLCanvasElement | null>(null);

		// 添加调试日志
		console.log('[BackgroundRender] Component rendered, curRenderer:', curRenderer?.name);
		console.log('[BackgroundRender] coreBGRenderRef.current:', coreBGRenderRef.current);
		console.log('[BackgroundRender] wrapperRef.current:', wrapperRef.current);

		useEffect(() => {
			console.log('[BackgroundRender] Renderer change effect triggered');
			if (
				lastRendererRef.current !== curRenderer ||
				coreBGRenderRef.current === undefined
			) {
				console.log('[BackgroundRender] Creating new renderer, old:', lastRendererRef.current, 'new:', curRenderer?.name);
				lastRendererRef.current = curRenderer;
				// 清理旧的 canvas 元素
				if (wrapperRef.current) {
					console.log('[BackgroundRender] Clearing old canvas from wrapper');
					wrapperRef.current.innerHTML = '';
				}
				coreBGRenderRef.current?.dispose();
				console.log('[BackgroundRender] Creating new CoreBackgroundRender instance');
				coreBGRenderRef.current = CoreBackgroundRender.new(curRenderer);
			}
		}, [curRenderer]);

		useEffect(() => {
			console.log('[BackgroundRender] setAlbum effect triggered, album:', album ? 'exists' : 'null');
			if (curRenderer && album) {
				console.log('[BackgroundRender] Calling setAlbum with:', album);
				console.log('[BackgroundRender] albumIsVideo:', albumIsVideo);
				console.log('[BackgroundRender] coreBGRenderRef.current:', coreBGRenderRef.current);
				console.log('[BackgroundRender] setAlbum function:', coreBGRenderRef.current?.setAlbum);
				coreBGRenderRef.current?.setAlbum(album, albumIsVideo);
				// 检查 setAlbum 后是否有错误
				setTimeout(() => {
					const canvas = coreBGRenderRef.current?.getElement() as HTMLCanvasElement;
					if (canvas) {
						console.log('[BackgroundRender] Canvas after setAlbum:', {
							width: canvas.width,
							height: canvas.height,
							hasContext: !!canvas.getContext('webgl')
						});
						// 检查渲染器状态
						const renderer = (coreBGRenderRef.current as any)?.renderer;
						console.log('[BackgroundRender] Renderer state after setAlbum:', {
							paused: renderer?.paused,
							frameCount: renderer?.frameCount,
							texture: renderer?.fboTexture ? 'exists' : 'null'
						});
					}
				}, 500);
			} else {
				console.log('[BackgroundRender] setAlbum NOT called - curRenderer:', !!curRenderer, 'album:', !!album);
			}
		}, [curRenderer, album, albumIsVideo]);

		useEffect(() => {
			if (curRenderer && fps) coreBGRenderRef.current?.setFPS(fps);
		}, [curRenderer, fps]);

		useEffect(() => {
			if (!curRenderer) return;
			if (playing === undefined) {
				coreBGRenderRef.current?.resume();
			} else if (playing) {
				coreBGRenderRef.current?.resume();
			} else {
				coreBGRenderRef.current?.pause();
			}
		}, [curRenderer, playing]);

		useEffect(() => {
			if (!curRenderer) return;
			if (flowSpeed) coreBGRenderRef.current?.setFlowSpeed(flowSpeed);
		}, [curRenderer, flowSpeed]);

		useEffect(() => {
			if (!curRenderer) return;
			coreBGRenderRef.current?.setStaticMode(staticMode ?? false);
		}, [curRenderer, staticMode]);

		useEffect(() => {
			if (curRenderer && renderScale)
				coreBGRenderRef.current?.setRenderScale(renderScale ?? 0.5);
		}, [curRenderer, renderScale]);

		useEffect(() => {
			if (curRenderer && lowFreqVolume)
				coreBGRenderRef.current?.setLowFreqVolume(lowFreqVolume ?? 1.0);
		}, [curRenderer, lowFreqVolume]);

		useEffect(() => {
			if (curRenderer && hasLyric !== undefined)
				coreBGRenderRef.current?.setHasLyric(hasLyric ?? true);
		}, [curRenderer, hasLyric]);

		// biome-ignore lint/correctness/useExhaustiveDependencies: coreBGRenderRef.current
		useLayoutEffect(() => {
			console.log('[BackgroundRender] useLayoutEffect for canvas mounting triggered');
			console.log('[BackgroundRender] Checking conditions - coreBGRenderRef:', !!coreBGRenderRef.current, 'wrapperRef:', !!wrapperRef.current);
			
			if (coreBGRenderRef.current && wrapperRef.current) {
				const el = coreBGRenderRef.current.getElement();
				console.log('[BackgroundRender] Got canvas element:', el);
				console.log('[BackgroundRender] Canvas tagName:', el?.tagName);
				console.log('[BackgroundRender] Canvas type:', typeof el);
				console.log('[BackgroundRender] Canvas parent before append:', el?.parentElement);
				const canvasEl = el as HTMLCanvasElement;
				console.log('[BackgroundRender] Canvas dimensions:', { width: canvasEl?.width, height: canvasEl?.height });
				console.log('[BackgroundRender] Canvas attributes:', {
					width: canvasEl.getAttribute('width'),
					height: canvasEl.getAttribute('height'),
					styleWidth: canvasEl.style.width,
					styleHeight: canvasEl.style.height
				});
				
				if (el) {
					el.style.width = "100%";
					el.style.height = "100%";
					el.style.minHeight = "0";
					el.style.minWidth = "0";
					el.style.overflow = "hidden";
					// 保存 canvas 引用
					canvasRef.current = el as HTMLCanvasElement;
					console.log('[BackgroundRender] About to append canvas to wrapper');
					console.log('[BackgroundRender] Wrapper before append:', wrapperRef.current);
					console.log('[BackgroundRender] Wrapper children count before:', wrapperRef.current.children.length);
					// 直接添加，不清空（React 会处理）
					wrapperRef.current.appendChild(el);
					console.log('[BackgroundRender] Canvas appended successfully');
					console.log('[BackgroundRender] Wrapper child count after:', wrapperRef.current.children.length);
					console.log('[BackgroundRender] Canvas parent after append:', el.parentElement);
					const computedStyle = window.getComputedStyle(el);
					console.log('[BackgroundRender] Canvas computed styles:', {
						display: computedStyle.display,
						visibility: computedStyle.visibility,
						position: computedStyle.position,
						width: computedStyle.width,
						height: computedStyle.height,
						zIndex: computedStyle.zIndex
					});
					// 打印完整的样式对象字符串
					console.log('[BackgroundRender] Canvas style attribute:', el.getAttribute('style'));
					console.log('[BackgroundRender] Canvas inline styles:', {
						width: el.style.width,
						height: el.style.height,
						position: el.style.position,
						display: el.style.display,
						minHeight: el.style.minHeight,
						minWidth: el.style.minWidth,
						overflow: el.style.overflow
					});
					console.log('[BackgroundRender] Canvas element offsetWidth:', el.offsetWidth, 'offsetHeight:', el.offsetHeight);
					console.log('[BackgroundRender] Canvas element clientWidth:', el.clientWidth, 'clientHeight:', el.clientHeight);
					
					// 检查 WebGL 上下文
					const glContext = (el as HTMLCanvasElement).getContext('webgl') || (el as HTMLCanvasElement).getContext('2d');
					console.log('[BackgroundRender] Canvas context:', glContext ? 'OK' : 'NULL');
					console.log('[BackgroundRender] Renderer type:', coreBGRenderRef.current.constructor?.name);
					console.log('[BackgroundRender] Renderer object:', coreBGRenderRef.current);
					
				} else {
					console.error('[BackgroundRender] ERROR: getElement() returned null!');
				}
				// 清理函数
				return () => {
					console.log('[BackgroundRender] Cleanup: removing canvas');
					if (wrapperRef.current && canvasRef.current) {
						wrapperRef.current.removeChild(canvasRef.current);
						console.log('[BackgroundRender] Canvas removed');
						canvasRef.current = null;
					}
				};
			} else {
				console.log('[BackgroundRender] useLayoutEffect skipped - coreBGRenderRef:', !!coreBGRenderRef.current, 'wrapperRef:', !!wrapperRef.current);
			}
		}, [coreBGRenderRef.current]);

		// biome-ignore lint/correctness/useExhaustiveDependencies: wrapperRef.current, coreBGRenderRef.current
		useImperativeHandle(
			ref,
			() => ({
				wrapperEl: wrapperRef.current,
				bgRender: coreBGRenderRef.current!,
			}),
			[wrapperRef.current, coreBGRenderRef.current],
		);

		// biome-ignore lint/correctness/useExhaustiveDependencies: wrapperRef.current
		useEffect(() => {
			if (wrapperRef.current) {
				const computedStyle = window.getComputedStyle(wrapperRef.current);
				const parent = wrapperRef.current.parentElement;
				console.log('[BackgroundRender] Wrapper div mounted, dimensions:', {
					offsetWidth: wrapperRef.current.offsetWidth,
					offsetHeight: wrapperRef.current.offsetHeight,
					clientWidth: wrapperRef.current.clientWidth,
					clientHeight: wrapperRef.current.clientHeight,
					children: wrapperRef.current.children.length
				});
				console.log('[BackgroundRender] Wrapper computed styles:', {
					width: computedStyle.width,
					height: computedStyle.height,
					position: computedStyle.position,
					display: computedStyle.display,
					inset: computedStyle.inset
				});
				console.log('[BackgroundRender] Wrapper inline styles:', {
					width: wrapperRef.current.style.width,
					height: wrapperRef.current.style.height,
					position: wrapperRef.current.style.position,
					inset: wrapperRef.current.style.inset
				});
				// 打印父元素信息
				if (parent) {
					const parentStyle = window.getComputedStyle(parent);
					console.log('[BackgroundRender] Parent element info:', {
						tagName: parent.tagName,
						id: parent.id,
						className: parent.className,
						offsetWidth: parent.offsetWidth,
						offsetHeight: parent.offsetHeight,
						width: parentStyle.width,
						height: parentStyle.height
					});
				}
				// 打印窗口尺寸
				console.log('[BackgroundRender] Window size:', {
					innerWidth: window.innerWidth,
					innerHeight: window.innerHeight,
					documentElementWidth: document.documentElement?.clientWidth,
					documentElementHeight: document.documentElement?.clientHeight,
					bodyWidth: document.body?.clientWidth,
					bodyHeight: document.body?.clientHeight
				});
			} else {
				console.log('[BackgroundRender] Wrapper div not ready');
			}
		}, [wrapperRef.current]);

		return (
			<div
				ref={wrapperRef}
				style={{
					position: "absolute",
					inset: 0,
					width: "100%",
					height: "100%",
					overflow: "hidden",
					...style,
				}}
				{...props}
			/>
		);
	},
);
