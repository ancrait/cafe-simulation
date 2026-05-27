package com.sorokaandriy.cafesimulation.simulation.service;

import com.sorokaandriy.cafesimulation.model.enums.MenuItem;
import com.sorokaandriy.cafesimulation.model.enums.MenuItemType;
import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.util.Pair;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class MenuService {


    private final EnumeratedDistribution<MenuItem> popularityDistribution;

    private static final double MAX_FATIGUE_MULTIPLIER = 3.0;

    public MenuService() {
        double totalWeight = Arrays.stream(MenuItem.values())
                .mapToDouble(MenuItem::getPopularityWeight)
                .sum();
        if (Math.abs(totalWeight - 1.0) > 0.0001) {
            throw new IllegalStateException(
                    "Сума ваг MenuItem повинна дорівнювати 1.0, але зараз: " + totalWeight
            );
        }

        List<Pair<MenuItem, Double>> pmf = Arrays.stream(MenuItem.values())
                .map(item -> new Pair<>(item, item.getPopularityWeight()))
                .collect(Collectors.toList());

        this.popularityDistribution = new EnumeratedDistribution<>(pmf);
    }


    public List<MenuItem> getAllItems() {
        return Arrays.asList(MenuItem.values());
    }


    public List<MenuItem> getFoodItems() {
        return Arrays.stream(MenuItem.values())
                .filter(item -> item.getType() == MenuItemType.FOOD)
                .collect(Collectors.toList());
    }


    public List<MenuItem> getDrinkItems() {
        return Arrays.stream(MenuItem.values())
                .filter(item -> item.getType() == MenuItemType.DRINK)
                .collect(Collectors.toList());
    }


    public MenuItem selectByPopularity() {
        return popularityDistribution.sample();
    }


    public long samplePreparationTime(MenuItem item, double fatigueLevel) {
        double baseMean = item.getPreparationMean();
        double baseStdDev = item.getPreparationStdDev();

        double fatiguedStdDev = baseStdDev * (1.0 + MAX_FATIGUE_MULTIPLIER * fatigueLevel);

        NormalDistribution dist = new NormalDistribution(baseMean, fatiguedStdDev);
        return Math.max(1, Math.round(dist.sample()));
    }

    public long samplePreparationTime(MenuItem item) {
        return samplePreparationTime(item, 0.0);
    }


}