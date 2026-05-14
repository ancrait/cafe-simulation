module com.sorokaandriy.cafesimulation {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;

    opens com.sorokaandriy.cafesimulation to javafx.fxml;
    exports com.sorokaandriy.cafesimulation;
}