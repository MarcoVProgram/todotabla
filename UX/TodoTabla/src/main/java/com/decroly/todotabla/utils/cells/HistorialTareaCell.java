package com.decroly.todotabla.utils.cells;

import com.decroly.todotabla.model.HistorialTareas;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

import java.time.format.DateTimeFormatter;

/**
 * Celda gráfica diseñada con propósitos de auditoría visual para renderizar trazas históricas de {@link HistorialTareas}.
 * <p>
 * Imprime con precisión de segundos las transiciones horarias de los flujos de las tarjetas e incorpora un
 * indicador cromático en forma de círculo relleno con el color hexadecimal representativo del estado Kanban de destino.
 * </p>
 *
 * @author Decroly
 * @version 1.0
 */
public class HistorialTareaCell extends ListCell<HistorialTareas> {

    /** Contenedor raíz vertical de la celda de logs. */
    private VBox card;
    /** Etiqueta que expone el instante temporal formateado de la transición. */
    private Label titulo;
    /** Contenedor horizontal que alinea de forma adyacente la viñeta de color y la descripción del estado. */
    private HBox hbox;
    /** Nodo geométrico circular que actúa como badge cromático representativo del estado Kanban. */
    private Circle circle;
    /** Etiqueta descriptiva con el nombre de la columna Kanban alcanzada. */
    private Label estado;

    /**
     * Construye e integra los elementos del árbol de nodos de la tarjeta de auditoría.
     * Añade selectores de eventos reactivos vinculados al foco dinámico del puntero del ratón en la UI.
     */
    public HistorialTareaCell() {
        this.card = new VBox(8);
        this.titulo = new Label();
        this.hbox = new HBox(2);
        this.circle = new Circle(4);
        this.estado = new Label();

        this.card.getChildren().add(this.titulo);
        this.card.getChildren().add(this.hbox);
        this.hbox.getChildren().add(this.circle);
        this.hbox.getChildren().add(this.estado);

        selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
            if (isNowSelected) {
                this.card.getStyleClass().setAll("user-card-selected");
            } else {
                this.card.getStyleClass().setAll("user-card");
            }
        });
    }

    /**
     * Traduce los eventos de auditoría cronológica del sistema en un formato de lectura humana estandarizado (dd/MM/yyyy - HH:mm:ss).
     * Modifica de manera dinámica las directivas del motor CSS subyacente para inyectar el color exclusivo de la fase Kanban.
     *
     * @param h     La instancia de la traza de auditoría de la tarea {@link HistorialTareas}.
     * @param empty Estado de vaciado físico de la celda en la rejilla gráfica.
     */
    @Override
    protected void updateItem(HistorialTareas h, boolean empty) {
        super.updateItem(h, empty);

        if (empty || h == null) {
            setGraphic(null);
            setText(null);
            return;
        }

        this.titulo.setText("Cambio: " + h.getFechaCambio().format(DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss")));
        this.circle.setStyle("-fx-fill: " + h.getEstado().getColor() + ";");
        this.estado.setText(h.getEstado().getNombre());

        setGraphic(card);
    }
}