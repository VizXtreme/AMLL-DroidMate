>[!Warning]
>
>前往 [Issues/Bug](https://github.com/Zeehan2005/AMLL-DroidMate/issues?q=is%3Aissue%20state%3Aopen%20label%3Abug) 查看已知问题。

>[!Important]
>
>本项目的维护可能时有时无，并且不做任何稳定性保证，望您知悉。

>[!Note]
>
>当前 Alpha 开发版与 Latest 稳定版已经有较大的差异，但部分功能仍未完工，达不到稳定版的要求。如欲体验新功能，请前往 [Releases](https://github.com/Zeehan2005/AMLL-DroidMate/releases) 中的 Pre-Release 下载 Alpha 开发版。

# AMLL DroidMate

Android 端外置歌词显示器：AMLL + ScoreMuse

<img width="640" height="1339" alt="Image" src="https://github.com/user-attachments/assets/b28b17e9-f73b-426e-b1aa-8b6ed8eb487d" />



## 核心特性

- 在你爱用的音乐源应用上享受 [AMLL](https://github.com/amll-dev/applemusic-like-lyrics) 的功能，精彩体验就在口袋之中。
  - 享受类似于 Apple Music 的动画效果，包括长音辉光、背景渐变等
    - 可选：基于修改 font-family 的自定义字体功能
  - [TTML](https://github.com/amll-dev/amll-ttml-db/blob/main/instructions/ttml-specification.md) 歌词特性：（仅限TTML格式）
    - 多角色歌词将不同角色的歌词分成靠左和靠右
    - 背景歌词将在主歌词下以小字显示
    - 支持行与行之间的时间轴重叠
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

- 核心动效：[Apple Music-like Lyrics （AMLL） `amll-dev/applemusic-like-lyrics`](https://github.com/amll-dev/applemusic-like-lyrics)
- Android 应用程序：基于 [ScoreMuse `Zeehan2005/ScoreMuse`](https://github.com/Zeehan2005/ScoreMuse)
- 多源歌词匹配：[Unilyric `apoint123/Unilyric`](https://github.com/apoint123/Unilyric)
- 优质歌词来源：[AMLL TTML DB `amll-dev/amll-ttml-db`](https://github.com/amll-dev/amll-ttml-db)
- 示例专辑图：[Background, Blue, Black royalty-free stock illustration](https://pixabay.com/zh/illustrations/background-blue-black-light-1591226/) by frankeh in pixabay under [pixabay content license](https://pixabay.com/service/license-summary/). 


