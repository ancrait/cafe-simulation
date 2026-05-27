package com.sorokaandriy.cafesimulation.simulation.tasks;

import com.sorokaandriy.cafesimulation.exception.CafeSimulationException;
import com.sorokaandriy.cafesimulation.model.Order;
import com.sorokaandriy.cafesimulation.model.OrderProcessor;
import com.sorokaandriy.cafesimulation.model.Staff;
import com.sorokaandriy.cafesimulation.simulation.SimulationCore;

public class CookOrderStrategy implements TaskAssignmentStrategy{
    @Override
    public boolean tryAssignTask(Staff worker, SimulationCore core) {
        if (worker instanceof OrderProcessor && !core.getPendingOrders().isEmpty()) {
            OrderProcessor processor = (OrderProcessor) worker;
            Order order = core.getPendingOrders().poll();

            processor.startCooking(order);
            core.getActiveKitchenOrders().put(worker, order);


            if (order.getMenuItem() == null) {
                throw new CafeSimulationException(
                        "Замовлення #" + order.getOrderId() + " не має страви"
                );
            }
            long time = core.getMenuService().samplePreparationTime(order.getMenuItem());

            order.setPreparationTime(time);
            core.setWorkerBusy(worker, time);

            core.getStatisticsCollector().recordMenuItemCooked(order.getMenuItem(), time);

            return true;
        }
        return false;
    }
}
