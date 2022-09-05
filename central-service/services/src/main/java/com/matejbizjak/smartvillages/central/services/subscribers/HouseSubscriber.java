package com.matejbizjak.smartvillages.central.services.subscribers;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.kumuluz.ee.logs.enums.LogLevel;
import com.kumuluz.ee.nats.common.util.SerDes;
import com.kumuluz.ee.nats.jetstream.annotations.JetStreamSubscriber;
import com.matejbizjak.smartvillages.house.lib.v1.EnergyHouseIntervalForHouse;
import com.matejbizjak.smartvillages.house.lib.v1.EnergyHouseIntervalForUser;
import com.matejbizjak.smartvillages.house.persistence.HouseEnergyEntity;
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
public class HouseSubscriber {

    @Inject
    @JetStreamSubscriber(connection = "main-secure", stream = "house", subject = "house.energy.dailyReportRes", durable = "house_energy_daily_user_pull")
    // TODO dodaj consumer nastavitev tako, da zavrne vsa sporočila starejša od par ur???
    private JetStreamSubscription dailyUserEnergySubscription;

    private final Logger LOG = LogManager.getLogger(HouseSubscriber.class.getSimpleName());

    public Map<String, EnergyHouseIntervalForUser> pullDailyUserHouseEnergy(List<User> users) {
        LOG.info("Pulling messages from subject " + "house.energy.dailyReportRes");
        Map<String, EnergyHouseIntervalForUser> userIdHouseMap = new HashMap<>();
        if (dailyUserEnergySubscription != null) {
            Iterator<Message> iterator = dailyUserEnergySubscription.iterate(users.size(), Duration.ofSeconds(5));
            iterator.forEachRemaining(message -> {
                try {
                    EnergyHouseIntervalForHouse houseEnergy = SerDes.deserialize(message.getData(), EnergyHouseIntervalForHouse.class);
                    EnergyHouseIntervalForUser userHouseEnergy = userIdHouseMap.get(houseEnergy.getHouse().getUserId());

                    if (userHouseEnergy == null) {
                        userHouseEnergy = new EnergyHouseIntervalForUser();
                        Optional<User> user = users.stream().filter(x -> Objects.equals(x.getId(), houseEnergy.getHouse().getUserId())).findFirst();
                        if (user.isEmpty()) {
                            LOG.log(LogLevel.FATAL, String.format("Owner of house %s was not found in the database.", houseEnergy.getHouse().getId()));
                            throw new IllegalStateException();
                        }
                        userHouseEnergy.setUser(user.get());

                        List<EnergyHouseIntervalForHouse> houseEnergyList = new ArrayList<>();
                        userHouseEnergy.setEnergyHouseList(houseEnergyList);

                        userHouseEnergy.setSum(new BigDecimal(0));

//                        userIdHouseMap.put(userHouseEnergy.getUser().getId(), userHouseEnergy);
                    }

                    userHouseEnergy.getEnergyHouseList().add(houseEnergy);

                    BigDecimal sumOfCurrentHouse = houseEnergy.getEnergyList()
                            .stream()
                            .map(HouseEnergyEntity::getValue)
                            .reduce(new BigDecimal(0), BigDecimal::add);
                    userHouseEnergy.setSum(userHouseEnergy.getSum().add(sumOfCurrentHouse));

                    userIdHouseMap.put(userHouseEnergy.getUser().getId(), userHouseEnergy);

                    message.ackSync(Duration.ofSeconds(5));
                } catch (IOException | InterruptedException | TimeoutException e) {
                    message.nak();
                    LOG.error("There was a problem at pulling messages.", e);
                    throw new RuntimeException(e);
                }
            });
        }
        return userIdHouseMap;
    }
}
