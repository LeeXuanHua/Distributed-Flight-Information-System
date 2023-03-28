package com.example.demo.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
