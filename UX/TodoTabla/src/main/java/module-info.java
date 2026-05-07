module com.decroly.todotabla {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.decroly.todotabla to javafx.fxml;
    exports com.decroly.todotabla;
}