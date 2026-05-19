package com.sorokaandriy.cafesimulation.simulation.tasks;


import com.sorokaandriy.cafesimulation.model.Customer;
import com.sorokaandriy.cafesimulation.model.CustomerHandler;
import com.sorokaandriy.cafesimulation.model.Order;
import com.sorokaandriy.cafesimulation.model.Staff;
import com.sorokaandriy.cafesimulation.model.enums.OrderStatus;
import com.sorokaandriy.cafesimulation.simulation.SimulationCore;

public class TakeOrderStrategy implements TaskAssignmentStrategy{
    @Override
    public boolean tryAssignTask(Staff worker, SimulationCore core) {
        if (worker instanceof CustomerHandler && !core.getCustomerQueue().isEmpty()) {
            CustomerHandler handler = (CustomerHandler) worker;
            Customer customer = core.getCustomerQueue().poll();

            Order newOrder = new Order(customer.getId(), customer, OrderStatus.PENDING, 0);
            customer.setOrder(newOrder);

            handler.takeOrder(customer);
            core.getPendingOrders().add(newOrder);

            long time = Math.round(Math.max(1, core.getServiceDistribution().sample()));
            core.setWorkerBusy(worker, time);

            return true;
        }
        return false;
    }
}
