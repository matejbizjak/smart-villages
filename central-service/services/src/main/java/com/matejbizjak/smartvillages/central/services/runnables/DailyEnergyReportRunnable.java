package com.matejbizjak.smartvillages.central.services.runnables;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.matejbizjak.smartvillages.central.lib.v1.TotalUserEnergyInterval;
import com.matejbizjak.smartvillages.central.services.MailService;
import com.matejbizjak.smartvillages.central.services.UserService;
import com.matejbizjak.smartvillages.central.services.VehicleService;
import com.matejbizjak.smartvillages.central.services.subscribers.ChargerSubscriber;
import com.matejbizjak.smartvillages.central.services.subscribers.SolarSubscriber;
import com.matejbizjak.smartvillages.charger.lib.v1.EnergyChargerIntervalForUser;
import com.matejbizjak.smartvillages.solar.lib.v1.EnergySolarIntervalForUser;
import com.matejbizjak.smartvillages.userlib.v1.User;
import com.matejbizjak.smartvillages.vehicle.lib.v1.Vehicle;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class DailyEnergyReportRunnable implements Runnable {

    @Inject
    private MailService mailService;
    @Inject
    private SolarSubscriber solarSubscriber;
    @Inject
    private ChargerSubscriber chargerSubscriber;
    @Inject
    private UserService userService;
    @Inject
    private VehicleService vehicleService;

    private final Logger LOG = LogManager.getLogger(DailyEnergyReportRunnable.class.getSimpleName());

    @Override
    public void run() {
        LOG.info("Starting combining data for daily energy report.");
        List<User> users = userService.getAllUsers();
        List<Vehicle> vehicles = vehicleService.getAll();

        Map<String, EnergySolarIntervalForUser> solarEnergyMap = solarSubscriber.pullDailyUserSolarEnergy(users);
        Map<String, EnergyChargerIntervalForUser> chargerEnergyMap = chargerSubscriber.pullDailyUserChargerEnergy(users, vehicles);
        // TODO other energies

        // TODO if solarEnergyMap.size() != carEnergyMap.size() itd. -> warning
        users.forEach(user -> {
            TotalUserEnergyInterval totalUserEnergy = new TotalUserEnergyInterval();
            EnergySolarIntervalForUser energySolarIntervalForUser = solarEnergyMap.get(user.getId());
            EnergyChargerIntervalForUser energyChargerIntervalForUser = chargerEnergyMap.get(user.getId());
            totalUserEnergy.setUser(user);
            totalUserEnergy.setEnergySolarList(energySolarIntervalForUser.getEnergySolarList());
            totalUserEnergy.setEnergyChargerVehicleList(energyChargerIntervalForUser.getEnergyVehicleList());
            totalUserEnergy.setSum(
                    energySolarIntervalForUser.getSum().add(energyChargerIntervalForUser.getSum())
            );
            // TODO dopolni ostale parametre in povečaj sum
            mailService.sendEnergyDailyMail(totalUserEnergy);
        });
    }
}