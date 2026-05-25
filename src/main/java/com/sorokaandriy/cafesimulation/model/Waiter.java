package com.sorokaandriy.cafesimulation.model;

import com.sorokaandriy.cafesimulation.exception.CafeSimulationException;
import com.sorokaandriy.cafesimulation.model.enums.OrderStatus;

public class Waiter extends Staff implements CustomerHandler{

    public Waiter(Long id, String name, boolean isAvailable) {
        super(id, name, isAvailable);
    }


    @Override
    public void takeOrder(Customer customer) {
        if (!this.isAvailable()) {
            throw new CafeSimulationException(
                    "Офіціант " + getName() + " зайнятий і не може прийняти замовлення"
            );
        }
        if (customer == null) {
            throw new CafeSimulationException("Неможливо прийняти замовлення: клієнт відсутній");
        }
        if (customer.getOrder() == null) {
            throw new CafeSimulationException(
                    "Клієнт " + customer.getName() + " не має замовлення"
            );
        }

        customer.getOrder().setOrderStatus(OrderStatus.PENDING);

    }

    @Override
    public void deliverOrder(Order order) {
        if (order == null) {
            throw new CafeSimulationException("Неможливо доставити: замовлення відсутнє");
        }
        if (order.getOrderStatus() != OrderStatus.READY) {
            throw new CafeSimulationException(
                    "Замовлення #" + order.getOrderId() + " ще не готове! Поточний статус: "
                            + order.getOrderStatus()
            );
        }

        order.setOrderStatus(OrderStatus.DELIVERED);

    }



}
