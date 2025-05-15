package com.example.flowermanagementsystem;

public class ShippingInfo {
    private String fullName;
    private String address;
    private String city;
    private String postalCode;
    private String phoneNumber;

    public ShippingInfo(String fullName, String address, String city, String postalCode, String phoneNumber) {
        this.fullName = fullName;
        this.address = address;
        this.city = city;
        this.postalCode = postalCode;
        this.phoneNumber = phoneNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    @Override
    public String toString() {
        return fullName + "\n" + address + "\n" + city + ", " + postalCode + "\n" + phoneNumber;
    }
}
