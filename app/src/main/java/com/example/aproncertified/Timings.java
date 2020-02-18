package com.example.aproncertified;

import java.io.Serializable;

public class Timings implements Serializable {

    String day;
    int fromHr;
    int toHr;
    int fromMin;
    int toMin;

    public Timings() {
    }

    public Timings(String day, int fromHr, int fromMin, int toHr, int toMin) {
        this.day = day;
        this.fromHr = fromHr;
        this.toHr = toHr;
        this.fromMin = fromMin;
        this.toMin = toMin;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public int getFromHr() {
        return fromHr;
    }

    public void setFromHr(int fromHr) {
        this.fromHr = fromHr;
    }

    public int getToHr() {
        return toHr;
    }

    public void setToHr(int toHr) {
        this.toHr = toHr;
    }

    public int getFromMin() {
        return fromMin;
    }

    public void setFromMin(int fromMin) {
        this.fromMin = fromMin;
    }

    public int getToMin() {
        return toMin;
    }

    public void setToMin(int toMin) {
        this.toMin = toMin;
    }
}
