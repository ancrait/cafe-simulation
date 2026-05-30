package com.sorokaandriy.cafesimulation.simulation.tasks;

import com.sorokaandriy.cafesimulation.model.CustomerHandler;
import com.sorokaandriy.cafesimulation.model.Staff;
import com.sorokaandriy.cafesimulation.model.Table;
import com.sorokaandriy.cafesimulation.model.enums.TableStatus;
import com.sorokaandriy.cafesimulation.simulation.SimulationCore;

public class CleanTableStrategy implements TaskAssignmentStrategy{
    @Override
    public boolean tryAssignTask(Staff worker, SimulationCore core) {
        Table dirtyTable = core.getTableService().findDirtyTable();

        if (worker instanceof CustomerHandler && dirtyTable != null) {


            long tableId = dirtyTable.getId();
            core.logEvent(worker.getName() + " прибирає стіл #" + tableId);
            dirtyTable.clean();

            long time = Math.round(Math.max(1, core.getServiceDistribution().sample()));
            core.getStatisticsCollector().recordCleanedTable(time);
            core.setWorkerBusy(worker, time);

            core.logEvent(worker.getName() + " прибирає стіл #" + dirtyTable.getId());

            return true;
        }
        return false;
    }
}
