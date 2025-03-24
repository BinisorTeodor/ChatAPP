module com.example.aplicatie {
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.j;
    requires java.naming;
    requires javafx.controls;

    opens com.example.aplicatie to javafx.fxml;
    exports com.example.aplicatie;
}