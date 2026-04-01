/**
 * AMLL Bridge Protocol V2 - JavaScript/TypeScript 实现
 * 
 * **协议用途**：
 * 用于 WebView 端（前端）与 Android 原生代码之间的双向通信。
 * 实现了完整的握手、状态同步和命令控制机制。
 * 
 * **主要功能**：
 * 1. 协议握手：检测 Android 桥接对象并协商协议版本
 * 2. 状态更新：接收来自 Android 的播放状态、歌词、专辑封面等信息
 * 3. 命令发送：向 Android 发送跳转、暂停、播放、音量控制等命令
 * 4. 事件系统：支持订阅/发布模式的事件监听
 * 
 * **使用方式**：
 * 1. 在 AMLL 前端初始化时会自动创建全局实例 (window.AMLLBridge)
 * 2. 监听来自 Android 的消息（通过事件系统）
 * 3. 使用统一的 API 发送命令和接收状态更新
 * 
 * @example
 * // 监听播放进度
 * AMLLBridge.on('progressUpdate', (data) => {
 *   console.log('播放进度:', data.progress);
 * });
 * 
 * // 点击歌词行
 * AMLLBridge.lyricLineClicked(index, time);
 */

// ==================== 类型定义 ====================
// 这些接口定义了 Android 与 WebView 之间通信的消息格式

/**
 * 握手请求消息
 * 
 * 由 WebView 发送给 Android，声明自己的能力和协议版本
 */
interface HandshakeRequest {
    type: 'handshake';           // 消息类型：握手
    version: number;             // 协议版本号（当前为 2）
    capabilities: string[];      // 支持的功能列表
}

/**
 * 握手响应消息
 * 
 * 由 Android 回复给 WebView，确认是否接受握手
 */
interface HandshakeResponse {
    type: 'handshake_ack';       // 消息类型：握手确认
    version: number;             // 协商后的协议版本
    accepted: boolean;           // 是否接受握手
    serverCapabilities: string[]; // Android 端支持的功能列表
    message?: string;            // 可选的拒绝原因说明
}

/**
 * 状态更新消息
 * 
 * 由 Android 发送给 WebView，通知播放状态变化
 */
interface StateUpdateMessage {
    type: 'stateUpdate';         // 消息类型：状态更新
    version: number;             // 协议版本
    payload: StatePayload;       // 具体的状态数据
}

/**
 * 命令消息
 * 
 * 由 WebView 发送给 Android，执行控制操作
 */
interface CommandMessage {
    type: 'command';             // 消息类型：命令
    version: number;             // 协议版本
    payload: CommandPayload;     // 具体的命令数据
}

/**
 * 状态载荷联合类型
 * 
 * 定义了所有可能的状态更新类型：
 * - setMusicInfo: 歌曲信息变化
 * - setLyric: 歌词数据更新
 * - progress: 播放进度更新
 * - paused/resumed: 播放状态变化
 * - volumeChanged: 音量变化
 */
type StatePayload =
    | { updateType: 'setMusicInfo'; musicId: string; musicName: string; albumName: string; artistName: string; duration: number }
    | { updateType: 'setLyric'; format: string; data: string }
    | { updateType: 'progress'; progress: number }
    | { updateType: 'paused' }
    | { updateType: 'resumed' }
    | { updateType: 'volumeChanged'; volume: number };

/**
 * 命令载荷联合类型
 * 
 * 定义了所有可以发送到 Android 的命令：
 * - seekPlayProgress: 跳转到指定时间
 * - pause/resume: 暂停/播放控制
 * - setVolume: 设置音量
 * - lyricLineClicked: 用户点击歌词行
 */
type CommandPayload =
    | { command: 'seekPlayProgress'; progress: number }
    | { command: 'pause' }
    | { command: 'resume' }
    | { command: 'setVolume'; volume: number }
    | { command: 'lyricLineClicked'; lineIndex: number; time: number };

// 所有消息类型的联合（用于类型检查）
type BridgeMessage = StateUpdateMessage | CommandMessage | HandshakeRequest | HandshakeResponse;

// ==================== 协议版本常量 ====================
const PROTOCOL_VERSION = 2;  // 当前使用 V2 版本（JSON 格式）

// ==================== Bridge Manager 类 ====================
/**
 * AMLL 桥接管理器 V2
 * 
 * 这是整个桥接系统的核心类，负责管理 Android 与 WebView 之间的所有通信。
 * 
 * **设计模式**：
 * - 单例模式：全局只有一个实例 (window.AMLLBridge)
 * - 观察者模式：支持事件订阅/发布
 * - 队列模式：握手完成前将命令入队等待
 * 
 * **生命周期**：
 * 1. 页面加载时自动创建实例
 * 2. 自动发起握手流程
 * 3. 握手成功后处理消息队列
 * 4. 持续监听和转发消息
 */
class AMLLBridgeManagerV2 {
    // ==================== 私有属性 ====================
    
