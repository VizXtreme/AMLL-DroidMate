>[!Warning]
>
>鉴于本仓库主各项水平有限，没有能力（即 AMLL 部分维护者称“智商太低”）。对本项目的维护可能时有时无，并且不做任何稳定性保证，望您知悉。

# AMLL DroidMate

Android 端外置歌词显示器：AMLL + ScoreMuse

<img width="1080" height="2400" alt="Image" src="https://github.com/user-attachments/assets/43114e82-f439-4e40-9ed6-2090ccd5b0e9" />



## 核心特性

- 在你爱用的音乐源应用上享受 [AMLL](https://github.com/amll-dev/applemusic-like-lyrics) 的功能，精彩体验就在口袋之中。
  - 享受类似于 Apple Music 的动画效果，包括长音辉光、背景渐变等
    - 可选：基于修改 font-family 的自定义字体功能
  - [TTML](https://github.com/amll-dev/amll-ttml-db/blob/main/instructions/ttml-specification.md) 歌词特性：（仅限TTML格式）
    - 多角色歌词将不同角色的歌词分成靠左和靠右
    - 背景歌词将在主歌词下以小字显示
    - 支持行与行之间的时间轴重叠
- WebSocket 传递：通过兼容的外部 AMLL 来显示歌词动画
- Android 应用程序：基于 [ScoreMuse](https://github.com/Zeehan2005/ScoreMuse) 修改
  - 无需切换应用和提前下载解密。继续使用且不影响您的音乐会员和喜爱的歌单。
  - 应用内可直接拖动进度条，暂停/播放，上下首等，而无需回到音乐源
    - 可选功能：按“上一首”时回到0:00处
  - 歌词结构显示：轻松跳转到喜欢的段落 
  - 多源歌词检索（酷狗、网易云、QQ），找到最符合且功能最多的歌词文件
  - 常驻通知实时歌词：可选常驻通知实时显示当前句歌词，并支持锁屏显示
  - 精美的 Material Design 3 UI
  - 应用全局颜色根据专辑图变换
  - 可设置基于歌曲和输出设备的歌词偏移，轻松解决延迟问题。

## 参考和接入的项目

- 核心动效：[Apple Music-like Lyrics （AMLL） `amll-dev/applemusic-like-lyrics`](https://github.com/amll-dev/applemusic-like-lyrics) (已存入本仓库`applemusic-like-lyrics`文件夹内以更好的参考)
- Android 应用程序：基于 [ScoreMuse `Zeehan2005/ScoreMuse`](https://github.com/Zeehan2005/ScoreMuse)
- 多源歌词匹配：[Unilyric `apoint123/Unilyric`](https://github.com/apoint123/Unilyric)
- 优质歌词来源：[AMLL TTML DB `amll-dev/amll-ttml-db`](https://github.com/amll-dev/amll-ttml-db)

## 提示
本项目目前 100% HITL Vibe coding。
