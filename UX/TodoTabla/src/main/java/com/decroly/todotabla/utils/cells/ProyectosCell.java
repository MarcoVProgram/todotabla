package com.decroly.todotabla.utils.cells;

import com.decroly.todotabla.model.Proyecto;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;

/**
 * Celda de renderizado personalizado encargada de construir las tarjetas informativas de los objetos {@link Proyecto}.
 * <p>
 * Muestra visualmente el nombre comercial o título del espacio de trabajo junto a sus hitos temporales
 * de inicio y finalización, gestionando de manera limpia los casos en los que no se ha estipulado una fecha de cierre.
 * </p>
 *
 * @author Decroly
 * @version 1.0
 */
public class ProyectosCell extends ListCell<Proyecto> {

    /** Etiqueta que representa el título del proyecto. */
    private Label titulo;
    /** Etiqueta descriptiva para la fecha de creación u origen. */
    private Label inicio;
    /** Etiqueta descriptiva para la fecha de clausura o archivo. */
    private Label fin;
    /** Contenedor estructural basado en caja vertical para agrupar el conjunto de etiquetas. */
    private VBox card;

    /**
     * Constructor único del componente. Instancia las etiquetas de control, las encapsula jerárquicamente
     * dentro de un {@link VBox} con espaciado uniforme y prepara el diseño gráfico por defecto.
     */
    public ProyectosCell() {
        titulo = new Label();
        inicio = new Label();
        fin = new Label();
        card = new VBox(10, titulo, inicio, fin);
    }

    /**
     * Método sobreescrito del ciclo de rendering de JavaFX. Enlaza los atributos cronológicos y descriptivos
     * del proyecto actual en las etiquetas correspondientes, aplicando clases CSS específicas para la maquetación.
     *
     * @param p     El objeto {@link Proyecto} evaluado en esta iteración de celda.
     * @param empty Indica si la celda se encuentra libre de datos.
     */
    @Override
    protected void updateItem(Proyecto p, boolean empty) {
        super.updateItem(p, empty);

        if (empty || p == null) {
            setGraphic(null);
            setText(null);
            return;
        }

        titulo.setText(p.getTitulo());
        titulo.getStyleClass().add("titulo-tarea");

        inicio.setText("Fecha inicio: " + p.getFechaCreacion());
        fin.setText("Fecha fin: " + p.getFechaCierre());

        // Manejo controlado del valor nulo si el proyecto sigue operando activamente
        if (fin.getText().contentEquals("Fecha fin: " + null)) {
            fin.setText("Sin Determinar");
        }

        card.getStyleClass().add("task-card");

        setGraphic(card);
    }
}