package com.jlinc.android.hyqfsad.MVP.presenter;

import com.google.gson.Gson;
import com.jlinc.android.hyqfsad.MVP.contract.UpgradeApkContract;
import com.jlinc.android.hyqfsad.bean.UploadApkBean;
import com.jlinc.android.hyqfsad.utils.CommonUtils;
import com.jlinc.android.hyqfsad.utils.ConstantUtils;
import com.jlinc.android.hyqfsad.utils.FileHelper;
import com.jlinc.android.hyqfsad.utils.XmlUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import com.tencent.bugly.crashreport.CrashReport;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * 更新apk的P层
 */
public class UpgradeApkPresenter extends UpgradeApkContract.Presenter {


    @Override
    public void onStart() {

    }

    /**
     * 请求更新apk
     */
    @Override
    public void upgradeApk() {
        String downLoadUrl = XmlUtils.getValue(ConstantUtils.CONFIG_WEBURL, FileHelper.SDCardPath() + ConstantUtils.QUEUINGFILE_CONFIG_XML) +
                XmlUtils.getValue(ConstantUtils.CONFIG_DOWNLOADURL, FileHelper.SDCardPath() + ConstantUtils.QUEUINGFILE_CONFIG_XML);
        OkGo.<String>get(downLoadUrl)
                .tag(this)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            //服务连接成功，将config.xml文件copy做备份
                            File file = new File(FileHelper.SDCardPath() + ConstantUtils.QUEUINGFILE_CONFIG_BACKUP);
                            if (FileHelper.isFileExist(ConstantUtils.QUEUINGFILE_CONFIG_BACKUP) && file.length() != 0 &&
                                    (!XmlUtils.getValue(ConstantUtils.CONFIG_WEBURL,FileHelper.SDCardPath()+ConstantUtils.QUEUINGFILE_CONFIG_BACKUP_XML)
                                            .equals(XmlUtils.getValue(ConstantUtils.CONFIG_WEBURL,FileHelper.SDCardPath()+ConstantUtils.QUEUINGFILE_CONFIG_XML))||
                                    !XmlUtils.getValue(ConstantUtils.CONFIG_PORT,FileHelper.SDCardPath()+ConstantUtils.QUEUINGFILE_CONFIG_BACKUP_XML)
                                            .equals(XmlUtils.getValue(ConstantUtils.CONFIG_PORT,FileHelper.SDCardPath()+ConstantUtils.QUEUINGFILE_CONFIG_XML)))){
                                FileHelper.copyFile(ConstantUtils.QUEUINGFILE_CONFIG_XML, ConstantUtils.QUEUINGFILE_CONFIG_BACKUP_XML);
                            }
                            JSONObject object = new JSONObject(response.body());
                            if (object != null) {
                                String data = object.getString("data");
                                Gson gson = new Gson();
                                UploadApkBean uploadApkBean = gson.fromJson(data, UploadApkBean.class);
                                mView.onUpgradeApk(uploadApkBean.getVer(), uploadApkBean.getApk_path());
                            } else {
                                CrashReport.postException(0, "请求接口", "请求接口内容返回空", null, null);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                    }

                });

    }

    /**
     * 下载apk
     *
     * @param url
     */
    @Override
    public void downloadApk(String url) {

        if (FileHelper.isFileExist(ConstantUtils.QUEUINGFILE_APK + "/" + ConstantUtils.QUEUINGFILE_APKNAME)) {
            FileHelper.deleteSingleFile(ConstantUtils.QUEUINGFILE_APK + "/" + ConstantUtils.QUEUINGFILE_APKNAME);
        }
        OkGo.<File>get(url)
                .tag(this)
                .execute(new FileCallback(FileHelper.SDPATH + ConstantUtils.QUEUINGFILE_APK, ConstantUtils.QUEUINGFILE_APKNAME) {
                    @Override
                    public void onSuccess(Response<File> response) {
                        mView.onDownLoadApk(response.body());
                    }

                    @Override
                    public void onError(Response<File> response) {
                        super.onError(response);
                    }

                    @Override
                    public void uploadProgress(Progress progress) {
                        super.uploadProgress(progress);
                    }
                });

    }
}
