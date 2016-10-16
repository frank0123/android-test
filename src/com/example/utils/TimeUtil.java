package com.example.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TimeUtil {
    //get the currenct time with format 2016-09-12 00:00:00
    public Date getCurrenctTime(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date currenctDate = new Date(System.currentTimeMillis());
        String currenctTimeStr = simpleDateFormat.format(currenctDate);

        Date currenctTime = null;
        try{
            currenctTime = simpleDateFormat.parse(currenctTimeStr);
        }catch (ParseException e){
            e.printStackTrace();
        }

        return currenctTime;
    }

    //calculate the time difference return value with the map formate, which contains days,hours,minutes and seconds information
    public Map<String, Long> caculateTimeDifference(Date currenctDate, Date lastDate){
        Map<String, Long> timeMap = new HashMap<String, Long>();
        //convert the millseconds value to days-hours-minutes-seconds
        long timeDiff = currenctDate.getTime() - lastDate.getTime();
        long timeDiffOfDays    = timeDiff / (1000 * 60 * 60 * 24);
        long timeDiffOfHours   = (timeDiff - timeDiffOfDays * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        long timeDiffOfMinutes = (timeDiff - timeDiffOfDays * (1000 * 60 * 60 * 24) - timeDiffOfHours * (1000 * 60 * 60)) / (1000 * 60);
        long timeDiffOfSeconds = (timeDiff - timeDiffOfDays * (1000 * 60 * 60 * 24) - timeDiffOfHours * (1000 * 60 * 60) - timeDiffOfMinutes * (1000 * 60)) / 1000;

        timeMap.put("days", timeDiffOfDays);
        timeMap.put("hours", timeDiffOfHours);
        timeMap.put("minutes", timeDiffOfMinutes);
        timeMap.put("seconds", timeDiffOfSeconds);

        return timeMap;
    }
}
