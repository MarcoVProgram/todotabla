package com.decroly.todotabla.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Gestiona la navegación entre escenas y ventanas secundarias de la aplicación.
 * Garantiza que solo exista una ventana secundaria abierta al mismo tiempo.
 */
public class Navigator {

    private static Stage ventanaSecundaria;

    private static Stage getVentanaSecundaria() {
        return ventanaSecundaria;
    }

    /**
     * Sustituye el contenido de la escena actual por el de un nuevo FXML.
     * Si el {@link Stage} no tiene escena asignada, crea una nueva.
     *
     * @param current el {@link Stage} cuya escena se va a reemplazar
     * @param fxml    la ruta al fichero FXML a cargar
     * @throws IOException si el fichero FXML no se encuentra o no puede cargarse
     */
    public static void changeScene(Stage current, String fxml) throws IOException {

        FXMLLoader loader = new FXMLLoader(Navigator.class.getResource(fxml));
        Parent root = loader.load();

        Scene scene = current.getScene();

        if (scene == null) {
            current.setScene(new Scene(root));
        } else {
            scene.setRoot(root);
        }
    }

    /**
     * Abre una ventana secundaria modal con el FXML indicado.
     * Si ya hay una ventana secundaria abierta, muestra un aviso y cancela la operación.
     *
     * @param fxml  la ruta al fichero FXML a cargar
     * @param title el título de la nueva ventana
     * @param clase la clase desde la que se resuelve el recurso FXML
     * @throws IOException si el fichero FXML no se encuentra o no puede cargarse
     */
    public static void arbrirVentanaSecundaria(String fxml, String title, Class clase) throws IOException {
        if(ventanaSecundaria != null && ventanaSecundaria.isShowing()){
            Notificator.advertencia("Sesión no válida", "No se puede volver a abrir, hay una sesión existente");
            System.out.println("No se puede volver a abrir, hay una sesion existente");
            return;
        }

        FXMLLoader loader = new FXMLLoader(clase.getResource(fxml));
        Parent root = loader.load();

        ventanaSecundaria = new Stage();
        ventanaSecundaria.setTitle(title);
        ventanaSecundaria.setScene(new Scene(root));

        ventanaSecundaria.setResizable(false);
        ventanaSecundaria.setAlwaysOnTop(false);

        ventanaSecundaria.showAndWait();
    }
}
