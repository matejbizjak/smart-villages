package com.matejbizjak.smartvillages.charger.lib.v1;

import com.matejbizjak.smartvillages.charger.persistence.ChargerEnergyEntity;
import com.matejbizjak.smartvillages.charger.persistence.ChargerEntity;
import com.matejbizjak.smartvillages.vehicle.lib.v1.Vehicle;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public class EnergyChargerIntervalForVehicle {

    private Vehicle vehicle;

    private Instant startTime;

    private Instant endTime;

    private BigDecimal sum;

    private List<ChargerEnergyEntity> energyList;

    public EnergyChargerIntervalForVehicle() {
    }

    public EnergyChargerIntervalForVehicle(Vehicle vehicle, Instant startTime, Instant endTime, BigDecimal sum, List<ChargerEnergyEntity> energyList) {
        this.vehicle = vehicle;
        this.startTime = startTime;
        this.endTime = endTime;
        this.sum = sum;
        this.energyList = energyList;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    public BigDecimal getSum() {
        return sum;
    }

    public void setSum(BigDecimal sum) {
        this.sum = sum;
    }

    public List<ChargerEnergyEntity> getEnergyList() {
        return energyList;
    }

    public void setEnergyList(List<ChargerEnergyEntity> energyList) {
        this.energyList = energyList;
    }
}
