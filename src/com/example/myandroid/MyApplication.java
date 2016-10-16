package com.example.myandroid;

import android.app.Application;

public class MyApplication extends Application{
    private static final String serverUrl = "http://testyidao.happysoft.co:8000";
    private static final String localVersionCode = "1.0.8";
    private String remoteVersionCode;
    private boolean hasNewVersion;

    @Override
    public void onCreate() {
        super.onCreate();

        setRemoteVersionCode(null);
        setHasNewVersion(false);
    }

    //serverUrl
    public static String getServerUrl() {
        return serverUrl;
    }
    //versionCode
    public String getLocalVersionCode() {
        return localVersionCode;
    }
    //remoteVersionCode
    public void setRemoteVersionCode(String remoteVersionCode){
        this.remoteVersionCode = remoteVersionCode;
    }
    public String getRemoteVersionCode(){
        return remoteVersionCode;
    }
    //hasNewVersion
    public void setHasNewVersion(boolean hasNewVersion){
        this.hasNewVersion = hasNewVersion;
    }
    public boolean getHasNewVersion(){
        return hasNewVersion;
    }
}
