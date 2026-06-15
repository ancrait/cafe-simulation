package com.sorokaandriy.cafesimulation.simulation;

import com.sorokaandriy.cafesimulation.config.SimulationConfig;
import com.sorokaandriy.cafesimulation.model.*;
import java.util.*;
import com.sorokaandriy.cafesimulation.model.enums.OrderStatus;
import com.sorokaandriy.cafesimulation.model.enums.TableStatus;
import com.sorokaandriy.cafesimulation.simulation.log.SimulationEventLog;
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
    private NormalDistribution eatingDistribution;
    private final TableService tableService;
    private final MenuService menuService;
    private final ExponentialDistribution patienceDistribution;

    private final SimulationEventLog eventLog;

    public SimulationCore(SimulationConfig config) {
        this.customerQueue = new LinkedList<>();
        this.pendingOrders = new LinkedList<>();
        this.readyOrders = new LinkedList<>();
        this.currentTime = 0;

        this.activeKitchenOrders = new HashMap<>();

        this.arrivalDistribution = new ExponentialDistribution(config.getArrivalMean());
        this.nextCustomerArrivalTime = Math.round(arrivalDistribution.sample());
        this.serviceDistribution = new NormalDistribution(config.getServiceMean(), config.getServiceStdDev());
        this.eatingDistribution = new NormalDistribution(config.getEatingMean(), config.getEatingMean() * 0.2);
        this.patienceDistribution = new ExponentialDistribution(config.getPatienceMean());


        this.staffList = new ArrayList<>(config.getStaffList());


        this.taskStrategies = Arrays.asList(
                new DeliverOrderStrategy(),
                new TakeOrderStrategy(),
                new CookOrderStrategy(),
                new CleanTableStrategy()
        );

        this.statisticsCollector = new StatisticsCollector();
        this.tableService = new TableService(config.getTableCount());
        this.menuService = new MenuService();

        this.eventLog = new SimulationEventLog();
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
    public SimulationEventLog getEventLog() {return eventLog;}
    public List<Staff> getStaffList() {return staffList;}

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

    public void logEvent(String message) {
        eventLog.add(currentTime, message);
    }


    private void handleCustomersEating() {
        for (Table table : tableService.getTables()) {
            if (table.getTableStatus() == TableStatus.OCCUPIED
                    && table.getCurrentCustomer() != null
                    && table.getOccupiedUntil() > 0
                    && table.getCurrentCustomer().getOrder().getOrderStatus() == OrderStatus.DELIVERED
                    && currentTime >= table.getOccupiedUntil()) {

                String customerName = table.getCurrentCustomer().getName();
                long tableId = table.getId();
                eventLog.add(currentTime, customerName + " завершив їжу за столом #" + tableId);
                table.markAsFinished();
                eventLog.add(currentTime, " Стіл #" + tableId + " потребує прибирання");
            }
        }
    }
    }



