package com.matejbizjak.smartvillages.charger.services.config;

import com.kumuluz.ee.configuration.cdi.ConfigBundle;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
@ConfigBundle("app-urls")
public class UrlProperties {

    private String vehicleApi;

    public String getVehicleApi() {
        return vehicleApi;
    }

    public void setVehicleApi(String vehicleApi) {
        this.vehicleApi = vehicleApi;
    }
}
