package com.sorokaandriy.cafesimulation.controller;

import com.sorokaandriy.cafesimulation.model.enums.MenuItem;
import com.sorokaandriy.cafesimulation.simulation.statistics.StatisticsCollector;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReportController {


    @FXML private Label totalArrivedLabel;
    @FXML private Label totalServedLabel;
    @FXML private Label totalLeftLabel;
    @FXML private Label lossRateLabel;
    @FXML private Label avgWaitLabel;
    @FXML private Label totalTablesCleanedLabel;
    @FXML private Label avgCleanTimeLabel;
    @FXML private Label simDurationLabel;


    @FXML private TableView<MenuRowData> menuTable;
    @FXML private TableColumn<MenuRowData, String> colName;
    @FXML private TableColumn<MenuRowData, String> colType;
    @FXML private TableColumn<MenuRowData, String> colCount;
    @FXML private TableColumn<MenuRowData, String> colAvgPrep;


    @FXML private ToggleGroup sortGroup;
    @FXML private RadioButton sortByPopularity;
    @FXML private RadioButton sortByPrepTime;
    @FXML private RadioButton sortByName;


    @FXML private TextField filterTextField;
    @FXML private ToggleGroup filterGroup;
    @FXML private RadioButton filterAll;
    @FXML private RadioButton filterClients;
    @FXML private RadioButton filterKitchen;
    @FXML private RadioButton filterSystem;


    @FXML private ListView<String> filteredLogView;

    @FXML private Label reportSavedLabel;


    private StatisticsCollector stats;
    private long simulationDuration;
    private List<String> allLogEvents = new ArrayList<>();


    private ObservableList<MenuRowData> menuData = FXCollections.observableArrayList();


    private ObservableList<String> allLogObservable = FXCollections.observableArrayList();
    private FilteredList<String> filteredLog;



    public void initReport(StatisticsCollector stats,
                           long simulationDuration,
                           List<String> logEvents,
                           String savedReportFilename) {
        this.stats = stats;
        this.simulationDuration = simulationDuration;
        this.allLogEvents = logEvents;


        fillSummaryStats(savedReportFilename);


        setupMenuTable();


        setupLogFilter(logEvents);
    }


    private void fillSummaryStats(String savedFilename) {
        totalArrivedLabel.setText(String.valueOf(stats.getTotalCustomersArrived()));
        totalServedLabel.setText(String.valueOf(stats.getTotalCustomersServed()));
        totalLeftLabel.setText(String.valueOf(stats.getTotalCustomersLeft()));
        lossRateLabel.setText(String.format("%.1f%%", stats.getCustomerLossRate()));
        avgWaitLabel.setText(String.format("%.2f тіків", stats.getAverageWaitTime()));
        totalTablesCleanedLabel.setText(String.valueOf(stats.getTotalTablesCleaned()));
        avgCleanTimeLabel.setText(String.format("%.2f тіків", stats.getAverageCleaningTime()));
        simDurationLabel.setText(simulationDuration + " тіків");

        if (savedFilename != null && !savedFilename.isEmpty()) {
            reportSavedLabel.setText("✓ Звіт збережено: " + savedFilename);
        }
    }

    private void setupMenuTable() {
        // Налаштування колонок
        colName.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().name()));
        colType.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().type()));
        colCount.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().count()));
        colAvgPrep.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().avgPrep()));


        for (MenuItem item : MenuItem.values()) {
            long count = stats.getItemOrderCount(item);
            double avgPrep = stats.getAverageItemPrepTime(item);
            menuData.add(new MenuRowData(
                    item.getDisplayName(),
                    item.getType().toString(),
                    String.valueOf(count),
                    String.format("%.2f", avgPrep)
            ));
        }

        menuTable.setItems(menuData);


        sortByPopularity.setOnAction(e -> applySorting());
        sortByPrepTime.setOnAction(e -> applySorting());
        sortByName.setOnAction(e -> applySorting());

        sortByPopularity.setSelected(true);
        applySorting();
    }


    @FXML
    private void applySorting() {
        List<MenuRowData> sorted = new ArrayList<>(menuData);

        if (sortByPopularity.isSelected()) {

            sorted.sort((a, b) -> Long.compare(
                    Long.parseLong(b.count()),
                    Long.parseLong(a.count())
            ));
        } else if (sortByPrepTime.isSelected()) {

            sorted.sort((a, b) -> Double.compare(
                    Double.parseDouble(a.avgPrep()),
                    Double.parseDouble(b.avgPrep())
            ));
        } else if (sortByName.isSelected()) {

            sorted.sort((a, b) -> a.name().compareToIgnoreCase(b.name()));
        }

        menuData.setAll(sorted);
    }


    private void setupLogFilter(List<String> logEvents) {
        allLogObservable.setAll(logEvents);


        filteredLog = new FilteredList<>(allLogObservable, s -> true);
        filteredLogView.setItems(filteredLog);


        filterAll.setOnAction(e -> applyFilter());
        filterClients.setOnAction(e -> applyFilter());
        filterKitchen.setOnAction(e -> applyFilter());
        filterSystem.setOnAction(e -> applyFilter());


        filterTextField.textProperty().addListener((obs, oldVal, newVal) -> applyFilter());

        filterAll.setSelected(true);
        applyFilter();
    }


    @FXML
    private void applyFilter() {
        String searchText = filterTextField.getText().toLowerCase().trim();

        filteredLog.setPredicate(line -> {
            if (line == null) return false;


            boolean categoryMatch = true;
            if (filterClients.isSelected()) {

                categoryMatch = line.contains("Клієнт") || line.contains("очікував")
                        || line.contains("їсти") || line.contains("завершив їжу");
            } else if (filterKitchen.isSelected()) {

                categoryMatch = line.contains("готував") || line.contains("готує")
                        || line.contains("замовлення") || line.contains("доставив");
            } else if (filterSystem.isSelected()) {

                categoryMatch = line.contains("[СИСТЕМА]") || line.contains("прибирає")
                        || line.contains("стіл") || line.contains("Звіт");
            }



            boolean textMatch = searchText.isEmpty()
                    || line.toLowerCase().contains(searchText);

            return categoryMatch && textMatch;
        });
    }


    @FXML
    private void onClearFilter() {
        filterTextField.clear();
    }




    @FXML
    private void onBackToSettings() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/com/sorokaandriy/cafesimulation/report-view.fxml")
            );
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) menuTable.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Cafe Simulation — Налаштування");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public record MenuRowData(String name, String type, String count, String avgPrep) {}
}