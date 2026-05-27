package com.sorokaandriy.cafesimulation.simulation.tasks;

import com.sorokaandriy.cafesimulation.exception.CafeSimulationException;
import com.sorokaandriy.cafesimulation.model.Chef;
import com.sorokaandriy.cafesimulation.model.Order;
import com.sorokaandriy.cafesimulation.model.OrderProcessor;
import com.sorokaandriy.cafesimulation.model.Staff;
import com.sorokaandriy.cafesimulation.simulation.SimulationCore;

public class CookOrderStrategy implements TaskAssignmentStrategy{
    @Override
    public boolean tryAssignTask(Staff worker, SimulationCore core) {
        if (worker instanceof OrderProcessor && !core.getPendingOrders().isEmpty()) {

            Order order = findSuitableOrder(worker, core);
            if (order == null) return false;

            core.getPendingOrders().remove(order);

            OrderProcessor processor = (OrderProcessor) worker;

            processor.startCooking(order);
            core.getActiveKitchenOrders().put(worker, order);


            if (order.getMenuItem() == null) {
                throw new CafeSimulationException(
                        "Замовлення #" + order.getOrderId() + " не має страви"
                );
            }

            double fatigueLevel = (worker instanceof Chef)
                    ? ((Chef) worker).getFatigueLevel()
                    : 0.0;

            long time = core.getMenuService().samplePreparationTime(order.getMenuItem(), fatigueLevel);

            order.setPreparationTime(time);
            core.setWorkerBusy(worker, time);

            if (worker instanceof Chef) {
                ((Chef) worker).addFatigue(time);
            }

            core.getStatisticsCollector().recordMenuItemCooked(order.getMenuItem(), time);

            return true;
        }
        return false;
    }

    private Order findSuitableOrder(Staff worker, SimulationCore core) {
        if (!(worker instanceof Chef)) {
            return core.getPendingOrders().peek();
        }

        Chef chef = (Chef) worker;

        if (chef.getSpecialization() == null) {
            return core.getPendingOrders().peek();
        }


        for (Order order : core.getPendingOrders()) {
            if (order.getMenuItem() != null
                    && chef.canCook(order.getMenuItem().getType())) {
                return order;
            }
        }

        return null;
    }
}
