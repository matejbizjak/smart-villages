package com.matejbizjak.smartvillages.solar.lib.v1;

import com.matejbizjak.smartvillages.userlib.v1.User;

import java.math.BigDecimal;
import java.util.List;

public class EnergySolarIntervalForUser {

    private User user;

    private List<EnergySolarIntervalForSolar> energySolarList;

    private BigDecimal sum;

    public EnergySolarIntervalForUser() {
    }

    public EnergySolarIntervalForUser(User user, List<EnergySolarIntervalForSolar> energySolarList, BigDecimal sum) {
        this.user = user;
        this.energySolarList = energySolarList;
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

    public BigDecimal getSum() {
        return sum;
    }

    public void setSum(BigDecimal sum) {
        this.sum = sum;
    }
}
