package com.matejbizjak.smartvillages.central.services;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.matejbizjak.smartvillages.central.lib.v1.TotalUserEnergyInterval;
import com.matejbizjak.smartvillages.central.services.subscribers.SolarSubscriber;
import com.matejbizjak.smartvillages.solar.lib.v1.EnergySolarIntervalForUser;
import com.matejbizjak.smartvillages.userlib.v1.User;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class EnergyService {

    private final Logger LOG = LogManager.getLogger(EnergyService.class.getSimpleName());


}
