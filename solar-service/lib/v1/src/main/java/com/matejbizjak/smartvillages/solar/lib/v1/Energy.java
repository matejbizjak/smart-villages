package com.matejbizjak.smartvillages.solar.lib.v1;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;

public class Energy {

    private BigDecimal watt;

    private Instant startTime;

    private Duration duration;

    public Energy() {
    }

    public Energy(BigDecimal watt, Instant startTime, Duration duration) {
        this.watt = watt;
        this.startTime = startTime;
        this.duration = duration;
    }

    public BigDecimal getWatt() {
        return watt;
    }

    public void setWatt(BigDecimal watt) {
        this.watt = watt;
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
