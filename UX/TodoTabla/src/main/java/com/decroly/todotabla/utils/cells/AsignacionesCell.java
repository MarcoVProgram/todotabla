package com.decroly.todotabla.utils.cells;

import com.decroly.todotabla.model.Asignacion;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;

import java.time.format.DateTimeFormatter;

/**
 * Componente gráfico especializado en representar el nexo asociativo de una {@link Asignacion} en la vista de lista.
 * <p>
 * Traduce visualmente la carga de trabajo de una tarea reflejando qué usuario tiene encomendada la labor
 * y computando la ventana de tiempo (duración en fechas ISO locales) desde que la asumió hasta su resolución.
 * </p>
 *
 * @author Decroly
 * @version 1.0
 */
public class AsignacionesCell extends ListCell<Asignacion> {

    /** Contenedor vertical de organización para los elementos del nodo gráfico. */
    private VBox card;
    /** Etiqueta superior que expone la identidad del operador asignado. */
    private Label titulo;
    /** Etiqueta secundaria adaptativa que describe los rangos de fecha de la asignación. */
    private Label extraInfo;

    /**
     * Constructor por defecto del renderizador. Instancia los sub-nodos del contenedor,
     * establece un margen interno de separación e inyecta un escuchador reactivo para conmutar
     * los estilos CSS de foco/selección del usuario.
     */
    public AsignacionesCell() {
        this.card = new VBox(8);
        this.titulo = new Label();
        this.extraInfo = new Label();

        this.card.getChildren().add(this.titulo);
        this.card.getChildren().add(this.extraInfo);

        selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
            if (isNowSelected) {
                this.card.getStyleClass().setAll("user-card-selected");
            } else {
                this.card.getStyleClass().setAll("user-card");
            }
        });
    }

    /**
     * Ejecuta el mapeo y formateo de datos de negocio a primitivas de control visual de JavaFX.
     * Parsea las fechas utilizando patrones estándares e inyecta estilos en línea para el manejo de fuentes secundarias.
     *
     * @param a     La entidad {@link Asignacion} mapeada en el índice de lista actual.
     * @param empty Bandera que evalúa si el nodo contenedor carece de datos válidos.
     */
    @Override
    protected void updateItem(Asignacion a, boolean empty) {
        super.updateItem(a, empty);

        if (empty || a == null) {
            setGraphic(null);
            setText(null);
            return;
        }

        this.titulo.setText(a.getIdUsuario().getNombre());

        String duracion = "Desde " + a.getFechaAsignacion().format(DateTimeFormatter.ISO_LOCAL_DATE) + " hasta ";
        if (a.getFechaFin() != null) {
            duracion += a.getFechaFin().format(DateTimeFormatter.ISO_LOCAL_DATE);
        } else {
            duracion += "hoy";
        }
        this.extraInfo.setText(duracion);
        this.extraInfo.setStyle("-fx-text-fill: lightgray; -fx-font-size: 11px;");

        setGraphic(card);
    }
}