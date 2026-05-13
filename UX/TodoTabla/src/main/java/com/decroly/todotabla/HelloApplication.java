package com.decroly.todotabla;

import com.decroly.todotabla.model.sql.BDD;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
//        WindowWatcher.init(); //iniciar clase que comprueba ventanas abiertas
        
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1500, 800);
        stage.setTitle("Bienvenido a TodoTabla!");
        stage.setScene(scene);

        stage.setResizable(false);//hacer que no se pueda cambiar tamaño de la ventana
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}