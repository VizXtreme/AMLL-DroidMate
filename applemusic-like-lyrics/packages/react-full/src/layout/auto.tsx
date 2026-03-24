import React from "react";
import type { HTMLProps } from "react";
import { useCallback, useLayoutEffect, useRef, useState, forwardRef } from "react";
import styles from "./auto.module.css";
import { HorizontalLayout } from "./horizontal";
import { VerticalLayout } from "./vertical";

/**
 * 会根据当前元素的宽高比自动选择横向或者纵向布局的组件
 */
export const AutoLyricLayout = forwardRef<HTMLDivElement, 
	{
		thumbSlot?: React.ReactNode;
		controlsSlot?: React.ReactNode;
		horizontalBottomControls?: React.ReactNode;
		smallControlsSlot?: React.ReactNode;
		bigControlsSlot?: React.ReactNode;
		coverSlot?: React.ReactNode;
		lyricSlot?: React.ReactNode;
		backgroundSlot?: React.ReactNode;
		hideLyric?: boolean;
		verticalImmerseCover?: boolean;
		onLayoutChange?: (isVertical: boolean) => void;
		onElementMounted?: (node: HTMLDivElement | null) => void;
	} & Omit<HTMLProps<HTMLDivElement>, 'thumbSlot' | 'controlsSlot' | 'horizontalBottomControls' | 'smallControlsSlot' | 'bigControlsSlot' | 'coverSlot' | 'lyricSlot' | 'backgroundSlot' | 'hideLyric' | 'verticalImmerseCover' | 'onLayoutChange' | 'onElementMounted'>
>(
	({
	thumbSlot,
	controlsSlot,
	horizontalBottomControls,
	smallControlsSlot,
	bigControlsSlot,
	coverSlot,
	lyricSlot,
	backgroundSlot,
	hideLyric,
	verticalImmerseCover,
	onLayoutChange,
	onElementMounted,
	...rest
}, ref) => {
	const [isVertical, setIsVertical] = useState(false);
	const rootRef = useRef<HTMLDivElement>(null);

	// 过滤掉不应该传递给 DOM 元素的自定义 props
	const { 
		lyricLines, 
		currentTime, 
		onLyricLineClick, 
		onLyricLineContextMenu,
		...domProps 
	} = rest as any;
	// 这些变量已被提取，不会传递给 DOM

	const setRefs = useCallback(
		(node: HTMLDivElement | null) => {
			rootRef.current = node;
			
			// Forward ref to parent
			if (ref) {
				if (typeof ref === 'function') {
					ref(node);
				} else {
					(ref as React.MutableRefObject<HTMLDivElement | null>).current = node;
				}
			}

			if (onElementMounted) {
				onElementMounted(node);
			}
		},
		[onElementMounted, ref],
	);

	useLayoutEffect(() => {
		const rootEl = rootRef.current;
		if (!rootEl) return;

		setIsVertical(rootEl.clientWidth < rootEl.clientHeight);

		const obz = new ResizeObserver(() => {
			const rootB = rootEl.getBoundingClientRect();
			setIsVertical(rootB.width < rootB.height);
		});
		obz.observe(rootEl);
		return () => obz.disconnect();
	}, []);

	useLayoutEffect(() => {
		onLayoutChange?.(isVertical);
	}, [isVertical, onLayoutChange]);

	// 如果分开使用两个布局，会导致不能衔接背景组件，导致两者间切换有闪屏情况
	// 如果直接使用背景并各套一个 div，会导致无法应用 plus-lighter 效果
	// 故借助 display: contents 来融合布局

	return (
		<div {...domProps} ref={setRefs}>
			<div className={styles.background}>{backgroundSlot}</div>
			{isVertical ? (
				<VerticalLayout
					thumbSlot={thumbSlot}
					smallControlsSlot={smallControlsSlot}
					bigControlsSlot={bigControlsSlot}
					coverSlot={coverSlot}
					lyricSlot={lyricSlot}
					hideLyric={hideLyric}
					immerseCover={verticalImmerseCover}
				/>
			) : (
				<HorizontalLayout
					thumbSlot={thumbSlot}
					controlsSlot={controlsSlot}
					coverSlot={coverSlot}
					lyricSlot={lyricSlot}
					bottomControls={horizontalBottomControls}
					hideLyric={hideLyric}
				/>
			)}
		</div>
	);
});
