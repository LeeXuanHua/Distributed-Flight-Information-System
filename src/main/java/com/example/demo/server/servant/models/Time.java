package com.example.demo.server.servant.models;

import java.time.LocalDateTime;

/**
 * Self-defined time class to store the year, month, date, hours, min, sec, and ms in integers.
 * Provides methods to convert to DateTime object and vice versa (for Java JPA).
 */
public class Time {
    private int year;
    private int month;
    private int date;
    private int hours;
    private int min;
    private int sec;
    private int ms;

    public Time() {}

    public Time(int year, int month, int date, int hours, int min, int sec, int ms) {
        this.year = year;
        this.month = month;
        this.date = date;
        this.hours = hours;
        this.min = min;
        this.sec = sec;
        this.ms = ms;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getSec() {
        return sec;
    }

    public void setSec(int sec) {
        this.sec = sec;
    }

    public int getMs() {
        return ms;
    }

    public void setMs(int ms) {
        this.ms = ms;
    }

    public LocalDateTime toDateTime() {
        return LocalDateTime.of(year, month, date, hours, min, sec, ms * 1000);
    }

    public static Time fromDateTime(LocalDateTime dt) {
        return new Time(dt.getYear(), dt.getMonthValue(), dt.getDayOfMonth(),
                dt.getHour(), dt.getMinute(), dt.getSecond(), dt.getNano() / 1000);
    }
}
