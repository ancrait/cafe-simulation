package com.sorokaandriy.cafesimulation.model;

import com.sorokaandriy.cafesimulation.model.enums.OrderStatus;

public class Chef extends Staff implements OrderProcessor{


    public Chef(Long id, String name, boolean isAvailable) {
        super(id, name, isAvailable);
    }

    @Override
    public void performWork() {
        System.out.println("Кухар " + getName() + " готує страву.");
    }


    @Override
    public void startCooking(Order order) {
        this.setAvailable(false);
        order.setOrderStatus(OrderStatus.PREPARING);
        System.out.println("Кухар " + getName() + " почав готувати замовлення #" + order.getOrderId());
    }

    @Override
    public void finishCooking(Order order) {
        order.setOrderStatus(OrderStatus.READY);
        this.setAvailable(true);
        System.out.println("Кухар " + getName() + " приготував замовлення #" + order.getOrderId());
    }
}
