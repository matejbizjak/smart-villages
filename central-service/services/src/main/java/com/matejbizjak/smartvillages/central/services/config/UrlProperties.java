package com.matejbizjak.smartvillages.central.services.config;

import com.kumuluz.ee.configuration.cdi.ConfigBundle;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
@ConfigBundle("app-urls")
public class UrlProperties {

    private String userApi;
    private String vehicleApi;

    public String getUserApi() {
        return userApi;
    }

    public void setUserApi(String userApi) {
        this.userApi = userApi;
    }

    public String getVehicleApi() {
        return vehicleApi;
    }

    public void setVehicleApi(String vehicleApi) {
        this.vehicleApi = vehicleApi;
    }
}
