package com.sorokaandriy.cafesimulation.simulation;

import com.sorokaandriy.cafesimulation.model.Customer;
import com.sorokaandriy.cafesimulation.model.Table;
import com.sorokaandriy.cafesimulation.model.enums.TableStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TableService {

    private final List<Table> tables = new ArrayList<>();

    public TableService(int tableCount) {
        for (int i = 1; i <= tableCount; i++) {
            tables.add(new Table(i));
        }
    }

    public List<Table> getTables() {
        return Collections.unmodifiableList(tables);
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
}
