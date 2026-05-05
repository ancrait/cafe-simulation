module com.sorokaandriy.cafesimulation {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires eu.hansolo.tilesfx;

    opens com.sorokaandriy.cafesimulation to javafx.fxml;
    exports com.sorokaandriy.cafesimulation;
}