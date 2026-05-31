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
    @FXML private Label reportSavedLabel;


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
    @FXML private RadioButton filterCleaning;
    @FXML private ListView<String> filteredLogView;


    private StatisticsCollector stats;
    private long simulationDuration;
    private final ObservableList<MenuRowData> menuData = FXCollections.observableArrayList();
    private final ObservableList<String> allLogObservable = FXCollections.observableArrayList();
    private FilteredList<String> filteredLog;



    public void initReport(StatisticsCollector stats,
                           long simulationDuration,
                           List<String> logEvents,
                           String savedReportFilename) {
        this.stats = stats;
        this.simulationDuration = simulationDuration;

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
        colName.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().name()));
        colType.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().type()));
        colCount.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().count()));
        colAvgPrep.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().avgPrep()));

        colName.setStyle("-fx-alignment: CENTER-LEFT;");
        colType.setStyle("-fx-alignment: CENTER;");
        colCount.setStyle("-fx-alignment: CENTER;");
        colAvgPrep.setStyle("-fx-alignment: CENTER;");

        for (MenuItem item : MenuItem.values()) {
            menuData.add(new MenuRowData(
                    item.getDisplayName(),
                    item.getType() == com.sorokaandriy.cafesimulation.model.enums.MenuItemType.FOOD ? " Страва" : " Напій",
                    String.valueOf(stats.getItemOrderCount(item)),
                    String.format("%.2f", stats.getAverageItemPrepTime(item))
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
                    Double.parseDouble(a.avgPrep().replace(",", ".")),
                    Double.parseDouble(b.avgPrep().replace(",", "."))
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
        filterCleaning.setOnAction(e -> applyFilter());
        filterTextField.textProperty().addListener((obs, o, n) -> applyFilter());

        filterAll.setSelected(true);
        applyFilter();
    }

    @FXML
    private void applyFilter() {
        String search = filterTextField.getText().toLowerCase().trim();

        filteredLog.setPredicate(line -> {
            if (line == null) return false;

            boolean categoryMatch;

            if (filterClients.isSelected()) {

                categoryMatch = line.contains("прийшов")
                        || line.contains("пішов")
                        || line.contains("починає їсти")
                        || line.contains("завершив їжу")
                        || line.contains("очікував");
            } else if (filterKitchen.isSelected()) {

                categoryMatch = line.contains("прийняв замовлення")
                        || line.contains("почав готувати")
                        || line.contains("доставив")
                        || line.contains("приготував");
            } else if (filterCleaning.isSelected()) {

                categoryMatch = line.contains("прибирає стіл")
                        || line.contains("вільний")
                        || line.contains("потребує прибирання")
                        || line.contains("Стіл #");
            } else {

                categoryMatch = true;
            }

            boolean textMatch = search.isEmpty()
                    || line.toLowerCase().contains(search);

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
                            "/com/sorokaandriy/cafesimulation/settings-view.fxml")
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