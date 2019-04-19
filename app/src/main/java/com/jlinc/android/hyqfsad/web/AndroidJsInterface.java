package com.jlinc.android.hyqfsad.web;

import android.webkit.JavascriptInterface;

import com.just.agentweb.AgentWeb;

public class AndroidJsInterface {

    private AgentWeb agentWeb;
    public static String CALL_ANDROID = "Queuing_Android";
    public AndroidJsInterface(AgentWeb agentWeb) {
        this.agentWeb = agentWeb;
    }

    /**
     * 管理端调用刷新H5页面
     */
    @JavascriptInterface
    public void doRefreshH5(){
        agentWeb.getUrlLoader().reload();
    }
}
