package com.sorokaandriy.cafesimulation.model.enums;

public enum MenuItem {

    BORSCH("Борщ",  MenuItemType.FOOD,  20.0, 3.0,  0.20),
    PASTA ("Паста", MenuItemType.FOOD,  15.0, 2.5,  0.30),
    STEAK ("Стейк", MenuItemType.FOOD,  30.0, 4.0,  0.10),
    COFFEE("Кава",  MenuItemType.DRINK,  5.0, 1.0,  0.30),
    JUICE ("Сік",   MenuItemType.DRINK,  3.0, 0.5,  0.10);

    private final String displayName;
    private final MenuItemType type;
    private final double preparationMean;
    private final double preparationStdDev;
    private final double popularityWeight;

    MenuItem(String displayName, MenuItemType type,
             double preparationMean, double preparationStdDev,
             double popularityWeight) {
        this.displayName = displayName;
        this.type = type;
        this.preparationMean = preparationMean;
        this.preparationStdDev = preparationStdDev;
        this.popularityWeight = popularityWeight;
    }

    public String getDisplayName() { return displayName; }
    public MenuItemType getType() { return type; }
    public double getPreparationMean() { return preparationMean; }
    public double getPreparationStdDev() { return preparationStdDev; }
    public double getPopularityWeight() { return popularityWeight; }

    @Override
    public String toString() {
        return displayName + " (" + type + ", ~" + preparationMean + " тіків)";
    }

}
