package com.sorokaandriy.cafesimulation.model;

import com.sorokaandriy.cafesimulation.model.enums.MenuItem;

import java.util.Objects;

public class Customer extends Person{
    private long arrivalTime;
    private Order order;
    private long patience;


    public Customer(Long id, String name, long arrivalTime, Order order, long patience) {
        super(id, name);
        this.arrivalTime = arrivalTime;
        this.order = order;
        this.patience = patience;
    }

    public long getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(long arrivalTime) { this.arrivalTime = arrivalTime; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public long getPatience() { return patience; }
    public void setPatience(long patience) { this.patience = patience; }


    public boolean hasRunOutOfPatience(long currentTime) {
        return (currentTime - arrivalTime) > patience;
    }


    @Override
    public String toString() {
        String orderInfo = (order != null) ? String.valueOf(order.getOrderId()) : "відсутнє";
        return super.toString()
                + " [arrivalTime=" + arrivalTime
                + ", patience=" + patience
                + ", orderId=" + orderInfo + "]";
    }
}

