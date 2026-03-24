import { LyricLine, LyricWord } from '../../interfaces.ts';
import { LyricLineBase } from '../base.ts';
import { LyricLineRenderMode } from '../index.ts';
import { DomLyricPlayer } from '.';
interface RealWord extends LyricWord {
    mainElement: HTMLSpanElement;
    subElements: HTMLSpanElement[];
    elementAnimations: Animation[];
    maskAnimations: Animation[];
    width: number;
    height: number;
    padding: number;
    shouldEmphasize: boolean;
}
export declare class RawLyricLineMouseEvent extends MouseEvent {
    readonly line: LyricLineBase;
    constructor(line: LyricLineBase, event: MouseEvent);
}
type MouseEventMap = {
    [evt in keyof HTMLElementEventMap]: HTMLElementEventMap[evt] extends MouseEvent ? evt : never;
};
type MouseEventTypes = MouseEventMap[keyof MouseEventMap];
type MouseEventListener = (this: LyricLineEl, ev: RawLyricLineMouseEvent) => void;
export declare class LyricLineEl extends LyricLineBase {
    private lyricPlayer;
    private lyricLine;
    private element;
    private splittedWords;
    private built;
    lineSize: number[];
    private renderMode;
    private currentBrightAlpha;
    private currentDarkAlpha;
    private targetBrightAlpha;
    private targetDarkAlpha;
    constructor(lyricPlayer: DomLyricPlayer, lyricLine?: LyricLine);
    private listenersMap;
    private readonly onMouseEvent;
    addMouseEventListener(type: MouseEventTypes, callback: MouseEventListener | null, options?: boolean | AddEventListenerOptions | undefined): void;
    removeMouseEventListener(type: MouseEventTypes, callback: MouseEventListener | null, options?: boolean | EventListenerOptions | undefined): void;
    areWordsOnSameLine(word1: RealWord, word2: RealWord): boolean;
    private isEnabled;
    enable(maskAnimationTime?: number, shouldPlay?: boolean): Promise<void>;
    disable(): void;
    private lastWord?;
    resume(): Promise<void>;
    pause(): Promise<void>;
    setMaskAnimationState(maskAnimationTime?: number): void;
    getLine(): LyricLine;
    private _prevParentEl;
    private lastStyle;
    show(): void;
    hide(): void;
    private rebuildStyle;
    rebuildElement(): void;
    /** 设置翻译与音译行文本 */
    private setSubLinesText;
    private getRubyCharCount;
    private getRubySegments;
    private createWord;
    private buildWord;
    private initFloatAnimation;
    private initEmphasizeAnimation;
    private get totalDuration();
    onLineSizeChange(_size: [number, number]): void;
    updateMaskImageSync(): void;
    private generateCalcBasedMaskImage;
    private generateWebAnimationBasedMaskImage;
    getElement(): HTMLElement;
    private updateMaskAlphaTargets;
    private applyAlphaToDom;
    setTransform(top?: number, scale?: number, opacity?: number, blur?: number, force?: boolean, delay?: number, mode?: LyricLineRenderMode): void;
    update(delta?: number): void;
    _getDebugTargetPos(): string;
    get isInSight(): boolean;
    private disposeElements;
    dispose(): void;
}
export {};
//# sourceMappingURL=lyric-line.d.ts.map