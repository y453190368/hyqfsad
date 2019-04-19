package com.jlinc.android.hyqfsad;

import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
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

import java.io.File;

public class MainActivity extends BaseActivity<UpgradeApkContract.Presenter, UpgradeApkPresenter> implements UpgradeApkContract.View {
    private RelativeLayout relativeLayout;
    private TextView textView, tvVersion;
    private AgentWeb agentWeb;
    private AlertView alertView;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_main);
    }

    @Override
    public void initData() {
        tvVersion.setText(CommonUtils.getVersionName(this));
        if (!CommonUtils.isNetworkAvailable(this)) {
            if (FileHelper.isFileExist(ConstantUtils.QUEUINGFILE_DEFH5_ERROR)) {
                agentWeb.getUrlLoader().loadUrl(ConstantUtils.SDHTML + ConstantUtils.QUEUINGFILE_DEFH5_ERROR);
            } else {
                relativeLayout.setVisibility(View.GONE);
                textView.setVisibility(View.VISIBLE);
            }
        }else{
            relativeLayout.setVisibility(View.VISIBLE);
            textView.setVisibility(View.GONE);
        }
        mPresenter.upgradeApk();

    }


    @Override
    public void initView() {
        alertView = new AlertView("提示",
                "您确定要退出排队屏显么？",
                "取消",
                null,
                new String[]{"确定"},
                this,
                AlertView.Style.Alert,
                new OnItemClickListener() {
                    @Override
                    public void onItemClick(Object o, int position) {
                        switch (position){
                            case 0:
                                finish();
                                System.exit(0);
                                break;
                        }
                    }
                });
        relativeLayout = findViewById(R.id.main_rl_webView);
        textView = findViewById(R.id.main_tv_error);
        tvVersion = findViewById(R.id.main_tv_version);
        agentWeb = AgentWeb.with(this)
                .setAgentWebParent(relativeLayout, new RelativeLayout.LayoutParams(-1, -1))
                .useDefaultIndicator()
                .setMainFrameErrorView(R.layout.web_error_page,-1)//加载异常的时候显示的页面
                .setWebViewClient(new MyWebViewClient(this))
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

        if (netWorkState){
            initData();
            agentWeb.getUrlLoader().reload();
        }
    }

    @Override
    public boolean onKeyWebDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && !agentWeb.back()) {
            if (alertView.isShowing() && alertView != null){
                alertView.dismiss();
            }else{
                alertView.show();
            }
            return true;
        }else{
            return false;
        }
    }


    @Override
    public void onResult(String code, String url) {
        if (!code.equals(CommonUtils.getVersionCode(this))) {
            mPresenter.downloadApk(XmlUtils.getValue(ConstantUtils.CONFIG_WEBURL, FileHelper.SDCardPath() + ConstantUtils.QUEUINGFILE_CONFIG_XML)+url);
        }
    }

    @Override
    public void onDownLoadApk(File file) {
        if (CommonUtils.isRoot()) {
            CommonUtils.silentInstallRoot(file.getAbsolutePath());
        } else {
            CrashReport.postException(null, 0, "root权限", "设备未授权", null, null);
            CommonUtils.silentInstallNotRoot(file.getAbsolutePath());
        }
    }

}
