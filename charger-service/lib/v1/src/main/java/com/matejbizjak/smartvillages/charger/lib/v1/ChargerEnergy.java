package com.matejbizjak.smartvillages.charger.lib.v1;

import com.matejbizjak.smartvillages.vehicle.lib.v1.Vehicle;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;

public class ChargerEnergy {

    private Vehicle vehicle;

    private BigDecimal value;

    private Instant startTime;

    private Duration duration;

    public ChargerEnergy() {
    }

    public ChargerEnergy(Vehicle vehicle, BigDecimal value, Instant startTime, Duration duration) {
        this.vehicle = vehicle;
        this.value = value;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }
}
