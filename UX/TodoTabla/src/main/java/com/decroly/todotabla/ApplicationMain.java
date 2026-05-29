package com.decroly.todotabla;

import com.decroly.todotabla.model.sql.BDD;
import com.decroly.todotabla.utils.AppErrorHandler;
import com.decroly.todotabla.utils.Notificator;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.SQLException;

public class ApplicationMain extends Application {
    @Override
    public void start(Stage stage) throws IOException {
//        WindowWatcher.init(); //iniciar clase que comprueba ventanas abiertas
        if (launchTest()) return;

        FXMLLoader fxmlLoader = new FXMLLoader(ApplicationMain.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1500,800);
        stage.setTitle("Bienvenido a TodoTabla!");
        stage.setScene(scene);

        if(stage.isFocused()){
            stage.setAlwaysOnTop(true);
        }else{
            stage.setAlwaysOnTop(false);
        }

        stage.setResizable(false);//hacer que no se pueda cambiar tamaño de la ventana
        stage.show();
    }

    private boolean launchTest() {
        boolean invalid = false;
        try {
            /*Notificator.advertencia("Hola", "Mundo");
            Notificator.error("Hola", "Mundo");
            Notificator.exito("Hola", "Mundo");
            Notificator.informar("Hola", "Mundo");*/
            Connection x = BDD.getConnection();
            x.close();
        } catch (SQLException e) {
            AppErrorHandler.manejar(e, "getConnection (Syntax SQL)");
            invalid = true;
        } catch (IOException e) {
            AppErrorHandler.manejar(e, "getConnection (IO)");
            invalid = true;
        } catch (URISyntaxException e) {
            AppErrorHandler.manejar(e, "getConnection (URI)");
            invalid = true;
        } catch (ClassNotFoundException e) {
            AppErrorHandler.manejar(e, "getConnection (ClassNotFound)");
            invalid = true;
        }
        if (invalid) {
            exitProgram();
            return true;
        }
        return false;
    }

    private void exitProgram() {
        Platform.exit();
    }

    public static void main(String[] args) {
        launch();
    }
}