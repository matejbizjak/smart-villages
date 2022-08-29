package com.matejbizjak.smartvillages.house.lib.v1;

import com.matejbizjak.smartvillages.userlib.v1.User;

import java.math.BigDecimal;
import java.util.List;

public class EnergyHouseIntervalForUser {

    private User user;

    private List<EnergyHouseIntervalForHouse> energyHouseList;

    private BigDecimal sum;

    public EnergyHouseIntervalForUser() {
    }

    public EnergyHouseIntervalForUser(User user, List<EnergyHouseIntervalForHouse> energyHouseList, BigDecimal sum) {
        this.user = user;
        this.energyHouseList = energyHouseList;
        this.sum = sum;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
