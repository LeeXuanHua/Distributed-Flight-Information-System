package com.distributedflightinfosys.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// Helper class to format strings for display (client-side)
public class StringHelper {
    public static String formatCurrency(double currency) {
        return "$" + currency;
    }

    public static String formatLocalDateTime(LocalDateTime dateTime) {
        return formatLocalDateTime(dateTime, "yyyy-MM-dd HH:mm:ss");
    }

    public static String formatLocalDateTime(LocalDateTime dateTime, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return dateTime.format(formatter);
    }
}
