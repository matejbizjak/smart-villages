package com.matejbizjak.smartvillages.central.api.v1.resources;

import com.matejbizjak.smartvillages.central.services.ChargerService;
import com.matejbizjak.smartvillages.central.services.HouseService;
import com.matejbizjak.smartvillages.central.services.SolarService;
import com.matejbizjak.smartvillages.central.services.UserService;
import com.matejbizjak.smartvillages.central.services.runnables.DailyEnergyReportRunnable;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Path("/coordinate")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CoordinatorResource {

    @Inject
    private SolarService solarService;
    @Inject
    private ChargerService chargerService;
    @Inject
    private HouseService houseService;
    @Inject
    private DailyEnergyReportRunnable dailyEnergyReportRunnable;

    @GET
    @Path("/dailyEnergyReport")
    public Response startDailyEnergyReport() {
        solarService.startDailyEnergyReportProcess();
        chargerService.startDailyEnergyReportProcess();
        houseService.startDailyEnergyReportProcess();

        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.schedule(dailyEnergyReportRunnable, 15, TimeUnit.SECONDS);  // TODO 10 min

        return Response.ok().build();
    }

    @GET
    @Path("/changeSolarPositions")
    public Response changeSolarPositions() {
        solarService.changeSolarPositions();
        return Response.ok().build();
    }
}
