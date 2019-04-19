package com.jlinc.android.hyqfsad.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.jlinc.android.hyqfsad.INetWorkState;
import com.jlinc.android.hyqfsad.base.BaseActivity;
import com.jlinc.android.hyqfsad.utils.CommonUtils;

/**
 * 网络变化的广播
 */
public class NetworkStateReceiver extends BroadcastReceiver {

    private INetWorkState mINetEvent= BaseActivity.iNetWorkState;


    @Override
    public void onReceive(Context context, Intent intent) {
        // 如果相等的话就说明网络状态发生了变化
        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            //容错机制
            if(mINetEvent!=null) {
                mINetEvent.onNetChange(CommonUtils.isNetworkAvailable(context));
            }
        }

    }
}
