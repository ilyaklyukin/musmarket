package com.umnix.musmarket.model;

import java.util.List;

public class Store {

    private long id;
    private String name;
    private String address;
    private String website;
    private String email;
    private String phone;
    private Location location;
    private int instrumentCount;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setInstrumentsCount(List<Stock> stocks) {
        instrumentCount = 0;
        if (stocks == null) {
            return;
        }

        for (Stock stock : stocks) {
            instrumentCount += stock.getQuantity();
        }
    }

    public int getInstrumentCount() {
        return instrumentCount;
    }

    @Override
    public String toString() {
        return "Store{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", website='" + website + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", location=" + location +
                '}';
    }
}
