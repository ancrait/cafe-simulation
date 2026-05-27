package com.sorokaandriy.cafesimulation.simulation.statistics;

import com.sorokaandriy.cafesimulation.model.enums.MenuItem;

import java.util.EnumMap;
import java.util.Map;

public class StatisticsCollector {

    private long totalCustomersArrived = 0;
    private long totalCustomersServed = 0;
    private long totalWaitTime = 0;

    private long totalTablesCleaned = 0;
    private long totalCleaningTime = 0;

    private long totalCustomersLeft = 0;
    private final Map<MenuItem, Long> itemOrderCount = new EnumMap<>(MenuItem.class);
    private final Map<MenuItem, Long> itemTotalPrepTime = new EnumMap<>(MenuItem.class);


    public StatisticsCollector() {
        for (MenuItem item : MenuItem.values()) {
            itemOrderCount.put(item, 0L);
            itemTotalPrepTime.put(item, 0L);
        }
    }


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

    public void recordCustomerLeft() {
        totalCustomersLeft++;
    }

    public void recordMenuItemCooked(MenuItem item, long prepTime) {
        itemOrderCount.put(item, itemOrderCount.get(item) + 1);
        itemTotalPrepTime.put(item, itemTotalPrepTime.get(item) + prepTime);
    }

    public long getItemOrderCount(MenuItem item) {
        return itemOrderCount.get(item);
    }

    public double getAverageItemPrepTime(MenuItem item) {
        long count = itemOrderCount.get(item);
        if (count == 0) return 0.0;
        return (double) itemTotalPrepTime.get(item) / count;
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

    public long getTotalCustomersLeft() {return totalCustomersLeft;}

    public Map<MenuItem, Long> getItemOrderCounts() { return itemOrderCount; }

    public Map<MenuItem, Long> getItemTotalPrepTimes() { return itemTotalPrepTime; }


    public double getAverageCleaningTime() {
        if (totalTablesCleaned == 0) return 0.0;
        return (double) totalCleaningTime / totalTablesCleaned;
    }

    public double getCustomerLossRate() {
        if (totalCustomersArrived == 0) return 0.0;
        return (double) totalCustomersLeft / totalCustomersArrived * 100.0;
    }
}
