package com.matejbizjak.smartvillages.central.services.runnables;

import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import com.kumuluz.ee.logs.enums.LogLevel;
import com.matejbizjak.smartvillages.central.lib.v1.TotalUserEnergyInterval;
import com.matejbizjak.smartvillages.central.services.MailService;
import com.matejbizjak.smartvillages.central.services.UserService;
import com.matejbizjak.smartvillages.central.services.subscribers.SolarSubscriber;
import com.matejbizjak.smartvillages.solar.lib.v1.EnergySolarIntervalForUser;
import com.matejbizjak.smartvillages.userlib.v1.User;

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
    private UserService userService;

    private final Logger LOG = LogManager.getLogger(DailyEnergyReportRunnable.class.getSimpleName());

    @Override
    public void run() {
        LOG.info("Starting combining data for daily energy report.");
        List<User> users = userService.getAllUsers();

        Map<String, EnergySolarIntervalForUser> solarEnergyMap = solarSubscriber.pullDailyUserSolarEnergy(users);
        // TODO other energies

        // TODO if solarEnergyMap.size() != carEnergyMap.size() itd. -> warning
        solarEnergyMap.forEach((userId, energySolar) -> {
            TotalUserEnergyInterval totalUserEnergy = new TotalUserEnergyInterval(energySolar.getUser(), energySolar.getEnergySolarList(), energySolar.getSum());
            // TODO dopolni ostale parametre in povečaj sum

            mailService.sendEnergyDailyMail(totalUserEnergy);
        });
    }
}