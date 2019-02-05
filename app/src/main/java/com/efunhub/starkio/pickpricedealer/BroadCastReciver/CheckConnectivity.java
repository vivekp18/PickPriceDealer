package com.efunhub.starkio.pickpricedealer.BroadCastReciver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.efunhub.starkio.pickpricedealer.Interface.NoInternetListener;

/**
 * Created by Admin on 08-12-2018.
 */

public class CheckConnectivity  extends BroadcastReceiver {

    NoInternetListener noInternetListener;
    Context mContext;

    public CheckConnectivity(Context context, NoInternetListener noInternetListener) {
        this.mContext = context;
        this.noInternetListener = noInternetListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            if (isOnline(context)) {
                noInternetListener.availConnection(true);
            } else {
                noInternetListener.availConnection(false);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private boolean isOnline(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = null;
            if (cm != null) {
                netInfo = cm.getActiveNetworkInfo();
            }
            //should check null because in airplane mode it will be null
            return (netInfo != null && netInfo.isConnected());

        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
    }
}
