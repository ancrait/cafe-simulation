package com.sorokaandriy.cafesimulation.model;

import java.util.Objects;

public class Customer extends Person{
    private long arrivalTime;
    private Order order;

    public Customer(Long id, String name, long arrivalTime, Order order) {
        super(id, name);
        this.arrivalTime = arrivalTime;
        this.order = order;
    }

    public long getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(long arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    @Override
    public String toString() {
        return super.toString() + " [arrivalTime=" + arrivalTime + ", order=" + order + "]";
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
