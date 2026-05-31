package com.sorokaandriy.cafesimulation.config;

import com.sorokaandriy.cafesimulation.exception.CafeSimulationException;
import com.sorokaandriy.cafesimulation.model.Chef;
import com.sorokaandriy.cafesimulation.model.Staff;
import com.sorokaandriy.cafesimulation.model.Waiter;
import com.sorokaandriy.cafesimulation.model.enums.MenuItemType;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;

public class ConfigService {

    public static void saveConfig(SimulationConfig config, String filePath) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"tableCount\": ").append(config.getTableCount()).append(",\n");
        sb.append("  \"simulationDuration\": ").append(config.getSimulationDuration()).append(",\n");
        sb.append("  \"tickDelayMs\": ").append(config.getTickDelayMs()).append(",\n");
        sb.append("  \"arrivalMean\": ").append(config.getArrivalMean()).append(",\n");
        sb.append("  \"patienceMean\": ").append(config.getPatienceMean()).append(",\n");
        sb.append("  \"serviceMean\": ").append(config.getServiceMean()).append(",\n");
        sb.append("  \"serviceStdDev\": ").append(config.getServiceStdDev()).append(",\n");
        sb.append("  \"eatingMean\": ").append(config.getEatingMean()).append(",\n");
        sb.append("  \"staff\": [\n");

        List<Staff> staffList = config.getStaffList();
        for (int i = 0; i < staffList.size(); i++) {
            Staff s = staffList.get(i);
            sb.append("    {\n");
            sb.append("      \"id\": ").append(s.getId()).append(",\n");
            sb.append("      \"name\": \"").append(s.getName()).append("\",\n");

            if (s instanceof Chef chef) {
                sb.append("      \"type\": \"CHEF\",\n");
                String spec = chef.getSpecialization() == null
                        ? "null"
                        : "\"" + chef.getSpecialization().name() + "\"";
                sb.append("      \"specialization\": ").append(spec).append("\n");
            } else {
                sb.append("      \"type\": \"WAITER\"\n");
            }

            sb.append("    }");
            if (i < staffList.size() - 1) sb.append(",");
            sb.append("\n");
        }

        sb.append("  ]\n");
        sb.append("}");

        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(sb.toString());
        } catch (IOException e) {
            throw new CafeSimulationException("Не вдалося зберегти конфігурацію: " + e.getMessage());
        }
    }


    public static SimulationConfig loadConfig(String filePath) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            throw new CafeSimulationException("Не вдалося завантажити конфігурацію: " + e.getMessage());
        }

        String json = sb.toString();

        int tableCount = parseInt(json, "tableCount");
        int simulationDuration = parseInt(json, "simulationDuration");
        int tickDelayMs = parseInt(json, "tickDelayMs");
        double arrivalMean = parseDouble(json, "arrivalMean");
        double patienceMean = parseDouble(json, "patienceMean");
        double serviceMean = parseDouble(json, "serviceMean");
        double serviceStdDev = parseDouble(json, "serviceStdDev");
        double eatingMean = parseDouble(json, "eatingMean");

        List<Staff> staffList = parseStaff(json);

        return new SimulationConfig(
                tableCount, simulationDuration, tickDelayMs,
                arrivalMean, patienceMean, serviceMean, serviceStdDev,
                eatingMean, staffList
        );
    }

    private static int parseInt(String json, String key) {
        return (int) parseDouble(json, key);
    }

    private static double parseDouble(String json, String key) {
        String search = "\"" + key + "\": ";
        int idx = json.indexOf(search);
        if (idx == -1) throw new CafeSimulationException("Ключ не знайдено: " + key);
        int start = idx + search.length();
        int end = start;
        while (end < json.length() && (Character.isDigit(json.charAt(end))
                || json.charAt(end) == '.' || json.charAt(end) == '-')) {
            end++;
        }
        return Double.parseDouble(json.substring(start, end));
    }

    private static List<Staff> parseStaff(String json) {
        List<Staff> list = new ArrayList<>();
        int staffStart = json.indexOf("\"staff\": [");
        if (staffStart == -1) return list;

        int arrStart = json.indexOf("[", staffStart) + 1;
        int arrEnd   = json.lastIndexOf("]");
        String staffArray = json.substring(arrStart, arrEnd);

        String[] objects = staffArray.split("\\{");
        for (String obj : objects) {
            obj = obj.trim();
            if (obj.isEmpty() || obj.equals("]") || obj.equals("}")) continue;

            long id = (long) parseDoubleFromBlock(obj, "id");
            String name = parseString(obj, "name");
            String type = parseString(obj, "type");

            if ("CHEF".equals(type)) {
                String specRaw = parseStringNullable(obj, "specialization");
                MenuItemType spec = specRaw == null ? null : MenuItemType.valueOf(specRaw);
                list.add(new Chef(id, name, true, spec));
            } else {
                list.add(new Waiter(id, name, true));
            }
        }
        return list;
    }

    private static double parseDoubleFromBlock(String block, String key) {
        String search = "\"" + key + "\": ";
        int idx = block.indexOf(search);
        if (idx == -1) return 0;
        int start = idx + search.length();
        int end = start;
        while (end < block.length() && (Character.isDigit(block.charAt(end))
                || block.charAt(end) == '.' || block.charAt(end) == '-')) {
            end++;
        }
        return Double.parseDouble(block.substring(start, end));
    }

    private static String parseString(String block, String key) {
        String search = "\"" + key + "\": \"";
        int idx = block.indexOf(search);
        if (idx == -1) return "";
        int start = idx + search.length();
        int end = block.indexOf("\"", start);
        return block.substring(start, end);
    }

    private static String parseStringNullable(String block, String key) {
        String search = "\"" + key + "\": ";
        int idx = block.indexOf(search);
        if (idx == -1) return null;
        int start = idx + search.length();
        if (block.substring(start).trim().startsWith("null")) return null;
        // є лапки — значить рядок
        int q1 = block.indexOf("\"", start);
        int q2 = block.indexOf("\"", q1 + 1);
        return block.substring(q1 + 1, q2);
    }
}
