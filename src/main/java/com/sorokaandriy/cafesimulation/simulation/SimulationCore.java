package com.sorokaandriy.cafesimulation.simulation;

import com.sorokaandriy.cafesimulation.model.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.sorokaandriy.cafesimulation.model.enums.OrderStatus;
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

    public SimulationCore() {
        this.customerQueue = new LinkedList<>();
        this.pendingOrders = new LinkedList<>();
        this.readyOrders = new LinkedList<>();
        this.currentTime = 0;

        this.arrivalDistribution = new ExponentialDistribution(15.0);

        this.nextCustomerArrivalTime = Math.round(arrivalDistribution.sample());

        this.serviceDistribution = new NormalDistribution(5.0, 1.5);

        initializeCafe();
    }

    public long getCurrentTime() {
        return currentTime;
    }

    public void addStaff(Staff staff){
        staffList.add(staff);
        System.out.println("Додано працівника: " + staff.getName());
    }

    private void initializeCafe() {
        this.staffList = new ArrayList<>();
    }

    public void addCustomer(Customer customer) {
        customerQueue.add(customer);
        System.out.println("Час " + currentTime + ": Прийшов новий клієнт " + customer.getName() +
                ". Наступний очікується о " + nextCustomerArrivalTime);
    }


    public void tick() {
        currentTime++;


        if (currentTime >= nextCustomerArrivalTime) {


            Customer newCustomer = new Customer(currentTime, "Клієнт-" + currentTime, currentTime, null);
            addCustomer(newCustomer);


            long intervalToNextCustomer = Math.round(arrivalDistribution.sample());
            nextCustomerArrivalTime = currentTime + intervalToNextCustomer;
        }

        assignTasks();
    }

    private void assignTasks() {
        for (Staff worker : staffList) {

            if (!worker.isAvailable() && currentTime >= worker.getBusyUntil()) {
                worker.setAvailable(true);
                System.out.println("Час " + currentTime + ": " + worker.getName() + " звільнився і готовий до роботи.");
            }

            if (worker.isAvailable()) {

                if (worker instanceof Waiter && !customerQueue.isEmpty()) {
                    Waiter waiter = (Waiter) worker;
                    Customer customer = customerQueue.poll();


                    long serviceTime = Math.round(Math.max(1, serviceDistribution.sample()));

                    waiter.setAvailable(false);
                    waiter.setBusyUntil(currentTime + serviceTime);

                    Order newOrder = new Order(customer.getId(), customer,OrderStatus.PENDING, 0);
                    pendingOrders.add(newOrder);

                    System.out.println("Час " + currentTime + ": " + waiter.getName() +
                            " приймає замовлення у " + customer.getName() +
                            " (буде зайнятий до " + waiter.getBusyUntil() + ")");
                }
            }
        }
    }





}
