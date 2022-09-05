package com.matejbizjak.smartvillages.vehicle.lib.v1;

public class Vehicle {

    private String id;

    private String userId;

    private String registrationNumber;

    public Vehicle() {
    }

    public Vehicle(String id, String userId, String registrationNumber) {
        this.id = id;
        this.userId = userId;
        this.registrationNumber = registrationNumber;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }
}
