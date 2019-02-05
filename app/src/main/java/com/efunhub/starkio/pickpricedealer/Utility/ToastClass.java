package com.efunhub.starkio.pickpricedealer.Utility;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Admin on 08-12-2018.
 */

public class ToastClass {

    public void makeToast(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}
