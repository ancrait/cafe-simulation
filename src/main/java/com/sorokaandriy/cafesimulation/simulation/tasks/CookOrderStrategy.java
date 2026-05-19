package com.sorokaandriy.cafesimulation.simulation.tasks;

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

            long time = Math.round(Math.max(1, core.getCookingDistribution().sample()));
            core.setWorkerBusy(worker, time);

            return true;
        }
        return false;
    }
}
