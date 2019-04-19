package com.jlinc.android.hyqfsad.web;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import java.io.BufferedInputStream;

public class MyWebViewClient extends WebViewClient {

    public static final String TAG = "MyWebViewClient";
    private Context context;
    private Handler handler;
    private WebView webView;
    private OnServiceErrListener onServiceErrListener;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                isUrlValid(webView);
            }catch (Exception e){
                e.getStackTrace();
            }
            handler.postDelayed(runnable,5000);
        }
    };

    public MyWebViewClient(Context context,OnServiceErrListener onServiceErrListener) {
        this.context = context;
        this.onServiceErrListener = onServiceErrListener;
        handler = new Handler();
    }
    @Override
    public void onReceivedError(final WebView view, int errorCode, String description, String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);
        webView = view;
        onServiceErrListener.setServiceErrShow(false);
        handler.post(runnable);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onReceivedError(final WebView view, WebResourceRequest request, WebResourceError error) {
        super.onReceivedError(view, request, error);
        webView = view;
        onServiceErrListener.setServiceErrShow(false);
        handler.post(runnable);
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onReceivedHttpError(final WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
        super.onReceivedHttpError(view, request, errorResponse);

        if (!request.isForMainFrame() && request.getUrl().getPath().endsWith("/favicon.ico")) {
            Log.e(TAG, "favicon.ico 请求错误" + errorResponse.getStatusCode() + errorResponse.getReasonPhrase());
        } else {
            webView = view;
            onServiceErrListener.setServiceErrShow(false);
            handler.post(runnable);
        }
        super.onReceivedHttpError(view, request, errorResponse);
    }

    /**
     * WebView在请求加载一个页面的同时，还会发送一个请求图标文件的请求
     * 比如我们采用WebView去加载一个页面http://192.168.5.40:9006/H5/aboutUs.html;
     * 同时还会发送一个请求图标文件的请求http://192.168.5.40:9006/favicon.ico
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        if (!request.isForMainFrame() && request.getUrl().getPath().endsWith("/favicon.ico")) {
            try {
                return new WebResourceResponse("image/png", null,
                        new BufferedInputStream(view.getContext().getAssets().open("empty_favicon.ico")));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        if (url.toLowerCase().contains("/favicon.ico")) {
            try {
                return new WebResourceResponse("image/png", null,
                        new BufferedInputStream(view.getContext().getAssets().open("empty_favicon.ico")));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 检测当前url是否可以打开
     * @param webView
     */
    private void isUrlValid(final WebView webView){
        OkGo.<String>get(webView.getUrl()).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                webView.reload();
                onServiceErrListener.setServiceErrShow(true);
                Toast.makeText(context,"连接成功，欢迎使用！",Toast.LENGTH_SHORT).show();
                handler.removeCallbacks(runnable);
            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
            }
        });
    }

    public interface OnServiceErrListener{
        void setServiceErrShow(boolean isShow);
    }
}
