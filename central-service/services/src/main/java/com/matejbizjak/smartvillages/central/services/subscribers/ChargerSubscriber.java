package com.matejbizjak.smartvillages.central.services.subscribers;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.kumuluz.ee.logs.enums.LogLevel;
import com.kumuluz.ee.nats.common.util.SerDes;
import com.kumuluz.ee.nats.jetstream.annotations.JetStreamSubscriber;
import com.matejbizjak.smartvillages.charger.lib.v1.EnergyChargerIntervalForUser;
import com.matejbizjak.smartvillages.charger.lib.v1.EnergyChargerIntervalForVehicle;
import com.matejbizjak.smartvillages.charger.persistence.ChargerEnergyEntity;
import com.matejbizjak.smartvillages.libutils.CommonUtil;
import com.matejbizjak.smartvillages.userlib.v1.User;
import com.matejbizjak.smartvillages.vehicle.lib.v1.Vehicle;
import io.nats.client.JetStreamSubscription;
import io.nats.client.Message;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeoutException;

@ApplicationScoped
public class ChargerSubscriber {

    @Inject
    @JetStreamSubscriber(connection = "main-secure", stream = "charger", subject = "charger.energy.dailyReportRes", durable = "charger_energy_daily_user_pull")
    // TODO dodaj consumer nastavitev tako, da zavrne vsa sporočila starejša od par ur???
    private JetStreamSubscription dailyUserEnergySubscription;

    private final Logger LOG = LogManager.getLogger(ChargerSubscriber.class.getSimpleName());

    public Map<String, EnergyChargerIntervalForUser> pullDailyUserChargerEnergy(List<User> users, List<Vehicle> vehicles) {
        LOG.info("Pulling messages from subject " + "charger.energy.dailyReportRes");
        Map<String, EnergyChargerIntervalForUser> userIdChargerMap = new HashMap<>();
        if (dailyUserEnergySubscription != null) {
            Iterator<Message> iterator = dailyUserEnergySubscription.iterate(vehicles.size(), Duration.ofSeconds(5));
            iterator.forEachRemaining(message -> {
                try {
                    EnergyChargerIntervalForVehicle chargerEnergyVehicle = SerDes.deserialize(message.getData(), EnergyChargerIntervalForVehicle.class);
                    Optional<Vehicle> vehicle = vehicles.stream().filter(v -> v.getId().equals(CommonUtil.safeNav(chargerEnergyVehicle.getVehicle(), Vehicle::getId))).findFirst();
                    if (vehicle.isEmpty()) {
                        LOG.log(LogLevel.FATAL, String.format("Vehicle %s was not found in the database.", CommonUtil.safeNav(chargerEnergyVehicle.getVehicle(), Vehicle::getId)));
                        throw new IllegalStateException();
                    }
                    chargerEnergyVehicle.setVehicle(vehicle.get());
                    Optional<User> userOptional = users.stream().filter(u -> u.getId().equals(CommonUtil.safeNav(chargerEnergyVehicle.getVehicle(), Vehicle::getUserId))).findFirst();
                    if (userOptional.isEmpty()) {
                        LOG.log(LogLevel.FATAL, String.format("Owner of vehicle %s was not found in the database.", CommonUtil.safeNav(chargerEnergyVehicle.getVehicle(), Vehicle::getId)));
                        throw new IllegalStateException();
                    }
                    User vehicleOwner = userOptional.get();
                    EnergyChargerIntervalForUser userChargerEnergy = userIdChargerMap.get(vehicleOwner.getId());

                    if (userChargerEnergy == null) {
                        userChargerEnergy = new EnergyChargerIntervalForUser();
                        userChargerEnergy.setUser(vehicleOwner);
                        List<EnergyChargerIntervalForVehicle> vehicleEnergyList = new ArrayList<>();
                        userChargerEnergy.setEnergyVehicleList(vehicleEnergyList);
                        userChargerEnergy.setSum(new BigDecimal(0));

                        userIdChargerMap.put(userChargerEnergy.getUser().getId(), userChargerEnergy);
                    }

                    userChargerEnergy.getEnergyVehicleList().add(chargerEnergyVehicle);
//
                    BigDecimal sumOfCurrentVehicle = chargerEnergyVehicle.getEnergyList()
                            .stream()
                            .map(ChargerEnergyEntity::getValue)
                            .reduce(new BigDecimal(0), BigDecimal::add);
                    userChargerEnergy.setSum(userChargerEnergy.getSum().add(sumOfCurrentVehicle));

                    message.ackSync(Duration.ofSeconds(5));
                } catch (IOException | InterruptedException | TimeoutException e) {
                    message.nak();
                    LOG.error("There was a problem at pulling messages.", e);
                    throw new RuntimeException(e);
                }
            });
        }
        return userIdChargerMap;
    }
}
