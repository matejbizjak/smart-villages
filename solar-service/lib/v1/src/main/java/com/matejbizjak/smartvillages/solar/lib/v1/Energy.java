package com.matejbizjak.smartvillages.solar.lib.v1;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;

public class Energy {

    private BigDecimal value;

    private Instant startTime;

    private Duration duration;

    public Energy() {
    }

    public Energy(BigDecimal value, Instant startTime, Duration duration) {
        this.value = value;
        this.startTime = startTime;
        this.duration = duration;
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
