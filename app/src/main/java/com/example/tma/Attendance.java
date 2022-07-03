package com.example.tma;

public class Attendance {
    String sup,date,workerName,status;

    public Attendance(String sup, String date, String workerName, String status) {
        this.sup = sup;
        this.date = date;
        this.workerName = workerName;
        this.status = status;
    }

    public String getSup() {
        return sup;
    }

    public void setSup(String sup) {
        this.sup = sup;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getWorkerName() {
        return workerName;
    }

    public void setWorkerName(String workerName) {
        this.workerName = workerName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
