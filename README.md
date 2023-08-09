# WechatMultiWebview

利用 Xposed 实现微信以前的 `//multiwebview` 功能。

![demo](images/demo.jpg)

实现很简单：hook `startActivity`，向 `intent` 添加 `FLAG_ACTIVITY_NEW_DOCUMENT` 和 `FLAG_ACTIVITY_MULTIPLE_TASK` 两个 Flag 即可，其中前者使得 `activity` 在新任务中开启，后者使得开启多个 `activity` 时每个 `activity` 都在新的任务中开启，这样可以同时打开多个网页，分别在多个窗口中显示。

[Xposed Repo](https://repo.xposed.info/node/1761)

[感谢原作者](https://github.com/chouqibao/WechatMultiWebview)，原作者未适配 8.x 版本微信，这里做了更新，由于换了签名，所以也换了包名。

## 更新日志

### 2023.08.09 适配 Google Play 版微信 8.0.37
目前支持阅读公众号文章和小程序页面。

### 2020.02.06 适配新版微信
新版微信更改了公众号文章页面的 `Activity` 的类名。目前在 Google Play 版微信 7.0.3 和 7.0.10 版本测试通过。