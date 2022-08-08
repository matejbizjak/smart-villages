package com.matejbizjak.smartvillages.solar.services.responses;

import com.matejbizjak.smartvillages.solar.persistence.EnergyEntity;
import com.matejbizjak.smartvillages.solar.persistence.SolarEntity;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public class EnergyResponse {

    private SolarEntity solar;

    private Instant startTime;

    private Instant endTime;

    private BigDecimal sum;

    private List<EnergyEntity> energyList;

    public EnergyResponse() {
    }

    public EnergyResponse(SolarEntity solar, Instant startTime, Instant endTime, BigDecimal sum, List<EnergyEntity> energyList) {
        this.solar = solar;
        this.startTime = startTime;
        this.endTime = endTime;
        this.sum = sum;
        this.energyList = energyList;
    }

    public SolarEntity getSolar() {
        return solar;
    }

    public void setSolar(SolarEntity solar) {
        this.solar = solar;
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

    public List<EnergyEntity> getEnergyList() {
        return energyList;
    }

    public void setEnergyList(List<EnergyEntity> energyList) {
        this.energyList = energyList;
    }
}
