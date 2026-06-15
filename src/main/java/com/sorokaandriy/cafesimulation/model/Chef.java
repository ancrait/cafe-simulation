package com.sorokaandriy.cafesimulation.model;

import com.sorokaandriy.cafesimulation.exception.CafeSimulationException;
import com.sorokaandriy.cafesimulation.model.enums.MenuItemType;
import com.sorokaandriy.cafesimulation.model.enums.OrderStatus;

public class Chef extends Staff implements OrderProcessor{

    private long fatigue = 0;
    private static final long MAX_FATIGUE = 200;
    private static final long RECOVERY_RATE = 1;
    private final MenuItemType specialization;


    public Chef(Long id, String name, boolean isAvailable, MenuItemType specialization) {
        super(id, name, isAvailable);
        this.specialization = specialization;
    }


    @Override
    public void startCooking(Order order) {
        if (order == null) {
            throw new CafeSimulationException("Шеф " + getName() + ": замовлення відсутнє");
        }
        if (order.getOrderStatus() != OrderStatus.PENDING) {
            throw new CafeSimulationException(
                    "Шеф " + getName() + ": замовлення #" + order.getOrderId()
                            + " не в статусі PENDING, поточний статус: " + order.getOrderStatus()
            );
        }
        if (!this.isAvailable()) {
            throw new CafeSimulationException("Шеф " + getName() + " зайнятий");
        }

        if (order.getMenuItem() != null && !canCook(order.getMenuItem().getType())) {
            throw new CafeSimulationException(
                    "Шеф " + getName() + " спеціалізується на " + specialization
                            + " і не може готувати " + order.getMenuItem().getDisplayName()
            );
        }
        order.setOrderStatus(OrderStatus.PREPARING);
    }

    @Override
    public void finishCooking(Order order) {
        if (order == null) {
            throw new CafeSimulationException("Шеф " + getName() + ": замовлення відсутнє");
        }
        if (order.getOrderStatus() != OrderStatus.PREPARING) {
            throw new CafeSimulationException(
                    "Шеф " + getName() + ": замовлення #" + order.getOrderId()
                            + " не готується! Поточний статус: " + order.getOrderStatus()
            );
        }
        order.setOrderStatus(OrderStatus.READY);

    }

    public boolean canCook(MenuItemType itemType) {
        if (specialization == null) return true;
        return specialization == itemType;
    }

    public void addFatigue(long cookingTime) {
        fatigue = Math.min(fatigue + cookingTime, MAX_FATIGUE);
    }

    public void recover() {
        fatigue = Math.max(0, fatigue - RECOVERY_RATE);
    }

    public double getFatigueLevel() {
        return (double) fatigue / MAX_FATIGUE;
    }

    public long getFatigue() { return fatigue; }

    public MenuItemType getSpecialization() {return specialization;}

    @Override
    public String toString() {
        String spec = (specialization != null) ? specialization.toString() : "універсальний";
        return super.toString() + " [fatigue=" + fatigue + "/" + MAX_FATIGUE
                + ", specialization=" + spec + "]";
    }
}
