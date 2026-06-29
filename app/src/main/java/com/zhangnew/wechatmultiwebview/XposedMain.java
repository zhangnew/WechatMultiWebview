package com.zhangnew.wechatmultiwebview;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.Objects;

import io.github.libxposed.api.XposedModule;
import io.github.libxposed.api.XposedModuleInterface;

public class XposedMain extends XposedModule {

    public static String LOG_TAG = "Xposed-WeChatMultiWebview";

    @Override
    public void onPackageLoaded(XposedModuleInterface.PackageLoadedParam param) {
        if (!param.getPackageName().equals("com.tencent.mm")) return;

        try {
            Method startActivityMethod = Activity.class.getDeclaredMethod("startActivity", Intent.class, Bundle.class);
            hook(startActivityMethod).intercept(chain -> {
                Intent intent = (Intent) chain.getArg(0);
                String target = Objects.requireNonNull(intent.getComponent()).getClassName();
                if (target.equals("com.tencent.mm.plugin.brandservice.ui.timeline.preload.ui.TmplWebViewMMUI")
                        || target.equals("com.tencent.mm.plugin.appbrand.ui.AppBrandUI")
                        || target.equals("com.tencent.mm.plugin.webview.ui.tools.MMWebViewUI")
                        || target.equals("com.tencent.mm.plugin.webview.ui.tools.WebViewUI")
                        || target.equals("com.tencent.mm.plugin.webview.ui.tools.preload.TmplWebViewTooLMpUI")
                        || target.equals("com.tencent.mm.plugin.brandservice.ui.timeline.preload.ui.TmplWebViewTooLMpUI")) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
                    intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                }
                return chain.proceed();
            });
        } catch (Throwable t) {
            Log.e(LOG_TAG, "Failed to hook startActivity", t);
        }
    }
}
