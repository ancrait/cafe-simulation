package com.sorokaandriy.cafesimulation.model;

public interface CustomerHandler {
    void takeOrder(Customer customer);
    void deliverOrder(Order order);

}
