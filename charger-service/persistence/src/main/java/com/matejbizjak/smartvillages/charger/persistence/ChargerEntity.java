package com.matejbizjak.smartvillages.charger.persistence;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "charger")
@NamedQueries({
        @NamedQuery(name = ChargerEntity.FIND_ALL, query = "SELECT c FROM ChargerEntity c"),
})
public class ChargerEntity {

    public static final String FIND_ALL = "Charger.findAll";

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "charger_id")
    private String id;

    @Column(name = "model_name")
    private String modelName;

//    @Column(name = "vehicle_id")
//    private String vehicleId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

//    public String getVehicleId() {
//        return vehicleId;
//    }
//
//    public void setVehicleId(String vehicleId) {
//        this.vehicleId = vehicleId;
//    }
}
