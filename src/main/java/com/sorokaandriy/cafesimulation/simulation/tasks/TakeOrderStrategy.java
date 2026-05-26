package com.sorokaandriy.cafesimulation.simulation.tasks;


import com.sorokaandriy.cafesimulation.model.*;
import com.sorokaandriy.cafesimulation.model.enums.MenuItem;
import com.sorokaandriy.cafesimulation.model.enums.OrderStatus;
import com.sorokaandriy.cafesimulation.model.enums.TableStatus;
import com.sorokaandriy.cafesimulation.simulation.SimulationCore;

public class TakeOrderStrategy implements TaskAssignmentStrategy{
    @Override
    public boolean tryAssignTask(Staff worker, SimulationCore core) {
        Table table = core.getTableService().findFreeTable();

        if (worker instanceof CustomerHandler && !core.getCustomerQueue().isEmpty() && table != null) {
            CustomerHandler handler = (CustomerHandler) worker;
            Customer customer = core.getCustomerQueue().poll();

            table.seatCustomer(customer);

            MenuItem selectedItem = customer.getSelectedMenuItem() != null
                    ? customer.getSelectedMenuItem()
                    : core.getMenuService().selectByPopularity();

            Order newOrder = new Order(customer.getId(), customer, OrderStatus.PENDING, selectedItem);
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
