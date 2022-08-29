package com.matejbizjak.smartvillages.charger.persistence;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;

@Entity
@Table(name = "charger_energy")
@NamedNativeQueries({
        @NamedNativeQuery(name = ChargerEnergyEntity.FIND_CHARGER_ENERGY_DURING_TIME_PERIOD
                , query = "SELECT * FROM charger_energy WHERE charger_id = :chargerId AND start_time >= :startTime AND start_time + CAST(duration/1000000000||' seconds' AS interval) < :endTime ORDER BY start_time"
                , resultClass = ChargerEnergyEntity.class),
        @NamedNativeQuery(name = ChargerEnergyEntity.FIND_ALL_CHARGER_ENERGY_DURING_TIME_PERIOD_FOR_USER
                , query = "SELECT * FROM charger_energy WHERE charger_id IN :chargerId AND start_time >= :startTime AND start_time + CAST(duration/1000000000||' seconds' AS interval) < :endTime ORDER BY charger_id, start_time"
                , resultClass = ChargerEnergyEntity.class),
        @NamedNativeQuery(name = ChargerEnergyEntity.FIND_ALL_CHARGER_ENERGY_DURING_TIME_PERIOD
                , query = "SELECT * FROM charger_energy WHERE start_time >= :startTime AND start_time + CAST(duration/1000000000||' seconds' AS interval) < :endTime ORDER BY charger_id, start_time"
                , resultClass = ChargerEnergyEntity.class),
        @NamedNativeQuery(name = ChargerEnergyEntity.FIND_ALL_VEHICLE_ENERGY_DURING_TIME_PERIOD
                , query = "SELECT * FROM charger_energy WHERE vehicle_id = :vehicleId AND start_time >= :startTime AND start_time + CAST(duration/1000000000||' seconds' AS interval) < :endTime ORDER BY charger_id, start_time"
                , resultClass = ChargerEnergyEntity.class)
})
public class ChargerEnergyEntity {

    public static final String FIND_CHARGER_ENERGY_DURING_TIME_PERIOD = "Charger.findChargerEnergyDuringTimePeriod";
    public static final String FIND_ALL_CHARGER_ENERGY_DURING_TIME_PERIOD_FOR_USER = "Charger.findAllChargerEnergyDuringTimePeriodForUser";
    public static final String FIND_ALL_CHARGER_ENERGY_DURING_TIME_PERIOD = "Charger.findAllChargerEnergyDuringTimePeriod";
    public static final String FIND_ALL_VEHICLE_ENERGY_DURING_TIME_PERIOD = "Charger.findAllVehicleEnergyDuringTimePeriod";

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "energy_id")
    private String id;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "charger_id", referencedColumnName = "charger_id")
    private ChargerEntity charger;

    @Column(name = "vehicle_id")
    private String vehicleId;

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

    public ChargerEntity getCharger() {
        return charger;
    }

    public void setCharger(ChargerEntity charger) {
        this.charger = charger;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
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
