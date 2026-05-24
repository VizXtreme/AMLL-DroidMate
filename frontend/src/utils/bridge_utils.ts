/**
 * AMLL Bridge 桥接工具
 *
 * 提供与 Android 原生代码交互的基础工具函数。
 */

/**
 * 将日志发送到 Android 原生日志系统
 */
export function logToAndroid(message: string, level: string = 'debug') {
  if ((window as any).Android?.log) {
    try {
      (window as any).Android.log(message, level)
    } catch (e) {
      console.log(`[ANDROID] ${message}`)
    }
  } else {
    console.log(`[${level.toUpperCase()}] ${message}`)
  }
}
