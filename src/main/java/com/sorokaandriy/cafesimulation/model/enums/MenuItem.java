package com.sorokaandriy.cafesimulation.model.enums;

public enum MenuItem {

    BORSCH    ("Борщ",     MenuItemType.FOOD,  20.0, 3.0,  0.10),
    PASTA     ("Паста",    MenuItemType.FOOD,  15.0, 2.5,  0.12),
    STEAK     ("Стейк",    MenuItemType.FOOD,  30.0, 4.0,  0.08),
    SALAD     ("Салат",    MenuItemType.FOOD,   8.0, 1.5,  0.08),
    CHICKEN   ("Курка",    MenuItemType.FOOD,  22.0, 3.5,  0.10),
    OMELET    ("Омлет",    MenuItemType.FOOD,  10.0, 2.0,  0.07),
    SYRNYKY   ("Сирники",  MenuItemType.FOOD,  18.0, 3.0,  0.05),
    CAKE      ("Торт",     MenuItemType.FOOD,   5.0, 1.0,  0.05),
    COFFEE    ("Кава",     MenuItemType.DRINK,  5.0, 1.0,  0.12),
    JUICE     ("Сік",      MenuItemType.DRINK,  3.0, 0.5,  0.08),
    TEA       ("Чай",      MenuItemType.DRINK,  3.0, 0.5,  0.10),
    LEMONADE  ("Лимонад",  MenuItemType.DRINK,  4.0, 1.0,  0.05);


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
