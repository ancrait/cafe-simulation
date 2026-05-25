package com.sorokaandriy.cafesimulation.simulation.tasks;

import com.sorokaandriy.cafesimulation.model.Staff;
import com.sorokaandriy.cafesimulation.model.CustomerHandler;
import com.sorokaandriy.cafesimulation.model.Order;
import com.sorokaandriy.cafesimulation.model.Table;
import com.sorokaandriy.cafesimulation.model.enums.TableStatus;
import com.sorokaandriy.cafesimulation.simulation.SimulationCore;

public class DeliverOrderStrategy implements TaskAssignmentStrategy{
    @Override
    public boolean tryAssignTask(Staff worker, SimulationCore core) {
        if (worker instanceof CustomerHandler && !core.getReadyOrders().isEmpty()) {
            CustomerHandler handler = (CustomerHandler) worker;
            Order order = core.getReadyOrders().poll();

            handler.deliverOrder(order);

            long waitTime = core.getCurrentTime() - order.getCustomer().getArrivalTime();
            core.getStatisticsCollector().recordServed(waitTime);

            Table table = core.getTableService().findTableByCustomer(order.getCustomer());
            if (table != null){
                long eatingTime = Math.round(Math.max(1, core.getEatingDistribution().sample()));
                table.startEating(core.getCurrentTime(), eatingTime);
            }

            long time = Math.round(Math.max(1, core.getServiceDistribution().sample()));
            core.setWorkerBusy(worker, time);

            return true;
        }
        return false;
    }
}
