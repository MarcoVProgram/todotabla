package com.decroly.todotabla.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Gestor utilitario estático centralizado que controla la navegación, el ruteo de archivos FXML
 * y la instanciación controlada de ventanas secundarias modales en el entorno gráfico JavaFX.
 * <p>
 * Cuenta con validaciones nativas para impedir la duplicación visual de ventanas operativas simultáneas y
 * optimiza el rendimiento reutilizando los grafos de nodos en transiciones internas del mismo contenedor.
 * </p>
 *
 * @author Decroly
 * @version 1.0
 */
public class Navigator {

    /** Escenario de persistencia único dedicado a retener y gestionar la ventana de diálogo secundaria actual. */
    private static Stage ventanaSecundaria;

    /**
     * Constructor privado estricto para impedir la inicialización de instancias físicas (Utility Class).
     */
    private Navigator() {}

    /**
     * Expone el puntero de control del escenario secundario desplegado en la interfaz.
     *
     * @return Instancia del {@link Stage} secundario actual, o {@code null} si se encuentra cerrado.
     */
    public static Stage getVentanaSecundaria() {
        return ventanaSecundaria;
    }

    /**
     * Realiza un reemplazo dinámico (conmutación) de la raíz visual de una escena existente sin destruir
     * el marco contenedor original de la aplicación, evitando parpadeos de renderizado.
     *
     * @param current Escenario base {@link Stage} sobre el cual se aplicará la inyección de la nueva interfaz.
     * @param fxml    Ruta absoluta o relativa interna hacia el recurso estructurado FXML (ej: "/views/Login.fxml").
     * @throws IOException Si ocurre una anomalía de E/S o no se encuentra el fichero del recurso descriptor del diseño.
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
     * Despliega de forma segura e independiente un nuevo cuadro de diálogo (Stage secundario) con dimensiones fijos.
     * Incorpora controles estandarizados para bloquear ejecuciones redundantes en caso de encontrarse activa una instancia previa.
     *
     * @param fxml   Ubicación del recurso gráfico FXML de la vista interna de la ventana secundaria.
     * @param title  Cadena de texto que definirá el título formal de la barra del sistema de la ventana.
     * @param clase  Clase contextual de referencia (Class) utilizada para resolver adecuadamente el ClassLoader del FXML.
     * @throws IOException Si la lectura del recurso FXML falla o el mapa de nodos no se puede consolidar.
     */
    public static void arbrirVentanaSecundaria(String fxml, String title, Class<?> clase) throws IOException {
        // Validación contra dobles instancias operativas
        if (ventanaSecundaria != null && ventanaSecundaria.isShowing()) {
            Notificator.advertencia("Sesión no válida", "No se puede volver a abrir, hay una sesión existente");
            System.out.println("No se puede volver a abrir, hay una sesion existente");
            return;
        }

        // Proceso estándar de instanciación guiada del módulo FXML
        FXMLLoader loader = new FXMLLoader(clase.getResource(fxml));
        Parent root = loader.load();

        ventanaSecundaria = new Stage();
        ventanaSecundaria.setTitle(title);
        ventanaSecundaria.setScene(new Scene(root));

        ventanaSecundaria.setResizable(false);
        ventanaSecundaria.setAlwaysOnTop(false);

        // Despliegue en pantalla
        ventanaSecundaria.show();
    }
}