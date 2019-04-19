package com.jlinc.android.hyqfsad.base;

import android.content.Context;

/**
 * Presenter基类
 * @param <T>
 */
public abstract class BasePresenter<T> {
    public Context context;
    public T mView;

    public void setVM(Context context,T v) {
        this.mView = v;
        this.context=context;
        this.onStart();
    }

    public abstract void onStart();

    public  void onDestroy(){
        mView=null;
        context=null;
    }
}
