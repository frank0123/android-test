package com.example.myandroid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import com.example.utils.GpsUtil;

public class LaunchActivity extends Activity{
    private int flag;
    private static final String Log_Tag = "LaunchActivity";

    private String longitudeLatitude;
    private GpsUtil gpsUtil;
    private LocationManager locationManager;
    private String bestProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //hiding the status navigation bar
        flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        Window window = LaunchActivity.this.getWindow();
        window.setFlags(flag, flag);

        setContentView(R.layout.launch_activity_layout);  //setting the page-layout

        //get location
        gpsUtil = new GpsUtil();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        bestProvider = locationManager.getBestProvider(gpsUtil.getCriteria(), true);
        Location location = locationManager.getLastKnownLocation(bestProvider);

        if(gpsUtil.GpsServiceAvailable(locationManager)){
            longitudeLatitude = gpsUtil.getLongitudeAndLatitude(location);
            Log.v(Log_Tag, "当前经纬度："+longitudeLatitude);
            System.out.println("当前经纬度LaunchActivity："+longitudeLatitude);
        } else {
            Toast.makeText(LaunchActivity.this, "GPS定位服务未开启，请开启GPS服务", Toast.LENGTH_SHORT).show();
            //jump to gps settings page
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }

        //go to login-activity after 4 seconds
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(LaunchActivity.this, LoginActivity.class);
                startActivity(intent);
                LaunchActivity.this.finish();
            }
        }, 4000);
    }
}
