package com.matejbizjak.smartvillages.central.lib.v1;

import com.matejbizjak.smartvillages.charger.lib.v1.EnergyChargerIntervalForVehicle;
import com.matejbizjak.smartvillages.house.lib.v1.EnergyHouseIntervalForHouse;
import com.matejbizjak.smartvillages.solar.lib.v1.EnergySolarIntervalForSolar;
import com.matejbizjak.smartvillages.userlib.v1.User;

import java.math.BigDecimal;
import java.util.List;

public class TotalUserEnergyInterval {

    private User user;
    private List<EnergySolarIntervalForSolar> energySolarList;
    private List<EnergyChargerIntervalForVehicle> energyChargerVehicleList;
    private List<EnergyHouseIntervalForHouse> energyHouseList;
    private BigDecimal sum;

    public TotalUserEnergyInterval() {
    }

    public TotalUserEnergyInterval(User user, List<EnergySolarIntervalForSolar> energySolarList, List<EnergyChargerIntervalForVehicle> energyChargerVehicleList, List<EnergyHouseIntervalForHouse> energyHouseList, BigDecimal sum) {
        this.user = user;
        this.energySolarList = energySolarList;
        this.energyChargerVehicleList = energyChargerVehicleList;
        this.energyHouseList = energyHouseList;
        this.sum = sum;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<EnergySolarIntervalForSolar> getEnergySolarList() {
        return energySolarList;
    }

    public void setEnergySolarList(List<EnergySolarIntervalForSolar> energySolarList) {
        this.energySolarList = energySolarList;
    }

    public List<EnergyChargerIntervalForVehicle> getEnergyChargerVehicleList() {
        return energyChargerVehicleList;
    }

    public void setEnergyChargerVehicleList(List<EnergyChargerIntervalForVehicle> energyChargerVehicleList) {
        this.energyChargerVehicleList = energyChargerVehicleList;
    }

    public List<EnergyHouseIntervalForHouse> getEnergyHouseList() {
        return energyHouseList;
    }

    public void setEnergyHouseList(List<EnergyHouseIntervalForHouse> energyHouseList) {
        this.energyHouseList = energyHouseList;
    }

    public BigDecimal getSum() {
        return sum;
    }

    public void setSum(BigDecimal sum) {
        this.sum = sum;
    }
}
