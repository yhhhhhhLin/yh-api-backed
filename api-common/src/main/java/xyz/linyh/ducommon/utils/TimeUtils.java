package xyz.linyh.ducommon.utils;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class TimeUtils {

    /**
     *
     */
    public static boolean isTimeBeforeNow(String timeStr, String timeFormat) {
        // 定义时间格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(timeFormat);

        try {
            // 解析输入时间
            LocalTime inputTime = LocalTime.parse(timeStr, formatter);

            // 获取当前时间
            LocalTime now = LocalTime.now();

            // 比较时间
            return inputTime.isBefore(now);
        } catch (DateTimeParseException e) {
            System.err.println("Invalid time format: " + e.getMessage());
            return false;
        }
    }
}
