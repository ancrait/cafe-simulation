package com.sorokaandriy.cafesimulation.config;

import com.sorokaandriy.cafesimulation.model.Staff;

import java.util.List;



public class SimulationConfig {

    private final int tableCount;
    private final int simulationDuration;
    private final int tickDelayMs;
    private final double arrivalMean;
    private final double patienceMean;
    private final double serviceMean;
    private final double serviceStdDev;
    private final double eatingMean;
    private final List<Staff> staffList;

    public SimulationConfig(int tableCount,
                            int simulationDuration,
                            int tickDelayMs,
                            double arrivalMean,
                            double patienceMean,
                            double serviceMean,
                            double serviceStdDev,
                            double eatingMean,
                            List<Staff> staffList) {
        this.tableCount = tableCount;
        this.simulationDuration = simulationDuration;
        this.tickDelayMs = tickDelayMs;
        this.arrivalMean = arrivalMean;
        this.patienceMean = patienceMean;
        this.serviceMean = serviceMean;
        this.serviceStdDev = serviceStdDev;
        this.eatingMean = eatingMean;
        this.staffList = staffList;
    }

    public int getTableCount() { return tableCount; }
    public int getSimulationDuration() { return simulationDuration; }
    public int getTickDelayMs() { return tickDelayMs; }
    public double getArrivalMean() { return arrivalMean; }
    public double getPatienceMean() { return patienceMean; }
    public double getServiceMean() { return serviceMean; }
    public double getServiceStdDev() { return serviceStdDev; }
    public double getEatingMean() { return eatingMean; }
    public List<Staff> getStaffList() { return staffList; }
}