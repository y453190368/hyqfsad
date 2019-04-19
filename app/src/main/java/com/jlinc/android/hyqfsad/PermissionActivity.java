package com.jlinc.android.hyqfsad;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.jlinc.android.hyqfsad.base.BaseActivity;
import com.jlinc.android.hyqfsad.utils.ConstantUtils;
import com.jlinc.android.hyqfsad.utils.FileHelper;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.functions.Consumer;

/**
 * 一次性获取权限
 */
public class PermissionActivity extends BaseActivity {

    private RxPermissions rxPermissions;
    private String [] permissionStr = {Manifest.permission.READ_EXTERNAL_STORAGE,
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
                        if (permission.granted){
                            if (count == permissionStr.length){
                                if (FileHelper.SDCardState()){
                                    FileHelper.createSDDir(ConstantUtils.QUEUINGFILES);
                                    FileHelper.copyAssetsXmlTo(PermissionActivity.this,ConstantUtils.QUEUINGFILE_CONFIG_XML);
                                }
                                Intent intent = new Intent();
                                intent.setClass(PermissionActivity.this,MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }else{
                            //拒绝权限并选择不在询问，前往app设置开启权限
                            new AlertView("警告",
                                    "您拒绝了" + permission.name + "权限访问，部分功能无法正常使用，请到设置页面手动授权",
                                    "取消",
                                    null,
                                    new String[]{"设置"},
                                    PermissionActivity.this,
                                    AlertView.Style.Alert,
                                    new OnItemClickListener() {
                                        @Override
                                        public void onItemClick(Object o, int position) {
                                            switch (position){
                                                case 0:
                                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                                    Uri uri = Uri.fromParts("package", getApplicationContext().getPackageName(), null);
                                                    intent.setData(uri);
                                                    startActivity(intent);
                                                    finish();
                                                    break;
                                            }
                                        }
                                    }).show();
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
