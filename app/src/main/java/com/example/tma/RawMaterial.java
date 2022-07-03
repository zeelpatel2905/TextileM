package com.example.tma;

public class RawMaterial {
    String sup,companyName,rate,quantity,type,price,date;

    public RawMaterial() {
    }

    public RawMaterial(String sup, String companyName, String rate, String quantity, String type, String price, String date) {
        this.sup = sup;
        this.companyName = companyName;
        this.rate = rate;
        this.quantity = quantity;
        this.type = type;
        this.price = price;
        this.date = date;
    }

    public String getSup() {
        return sup;
    }

    public void setSup(String sup) {
        this.sup = sup;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
