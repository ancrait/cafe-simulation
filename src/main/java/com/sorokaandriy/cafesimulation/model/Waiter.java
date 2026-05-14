package com.sorokaandriy.cafesimulation.model;

import com.sorokaandriy.cafesimulation.model.enums.OrderStatus;

public class Waiter extends Staff{


    public Waiter(Long id, String name, boolean isAvailable) {
        super(id, name, isAvailable);
    }

    @Override
    public void performWork() {
        System.out.println("Офіціант " + getName() + " обслуговує столики.");
    }

    public void takeOrder(Customer customer) {
        if (this.isAvailable() && customer.getOrder() != null) {
            this.setAvailable(false);
            customer.getOrder().setOrderStatus(OrderStatus.PENDING);
            System.out.println("Офіціант " + getName() + " прийняв замовлення #" + customer.getOrder().getOrderId());
            this.setAvailable(true);
        }
    }

    public void deliverOrder(Order order) {
        this.setAvailable(false);
        order.setOrderStatus(OrderStatus.DELIVERED);
        System.out.println("Офіціант " + getName() + " приніс замовлення для " + order.getCustomer().getName());
        this.setAvailable(true);
    }

    public void cleanTable() {
        this.setAvailable(false);
        System.out.println("Офіціант " + getName() + " прибирає столик.");
        this.setAvailable(true);
    }


}
