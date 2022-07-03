package com.example.tma;

public class WorkerProduction {
    String sup, name,meter, date;

    public WorkerProduction(String sup, String name,String meter,  String date) {
        this.sup = sup;
        this.meter = meter;
        this.name = name;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
