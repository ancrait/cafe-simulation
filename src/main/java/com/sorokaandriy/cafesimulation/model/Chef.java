package com.sorokaandriy.cafesimulation.model;

import com.sorokaandriy.cafesimulation.exception.CafeSimulationException;
import com.sorokaandriy.cafesimulation.model.enums.OrderStatus;

public class Chef extends Staff implements OrderProcessor{


    public Chef(Long id, String name, boolean isAvailable) {
        super(id, name, isAvailable);
    }


    @Override
    public void startCooking(Order order) {
        if (order == null) {
            throw new CafeSimulationException("Шеф " + getName() + ": замовлення відсутнє");
        }
        if (order.getOrderStatus() != OrderStatus.PENDING) {
            throw new CafeSimulationException(
                    "Шеф " + getName() + ": замовлення #" + order.getOrderId()
                            + " не в статусі PENDING, поточний статус: " + order.getOrderStatus()
            );
        }
        if (!this.isAvailable()) {
            throw new CafeSimulationException("Шеф " + getName() + " зайнятий");
        }

        order.setOrderStatus(OrderStatus.PREPARING);


    }

    @Override
    public void finishCooking(Order order) {
        if (order == null) {
            throw new CafeSimulationException("Шеф " + getName() + ": замовлення відсутнє");
        }
        if (order.getOrderStatus() != OrderStatus.PREPARING) {
            throw new CafeSimulationException(
                    "Шеф " + getName() + ": замовлення #" + order.getOrderId()
                            + " не готується! Поточний статус: " + order.getOrderStatus()
            );
        }
        order.setOrderStatus(OrderStatus.READY);

    }
}
