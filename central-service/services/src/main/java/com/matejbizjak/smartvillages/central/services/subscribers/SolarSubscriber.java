package com.matejbizjak.smartvillages.central.services.subscribers;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.kumuluz.ee.logs.enums.LogLevel;
import com.kumuluz.ee.nats.common.util.SerDes;
import com.kumuluz.ee.nats.jetstream.annotations.JetStreamSubscriber;
import com.matejbizjak.smartvillages.solar.lib.v1.EnergySolarIntervalForSolar;
import com.matejbizjak.smartvillages.solar.lib.v1.EnergySolarIntervalForUser;
import com.matejbizjak.smartvillages.solar.persistence.EnergyEntity;
import com.matejbizjak.smartvillages.userlib.v1.User;
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
public class SolarSubscriber {

    @Inject
    @JetStreamSubscriber(connection = "main-secure", subject = "solar.energy.dailyReportRes", durable = "solar_energy_daily_user_pull")
    // TODO dodaj consumer nastavitev tako, da zavrne vsa sporočila starejša od par ur???
    private JetStreamSubscription dailyUserEnergySubscription;

    private final Logger LOG = LogManager.getLogger(SolarSubscriber.class.getSimpleName());

    public Map<String, EnergySolarIntervalForUser> pullDailyUserSolarEnergy(List<User> users) {
        LOG.info("Pulling messages from subject " + "solar.energy.dailyReportRes");
        Map<String, EnergySolarIntervalForUser> userIdSolarMap = new HashMap<>();
        if (dailyUserEnergySubscription != null) {
            Iterator<Message> iterator = dailyUserEnergySubscription.iterate(users.size(), Duration.ofSeconds(5));
            iterator.forEachRemaining(message -> {
                try {
                    EnergySolarIntervalForSolar solarEnergy = SerDes.deserialize(message.getData(), EnergySolarIntervalForSolar.class);
                    EnergySolarIntervalForUser userSolarEnergy = userIdSolarMap.get(solarEnergy.getSolar().getUserId());

                    if (userSolarEnergy == null) {
                        userSolarEnergy = new EnergySolarIntervalForUser();
                        Optional<User> user = users.stream().filter(x -> Objects.equals(x.getId(), solarEnergy.getSolar().getUserId())).findFirst();
                        if (user.isEmpty()) {
                            LOG.log(LogLevel.FATAL, String.format("Owner of solar %s was not found in the database.", solarEnergy.getSolar().getId()));
                            throw new IllegalStateException();
                        }
                        userSolarEnergy.setUser(user.get());

                        List<EnergySolarIntervalForSolar> solarEnergyList = new ArrayList<>();
                        userSolarEnergy.setEnergySolarList(solarEnergyList);

                        userSolarEnergy.setSum(new BigDecimal(0));

//                        userIdSolarMap.put(userSolarEnergy.getUser().getId(), userSolarEnergy);
                    }

                    userSolarEnergy.getEnergySolarList().add(solarEnergy);

                    BigDecimal sumOfCurrentSolar = solarEnergy.getEnergyList()
                            .stream()
                            .map(EnergyEntity::getValue)
                            .reduce(new BigDecimal(0), BigDecimal::add);
                    userSolarEnergy.setSum(userSolarEnergy.getSum().add(sumOfCurrentSolar));

                    userIdSolarMap.put(userSolarEnergy.getUser().getId(), userSolarEnergy);

                    message.ackSync(Duration.ofSeconds(5));
                } catch (IOException | InterruptedException | TimeoutException e) {
                    message.nak();
                    LOG.error("There was a problem at pulling messages.", e);
                    throw new RuntimeException(e);
                }
            });
        }
        return userIdSolarMap;
    }
}
