package com.sorokaandriy.cafesimulation.model;

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
