package com.jlinc.android.hyqfsad;

import android.view.KeyEvent;
import android.view.MotionEvent;
import android.webkit.WebSettings;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jlinc.android.hyqfsad.MVP.contract.UpgradeApkContract;
import com.jlinc.android.hyqfsad.MVP.presenter.UpgradeApkPresenter;
import com.jlinc.android.hyqfsad.base.BaseActivity;
import com.jlinc.android.hyqfsad.utils.CommonUtils;
import com.jlinc.android.hyqfsad.utils.ConstantUtils;
import com.jlinc.android.hyqfsad.utils.FileHelper;
import com.jlinc.android.hyqfsad.utils.XmlUtils;
import com.jlinc.android.hyqfsad.web.MyWebViewClient;
import com.just.agentweb.AgentWeb;
import com.tencent.bugly.crashreport.CrashReport;
import com.ys.myapi.MyManager;

import java.io.File;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends BaseActivity<UpgradeApkContract.Presenter, UpgradeApkPresenter> implements UpgradeApkContract.View {
    private RelativeLayout relativeLayout;
    private TextView tvVersion, tvErrorMsg;
    private AgentWeb agentWeb;
    private MyManager myManager;


    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_main);
    }

    @Override
    public void initData() {
        tvVersion.setText(CommonUtils.getVersionName(this));
        mPresenter.upgradeApk();

    }


    @Override
    public void initView() {
        myManager = MyManager.getInstance(this);

        relativeLayout = findViewById(R.id.main_rl_webView);
        tvErrorMsg = findViewById(R.id.main_tv_errorMsg);
        tvVersion = findViewById(R.id.main_tv_version);
        agentWeb = AgentWeb.with(this)
                .setAgentWebParent(relativeLayout, new RelativeLayout.LayoutParams(-1, -1))
                .useDefaultIndicator()
//                .setMainFrameErrorView(R.layout.web_error_page, -1)//加载异常的时候显示的页面
//                .setWebViewClient(new MyWebViewClient(this))
                .createAgentWeb()
                .ready()
                .go(XmlUtils.getValue(ConstantUtils.CONFIG_WEBURL, FileHelper.SDCardPath() + ConstantUtils.QUEUINGFILE_CONFIG_XML));
        //js和安卓交互
//        agentWeb.getJsInterfaceHolder().addJavaObject(AndroidJsInterface.CALL_ANDROID, new AndroidJsInterface(agentWeb));
        //设置支持自动加载图片
        agentWeb.getAgentWebSettings().getWebSettings().setLoadsImagesAutomatically(true);
        //设置不能有缓存
        agentWeb.getAgentWebSettings().getWebSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        agentWeb.getAgentWebSettings().getWebSettings().setAllowFileAccess(true);
        agentWeb.getAgentWebSettings().getWebSettings().setDomStorageEnabled(true);
        //遥控触发时点击时间被webview焦点拦截，无法触发activity的onKeyDown事件执行退出
        agentWeb.getWebCreator().getWebView().setFocusable(false);
        agentWeb.getWebCreator().getWebView().setFocusableInTouchMode(false);
    }


    @Override
    protected void onPause() {
        agentWeb.getWebLifeCycle().onPause();
        super.onPause();

    }

    @Override
    protected void onResume() {
        agentWeb.getWebLifeCycle().onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        agentWeb.getWebLifeCycle().onDestroy();
        super.onDestroy();
    }

    @Override
    public void onNetChanged(boolean netWorkState) {

        if (netWorkState) {
            tvErrorMsg.setText("");
            agentWeb.getUrlLoader().reload();
        } else {
            tvErrorMsg.setText(R.string.app_net_error);
        }
    }

    @Override
    public boolean onKeyWebDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && !agentWeb.back()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onUpgradeApk(String code, String url) {
        if (code.equals(CommonUtils.getVersionCode(this))) {
            mPresenter.downloadApk(XmlUtils.getValue(ConstantUtils.CONFIG_WEBURL, FileHelper.SDCardPath() + ConstantUtils.QUEUINGFILE_CONFIG_XML) + url);
        }
    }

    /**
     * 下载apk成功回调
     * @param file 文件路径
     * root过的板卡测试过，没root过的为测试
     */
    @Override
    public void onDownLoadApk(File file) {
        if (CommonUtils.isRoot()) {
            if (!myManager.silentInstallApk(file.getAbsolutePath())){
                Toast.makeText(this,"静默安装失败!",Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this,"无法获取设备权限，安装失败!",Toast.LENGTH_SHORT).show();
            CrashReport.postException(null, 0, "root权限", "设备未授权", null, null);
//            CommonUtils.silentInstallNotRoot(file.getAbsolutePath());
        }
    }

}
