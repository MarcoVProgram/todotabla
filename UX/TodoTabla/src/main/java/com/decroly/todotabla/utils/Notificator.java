package com.decroly.todotabla.utils;

import javafx.application.Platform;
import javafx.scene.control.Alert;

/**
 * Motor utilitario centralizado de alertas gráficas sincronizadas encargado de notificar
 * incidencias, advertencias y confirmaciones de éxito en tiempo real al usuario final.
 * <p>
 * Implementa de forma mandatoria un filtro defensivo multihilo: verifica si la llamada se ejecuta dentro del
 * Hilo de la Aplicación de JavaFX (JavaFX Application Thread). De no ser así, delega el procesamiento asíncronamente
 * mediante {@link Platform#runLater(Runnable)} para evitar excepciones críticas de concurrencia gráfica.
 * </p>
 * <p>Todas las alertas se vinculan automáticamente al patrón estético de hojas de estilo corporativas {@code style.css}.</p>
 *
 * @author Decroly
 * @version 1.0
 */
public class Notificator {

    /**
     * Constructor privado diseñado explícitamente para deshabilitar la instanciación pública de esta clase utilitaria.
     */
    private Notificator() {}

    /**
     * Genera un cuadro de diálogo informativo estándar en pantalla de tipo {@link javafx.scene.control.Alert.AlertType#INFORMATION}.
     *
     * @param cabecera Texto de énfasis intermedio que detalla el evento sucedido.
     * @param mensage  Cuerpo o descripción analítica pormenorizada del aviso.
     */
    public static void informar(String cabecera, String mensage) {
        Runnable showAlert = () -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            inyectarEstilo(alert, "inform-alert");

            alert.setTitle("Información");
            alert.setHeaderText(cabecera);
            alert.setContentText(mensage);
            alert.showAndWait();
        };

        ejecutarEnHiloGrafico(showAlert);
    }

    /**
     * Muestra una ventana de notificación con formato estilizado de Éxito para hitos completados de forma idónea.
     *
     * @param cabecera Breve resumen aclaratorio de la transacción exitosa.
     * @param mensage  Cuerpo con el desglose del identificador o cambios aplicados de forma persistente.
     */
    public static void exito(String cabecera, String mensage) {
        Runnable showAlert = () -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            inyectarEstilo(alert, "success-alert");

            alert.setTitle("Éxito");
            alert.setHeaderText(cabecera);
            alert.setContentText(mensage);
            alert.showAndWait();
        };

        ejecutarEnHiloGrafico(showAlert);
    }

    /**
     * Dispara una advertencia en pantalla de severidad intermedia utilizando el tipo {@link javafx.scene.control.Alert.AlertType#WARNING}
     * para advertir sobre flujos bloqueados o entradas de datos no conformes.
     *
     * @param cabecera Mensaje directo de restricción o fallo de parámetros.
     * @param mensage  Texto extendido que detalla la acción correctiva esperada del operador.
     */
    public static void advertencia(String cabecera, String mensage) {
        Runnable showAlert = () -> {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            inyectarEstilo(alert, "warning-alert");

            alert.setTitle("Aviso");
            alert.setHeaderText(cabecera);
            alert.setContentText(mensage);
            alert.showAndWait();
        };

        ejecutarEnHiloGrafico(showAlert);
    }

    /**
     * Renderiza un aviso emergente crítico de tipo {@link javafx.scene.control.Alert.AlertType#ERROR}.
     * Se utiliza para alertar al usuario de problemas serios internos o interrupciones graves de lógica de negocio.
     *
     * @param cabecera Identificador rápido de la zona de falla o proceso interrumpido.
     * @param mensage  Desglose literal legible de la excepción interceptada.
     */
    public static void error(String cabecera, String mensage) {
        Runnable showAlert = () -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            inyectarEstilo(alert, "error-alert");

            alert.setTitle("Error Técnico");
            alert.setHeaderText(cabecera);
            alert.setContentText(mensage);
            alert.showAndWait();
        };

        ejecutarEnHiloGrafico(showAlert);
    }

    /**
     * Modifica el árbol de nodos de la alerta agregando la hoja de estilos compartida del programa y su clase CSS correspondiente.
     */
    private static void inyectarEstilo(Alert alert, String claseCss) {
        try {
            alert.getDialogPane().getStylesheets().add(
                    Notificator.class.getResource("/com/decroly/todotabla/style.css").toExternalForm()
            );
            alert.getDialogPane().getStyleClass().add(claseCss);
        } catch (Exception e) {
            System.err.println("Advertencia: No se pudo cargar la hoja de estilos style.css en el cuadro de diálogo.");
        }
    }

    /**
     * Valida de forma interna el contexto de ejecución del hilo concurrente y rutea la tarea al procesador de JavaFX si es necesario.
     */
    private static void ejecutarEnHiloGrafico(Runnable tareaAlerta) {
        if (Platform.isFxApplicationThread()) {
            tareaAlerta.run();
        } else {
            Platform.runLater(tareaAlerta);
        }
    }
}