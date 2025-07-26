package web;

import java.util.Calendar;

public class WebUtils {
    public static String getFriendlyDatetime() {
        var c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DATE);
        int hour = c.get(Calendar.HOUR);
        int min = c.get(Calendar.MINUTE);
        int sec = c.get(Calendar.SECOND);
        return String.format("%d-%d-%d %d:%d:%d",year, month, day, hour, min, sec);
    }
}
