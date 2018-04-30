package com.sdm.sdmflash.databases.dataTypes;

import java.util.Calendar;
import java.util.Date;

/**
 * Třída pro úpravu času
 */
public class DateTools {

    public static Date getDayBack(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, -24);
        return calendar.getTime();
    }

    public static Date getWeekBack(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -7);
        return calendar.getTime();
    }

    public static Date getMonthBack(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        return calendar.getTime();
    }

    public static Date getYearBack(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -1);
        return calendar.getTime();
    }

}
