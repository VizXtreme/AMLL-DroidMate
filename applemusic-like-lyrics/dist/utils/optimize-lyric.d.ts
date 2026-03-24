import { LyricLine, OptimizeLyricOptions } from '../interfaces.ts';
/**
 * 优化歌词行的展示效果
 *
 * 注意会直接原地修改入参，确保你已经提前深克隆了歌词行数组
 * @param lines 歌词行数组
 * @param options 优化的可选配置，默认全部开启
 */
export declare function optimizeLyricLines(lines: LyricLine[], options?: OptimizeLyricOptions): void;
//# sourceMappingURL=optimize-lyric.d.ts.map