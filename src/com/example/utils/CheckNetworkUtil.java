package com.example.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class CheckNetworkUtil {
    private static final String Log_Tag = "CheckNetworkUtil";
    private Context context;
    private ConnectivityManager connectivityManager;
    private NetworkInfo[] networkInfo;

    public boolean isAvailable(Activity activity){
        context = activity.getApplicationContext();
        connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if(connectivityManager == null){
            return false;
        }
        else{
            networkInfo = connectivityManager.getAllNetworkInfo();
            if(networkInfo != null && networkInfo.length > 0){
                for(int i = 0; i < networkInfo.length; i++){
                    Log.v(Log_Tag, "type of net-work：" + networkInfo[i].getTypeName() + "，status of net-work：" + networkInfo[i].getState());

                    if(networkInfo[i].getState() == NetworkInfo.State.CONNECTED){
                        return true;
                    }
                }
            }
        }

        return false;
    }
}