    /** Android 桥接对象（WebView JavascriptInterface） */
    private androidBridge: any;
    
    /** 协议是否已协商完成 */
    private protocolNegotiated = false;
    
    /** 客户端（WebView）支持的能力列表 */
    private clientCapabilities: string[] = [];
    
    /** 服务端（Android）支持的能力列表 */
    private serverCapabilities: string[] = [];
    
    /** 消息队列（握手完成前暂存命令） */
    private messageQueue: CommandMessage[] = [];
    
    /** 事件监听器映射表（事件名 -> 回调函数集合） */
    private listeners: Map<string, Set<Function>> = new Map();
    
    /**
     * 构造函数
     * 
     * 在实例化时自动检测 Android 桥接对象并初始化
     */
    constructor() {
        // 检查 Android 桥接对象是否存在
        if ((window as any).AndroidBridge) {
            this.androidBridge = (window as any).AndroidBridge;
            console.log('[AMLL Bridge] Android Bridge detected');
        } else {
            // 如果没有找到桥接对象，说明是在浏览器中独立运行
            console.warn('[AMLL Bridge] Android Bridge not found, running in standalone mode');
        }
        
        // 自动初始化（发起握手等）
        this.init();
    }
    
    /**
     * 初始化桥接管理器
     * 
     * 等待页面加载完成后发起握手流程
     */
    private init() {
        // 如果页面还在加载中，等待 DOMContentLoaded 事件
        if (document.readyState === 'loading') {
            document.addEventListener('DOMContentLoaded', () => this.initiateHandshake());
        } else {
            // 页面已加载完成，立即发起握手
            this.initiateHandshake();
        }
    }
    
    /**
     * 主动发起握手（类似 Tauri 的 Initialize）
     * 
     * 握手流程：
     * 1. 创建握手请求消息，包含协议版本和能力列表
     * 2. 通过 postMessage 发送到 Android
     * 3. 等待 Android 回复 handshake_ack
     * 4. 根据响应决定是否启用协议功能
     */
    public initiateHandshake() {
        // 没有桥接对象则无法握手
        if (!this.androidBridge) {
            console.warn('[AMLL Bridge] Cannot handshake without AndroidBridge');
            return;
        }
        
        // 构建握手请求
        const request: HandshakeRequest = {
            type: 'handshake',
            version: PROTOCOL_VERSION,  // 声明使用 V2 协议
            capabilities: [
                'ttml_lyrics',          // 支持 TTML 格式歌词
                'word_by_word',         // 支持逐字歌词
                'album_art',            // 支持专辑封面
                'audio_visualization'   // 支持音频可视化
            ]
        };
        
        console.log('[AMLL Bridge] Initiating handshake:', request);
        this.postMessage(request);
    }
    
    /**
     * 接收来自 Android 的消息
     * 
     * 这个方法会被 Android 的 JavascriptInterface 调用，
     * 负责解析 JSON 消息并分发到相应的处理器。
     * 
     * @param jsonString Android 发送的 JSON 字符串
     */
    public receiveFromAndroid(jsonString: string) {
        try {
            // 解析 JSON 消息
            const message: BridgeMessage = JSON.parse(jsonString);
            console.log('[AMLL Bridge] Received from Android:', message);
            
            // 处理握手响应
            if (message.type === 'handshake_ack') {
                this.handleHandshakeResponse(message as HandshakeResponse);
                return;
            }
            
            // 处理状态更新
            if (message.type === 'stateUpdate') {
                this.handleStateUpdate(message as StateUpdateMessage);
            }
        } catch (error) {
            // JSON 解析失败时记录错误
            console.error('[AMLL Bridge] Error parsing message:', error);
        }
    }
    
    /**
     * 处理握手响应
     * 
     * 当 Android 回复握手确认时调用此方法。
     * 如果握手成功，会标记协议已协商并发送队列中的消息。
     * 
     * @param response Android 返回的握手响应
     */
    private handleHandshakeResponse(response: HandshakeResponse) {
        if (response.accepted) {
            // ✅ 握手成功，开始使用 V2 协议
            this.protocolNegotiated = true;
            this.serverCapabilities = response.serverCapabilities;
            
            console.log('[AMLL Bridge] Handshake successful:', response);
            console.log('[AMLL Bridge] Server capabilities:', this.serverCapabilities);
            
            // 触发协议协商完成事件
            this.emit('protocolNegotiated', response.version);
            
            // 发送等待队列中的所有命令
            this.messageQueue.forEach(msg => this.sendToAndroid(msg));
            this.messageQueue = [];  // 清空队列
        } else {
            // ❌ 握手失败，记录原因
            console.error('[AMLL Bridge] Handshake rejected:', response.message);
            this.emit('handshakeFailed', response.message);
        }
    }
    
