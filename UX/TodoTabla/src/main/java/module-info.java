module com.decroly.todotabla {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires jdk.jdi;


    opens com.decroly.todotabla to javafx.fxml;
    exports com.decroly.todotabla;
    exports com.decroly.todotabla.model;
}