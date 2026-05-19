package com.sorokaandriy.cafesimulation.simulation;

import com.sorokaandriy.cafesimulation.model.*;
import java.util.*;
import com.sorokaandriy.cafesimulation.model.enums.OrderStatus;
import com.sorokaandriy.cafesimulation.simulation.statistics.StatisticsCollector;
import com.sorokaandriy.cafesimulation.simulation.tasks.CookOrderStrategy;
import com.sorokaandriy.cafesimulation.simulation.tasks.DeliverOrderStrategy;
import com.sorokaandriy.cafesimulation.simulation.tasks.TakeOrderStrategy;
import com.sorokaandriy.cafesimulation.simulation.tasks.TaskAssignmentStrategy;
import org.apache.commons.math3.analysis.solvers.NewtonRaphsonSolver;
import org.apache.commons.math3.distribution.ExponentialDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;

public class SimulationCore {
    private List<Staff> staffList;
    private Queue<Customer> customerQueue;
    private Queue<Order> pendingOrders;
    private Queue<Order> readyOrders;
    private long currentTime;
    private ExponentialDistribution arrivalDistribution;
    private long nextCustomerArrivalTime;
    private NormalDistribution serviceDistribution;
    private Map<Staff, Order> activeKitchenOrders;
    private NormalDistribution cookingDistribution;
    private List<TaskAssignmentStrategy> taskStrategies;
    private StatisticsCollector statisticsCollector;

    public SimulationCore() {
        this.customerQueue = new LinkedList<>();
        this.pendingOrders = new LinkedList<>();
        this.readyOrders = new LinkedList<>();
        this.currentTime = 0;

        this.arrivalDistribution = new ExponentialDistribution(15.0);
        this.nextCustomerArrivalTime = Math.round(arrivalDistribution.sample());
        this.serviceDistribution = new NormalDistribution(5.0, 1.5);

        this.activeKitchenOrders = new HashMap<>();
        this.cookingDistribution = new NormalDistribution(15.0, 3.0);
        this.staffList = new ArrayList<>();


        this.taskStrategies = Arrays.asList(
                new DeliverOrderStrategy(),
                new TakeOrderStrategy(),
                new CookOrderStrategy()
        );

        this.statisticsCollector = new StatisticsCollector();
    }

    public long getCurrentTime() { return currentTime; }
    public Queue<Customer> getCustomerQueue() { return customerQueue; }
    public Queue<Order> getPendingOrders() { return pendingOrders; }
    public Queue<Order> getReadyOrders() { return readyOrders; }
    public Map<Staff, Order> getActiveKitchenOrders() { return activeKitchenOrders; }
    public NormalDistribution getServiceDistribution() { return serviceDistribution; }
    public NormalDistribution getCookingDistribution() { return cookingDistribution; }
    public StatisticsCollector getStatisticsCollector() {return statisticsCollector;}

    public void tick() {
        currentTime++;
        if (currentTime >= nextCustomerArrivalTime) {
            Customer newCustomer = new Customer(currentTime, "Клієнт-" + currentTime, currentTime, null);
            customerQueue.add(newCustomer);


            statisticsCollector.recordArrival();

            long intervalToNextCustomer = Math.round(arrivalDistribution.sample());
            nextCustomerArrivalTime = currentTime + intervalToNextCustomer;
        }

        assignTasks();
    }

    private void assignTasks() {
        for (Staff worker : staffList) {
            handleStaffFreeing(worker);

            if (worker.isAvailable()) {
                for (TaskAssignmentStrategy strategy : taskStrategies) {
                    if (strategy.tryAssignTask(worker, this)) {
                        break;
                    }
                }
            }
        }
    }


    private void handleStaffFreeing(Staff worker) {
        if (!worker.isAvailable() && currentTime >= worker.getBusyUntil()) {
            if (worker instanceof OrderProcessor && activeKitchenOrders.containsKey(worker)) {
                OrderProcessor processor = (OrderProcessor) worker;
                Order finishedOrder = activeKitchenOrders.remove(worker);
                processor.finishCooking(finishedOrder);
                readyOrders.add(finishedOrder);
            }

            worker.setAvailable(true);
        }
    }


    public void setWorkerBusy(Staff worker, long duration) {
        worker.setAvailable(false);
        worker.setBusyUntil(currentTime + duration);
    }

    public void addStaff(Staff staff){
        staffList.add(staff);
    }



}
