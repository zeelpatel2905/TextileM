package com.example.tma;

public class Supervisor {
    public String fname,lname,age,contactNo,emailID,date;

    public Supervisor(String fname, String lname, String age, String contactNo, String emailID,String date) {
        this.fname = fname;
        this.lname = lname;
        this.age = age;
        this.contactNo = contactNo;
        this.emailID = emailID;
        this.date=date;
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

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public String getEmailID() {
        return emailID;
    }

    public void setEmailID(String emailID) {
        this.emailID = emailID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
