package com.jlinc.android.hyqfsad.MVP.contract;

import com.jlinc.android.hyqfsad.base.BasePresenter;
import com.jlinc.android.hyqfsad.base.IBaseView;

import java.io.File;

public interface UpgradeApkContract {

    interface View extends IBaseView {

        void onUpgradeApk(String versionCode,String url);

        void onDownLoadApk(File file);
    }

    abstract class Presenter extends BasePresenter<UpgradeApkContract.View> {

        /**
         * 请求版本号接口
         */
        public abstract void upgradeApk();

        /**
         * 下载apk
         */
        public abstract void downloadApk(String url);
    }
}
