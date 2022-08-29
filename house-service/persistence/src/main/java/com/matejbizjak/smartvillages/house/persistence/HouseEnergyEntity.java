package com.matejbizjak.smartvillages.house.persistence;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;

@Entity
@Table(name = "house_energy")
@NamedNativeQueries({
        @NamedNativeQuery(name = HouseEnergyEntity.FIND_HOUSE_ENERGY_DURING_TIME_PERIOD
                , query = "SELECT * FROM house_energy WHERE house_id = :houseId AND start_time >= :startTime AND start_time + CAST(duration/1000000000||' seconds' AS interval) < :endTime ORDER BY start_time"
                , resultClass = HouseEnergyEntity.class),
        @NamedNativeQuery(name = HouseEnergyEntity.FIND_ALL_HOUSE_ENERGY_DURING_TIME_PERIOD_FOR_USER
                , query = "SELECT * FROM house_energy WHERE house_id IN :houseId AND start_time >= :startTime AND start_time + CAST(duration/1000000000||' seconds' AS interval) < :endTime ORDER BY house_id, start_time"
                , resultClass = HouseEnergyEntity.class),
        @NamedNativeQuery(name = HouseEnergyEntity.FIND_ALL_HOUSE_ENERGY_DURING_TIME_PERIOD
                , query = "SELECT * FROM house_energy WHERE start_time >= :startTime AND start_time + CAST(duration/1000000000||' seconds' AS interval) < :endTime ORDER BY house_id, start_time"
                , resultClass = HouseEnergyEntity.class)
})
public class HouseEnergyEntity {

    public static final String FIND_HOUSE_ENERGY_DURING_TIME_PERIOD = "House.findHouseEnergyDuringTimePeriod";
    public static final String FIND_ALL_HOUSE_ENERGY_DURING_TIME_PERIOD_FOR_USER = "House.findAllHouseEnergyDuringTimePeriodForUser";
    public static final String FIND_ALL_HOUSE_ENERGY_DURING_TIME_PERIOD = "House.findAllHouseEnergyDuringTimePeriod";

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "energy_id")
    private String id;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "house_id", referencedColumnName = "house_id")
    private HouseEntity house;

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
