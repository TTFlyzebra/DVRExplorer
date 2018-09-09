package com.longhorn.dvrexplorer.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by FlyZebra on 2018/6/13.
 * Descrip:
 */

public class IPAdressTools {
    public static boolean isIP(String addr) {
        if (addr.length() < 7 || addr.length() > 15 || "".equals(addr)) {
            return false;
        }
        String rexp = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";
        Pattern pat = Pattern.compile(rexp);
        Matcher mat = pat.matcher(addr);
        return mat.find();
    }
}
