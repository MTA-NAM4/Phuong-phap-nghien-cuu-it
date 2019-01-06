package com.binh.maytinhsieuthi.model;

/**
 * Created by NgocTri on 11/7/2015.
 */
public class Product {
    private String ma;
    private String name;
    private String gia;
    private String thongtin;

    public Product() {
    }

    public Product(String ma, String name, String gia, String thongtin) {
        this.ma = ma;
        this.name = name;
        this.gia = gia;
        this.thongtin = thongtin;
    }

    public String getMa() {
        return ma;
    }

    public void setMa(String ma) {
        this.ma = ma;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGia() {
        return gia;
    }

    public void setGia(String gia) {
        this.gia = gia;
    }

    public String getThongtin() {
        return thongtin;
    }

    public void setThongtin(String thongtin) {
        this.thongtin = thongtin;
    }
}
