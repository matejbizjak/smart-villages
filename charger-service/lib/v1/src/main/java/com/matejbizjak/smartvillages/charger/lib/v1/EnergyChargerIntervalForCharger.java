package com.matejbizjak.smartvillages.charger.lib.v1;

import com.matejbizjak.smartvillages.charger.persistence.ChargerEnergyEntity;
import com.matejbizjak.smartvillages.charger.persistence.ChargerEntity;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public class EnergyChargerIntervalForCharger {

    private ChargerEntity charger;

    private Instant startTime;

    private Instant endTime;

    private BigDecimal sum;

    private List<ChargerEnergyEntity> energyList;

    public EnergyChargerIntervalForCharger() {
    }

    public EnergyChargerIntervalForCharger(ChargerEntity charger, Instant startTime, Instant endTime, BigDecimal sum, List<ChargerEnergyEntity> energyList) {
        this.charger = charger;
        this.startTime = startTime;
        this.endTime = endTime;
        this.sum = sum;
        this.energyList = energyList;
    }

    public ChargerEntity getCharger() {
        return charger;
    }

    public void setCharger(ChargerEntity charger) {
        this.charger = charger;
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
