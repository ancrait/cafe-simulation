package com.sorokaandriy.cafesimulation.model;

import com.sorokaandriy.cafesimulation.exception.CafeSimulationException;
import com.sorokaandriy.cafesimulation.model.enums.OrderStatus;
import com.sorokaandriy.cafesimulation.model.enums.TableStatus;

import java.util.Objects;

public class Table {

    private long id;
    private TableStatus tableStatus;
    private Customer currentCustomer;
    private long occupiedUntil = 0;

    public Table(long id) {
        this.id = id;
        this.tableStatus = TableStatus.FREE;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public TableStatus getTableStatus() {
        return tableStatus;
    }

    public void setTableStatus(TableStatus tableStatus) {
        this.tableStatus = tableStatus;
    }

    public Customer getCurrentCustomer() {
        return currentCustomer;
    }

    public void setCurrentCustomer(Customer currentCustomer) {
        this.currentCustomer = currentCustomer;
    }

    public long getOccupiedUntil() {return occupiedUntil;}

    public void setOccupiedUntil(long occupiedUntil) {this.occupiedUntil = occupiedUntil;}

    public void seatCustomer(Customer customer) {
        if (customer == null) {
            throw new CafeSimulationException("Неможливо посадити: клієнт відсутній.");
        }
        if (this.tableStatus != TableStatus.FREE) {
            throw new CafeSimulationException(
                    "Стіл #" + id + " не вільний! Поточний статус: " + tableStatus
            );
        }
        this.currentCustomer = customer;
        this.tableStatus = TableStatus.OCCUPIED;
    }

    public void startEating(long currentTime, long eatingDuration) {
        if (this.tableStatus != TableStatus.OCCUPIED) {
            throw new CafeSimulationException(
                    "Стіл #" + id + " не зайнятий, неможливо почати їжу"
            );
        }
        if (currentCustomer == null) {
            throw new CafeSimulationException("Стіл #" + id + ": клієнт відсутній");
        }
        this.occupiedUntil = currentTime + eatingDuration;

    }

    public void clean() {
        if (this.tableStatus != TableStatus.DIRTY) {
            throw new CafeSimulationException(
                    "Стіл #" + id + " не брудний, прибирати не потрібно. Статус: " + tableStatus
            );
        }
        this.tableStatus = TableStatus.FREE;
    }

    public void markAsFinished() {
        if (this.tableStatus != TableStatus.OCCUPIED) {
            throw new CafeSimulationException(
                    "Стіл #" + id + " не зайнятий, неможливо завершити обслуговування"
            );
        }
        if (currentCustomer == null || currentCustomer.getOrder() == null) {
            throw new CafeSimulationException("Стіл #" + id + ": клієнт або замовлення відсутні");
        }

        currentCustomer.getOrder().setOrderStatus(OrderStatus.COMPLETED);
        this.tableStatus = TableStatus.DIRTY;
        this.currentCustomer = null;
    }

    @Override
    public String toString() {
        return "Table{id=" + getId() + ", status=" + getTableStatus() + "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Table table = (Table) o;
        return getId() == table.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
