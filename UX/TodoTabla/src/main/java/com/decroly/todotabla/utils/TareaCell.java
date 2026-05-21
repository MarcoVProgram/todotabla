package com.decroly.todotabla.utils;

import com.decroly.todotabla.model.Tarea;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.Comparator;

public class TareaCell extends ListCell<Tarea> {

    //Variables a dibujar
    private final Label titulo;
    private final Label prioLabel;
    private final Region dot;
    private final VBox card;
    private final Label ownerLabel;

    //Constructor
    public TareaCell() {

        // Titulo
        titulo = new Label();
        titulo.getStyleClass().add("titulo-tarea");
        titulo.setMaxWidth(Double.MAX_VALUE);
        titulo.setWrapText(true);
        HBox.setHgrow(titulo, Priority.ALWAYS);

        // Color Punto Prioridad
        dot = new Region();
        dot.getStyleClass().add("prioridad-dot");

        // Texto Prioridad
        prioLabel = new Label();
        prioLabel.getStyleClass().add("prioridad-label");

        HBox prioRow = new HBox(6, dot, prioLabel);
        prioRow.setAlignment(Pos.CENTER_LEFT);

        // Label Letra User
        ownerLabel = new Label("");

        // Avatar - Dibuja o toma
        StackPane avatarPane = buildAvatarPane();

        // Final de la tarea
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox bottomRow = new HBox(prioRow, spacer, avatarPane);
        bottomRow.setAlignment(Pos.CENTER);

        // Tarea
        card = new VBox(8, titulo, bottomRow);
        card.getStyleClass().add("task-card");
        card.setMaxWidth(Double.MAX_VALUE);
        card.setPrefWidth(Region.USE_COMPUTED_SIZE);

        setMaxWidth(Double.MAX_VALUE);

        // Eventos
        card.setOnMousePressed(e -> onDragStart(e));
        card.setOnMouseDragged(e -> onDragMove(e));
        card.setOnMouseReleased(e -> onDragEnd(e));
    }

    // Metodo publico para ordenar tareas para estas celdas
    public static SortedList<Tarea> sorted(ObservableList<Tarea> source) {
        return new SortedList<>(source,
                Comparator.comparingInt(t ->
                        (t.getPrioridad())
                )
        );
    }

    // Metodo de mostrar tareas correcto
    @Override
    protected void updateItem(Tarea tarea, boolean empty) {
        super.updateItem(tarea, empty);

        if (empty || tarea == null) {
            setGraphic(null);
            setText(null);
            setStyle("-fx-background-color: transparent;");
            return;
        }

        // Título
        titulo.setText(tarea.getNombre());

        // Color asociado a prioridad
        dot.setStyle("-fx-background-color: " + ColoresPrioridad.getColores(tarea.getPrioridad()) + ";");
        prioLabel.setText(tarea.getPrioridad() + "");

        // Avatar Letra
        ownerLabel.setText(tarea.getNombre().substring(0, 1).toUpperCase());

        setGraphic(card);
        setStyle("-fx-background-color: transparent;");
    }

    private StackPane buildAvatarPane() {
        Circle bg = new Circle(14);
        bg.setFill(Color.web("#1f6feb"));

         // placeholder, filled in updateItem
        ownerLabel.setStyle("-fx-text-fill: #e6edf3; -fx-font-size: 11px; -fx-font-weight: bold;");

        StackPane pane = new StackPane(bg, ownerLabel);
        pane.setPrefSize(28, 28);
        pane.setMinSize(28, 28);
        pane.setMaxSize(28, 28);

        return pane;
    }

    private void onDragStart(MouseEvent e) {
        // Agarrar tarea, snapshot, y un controlador del drag
    }

    private void onDragMove(MouseEvent e) {
        // move el fantasma, detectar columna y fila, cambiar color en base a esto
        System.out.println(e.getX() + " " + e.getY());
    }

    private void onDragEnd(MouseEvent e) {
        // realizar los updates en base al resultado
        System.out.println("onDragEnd");
    }
}
