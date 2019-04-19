package com.jlinc.android.hyqfsad.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

import com.jlinc.android.hyqfsad.INetWorkState;
import com.jlinc.android.hyqfsad.utils.CommonUtils;
import com.jlinc.android.hyqfsad.utils.TUtils;

public abstract class BaseActivity<T extends BasePresenter,TT extends BasePresenter> extends AppCompatActivity implements INetWorkState {

    public T mPresenter;
    public static INetWorkState iNetWorkState;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initApp();
        setContentView();
        initView();
        initData();
        setStatusAndNavBar();
        //初始化网络状态的监听
        iNetWorkState=this;

    }

    protected abstract void setContentView();


    private void initApp() {
        mPresenter = TUtils.getT(this, 1);
        if (this instanceof IBaseView) mPresenter.setVM(this, this);
    }


    public void setStatusAndNavBar() {
        //设置状态栏颜色和字体
//        StatusBarUtil.setTranslucent(this,0);
//        StatusBarUtil.setLightMode(this);
        if (CommonUtils.hasNavBar(this)) {
            CommonUtils.hideBottomUIMenu(this);
        }
    }

    public abstract void initData();

    public abstract void initView();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mPresenter!=null){
            mPresenter.onDestroy();
            mPresenter=null;
        }
    }

    public abstract void onNetChanged(boolean netWorkState);

    @Override
    public void onNetChange(boolean netWorkState) {
        onNetChanged(netWorkState);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (onKeyWebDown(keyCode,event)){
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    public boolean onKeyWebDown(int keyCode, KeyEvent event){
        return false;
    }

}
