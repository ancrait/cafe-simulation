package com.sorokaandriy.cafesimulation.model;

import com.sorokaandriy.cafesimulation.model.enums.OrderStatus;

public class Waiter extends Staff implements CustomerHandler{

    public Waiter(Long id, String name, boolean isAvailable) {
        super(id, name, isAvailable);
    }


    @Override
    public void takeOrder(Customer customer) {
        if (this.isAvailable() && customer.getOrder() != null) {
            customer.getOrder().setOrderStatus(OrderStatus.PENDING);
        }
    }

    @Override
    public void deliverOrder(Order order) {
        order.setOrderStatus(OrderStatus.DELIVERED);
    }

    @Override
    public void cleanTable() {

    }


}
