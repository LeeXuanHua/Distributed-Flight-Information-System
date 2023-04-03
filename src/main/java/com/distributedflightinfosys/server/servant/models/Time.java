package com.distributedflightinfosys.server.servant.models;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Self-defined time class to store the year, month, date, hours, min, sec, and ms in integers.
 * Provides methods to convert to DateTime object and vice versa (for Java JPA).
 */
@Getter
@Setter
@NoArgsConstructor
public class Time {
    private int year;
    private int month;
    private int date;
    private int hours;
    private int min;
    private int sec;
    private int ms;

    public Time(int year, int month, int date, int hours, int min, int sec, int ms) {
        this.year = year;
        this.month = month;
        this.date = date;
        this.hours = hours;
        this.min = min;
        this.sec = sec;
        this.ms = ms;
    }

    // Convert to DateTime object - note that LocalDateTime uses nanoseconds instead of milliseconds
    public LocalDateTime toDateTime() {
        return LocalDateTime.of(year, month, date, hours, min, sec, ms * 1000);
    }

    // Convert from DateTime object - note that LocalDateTime uses nanoseconds instead of milliseconds
    public static Time fromDateTime(LocalDateTime dt) {
        return new Time(dt.getYear(), dt.getMonthValue(), dt.getDayOfMonth(),
                dt.getHour(), dt.getMinute(), dt.getSecond(), dt.getNano() / 1000);
    }
}
