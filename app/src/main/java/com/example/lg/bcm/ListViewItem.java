package com.example.lg.bcm;

/**
 * Created by byunseonuk on 2018-06-04.
 */

public class ListViewItem {
    private String company;
    private String name;
    private String phone;
    private String tel;
    private String email;
    private String address;
    private String imgurl;
    private String user_id;

    public ListViewItem(String company, String name, String phone, String tel, String email, String address, String imgurl, String user_id) {
        this.company = company;
        this.name = name;
        this.phone = phone;
        this.tel = tel;
        this.email = email;
        this.address = address;
        this.imgurl = imgurl;
        this.user_id = user_id;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
