package com.matejbizjak.smartvillages.solar.persistence;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "solar")
@NamedQueries({
        @NamedQuery(name = SolarEntity.FIND_ALL, query = "SELECT s FROM SolarEntity s"),
        @NamedQuery(name = SolarEntity.FIND_FOR_USER, query = "SELECT s FROM SolarEntity s WHERE s.userId = :userId")
})
public class SolarEntity {

    public static final String FIND_ALL = "Solar.findAll";
    public static final String FIND_FOR_USER = "Solar.findForUser";

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "solar_id")
    private String id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "start_time")
    private Instant startTime;

    @Column(name = "next_service_time")
    private Instant nextServiceTime;

    @Column(name = "location_x")
    private BigDecimal locationX;

    @Column(name = "location_y")
    private BigDecimal locationY;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getNextServiceTime() {
        return nextServiceTime;
    }

    public void setNextServiceTime(Instant nextServiceTime) {
        this.nextServiceTime = nextServiceTime;
    }

    public BigDecimal getLocationX() {
        return locationX;
    }

    public void setLocationX(BigDecimal locationX) {
        this.locationX = locationX;
    }

    public BigDecimal getLocationY() {
        return locationY;
    }

    public void setLocationY(BigDecimal locationY) {
        this.locationY = locationY;
    }
}
