package com.example.tma;

public class Production {
    String sup, meter, taka, date;

    public Production(String sup, String meter, String taka, String date) {
        this.sup = sup;
        this.meter = meter;
        this.taka = taka;
        this.date = date;
    }

    public String getSup() {
        return sup;
    }

    public void setSup(String sup) {
        this.sup = sup;
    }

    public String getMeter() {
        return meter;
    }

    public void setMeter(String meter) {
        this.meter = meter;
    }

    public String getTaka() {
        return taka;
    }

    public void setTaka(String taka) {
        this.taka = taka;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
