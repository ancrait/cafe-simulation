package com.sorokaandriy.cafesimulation.simulation.statistics;

public class StatisticsCollector {

    private long totalCustomersArrived = 0;
    private long totalCustomersServed = 0;
    private long totalWaitTime = 0;

    private long totalTablesCleaned = 0;
    private long totalCleaningTime = 0;


    public void recordArrival() {
        totalCustomersArrived++;
    }

    public void recordServed(long waitTime) {
        totalCustomersServed++;
        totalWaitTime += waitTime;
    }

    public void recordCleanedTable(long timeSpent) {
        totalTablesCleaned++;
        totalCleaningTime += timeSpent;
    }

    public double getAverageWaitTime() {
        if (totalCustomersServed == 0) return 0.0;
        return (double) totalWaitTime / totalCustomersServed;
    }

    public long getTotalCustomersArrived() {
        return totalCustomersArrived;
    }

    public long getTotalCustomersServed() {
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
