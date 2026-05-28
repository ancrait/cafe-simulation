package com.sorokaandriy.cafesimulation.simulation;

import com.sorokaandriy.cafesimulation.model.*;
import java.util.*;
import com.sorokaandriy.cafesimulation.model.enums.OrderStatus;
import com.sorokaandriy.cafesimulation.model.enums.TableStatus;
import com.sorokaandriy.cafesimulation.simulation.service.MenuService;
import com.sorokaandriy.cafesimulation.simulation.service.TableService;
import com.sorokaandriy.cafesimulation.simulation.statistics.StatisticsCollector;
import com.sorokaandriy.cafesimulation.simulation.tasks.*;
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
    private List<TaskAssignmentStrategy> taskStrategies;
    private StatisticsCollector statisticsCollector;
    private List<Table> tables;
    private NormalDistribution eatingDistribution;
    private final TableService tableService;
    private final MenuService menuService;
    private final ExponentialDistribution patienceDistribution;

    public SimulationCore() {
        this.customerQueue = new LinkedList<>();
        this.pendingOrders = new LinkedList<>();
        this.readyOrders = new LinkedList<>();
        this.currentTime = 0;

        this.arrivalDistribution = new ExponentialDistribution(15.0);
        this.nextCustomerArrivalTime = Math.round(arrivalDistribution.sample());
        this.serviceDistribution = new NormalDistribution(5.0, 1.5);

        this.activeKitchenOrders = new HashMap<>();

        this.staffList = new ArrayList<>();


        this.taskStrategies = Arrays.asList(
                new DeliverOrderStrategy(),
                new TakeOrderStrategy(),
                new CookOrderStrategy(),
                new CleanTableStrategy()
        );

        this.statisticsCollector = new StatisticsCollector();
        this.tables = new ArrayList<>();
        this.eatingDistribution = new NormalDistribution(25.0, 5.0);
        this.tableService = new TableService(5);
        this.menuService = new MenuService();

        this.patienceDistribution = new ExponentialDistribution(20.0);
    }

    public long getCurrentTime() { return currentTime; }
    public Queue<Customer> getCustomerQueue() { return customerQueue; }
    public Queue<Order> getPendingOrders() { return pendingOrders; }
    public Queue<Order> getReadyOrders() { return readyOrders; }
    public Map<Staff, Order> getActiveKitchenOrders() { return activeKitchenOrders; }
    public NormalDistribution getServiceDistribution() { return serviceDistribution; }
    public StatisticsCollector getStatisticsCollector() {return statisticsCollector;}
    public NormalDistribution getEatingDistribution() {return eatingDistribution;}
    public TableService getTableService() {return tableService;}
    public MenuService getMenuService() { return menuService; }


    public void addStaff(Staff staff) {
        staffList.add(staff);
    }

    public void tick() {
        currentTime++;
        if (currentTime >= nextCustomerArrivalTime) {

            long patience = Math.max(5, Math.round(patienceDistribution.sample()));

            Customer newCustomer = new Customer(currentTime, "Клієнт-" + currentTime,
                    currentTime, null, patience);
            customerQueue.add(newCustomer);


            statisticsCollector.recordArrival();

            long intervalToNextCustomer = Math.round(arrivalDistribution.sample());
            nextCustomerArrivalTime = currentTime + intervalToNextCustomer;
        }


        handleImpatientCustomers();
        assignTasks();
        handleCustomersEating();
        handleChefRecovery();
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

    private void handleImpatientCustomers() {
        customerQueue.removeIf(customer -> {
            if (customer.hasRunOutOfPatience(currentTime)) {
                statisticsCollector.recordCustomerLeft();
                return true;
            }
            return false;
        });
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

    private void handleChefRecovery() {
        for (Staff worker : staffList) {
            if (worker instanceof Chef && worker.isAvailable()) {
                ((Chef) worker).recover();
            }
        }
    }


    public void setWorkerBusy(Staff worker, long duration) {
        worker.setAvailable(false);
        worker.setBusyUntil(currentTime + duration);
    }


    private void handleCustomersEating() {
        for (Table table : tables) {
            if (table.getTableStatus() == TableStatus.OCCUPIED
                    && table.getCurrentCustomer() != null
                    && table.getCurrentCustomer().getOrder().getOrderStatus() == OrderStatus.DELIVERED
                    && currentTime >= table.getOccupiedUntil()) {

                table.markAsFinished();
                }
            }
        }
    }



