package com.sorokaandriy.cafesimulation.model;

import com.sorokaandriy.cafesimulation.model.enums.MenuItem;

import java.util.Objects;

public class Customer extends Person{
    private long arrivalTime;
    private Order order;
    private MenuItem selectedMenuItem;

    public Customer(Long id, String name, long arrivalTime, Order order) {
        super(id, name);
        this.arrivalTime = arrivalTime;
        this.order = order;
        this.selectedMenuItem = null;
    }

    public Customer(Long id, String name, long arrivalTime, Order order, MenuItem selectedMenuItem) {
        super(id, name);
        this.arrivalTime = arrivalTime;
        this.order = order;
        this.selectedMenuItem = selectedMenuItem;
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

    public MenuItem getSelectedMenuItem() { return selectedMenuItem; }
    public void setSelectedMenuItem(MenuItem selectedMenuItem) { this.selectedMenuItem = selectedMenuItem; }

    @Override
    public String toString() {
        String orderInfo = (order != null) ? String.valueOf(order.getOrderId()) : "відсутнє";
        String itemInfo = (selectedMenuItem != null) ? selectedMenuItem.getDisplayName() : "не обрано";
        return super.toString()
                + " [arrivalTime=" + getArrivalTime()
                + ", orderId=" + orderInfo
                + ", selectedItem=" + itemInfo + "]";
    }

}
