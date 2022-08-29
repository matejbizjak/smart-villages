package com.matejbizjak.smartvillages.house.persistence;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "house")
@NamedQueries({
        @NamedQuery(name = HouseEntity.FIND_ALL, query = "SELECT h FROM HouseEntity h"),
})
public class HouseEntity {

    public static final String FIND_ALL = "House.findAll";

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "house_id")
    private String id;

    @Column(name = "street_house_number")
    private String streetHouseNumber;

    @Column(name = "user_id")
    private String userId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStreetHouseNumber() {
        return streetHouseNumber;
    }

    public void setStreetHouseNumber(String streetHouseNumber) {
        this.streetHouseNumber = streetHouseNumber;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
