package com.matejbizjak.smartvillages.vehicle.lib.v1;

import java.time.Instant;

public class Vehicle {

    private String id;

    private String userId;

    private String registrationNumber;

    private Instant firstRegistrationDate;

    public Vehicle() {
    }

    public Vehicle(String id, String userId, String registrationNumber, Instant firstRegistrationDate) {
        this.id = id;
        this.userId = userId;
        this.registrationNumber = registrationNumber;
        this.firstRegistrationDate = firstRegistrationDate;
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

    public Instant getFirstRegistrationDate() {
        return firstRegistrationDate;
    }

    public void setFirstRegistrationDate(Instant firstRegistrationDate) {
        this.firstRegistrationDate = firstRegistrationDate;
    }
}
