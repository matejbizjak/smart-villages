package com.matejbizjak.smartvillages.charger.lib.v1;

import com.matejbizjak.smartvillages.userlib.v1.User;

import java.math.BigDecimal;
import java.util.List;

public class EnergyChargerIntervalForUser {

    private User user;

    private List<EnergyChargerIntervalForVehicle> energyVehicleList;

    private BigDecimal sum;

    public EnergyChargerIntervalForUser() {
    }

    public EnergyChargerIntervalForUser(User user, List<EnergyChargerIntervalForVehicle> energyVehicleList, BigDecimal sum) {
        this.user = user;
        this.energyVehicleList = energyVehicleList;
        this.sum = sum;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<EnergyChargerIntervalForVehicle> getEnergyVehicleList() {
        return energyVehicleList;
    }

    public void setEnergyVehicleList(List<EnergyChargerIntervalForVehicle> energyVehicleList) {
        this.energyVehicleList = energyVehicleList;
    }

    public BigDecimal getSum() {
        return sum;
    }

    public void setSum(BigDecimal sum) {
        this.sum = sum;
    }
}
