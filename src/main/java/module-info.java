module com.sorokaandriy.cafesimulation {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires commons.math3;

    opens com.sorokaandriy.cafesimulation to javafx.fxml;
    exports com.sorokaandriy.cafesimulation;
}