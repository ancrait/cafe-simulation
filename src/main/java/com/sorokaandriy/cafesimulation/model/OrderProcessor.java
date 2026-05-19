package com.sorokaandriy.cafesimulation.model;

public interface OrderProcessor {
    void startCooking(Order order);
    void finishCooking(Order order);
}