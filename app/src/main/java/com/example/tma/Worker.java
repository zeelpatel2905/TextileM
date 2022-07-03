package com.example.tma;

public class Worker {
    public String sup,fname,lname,contactNo,type,dailyWages,date;
    public double totalAttendance;
    public double totalSalary;

    public Worker(String sup, String fname, String lname, String contactNo, String type, String dailyWages, double totalAttendance, double totalSalary, String date) {
        this.sup=sup;
        this.fname = fname;
        this.lname = lname;
        this.contactNo = contactNo;
        this.type = type;
        this.dailyWages = dailyWages;
        this.totalAttendance = totalAttendance;
        this.totalSalary = totalSalary;
        this.date=date;
    }

    public Worker() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public String getContactNo() {
        return contactNo;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDailyWages() {
        return dailyWages;
    }

    public double getTotalAttendance() {
        return totalAttendance;
    }

    public double getTotalSalary() {
        return totalSalary;
    }
}
