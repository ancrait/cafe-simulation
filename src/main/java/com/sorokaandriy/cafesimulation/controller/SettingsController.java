package com.sorokaandriy.cafesimulation.controller;

import com.sorokaandriy.cafesimulation.model.Chef;
import com.sorokaandriy.cafesimulation.model.Staff;
import com.sorokaandriy.cafesimulation.model.Waiter;
import com.sorokaandriy.cafesimulation.model.enums.MenuItemType;
import com.sorokaandriy.cafesimulation.config.SimulationConfig;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SettingsController {

    @FXML private Spinner<Integer> tablesCountSpinner;
    @FXML private Spinner<Integer> durationSpinner;
    @FXML private Spinner<Integer> tickDelaySpinner;
    @FXML private Spinner<Integer> arrivalMeanSpinner;
    @FXML private Spinner<Integer> patienceMeanSpinner;
    @FXML private Spinner<Integer> serviceMeanSpinner;
    @FXML private Spinner<Integer> serviceStdDevSpinner;
    @FXML private Spinner<Integer> eatingMeanSpinner;
    @FXML private VBox waitersContainer;
    @FXML private VBox chefsContainer;
    @FXML private Label validationLabel;


    private long waiterIdCounter = 1;
    private long chefIdCounter = 100;

    @FXML
    public void initialize() {

        addWaiterRow("Офіціант-1");
        addChefRow("Шеф-1", null);
        addChefRow("Шеф-2", MenuItemType.DRINK);
    }



    @FXML
    private void onAddWaiter() {
        int count = waitersContainer.getChildren().size() + 1;
        addWaiterRow("Офіціант-" + count);
    }

    private void addWaiterRow(String defaultName) {
        long id = waiterIdCounter++;
        HBox row = new HBox(12);
        row.getStyleClass().add("staff-row");

        Label badge = new Label("👤 ОФІЦІАНТ");
        badge.getStyleClass().add("staff-badge");

        TextField nameField = new TextField(defaultName);
        nameField.getStyleClass().add("staff-name-field");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button removeBtn = new Button("✕");
        removeBtn.getStyleClass().add("btn-remove");
        removeBtn.setOnAction(e -> waitersContainer.getChildren().remove(row));

        row.getChildren().addAll(badge, nameField, spacer, removeBtn);
        row.setUserData(id);
        waitersContainer.getChildren().add(row);
    }



    @FXML
    private void onAddChef() {
        int count = chefsContainer.getChildren().size() + 1;
        addChefRow("Шеф-" + count, null);
    }

    private void addChefRow(String defaultName, MenuItemType specialization) {
        long id = chefIdCounter++;
        HBox row = new HBox(12);
        row.getStyleClass().add("staff-row");

        Label badge = new Label("👨‍🍳 ШЕФ");
        badge.getStyleClass().add("staff-badge");

        TextField nameField = new TextField(defaultName);
        nameField.getStyleClass().add("staff-name-field");

        // ComboBox спеціалізації
        ComboBox<String> specCombo = new ComboBox<>();
        specCombo.getStyleClass().add("combo-specialization");
        specCombo.getItems().addAll("Універсальний", "Тільки страви", "Тільки напої");
        specCombo.setValue(toComboValue(specialization));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button removeBtn = new Button("✕");
        removeBtn.getStyleClass().add("btn-remove");
        removeBtn.setOnAction(e -> chefsContainer.getChildren().remove(row));

        row.getChildren().addAll(badge, nameField, specCombo, spacer, removeBtn);
        row.setUserData(id);
        chefsContainer.getChildren().add(row);
    }



    @FXML
    private void onStartSimulation() {
        validationLabel.setText("");


        if (waitersContainer.getChildren().isEmpty()) {
            validationLabel.setText("Додайте хоча б одного офіціанта!");
            return;
        }
        if (chefsContainer.getChildren().isEmpty()) {
            validationLabel.setText("Додайте хоча б одного шефа!");
            return;
        }


        List<Staff> staffList = new ArrayList<>();

        for (var node : waitersContainer.getChildren()) {
            HBox row = (HBox) node;
            long id = (long) row.getUserData();
            String name = ((TextField) row.getChildren().get(1)).getText().trim();
            if (name.isEmpty()) name = "Офіціант-" + id;
            staffList.add(new Waiter(id, name, true));
        }

        for (var node : chefsContainer.getChildren()) {
            HBox row = (HBox) node;
            long id = (long) row.getUserData();
            String name = ((TextField) row.getChildren().get(1)).getText().trim();
            if (name.isEmpty()) name = "Шеф-" + id;
            String specValue = ((ComboBox<String>) row.getChildren().get(2)).getValue();
            MenuItemType spec = fromComboValue(specValue);
            staffList.add(new Chef(id, name, true, spec));
        }


        SimulationConfig config = new SimulationConfig(
                tablesCountSpinner.getValue(),
                durationSpinner.getValue(),
                tickDelaySpinner.getValue(),
                arrivalMeanSpinner.getValue(),
                patienceMeanSpinner.getValue(),
                serviceMeanSpinner.getValue(),
                serviceStdDevSpinner.getValue(),
                eatingMeanSpinner.getValue(),
                staffList
        );


        try {
            // створюється завантажувач для view симуляції
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/sorokaandriy/cafesimulation/simulation-view.fxml")
            );
            Scene scene = new Scene(loader.load()); // створюється нова сцена
            SimulationController controller = loader.getController(); // новий контролер
            controller.initSimulation(config); // в контролер передається конфіг (як dto) для даних для подальш симуляції

            Stage stage = (Stage) tablesCountSpinner.getScene().getWindow(); // створюється нова сцена
            stage.setScene(scene);
            stage.setTitle("Cafe Simulation — Running");
        } catch (IOException e) {
            validationLabel.setText("Помилка завантаження екрану симуляції");
            e.printStackTrace();
        }
    }


    private String toComboValue(MenuItemType type) {
        if (type == null) return "Універсальний";
        return switch (type) {
            case FOOD  -> "Тільки страви";
            case DRINK -> "Тільки напої";
        };
    }

    private MenuItemType fromComboValue(String value) {
        return switch (value) {
            case "Тільки страви" -> MenuItemType.FOOD;
            case "Тільки напої" -> MenuItemType.DRINK;
            default -> null;
        };
    }
}