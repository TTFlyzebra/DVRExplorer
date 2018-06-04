package com.longhorn.dvrexplorer.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by FlyZebra on 2018/5/25.
 * Descrip:
 */

public class DateTools {
    public static final String DATE_FORMAT_TIME = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT_FILENAME = "yyyyMMdd_HHmmss";

    public static String date2String(long time) {
        return date2String(time, DATE_FORMAT_TIME);
    }

    public static String date2String(long time, String fomat) {
        SimpleDateFormat format = new SimpleDateFormat(fomat);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date date = new Date(time);
        return format.format(date);
    }
}
