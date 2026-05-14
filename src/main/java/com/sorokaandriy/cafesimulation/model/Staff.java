package com.sorokaandriy.cafesimulation.model;

import java.util.Objects;

public abstract class Staff extends Person{
    private boolean isAvailable;

    public Staff(Long id, String name, boolean isAvailable ) {
        super(id, name);
        this.isAvailable = isAvailable;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public abstract void performWork();

    @Override
    public String toString() {
        return super.toString() + " [isAvailable= " + isAvailable + "]";
        }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }


}
