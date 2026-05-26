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

    public MenuService() {

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


    public long samplePreparationTime(MenuItem item) {
        NormalDistribution dist = new NormalDistribution(
                item.getPreparationMean(),
                item.getPreparationStdDev()
        );
        return Math.max(1, Math.round(dist.sample()));
    }
}