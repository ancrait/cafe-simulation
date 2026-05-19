package com.sorokaandriy.cafesimulation.model;

import com.sorokaandriy.cafesimulation.model.enums.OrderStatus;

import java.util.Objects;

public class Order {
    private long orderId;
    private Customer customer;
    private OrderStatus orderStatus;
    private long preparationTime;

    public Order(long orderId, Customer customer, OrderStatus orderStatus, long preparationTime) {
        this.orderId = orderId;
        this.customer = customer;
        this.orderStatus = orderStatus;
        this.preparationTime = preparationTime;
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



    @Override
    public String toString() {
        String customerName = (customer != null) ? customer.getName() : "Невідомий";
        return "Order{" +
                "orderId=" + orderId +
                ", customer='" + customerName + '\'' +
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
