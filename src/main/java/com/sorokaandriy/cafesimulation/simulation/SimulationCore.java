package com.sorokaandriy.cafesimulation.simulation;

import com.sorokaandriy.cafesimulation.model.*;
import java.util.*;
import com.sorokaandriy.cafesimulation.model.enums.OrderStatus;
import com.sorokaandriy.cafesimulation.model.enums.TableStatus;
import com.sorokaandriy.cafesimulation.simulation.statistics.StatisticsCollector;
import com.sorokaandriy.cafesimulation.simulation.tasks.*;
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
    List<Table> tables;
    private NormalDistribution eatingDistribution;

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
                new CookOrderStrategy(),
                new CleanTableStrategy()
        );

        this.statisticsCollector = new StatisticsCollector();
        this.tables = new ArrayList<>();
        initTables();
        this.eatingDistribution = new NormalDistribution(25.0, 5.0);
    }

    public long getCurrentTime() { return currentTime; }
    public Queue<Customer> getCustomerQueue() { return customerQueue; }
    public Queue<Order> getPendingOrders() { return pendingOrders; }
    public Queue<Order> getReadyOrders() { return readyOrders; }
    public Map<Staff, Order> getActiveKitchenOrders() { return activeKitchenOrders; }
    public NormalDistribution getServiceDistribution() { return serviceDistribution; }
    public NormalDistribution getCookingDistribution() { return cookingDistribution; }
    public StatisticsCollector getStatisticsCollector() {return statisticsCollector;}
    public List<Table> getTables(){return tables;}
    public NormalDistribution getEatingDistribution() {return eatingDistribution;}

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
        handleCustomersEating();
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


    public void initTables() {
        for (int i = 1; i <= 5; i++) {
            tables.add(new Table(i));
        }
    }

    public Table findFreeTable() {
        for (Table table : tables) {
            if (table.getTableStatus() == TableStatus.FREE) return table;
        }
        return null;
    }

    public Table findDirtyTable() {
        for (Table table : tables) {
            if (table.getTableStatus() == TableStatus.DIRTY) return table;
        }
        return null;
    }

    public Table findTableByCustomer(Customer customer) {
        for (Table table : tables) {
            if (table.getCurrentCustomer() != null && table.getCurrentCustomer().equals(customer)) {
                return table;
            }
        }
        return null;
    }


    private void handleCustomersEating() {
        for (Table table : tables) {
            if (table.getTableStatus() == TableStatus.OCCUPIED
                    && table.getCurrentCustomer() != null
                    && table.getCurrentCustomer().getOrder().getOrderStatus() == OrderStatus.DELIVERED) {

                if (currentTime >= table.getOccupiedUntil()) {
                    table.getCurrentCustomer().getOrder().setOrderStatus(OrderStatus.COMPLETED);
                    table.setTableStatus(TableStatus.DIRTY);
                    table.setCurrentCustomer(null);
                }
            }
        }
    }


}
