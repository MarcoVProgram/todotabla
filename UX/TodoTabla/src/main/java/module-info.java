module com.decroly.todotabla {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires jdk.jdi;
    requires java.desktop;
    requires org.slf4j;


    opens com.decroly.todotabla to javafx.fxml;
    exports com.decroly.todotabla;
    exports com.decroly.todotabla.model;
    exports com.decroly.todotabla.utils;
    exports com.decroly.todotabla.utils.constants;
}