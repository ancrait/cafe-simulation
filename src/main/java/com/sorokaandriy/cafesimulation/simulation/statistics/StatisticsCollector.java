package com.sorokaandriy.cafesimulation.simulation.statistics;

public class StatisticsCollector {

    private int totalCustomersArrived = 0;
    private int totalCustomersServed = 0;
    private long totalWaitTime = 0;

    private long totalTablesCleaned = 0;
    private long totalCleaningTime;


    public void recordArrival() {
        totalCustomersArrived++;
    }

    public void recordServed(long waitTime) {
        totalCustomersServed++;
        totalWaitTime += waitTime;
    }

    public double getAverageWaitTime() {
        if (totalCustomersServed == 0) return 0.0;
        return (double) totalWaitTime / totalCustomersServed;
    }

    public int getTotalCustomersArrived() {
        return totalCustomersArrived;
    }

    public int getTotalCustomersServed() {
        return totalCustomersServed;
    }

    public long getTotalWaitTime() {
        return totalWaitTime;
    }

    public long getTotalTablesCleaned() {
        return totalTablesCleaned;
    }

    public double getAverageCleaningTime() {
        if (totalTablesCleaned == 0) return 0.0;
        return (double) totalCleaningTime / totalTablesCleaned;
    }
}
