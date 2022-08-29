package com.matejbizjak.smartvillages.central.services.runnables;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.matejbizjak.smartvillages.central.lib.v1.TotalUserEnergyInterval;
import com.matejbizjak.smartvillages.central.services.MailService;
import com.matejbizjak.smartvillages.central.services.UserService;
import com.matejbizjak.smartvillages.central.services.VehicleService;
import com.matejbizjak.smartvillages.central.services.subscribers.ChargerSubscriber;
import com.matejbizjak.smartvillages.central.services.subscribers.HouseSubscriber;
import com.matejbizjak.smartvillages.central.services.subscribers.SolarSubscriber;
import com.matejbizjak.smartvillages.charger.lib.v1.EnergyChargerIntervalForUser;
import com.matejbizjak.smartvillages.house.lib.v1.EnergyHouseIntervalForUser;
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
    private HouseSubscriber houseSubscriber;
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
        Map<String, EnergyHouseIntervalForUser> houseEnergyMap = houseSubscriber.pullDailyUserHouseEnergy(users);

        // TODO if solarEnergyMap.size() != carEnergyMap.size() itd. -> warning
        users.forEach(user -> {
            EnergySolarIntervalForUser energySolarIntervalForUser = solarEnergyMap.get(user.getId());
            EnergyChargerIntervalForUser energyChargerIntervalForUser = chargerEnergyMap.get(user.getId());
            EnergyHouseIntervalForUser energyHouseIntervalForUser = houseEnergyMap.get(user.getId());

            TotalUserEnergyInterval totalUserEnergy = new TotalUserEnergyInterval();
            totalUserEnergy.setUser(user);
            totalUserEnergy.setEnergySolarList(energySolarIntervalForUser.getEnergySolarList());
            totalUserEnergy.setEnergyChargerVehicleList(energyChargerIntervalForUser.getEnergyVehicleList());
            totalUserEnergy.setEnergyHouseList(energyHouseIntervalForUser.getEnergyHouseList());
            totalUserEnergy.setSum(
                    energySolarIntervalForUser.getSum().add(energyChargerIntervalForUser.getSum()).add(energyHouseIntervalForUser.getSum())
            );
            mailService.sendEnergyDailyMail(totalUserEnergy);
        });
    }
}