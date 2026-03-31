/**
 * AMLL Bridge Protocol V2 - JavaScript/TypeScript 实现
 * 
 * 用于 WebView 端的协议实现，支持与 Android 的协议检测和握手
 * 
 * 使用方式：
 * 1. 在 AMLL 前端初始化时调用 initiateHandshake()
 * 2. 监听来自 Android 的消息
 * 3. 使用统一的 API 发送命令和接收状态更新
 */

// ==================== 类型定义 ====================

interface HandshakeRequest {
    type: 'handshake';
    version: number;
    capabilities: string[];
}

interface HandshakeResponse {
    type: 'handshake_ack';
    version: number;
    accepted: boolean;
    serverCapabilities: string[];
    message?: string;
}

interface StateUpdateMessage {
    type: 'stateUpdate';
    version: number;
    payload: StatePayload;
}

interface CommandMessage {
    type: 'command';
    version: number;
    payload: CommandPayload;
}

type StatePayload =
    | { updateType: 'setMusicInfo'; musicId: string; musicName: string; albumName: string; artistName: string; duration: number }
    | { updateType: 'setLyric'; format: string; data: string }
    | { updateType: 'progress'; progress: number }
    | { updateType: 'paused' }
    | { updateType: 'resumed' }
    | { updateType: 'volumeChanged'; volume: number };

type CommandPayload =
    | { command: 'seekPlayProgress'; progress: number }
    | { command: 'pause' }
    | { command: 'resume' }
    | { command: 'setVolume'; volume: number }
    | { command: 'lyricLineClicked'; lineIndex: number; time: number };

type BridgeMessage = StateUpdateMessage | CommandMessage | HandshakeRequest | HandshakeResponse;

// ==================== 协议版本 ====================

const PROTOCOL_VERSION = 2;

// ==================== Bridge Manager ====================

class AMLLBridgeManagerV2 {
    private androidBridge: any;
    private protocolNegotiated = false;
    private clientCapabilities: string[] = [];
    private serverCapabilities: string[] = [];
    private messageQueue: CommandMessage[] = [];
    private listeners: Map<string, Set<Function>> = new Map();
    
    constructor() {
        // 检查 Android 桥接对象是否存在
        if ((window as any).AndroidBridge) {
            this.androidBridge = (window as any).AndroidBridge;
            console.log('[AMLL Bridge] Android Bridge detected');
        } else {
            console.warn('[AMLL Bridge] Android Bridge not found, running in standalone mode');
        }
        
        // 自动初始化
        this.init();
    }
    
    /**
     * 初始化桥接管理器
     */
    private init() {
        // 等待页面加载完成后发起握手
        if (document.readyState === 'loading') {
            document.addEventListener('DOMContentLoaded', () => this.initiateHandshake());
        } else {
            this.initiateHandshake();
        }
    }
    
    /**
     * 主动发起握手（类似 Tauri 的 Initialize）
     */
    public initiateHandshake() {
        if (!this.androidBridge) {
            console.warn('[AMLL Bridge] Cannot handshake without AndroidBridge');
            return;
        }
        
        const request: HandshakeRequest = {
            type: 'handshake',
            version: PROTOCOL_VERSION,
            capabilities: [
                'ttml_lyrics',
                'word_by_word',
                'album_art',
                'audio_visualization'
            ]
        };
        
        console.log('[AMLL Bridge] Initiating handshake:', request);
        this.postMessage(request);
    }
    
    /**
     * 接收来自 Android 的消息
     */
    public receiveFromAndroid(jsonString: string) {
        try {
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
            console.error('[AMLL Bridge] Error parsing message:', error);
        }
    }
    
    /**
     * 处理握手响应
     */
    private handleHandshakeResponse(response: HandshakeResponse) {
        if (response.accepted) {
            this.protocolNegotiated = true;
            this.serverCapabilities = response.serverCapabilities;
            
            console.log('[AMLL Bridge] Handshake successful:', response);
            console.log('[AMLL Bridge] Server capabilities:', this.serverCapabilities);
            
            // 触发事件
            this.emit('protocolNegotiated', response.version);
            
            // 发送队列中的消息
            this.messageQueue.forEach(msg => this.sendToAndroid(msg));
            this.messageQueue = [];
        } else {
            console.error('[AMLL Bridge] Handshake rejected:', response.message);
            this.emit('handshakeFailed', response.message);
        }
    }
    
    /**
     * 处理状态更新
     */
    private handleStateUpdate(message: StateUpdateMessage) {
        const { payload } = message;
        
        switch (payload.updateType) {
            case 'setMusicInfo':
                this.emit('musicInfoChanged', payload);
                break;
            case 'setLyric':
                this.emit('lyricChanged', payload);
                break;
            case 'progress':
                this.emit('progressUpdate', payload);
                break;
            case 'paused':
                this.emit('playbackPaused');
                break;
            case 'resumed':
                this.emit('playbackResumed');
                break;
            case 'volumeChanged':
                this.emit('volumeChanged', payload);
                break;
        }
    }
    
    /**
     * 发送命令到 Android
     */
    public sendCommand(payload: CommandPayload) {
        const message: CommandMessage = {
            type: 'command',
            version: PROTOCOL_VERSION,
            payload
        };
        
        if (!this.protocolNegotiated) {
            console.warn('[AMLL Bridge] Queueing command before handshake:', message);
            this.messageQueue.push(message);
            return;
        }
        
        this.sendToAndroid(message);
    }
    
    /**
     * 实际发送到 Android
     */
    private sendToAndroid(message: BridgeMessage) {
        if (this.androidBridge) {
            const jsonString = JSON.stringify(message);
            console.log('[AMLL Bridge] Sending to Android:', jsonString);
            this.androidBridge.postMessage(jsonString);
        }
    }
    
    /**
     * 兼容旧版 postMessage
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
