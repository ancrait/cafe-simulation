package com.sorokaandriy.cafesimulation.controller;

import com.sorokaandriy.cafesimulation.config.SimulationConfig;
import com.sorokaandriy.cafesimulation.model.*;
import com.sorokaandriy.cafesimulation.model.enums.TableStatus;
import com.sorokaandriy.cafesimulation.simulation.SimulationCore;
import com.sorokaandriy.cafesimulation.simulation.log.SimulationEventLog;
import com.sorokaandriy.cafesimulation.simulation.statistics.ReportGenerator;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.List;

public class SimulationController {

    @FXML private Label tickLabel;
    @FXML private Label queueCountLabel;
    @FXML private Label ordersCountLabel;
    @FXML private Label arrivedLabel;
    @FXML private Label servedLabel;
    @FXML private Label leftLabel;
    @FXML private Label avgWaitLabel;
    @FXML private Label statusLabel;
    @FXML private Button pauseButton;
    @FXML private GridPane tablesGrid;
    @FXML private ListView<String> queueListView;
    @FXML private ListView<String> ordersListView;
    @FXML private ListView<String> logListView;
    @FXML private VBox staffContainer;

    private SimulationCore core;
    private SimulationConfig config;
    private Timeline timeline;
    private boolean paused = false;
    private int logIndex = 0;


    public void initSimulation(SimulationConfig config) {
        this.config = config;
        this.core = new SimulationCore(config);
        startTimeline();
    }

