package com.example.utils;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

public class GpsUtil {
    //GGWMi93ROHln4e5ZZlN1YHIGTPknw2Ly
    private static final String Log_Tag = "GpsUtil";

    //check the gps service if available
    public boolean GpsServiceAvailable(LocationManager locationManager){
        if(locationManager.getProvider(LocationManager.GPS_PROVIDER) != null || locationManager.getProvider(LocationManager.GPS_PROVIDER) != null){
            return true;
        } else {
            return false;
        }
    }

    //setting of query location
    public Criteria getCriteria(){
        Criteria criteria = new Criteria();
        //fine criteria
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        //speed of query-reactions needs
        criteria.setSpeedRequired(true);
        //free or charge
        criteria.setCostAllowed(false);
        //bearing needs
        criteria.setBearingRequired(false);
        //altitude needs
        criteria.setAltitudeRequired(true);
        //power requirement
        criteria.setPowerRequirement(Criteria.POWER_LOW);

        return criteria;
    }

    //get the longitude and latitude
    public String getLongitudeAndLatitude(Location location){
        if(location != null){
            Log.v(Log_Tag, "当前经纬度："+location.getLongitude()+"，"+ location.getLatitude());
            System.out.println("当前经纬度GpsUtil："+location.getLongitude()+"，"+ location.getLatitude());
            return String.valueOf(location.getLongitude() +"，"+ location.getLatitude());
        }
        else{
            return "获取地理位置失败";
        }
    }
}
