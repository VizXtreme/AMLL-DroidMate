>[!Warning]
>
>前往 [Issues/Bug](https://github.com/Zeehan2005/AMLL-DroidMate/issues?q=is%3Aissue%20state%3Aopen%20label%3Abug) 查看已知问题。

>[!Important]
>
>本项目为为爱发电，维护可能时有时无，并且不做任何稳定性保证，望您知悉。

# AMLL DroidMate

即开即用 Android 端外置歌词显示器，集成 AMLL 风格渲染与多源歌词检索能力。

<img width="640" height="1339" alt="Image" src="https://github.com/user-attachments/assets/b28b17e9-f73b-426e-b1aa-8b6ed8eb487d" />

<img width="1280" height="800" alt="Image" src="https://github.com/user-attachments/assets/ec272e93-29d0-4b5f-b1f2-7591f3079341" />

<img width="1280" height="800" alt="Image" src="https://github.com/user-attachments/assets/23da7bfc-dc8b-4093-98b7-3c067cd0a79e" />

<img width="1280" height="800" alt="Image" src="https://github.com/user-attachments/assets/39bd873f-7810-49ff-a842-8b9beda0c893" />

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
  - 多源歌词检索（QQ音乐、酷狗、网易云），找到最符合且功能最多的歌词文件
  - 常驻通知实时歌词：可选常驻通知实时显示当前句歌词，并支持锁屏显示
  - 精美的 Material Design 3 UI
  - 为平板横屏布局适配
  - 应用全局颜色根据专辑图变换
  - 可设置基于歌曲和输出设备的歌词偏移，轻松解决延迟问题。

## 参考和接入的项目

- 核心动效：[Apple Music-like Lyrics （AMLL） `amll-dev/applemusic-like-lyrics`](https://github.com/amll-dev/applemusic-like-lyrics)
- Android 应用程序：基于 [ScoreMuse `Zeehan2005/ScoreMuse`](https://github.com/Zeehan2005/ScoreMuse)
- 多源歌词匹配：[Unilyric `apoint123/Unilyric`](https://github.com/apoint123/Unilyric)
- QQ音乐歌词获取与解密：参照 [`L-1124/QQMusicApi`](https://github.com/L-1124/QQMusicApi) 及其上游项目算法
- 优质歌词来源：[AMLL TTML DB `amll-dev/amll-ttml-db`](https://github.com/amll-dev/amll-ttml-db)
- 示例专辑图：[Background, Blue, Black royalty-free stock illustration](https://pixabay.com/zh/illustrations/background-blue-black-light-1591226/) by frankeh in pixabay under [pixabay content license](https://pixabay.com/service/license-summary/). 


# Star & Donation
为爱发电项目！阁下可否点点小星星支持一下？

<picture>
  <source media="(prefers-color-scheme: dark)" srcset="https://api.star-history.com/chart?repos=Zeehan2005/AMLL-DroidMate&type=date&theme=dark&legend=top-left" />
  <source media="(prefers-color-scheme: light)" srcset="https://api.star-history.com/chart?repos=Zeehan2005/AMLL-DroidMate&type=date&legend=top-left" />
  <img alt="Star History Chart" src="https://api.star-history.com/chart?repos=Zeehan2005/AMLL-DroidMate&type=date&legend=top-left" />
</picture>

众所周知，本项目使用较多的AI辅助。Claude正在侵蚀主播的钱包！给主播捐点tokens修修bug？

<img width="1037" height="1037" alt="Image" src="https://github.com/user-attachments/assets/5a61a8d6-2694-42c0-9da3-14ea7917b03c" />