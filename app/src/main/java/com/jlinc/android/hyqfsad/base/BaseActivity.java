package com.jlinc.android.hyqfsad.base;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.jlinc.android.hyqfsad.INetWorkState;
import com.jlinc.android.hyqfsad.utils.CommonUtils;
import com.jlinc.android.hyqfsad.utils.ConstantUtils;
import com.jlinc.android.hyqfsad.utils.FileHelper;
import com.jlinc.android.hyqfsad.utils.TUtils;
import com.jlinc.android.hyqfsad.utils.XmlUtils;

public abstract class BaseActivity<T extends BasePresenter, TT extends BasePresenter> extends AppCompatActivity implements INetWorkState {

    public T mPresenter;
    public static INetWorkState iNetWorkState;
    private String cmd = "";
    private String controlKey = "";//遥控器数字
    private long exitTime = 0;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    finish();
                    System.exit(0);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initApp();//初始化p层和v层并绑定
        setContentView();
        initView();
        initData();
        //初始化网络状态的监听
        iNetWorkState = this;

    }

    protected abstract void setContentView();


    private void initApp() {
        mPresenter = TUtils.getT(this, 1);
        if (this instanceof IBaseView) mPresenter.setVM(this, this);
    }

    public abstract void initData();

    public abstract void initView();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.onDestroy();
            mPresenter = null;
        }
    }

    /**
     * 屏幕点击时间由该方法分发给当前activity的onTouchEvent处理
     *
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return onTouchEvent(ev);
    }

    /**
     * 点击屏幕4个角落退出程序
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.i("坐标", (System.currentTimeMillis() - exitTime) + "");
                if ((System.currentTimeMillis() - exitTime) > 8000) {
                    cmd = "";
                    exitTime = System.currentTimeMillis();
                }
                if (event.getX() <= 100.00 && event.getY() <= 100.00) {
                    cmd += "1";
                }
                if (event.getX() >= CommonUtils.getScreenWidth(this) - 100.00 && event.getY() <= 100.00) {
                    cmd += "2";
                }
                if (event.getX() <= 100.00 && event.getY() >= CommonUtils.getScreenHeight(this) - 100.00) {
                    cmd += "3";
                }
                if (event.getX() >= CommonUtils.getScreenWidth(this) - 100.00
                        && event.getY() >= CommonUtils.getScreenHeight(this) - 100.00) {
                    cmd += "4";
                }
                break;

            case MotionEvent.ACTION_UP:
                Log.i("cmd",cmd);
                if ((System.currentTimeMillis() - exitTime) <= 8000) {
                    if (cmd.equals(XmlUtils.getValue(ConstantUtils.CONFIG_ADMINCMD, FileHelper.SDCardPath()
                            + ConstantUtils.QUEUINGFILE_CONFIG_XML))) {
                        handler.sendEmptyMessage(0);
                    }
                }
                break;
        }
        return super.onTouchEvent(event);
    }


    public abstract void onNetChanged(boolean netWorkState);

    @Override
    public void onNetChange(boolean netWorkState) {
        onNetChanged(netWorkState);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //如果有遥控器数字监听
        switch (keyCode) {
            case KeyEvent.KEYCODE_ENTER:     //确定键enter
            case KeyEvent.KEYCODE_DPAD_CENTER:

                if (controlKey.equals(XmlUtils.getValue(ConstantUtils.CONFIG_ADMINCMD, FileHelper.SDCardPath()
                        + ConstantUtils.QUEUINGFILE_CONFIG_XML))) {
                    handler.sendEmptyMessage(0);
                }
                controlKey = "";
                break;
            case KeyEvent.KEYCODE_1:   //数字键0
                controlKey += "1";
                break;
            case KeyEvent.KEYCODE_2:   //数字键0
                controlKey += "2";
                break;
            case KeyEvent.KEYCODE_3:   //数字键0
                controlKey += "3";
                break;
            case KeyEvent.KEYCODE_4:   //数字键0
                controlKey += "4";
                break;

        }
        if (onKeyWebDown(keyCode, event)) {
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    public boolean onKeyWebDown(int keyCode, KeyEvent event) {
        return false;
    }

}
