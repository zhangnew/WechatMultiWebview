# WechatMultiWebview

利用 Xposed 实现微信以前的 `//multiwebview` 功能，在最近任务列表中可以同时显示微信聊天窗口和网页窗口，并可以互相切换，避免回消息的时候丢失网页窗口。

[![GitHub all releases](https://img.shields.io/github/downloads/Xposed-Modules-Repo/com.zhangnew.wechatmultiwebview/total?logo=android&label=Xposed%20Modules%20Repo%20Downloads&color=F48FB1)](https://github.com/Xposed-Modules-Repo/com.zhangnew.wechatmultiwebview)
[![GitHub all releases](https://img.shields.io/github/downloads/zhangnew/wechatmultiwebview/total?logo=android&label=Source%20Code%20Repo%20Downloads&color=F48FB1)](https://github.com/zhangnew/WechatMultiWebview)

![demo](https://raw.githubusercontent.com/zhangnew/WechatMultiWebview/master/images/demo.jpg)

实现很简单：hook `startActivity`，向 `intent` 添加 `FLAG_ACTIVITY_NEW_DOCUMENT` 和 `FLAG_ACTIVITY_MULTIPLE_TASK` 两个 Flag 即可，其中前者使得 `activity` 在新任务中开启，后者使得开启多个 `activity` 时每个 `activity` 都在新的任务中开启，这样可以同时打开多个网页，分别在多个窗口中显示。

[感谢原作者](https://github.com/chouqibao/WechatMultiWebview)，原作者未适配 8.x 版本微信，这里做了更新，并提交到了[LSPosed Repo](https://modules.lsposed.org/module/com.zhangnew.wechatmultiwebview) ，由于换了签名，所以也换了包名。

## 更新日志

### 2023.08.09 适配 Google Play 版微信 8.0.37
目前支持阅读公众号文章和小程序页面。如有问题或者需要适配新版本，欢迎提 Issue or PR

### 2020.02.06 适配新版微信
新版微信更改了公众号文章页面的 `Activity` 的类名。目前在 Google Play 版微信 7.0.3 和 7.0.10 版本测试通过。

