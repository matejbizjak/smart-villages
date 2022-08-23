package com.matejbizjak.smartvillages.vehicle.persistence;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "vehicle")
@NamedQueries({
        @NamedQuery(name = VehicleEntity.FIND_ALL, query = "SELECT v FROM VehicleEntity v"),
        @NamedQuery(name = VehicleEntity.FIND_FOR_USER, query = "SELECT v FROM VehicleEntity v WHERE v.userId = :userId")
})
public class VehicleEntity {

    public static final String FIND_ALL = "Solar.findAll";
    public static final String FIND_FOR_USER = "Solar.findForUser";

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "vehicle_id")
    private String id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "registration_number")
    private String registrationNumber;

    @Column(name = "first_registration_date")
    private Instant firstRegistrationDate;

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

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public Instant getFirstRegistrationDate() {
        return firstRegistrationDate;
    }

    public void setFirstRegistrationDate(Instant firstRegistrationDate) {
        this.firstRegistrationDate = firstRegistrationDate;
    }
}