    private void startTimeline() {
        timeline = new Timeline(new KeyFrame(
                Duration.millis(config.getTickDelayMs()),
                e -> {
                    core.tick();
                    updateUI();

                    if (core.getCurrentTime() >= config.getSimulationDuration()) {
                        timeline.stop();
                        onSimulationFinished();
                    }
                }
        ));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void updateUI() {
        long tick = core.getCurrentTime();


        tickLabel.setText("Тік: " + tick);


        arrivedLabel.setText(String.valueOf(core.getStatisticsCollector().getTotalCustomersArrived()));
        servedLabel.setText(String.valueOf(core.getStatisticsCollector().getTotalCustomersServed()));
        leftLabel.setText(String.valueOf(core.getStatisticsCollector().getTotalCustomersLeft()));
        avgWaitLabel.setText(String.format("%.1f", core.getStatisticsCollector().getAverageWaitTime()));


        queueListView.getItems().clear();
        for (Customer c : core.getCustomerQueue()) {
            long waiting = tick - c.getArrivalTime();
            long patience = c.getPatience();
            queueListView.getItems().add(c.getName() + " — чекає: " + waiting + "/" + patience + " тіків");
        }
        queueCountLabel.setText(String.valueOf(core.getCustomerQueue().size()));


        ordersListView.getItems().clear();
        for (var entry : core.getActiveKitchenOrders().entrySet()) {
            Order order = entry.getValue();
            Staff chef = entry.getKey();
            long timeLeft = chef.getBusyUntil() - tick;
            ordersListView.getItems().add(
                    order.getMenuItem().getDisplayName()
                            + " → " + order.getCustomer().getName()
                            + " [" + chef.getName() + ", ще ~" + Math.max(0, timeLeft) + " тіків]"
            );
        }

        for (Order order : core.getPendingOrders()) {
            ordersListView.getItems().add(
                    "⏳ " + order.getMenuItem().getDisplayName()
                            + " → " + order.getCustomer().getName() + " [в черзі]"
            );
        }
        ordersCountLabel.setText(String.valueOf(
                core.getActiveKitchenOrders().size() + core.getPendingOrders().size()
        ));


        updateTablesGrid();


        updateStaffPanel();


        List<SimulationEventLog.Event> newEvents = core.getEventLog().getEventsSince(logIndex);
        for (SimulationEventLog.Event event : newEvents) {
            logListView.getItems().add(event.toString());
        }
        logIndex = core.getEventLog().size();

        if (!logListView.getItems().isEmpty()) {
            logListView.scrollTo(logListView.getItems().size() - 1);
        }
    }

    private void updateTablesGrid() {
        tablesGrid.getChildren().clear();
        List<Table> tables = core.getTableService().getTables();
        int cols = 3;
        for (int i = 0; i < tables.size(); i++) {
            Table table = tables.get(i);
            VBox card = buildTableCard(table);
            tablesGrid.add(card, i % cols, i / cols);
        }
    }

    private VBox buildTableCard(Table table) {
        VBox card = new VBox(3);
        card.getStyleClass().add("table-card");

        String statusStyle = switch (table.getTableStatus()) {
            case FREE     -> "table-free";
            case OCCUPIED -> "table-occupied";
            case DIRTY    -> "table-dirty";
        };
        card.getStyleClass().add(statusStyle);

        String icon = switch (table.getTableStatus()) {
            case FREE     -> "🟢";
            case OCCUPIED -> "🔴";
            case DIRTY    -> "🟡";
        };

        Label idLabel = new Label(icon + " Стіл #" + table.getId());
        idLabel.getStyleClass().add("table-id");

        Label statusLabel = new Label(table.getTableStatus().toString());
        statusLabel.getStyleClass().add("table-status");

        card.getChildren().addAll(idLabel, statusLabel);

        if (table.getCurrentCustomer() != null) {
            Label customerLabel = new Label(table.getCurrentCustomer().getName());
            customerLabel.getStyleClass().add("table-customer");
            card.getChildren().add(customerLabel);
        }

        return card;
    }

    private void updateStaffPanel() {
        staffContainer.getChildren().clear();
        for (Staff worker : core.getStaffList()) {
            HBox row = new HBox(10);
            row.getStyleClass().add("staff-row");

            String icon = (worker instanceof Chef) ? "👨‍🍳" : "👤";
            Label nameLabel = new Label(icon + " " + worker.getName());
            nameLabel.getStyleClass().add("staff-name");

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Label statusLabel;
            if (worker.isAvailable()) {
                statusLabel = new Label("● вільний");
                statusLabel.getStyleClass().add("staff-status-free");
            } else {
                long timeLeft = worker.getBusyUntil() - core.getCurrentTime();
                statusLabel = new Label("● зайнятий ще ~" + Math.max(0, timeLeft) + " тіків");
                statusLabel.getStyleClass().add("staff-status-busy");
            }

            row.getChildren().addAll(nameLabel, spacer, statusLabel);


            if (worker instanceof Chef chef) {
                VBox wrapper = new VBox(4);
                wrapper.getChildren().add(row);

                double fatigue = chef.getFatigueLevel();
                ProgressBar fatigueBar = new ProgressBar(fatigue);
                fatigueBar.setPrefWidth(Double.MAX_VALUE);
                fatigueBar.getStyleClass().add("fatigue-bar");
                fatigueBar.setStyle(fatigue > 0.7
                        ? "-fx-accent: #e05555;"
                        : fatigue > 0.4
                        ? "-fx-accent: #e8a547;"
                        : "-fx-accent: #4caf82;"
                );

                wrapper.getChildren().add(fatigueBar);
                staffContainer.getChildren().add(wrapper);
            } else {
                staffContainer.getChildren().add(row);
            }
        }
    }

    @FXML
    private void onPauseResume() {
        paused = !paused;
        if (paused) {
            timeline.pause();
            pauseButton.setText("▶ Продовжити");
            statusLabel.setText("⏸ Пауза");
            statusLabel.getStyleClass().setAll("status-paused");
        } else {
            timeline.play();
            pauseButton.setText("⏸ Пауза");
            statusLabel.setText("● Симуляція йде...");
            statusLabel.getStyleClass().setAll("status-running");
        }
    }

    @FXML
    private void onStop() {
        timeline.stop();
        onSimulationFinished();
    }

    @FXML
    private void onClearLog() {
        logListView.getItems().clear();
    }

    private void onSimulationFinished() {
        statusLabel.setText("✓ Завершено");
        statusLabel.getStyleClass().setAll("status-finished");
        pauseButton.setDisable(true);


        String filename = ReportGenerator.saveReportToJsonFile(
                core.getStatisticsCollector(), core.getCurrentTime()
        );
        logListView.getItems().add("[СИСТЕМА] Звіт збережено: " + filename);
    }
}