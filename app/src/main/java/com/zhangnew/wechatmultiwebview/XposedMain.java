package com.zhangnew.wechatmultiwebview;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

public class XposedMain implements IXposedHookLoadPackage {

    public static String LOG_TAG = "Xposed-WeChatMultiWebview";

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) {
        if (lpparam.packageName.equals("com.tencent.mm")) {
            //Log.i(LOG_TAG, "WeChat Hooked!");
            findAndHookMethod(Activity.class, "startActivity", Intent.class, Bundle.class, new XC_MethodHook() {
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    Intent intent = (Intent) param.args[0];
                    String target = intent.getComponent().getClassName();
                    //Log.i(LOG_TAG + "-IntentTarget", target);
                    if (target.equals("com.tencent.mm.plugin.brandservice.ui.timeline.preload.ui.TmplWebViewMMUI")
                            || target.equals("com.tencent.mm.plugin.appbrand.ui.AppBrandUI")
                            || target.equals("com.tencent.mm.plugin.webview.ui.tools.WebViewUI")
                            || target.equals("com.tencent.mm.plugin.webview.ui.tools.preload.TmplWebViewTooLMpUI")
                            || target.equals("com.tencent.mm.plugin.brandservice.ui.timeline.preload.ui.TmplWebViewTooLMpUI")) {
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
                        intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                    }
                }
            });
        }
    }
}

