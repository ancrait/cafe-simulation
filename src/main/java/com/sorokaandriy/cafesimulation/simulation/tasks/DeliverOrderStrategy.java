package com.sorokaandriy.cafesimulation.simulation.tasks;

import com.sorokaandriy.cafesimulation.model.Staff;
import com.sorokaandriy.cafesimulation.model.CustomerHandler;
import com.sorokaandriy.cafesimulation.model.Order;
import com.sorokaandriy.cafesimulation.simulation.SimulationCore;

public class DeliverOrderStrategy implements TaskAssignmentStrategy{
    @Override
    public boolean tryAssignTask(Staff worker, SimulationCore core) {
        if (worker instanceof CustomerHandler && !core.getReadyOrders().isEmpty()) {
            CustomerHandler handler = (CustomerHandler) worker;
            Order order = core.getReadyOrders().poll();

            handler.deliverOrder(order);

            long time = Math.round(Math.max(1, core.getServiceDistribution().sample()));
            core.setWorkerBusy(worker, time);

            return true;
        }
        return false;
    }
}
