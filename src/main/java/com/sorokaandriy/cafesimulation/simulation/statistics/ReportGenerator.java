package com.sorokaandriy.cafesimulation.simulation.statistics;

import com.sorokaandriy.cafesimulation.exception.CafeSimulationException;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ReportGenerator {

    public static String saveReportToTextFile(StatisticsCollector stats, long currentTime){
        String timeStamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String filename = "Cafe_Report_" + timeStamp + ".txt";

        try (FileWriter writer = new FileWriter(filename)) {
            writer.write("      ЗВІТ СИМУЛЯЦІЇ РОБОТИ КАФЕ         \n");
            writer.write("Тривалість симуляції: " + currentTime + " тиків\n\n");
            writer.write("1. Всього клієнтів прийшло: " + stats.getTotalCustomersArrived() + "\n");
            writer.write("2. Успішно обслуговано: " + stats.getTotalCustomersServed() + "\n");


            String avgTime = String.format("%.2f", stats.getAverageWaitTime());
            writer.write("3. Середній час очікування: " + avgTime + " тиків\n");

            String avgCleanTime = String.format("%.2f", stats.getAverageCleaningTime());
            writer.write("5. Середній час прибирання столу: " + avgCleanTime + " тиків\n");

            return filename;


        } catch (IOException e) {
            throw new CafeSimulationException("Не вдалося зберегти звіт у файлі:" + e.getMessage());
        }
    }
}
