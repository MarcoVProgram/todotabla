package com.decroly.todotabla.utils.cells;

import com.decroly.todotabla.model.Proyecto;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;

/**
 * Celda de lista que representa un {@link Proyecto}.
 * Muestra el título del proyecto y sus fechas de inicio y cierre.
 */
public class ProyectosCell extends ListCell<Proyecto> {

    Label titulo, inicio, fin;
    VBox card;

    /**
     * Crea la celda e inicializa los componentes visuales.
     */
    public ProyectosCell() {
        titulo = new Label();
        inicio = new Label();
        fin = new Label();
        card = new VBox(10, titulo, inicio, fin);
    }

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

        if(fin.getText().contentEquals("Fecha fin: " + null)){
            fin.setText("Sin Determinar");
        }
        card.getStyleClass().add("task-card");

        setGraphic(card);
    }
}
