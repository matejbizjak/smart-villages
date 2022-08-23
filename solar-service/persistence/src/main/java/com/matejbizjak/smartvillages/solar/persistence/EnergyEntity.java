package com.matejbizjak.smartvillages.solar.persistence;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;

@Entity
@Table(name = "solar_energy")
@NamedNativeQueries({
        @NamedNativeQuery(name = EnergyEntity.FIND_SOLAR_ENERGY_DURING_TIME_PERIOD
                , query = "SELECT * FROM solar_energy WHERE solar_id = :solarId AND start_time >= :startTime AND start_time + CAST(duration/1000000000||' seconds' AS interval) < :endTime ORDER BY start_time"
                , resultClass = EnergyEntity.class),
        @NamedNativeQuery(name = EnergyEntity.FIND_ALL_SOLAR_ENERGY_DURING_TIME_PERIOD_FOR_USER
                , query = "SELECT * FROM solar_energy WHERE solar_id IN :solarId AND start_time >= :startTime AND start_time + CAST(duration/1000000000||' seconds' AS interval) < :endTime ORDER BY solar_id, start_time"
                , resultClass = EnergyEntity.class),
        @NamedNativeQuery(name = EnergyEntity.FIND_ALL_SOLAR_ENERGY_DURING_TIME_PERIOD
                , query = "SELECT * FROM solar_energy WHERE start_time >= :startTime AND start_time + CAST(duration/1000000000||' seconds' AS interval) < :endTime ORDER BY solar_id, start_time"
                , resultClass = EnergyEntity.class)
})
public class EnergyEntity {

    public static final String FIND_SOLAR_ENERGY_DURING_TIME_PERIOD = "Solar.findSolarEnergyDuringTimePeriod";
    public static final String FIND_ALL_SOLAR_ENERGY_DURING_TIME_PERIOD_FOR_USER = "Solar.findAllSolarEnergyDuringTimePeriodForUser";
    public static final String FIND_ALL_SOLAR_ENERGY_DURING_TIME_PERIOD = "Solar.findAllSolarEnergyDuringTimePeriod";

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "energy_id")
    private String id;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "solar", referencedColumnName = "solar_id")
    private SolarEntity solar;

    @Column(name = "start_time")
    private Instant startTime;

    private Duration duration;

    private BigDecimal value;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }
}
