package com.sorokaandriy.cafesimulation.model;

import com.sorokaandriy.cafesimulation.model.enums.OrderStatus;

public class Chef extends Staff implements OrderProcessor{


    public Chef(Long id, String name, boolean isAvailable) {
        super(id, name, isAvailable);
    }


    @Override
    public void startCooking(Order order) {
        order.setOrderStatus(OrderStatus.PREPARING);
    }

    @Override
    public void finishCooking(Order order) {
        order.setOrderStatus(OrderStatus.READY);
    }
}
