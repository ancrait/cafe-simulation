package com.sorokaandriy.cafesimulation.model;

import com.sorokaandriy.cafesimulation.model.enums.MenuItem;
import com.sorokaandriy.cafesimulation.model.enums.OrderStatus;

import java.util.Objects;

public class Order {
    private long orderId;
    private Customer customer;
    private OrderStatus orderStatus;
    private long preparationTime;
    private MenuItem menuItem;

    public Order(long orderId, Customer customer, OrderStatus orderStatus, MenuItem menuItem) {
        this.orderId = orderId;
        this.customer = customer;
        this.orderStatus = orderStatus;
        this.menuItem = menuItem;
        this.preparationTime = 0;
    }

    public Order(long orderId, Customer customer, OrderStatus orderStatus, long preparationTime) {
        this.orderId = orderId;
        this.customer = customer;
        this.orderStatus = orderStatus;
        this.preparationTime = preparationTime;
        this.menuItem = null;
    }


    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public long getPreparationTime() {
        return preparationTime;
    }

    public void setPreparationTime(long preparationTime) {
        this.preparationTime = preparationTime;
    }



    public MenuItem getMenuItem() { return menuItem; }
    public void setMenuItem(MenuItem menuItem) { this.menuItem = menuItem; }

    @Override
    public String toString() {
        String customerName = (customer != null) ? customer.getName() : "Невідомий";
        String itemName = (menuItem != null) ? menuItem.getDisplayName() : "не обрано";
        return "Order{" +
                "orderId=" + orderId +
                ", customer='" + customerName + '\'' +
                ", menuItem='" + itemName + '\'' +
                ", orderStatus=" + orderStatus +
                ", preparationTime=" + preparationTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return orderId == order.orderId;
    }

    @Override
        public int hashCode() {
        return Objects.hash(getOrderId());
    }
}
