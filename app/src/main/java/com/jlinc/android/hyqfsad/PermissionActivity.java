package com.jlinc.android.hyqfsad;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;

import com.jlinc.android.hyqfsad.MVP.contract.CheckServiceContract;
import com.jlinc.android.hyqfsad.MVP.presenter.CheckServicePresenter;
import com.jlinc.android.hyqfsad.base.BaseActivity;
import com.jlinc.android.hyqfsad.utils.CommonUtils;
import com.jlinc.android.hyqfsad.utils.ConstantUtils;
import com.jlinc.android.hyqfsad.utils.FileHelper;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.functions.Consumer;

/**
 * 一次性获取权限
 */
public class PermissionActivity extends BaseActivity<CheckServiceContract.Presenter, CheckServicePresenter> implements CheckServiceContract.View {

    private TextView errPage;
    private SweetAlertDialog pDialog;
    private RxPermissions rxPermissions;
    private String[] permissionStr = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE};
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_permission);
    }

    private void showDialogPermession() {
        rxPermissions.requestEach(permissionStr)
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        count++;
                        if (permission.granted) {
                            if (count == permissionStr.length) {
                                if (FileHelper.SDCardState()) {
                                    FileHelper.createSDDir(ConstantUtils.QUEUINGFILES);
                                    FileHelper.copyAssetsXmlTo(PermissionActivity.this, ConstantUtils.QUEUINGFILE_CONFIG_XML);
                                }
                                if (FileHelper.isFileExist(ConstantUtils.QUEUINGFILE_CONFIG_XML)) {
                                    mPresenter.connectService();
                                }
                            }
                        } else {
                            //拒绝权限并选择不在询问，前往app设置开启权限
                            if (count == permissionStr.length) {
                                new SweetAlertDialog(PermissionActivity.this, SweetAlertDialog.WARNING_TYPE)
                                        .setContentText("您已禁止权限，请手动开启")
                                        .setConfirmText("开启")
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sDialog) {
                                                sDialog.dismiss();
                                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                                Uri uri = Uri.fromParts("package", getApplicationContext().getPackageName(), null);
                                                intent.setData(uri);
                                                startActivity(intent);
                                                finish();
                                            }
                                        })
                                        .show();

                            }
                        }
                    }
                });
    }

    @Override
    public void onNetChange(boolean netWorkState) {
        if (netWorkState) {
            initData();
        }
    }

    @Override
    public void initData() {
        if (CommonUtils.isNetworkAvailable(this)) {
            errPage.setVisibility(View.GONE);
            showDialogPermession();
        } else {
            errPage.setVisibility(View.VISIBLE);
            errPage.setText(R.string.net_error_page);
        }

    }

    @Override
    public void initView() {
        rxPermissions = new RxPermissions(this);
        rxPermissions.setLogging(true);
        errPage = findViewById(R.id.permis_tv_error);
    }

    @Override
    public void onNetChanged(boolean netWorkState) {
        if (netWorkState) {
            initData();
        }
    }

    @Override
    public void showDialog() {
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("连接服务中，请耐心等待...");
        pDialog.setCancelable(false);
        pDialog.show();
    }

    @Override
    public void hideDialog() {
        pDialog.dismiss();
    }

    @Override
    public void onConnectService(boolean isConnect) {
        if (false) {
            errPage.setVisibility(View.GONE);
            Intent intent = new Intent();
            intent.setClass(PermissionActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            errPage.setVisibility(View.VISIBLE);
            errPage.setText(R.string.service_error_page);
            mPresenter.searchService();
        }
    }

    @Override
    public void onSearchService() {
        Intent intent = new Intent();
        intent.setClass(PermissionActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
