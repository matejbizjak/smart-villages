package com.matejbizjak.smartvillages.house.lib.v1;

import com.matejbizjak.smartvillages.house.persistence.HouseEnergyEntity;
import com.matejbizjak.smartvillages.house.persistence.HouseEntity;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public class EnergyHouseIntervalForHouse {

    private HouseEntity house;

    private Instant startTime;

    private Instant endTime;

    private BigDecimal sum;

    private List<HouseEnergyEntity> energyList;

    public EnergyHouseIntervalForHouse() {
    }

    public EnergyHouseIntervalForHouse(HouseEntity house, Instant startTime, Instant endTime, BigDecimal sum, List<HouseEnergyEntity> energyList) {
        this.house = house;
        this.startTime = startTime;
        this.endTime = endTime;
        this.sum = sum;
        this.energyList = energyList;
    }

    public HouseEntity getHouse() {
        return house;
    }

    public void setHouse(HouseEntity house) {
        this.house = house;
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

    public List<HouseEnergyEntity> getEnergyList() {
        return energyList;
    }

    public void setEnergyList(List<HouseEnergyEntity> energyList) {
        this.energyList = energyList;
    }
}
