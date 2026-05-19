package com.sorokaandriy.cafesimulation.simulation.tasks;

import com.sorokaandriy.cafesimulation.model.Staff;
import com.sorokaandriy.cafesimulation.simulation.SimulationCore;

public interface TaskAssignmentStrategy {

    boolean tryAssignTask(Staff worker, SimulationCore core);
}
