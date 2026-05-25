package com.sorokaandriy.cafesimulation.simulation.statistics;

import com.sorokaandriy.cafesimulation.exception.CafeSimulationException;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ReportGenerator {

    public static String saveReportToJsonFile(StatisticsCollector stats, long currentTime) {
        String timeStamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String filename = "Cafe_Report_" + timeStamp + ".json";

        String generatedAt = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String avgWaitTime  = String.format("%.2f", stats.getAverageWaitTime());
        String avgCleanTime = String.format("%.2f", stats.getAverageCleaningTime());


        String json = "{\n"
                + "  \"report\": {\n"
                + "    \"title\": \"ЗВІТ СИМУЛЯЦІЇ РОБОТИ КАФЕ\",\n"
                + "    \"generatedAt\": \"" + generatedAt + "\",\n"
                + "    \"simulationDurationTicks\": " + currentTime + ",\n"
                + "    \"statistics\": {\n"
                + "      \"totalCustomersArrived\": "      + stats.getTotalCustomersArrived() + ",\n"
                + "      \"totalCustomersServed\": "       + stats.getTotalCustomersServed()  + ",\n"
                + "      \"totalWaitTimeTicks\": "         + stats.getTotalWaitTime()          + ",\n"
                + "      \"averageWaitTimeTicks\": "       + avgWaitTime                       + ",\n"
                + "      \"totalTablesCleaned\": "         + stats.getTotalTablesCleaned()     + ",\n"
                + "      \"averageCleaningTimeTicks\": "   + avgCleanTime                      + "\n"
                + "    }\n"
                + "  }\n"
                + "}";

        try (FileWriter writer = new FileWriter(filename)) {
            writer.write(json);
            return filename;
        } catch (IOException e) {
            throw new CafeSimulationException("Не вдалося зберегти звіт у файлі: " + e.getMessage());
        }
    }
}
