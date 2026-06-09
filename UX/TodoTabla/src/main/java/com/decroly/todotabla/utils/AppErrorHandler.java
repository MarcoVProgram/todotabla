package com.decroly.todotabla.utils;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Unidad de interceptación y tratamiento global de excepciones no capturadas del ecosistema corporativo.
 * <p>
 * Su rol principal abarca la bifurcación asíncrona del flujo del error: realiza el volcado técnico detallado
 * (Stack Trace) en el sistema de almacenamiento físico de bitácoras SLF4J (Loggers), y paralelamente,
 * renderiza un cuadro de diálogo adaptado para el entendimiento inmediato del operario.
 * </p>
 * <p>Garantiza la estabilidad de la ejecución previniendo el congelamiento abrupto de las vistas del cliente.</p>
 *
 * @author Decroly
 * @version 1.0
 */
public class AppErrorHandler {

    /** Motor de registro cronológico (Logger) unificado por la firma de clase de la librería SLF4J. */
    private static final Logger logger = LoggerFactory.getLogger(AppErrorHandler.class.getName());

    /**
     * Constructor privado para mitigar e impedir cualquier tipo de instanciación no programada (Utility Class).
     */
    private AppErrorHandler() {}

    /**
     * Procesa, registra en logs y presenta visualmente una excepción capturada en cualquier sección crítica del programa.
     * <p>
     * Se encarga de evaluar si la traza proviene de un hilo asíncrono o de transacciones secundarias de la base de datos,
     * reconduciendo de forma segura el despliegue del componente gráfico mediante {@link Platform#runLater(Runnable)}.
     * </p>
     *
     * @param ex              Objeto raíz de la excepción capturada (contiene la causa original y la traza detallada).
     * @param metodoCulpable  Firma textual del método o procedimiento en el que se gatilló el error (facilita la depuración).
     */
    public static void manejar(Exception ex, String metodoCulpable) {
        // Almacenamiento inmediato y estructurado en la bitácora física (Archivos .log)
        logger.error("Error crítico interceptado en el método: {}", metodoCulpable, ex);

        // Bloque funcional encargado de la construcción de la alerta nativa FX
        Runnable showAlert = () -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);

            try {
                alert.getDialogPane().getStylesheets().add(
                        AppErrorHandler.class.getResource("/com/decroly/todotabla/style.css").toExternalForm()
                );
                alert.getDialogPane().getStyleClass().add("error-alert");
            } catch (Exception cssEx) {
                logger.warn("No se pudo asociar la hoja de estilos css en la ventana de control de errores.");
            }

            alert.setTitle("Error Interno de Aplicación");
            alert.setHeaderText("Fallo detectado en: " + metodoCulpable);

            // Tratamiento seguro de cadenas de error vacías o nulas
            String mensajeDetalle = (ex != null && ex.getMessage() != null) ? ex.getMessage() : "Causa raíz no identificada.";
            alert.setContentText(mensajeDetalle);

            alert.showAndWait();
        };

        // Enrutamiento seguro multihilo hacia el despachador de la UI de JavaFX
        if (Platform.isFxApplicationThread()) {
            showAlert.run();
        } else {
            Platform.runLater(showAlert);
        }
    }
}