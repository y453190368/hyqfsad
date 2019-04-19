package com.jlinc.android.hyqfsad;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;

import com.jlinc.android.hyqfsad.base.BaseActivity;
import com.jlinc.android.hyqfsad.utils.ConstantUtils;
import com.jlinc.android.hyqfsad.utils.FileHelper;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.functions.Consumer;

//import com.bigkoo.alertview.AlertView;
//import com.bigkoo.alertview.OnDismissListener;
//import com.bigkoo.alertview.OnItemClickListener;

/**
 * 一次性获取权限
 */
public class PermissionActivity extends BaseActivity {

    private RxPermissions rxPermissions;
    private String[] permissionStr = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE};
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rxPermissions = new RxPermissions(this);
        rxPermissions.setLogging(true);
        showDialogPermession();
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
                                Intent intent = new Intent();
                                intent.setClass(PermissionActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
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
    public void initData() {

    }

    @Override
    public void initView() {

    }

    @Override
    public void onNetChanged(boolean netWorkState) {

    }
}