    /**
     * 处理状态更新
     * 
     * 解析来自 Android 的状态变化通知，并触发相应的事件。
     * 
     * @param message 包含状态数据的消息对象
     */
    private handleStateUpdate(message: StateUpdateMessage) {
        const { payload } = message;
        
        // 根据 updateType 分发到不同的事件处理器
        switch (payload.updateType) {
            case 'setMusicInfo':
                // 歌曲信息变化（歌名、艺术家、专辑等）
                this.emit('musicInfoChanged', payload);
                break;
            case 'setLyric':
                // 歌词数据更新（TTML 格式）
                this.emit('lyricChanged', payload);
                break;
            case 'progress':
                // 播放进度更新（毫秒）
                this.emit('progressUpdate', payload);
                break;
            case 'paused':
                // 播放暂停
                this.emit('playbackPaused');
                break;
            case 'resumed':
                // 播放恢复
                this.emit('playbackResumed');
                break;
            case 'volumeChanged':
                // 音量变化（0.0 - 1.0）
                this.emit('volumeChanged', payload);
                break;
        }
    }
    
    /**
     * 发送命令到 Android
     * 
     * 这是发送控制命令的统一入口。
     * 如果协议尚未协商完成，会将命令加入队列等待。
     * 
     * @param payload 命令载荷（seek、pause、resume 等）
     */
    public sendCommand(payload: CommandPayload) {
        // 构建命令消息
        const message: CommandMessage = {
            type: 'command',
            version: PROTOCOL_VERSION,
            payload
        };
        
        // 如果还未握手成功，先将命令入队
        if (!this.protocolNegotiated) {
            console.warn('[AMLL Bridge] Queueing command before handshake:', message);
            this.messageQueue.push(message);
            return;
        }
        
        // 立即发送到 Android
        this.sendToAndroid(message);
    }
    
    /**
     * 实际发送到 Android
     * 
     * 通过 JavascriptInterface 的 postMessage 方法将 JSON 消息发送给 Android。
     * 
     * @param message 要发送的消息对象
     */
    private sendToAndroid(message: BridgeMessage) {
        if (this.androidBridge) {
            // 序列化为 JSON 字符串
            const jsonString = JSON.stringify(message);
            console.log('[AMLL Bridge] Sending to Android:', jsonString);
            // 调用 Android 的 postMessage 方法
            this.androidBridge.postMessage(jsonString);
        }
    }
    
    /**
     * 兼容旧版 postMessage
     * 
     * @deprecated 已废弃，请使用 sendToAndroid
     */
    private postMessage(message: BridgeMessage) {
        this.sendToAndroid(message);
    }
    
    // ==================== 便捷方法 ====================
    
    public seekTo(progress: number) {
        this.sendCommand({ command: 'seekPlayProgress', progress });
    }
    
    public pause() {
        this.sendCommand({ command: 'pause' });
    }
    
    public resume() {
        this.sendCommand({ command: 'resume' });
    }
    
    public setVolume(volume: number) {
        this.sendCommand({ command: 'setVolume', volume: Math.max(0, Math.min(1, volume)) });
    }
    
    public lyricLineClicked(lineIndex: number, time: number) {
        this.sendCommand({ command: 'lyricLineClicked', lineIndex, time });
    }
    
    // ==================== 事件系统 ====================
    
    public on(event: string, callback: Function) {
        if (!this.listeners.has(event)) {
            this.listeners.set(event, new Set());
        }
        this.listeners.get(event)!.add(callback);
    }
    
    public off(event: string, callback: Function) {
        this.listeners.get(event)?.delete(callback);
    }
    
    private emit(event: string, ...args: any[]) {
        this.listeners.get(event)?.forEach(callback => callback(...args));
    }
    
    // ==================== 工具方法 ====================
    
    public hasCapability(capability: string): boolean {
        return this.serverCapabilities.includes(capability);
    }
    
    public getProtocolVersion(): number {
        return this.protocolNegotiated ? PROTOCOL_VERSION : 0;
    }
    
    public isReady(): boolean {
        return this.protocolNegotiated;
    }
}

// ==================== 全局导出 ====================

// 创建全局实例
(window as any).AMLLBridge = new AMLLBridgeManagerV2();

// 暴露给全局作用域以便调试
if (typeof window !== 'undefined') {
    (window as any).AMLLBridgeManager = AMLLBridgeManagerV2;
}

// ==================== 使用示例 ====================

/*
// 在前端代码中使用：

import { AMLLBridge } from './bridge';

// 监听播放进度
AMLLBridge.on('progressUpdate', (data: { progress: number }) => {
    console.log('播放进度:', data.progress);
    updateProgressBar(data.progress);
});

// 监听歌词变化
AMLLBridge.on('lyricChanged', (data: { format: string; data: string }) => {
    console.log('歌词已更新:', data);
    loadLyric(data.data);
});

// 点击歌词行
function onLyricLineClick(index: number, time: number) {
    AMLLBridge.lyricLineClicked(index, time);
}

// 检查功能支持
if (AMLLBridge.hasCapability('word_by_word')) {
    enableWordByWordHighlight();
}
*/

export default AMLLBridgeManagerV2;
