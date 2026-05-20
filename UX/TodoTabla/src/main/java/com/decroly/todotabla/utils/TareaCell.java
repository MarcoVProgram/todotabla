package com.decroly.todotabla.utils;

import com.decroly.todotabla.HelloApplication;
import com.decroly.todotabla.model.Tarea;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.io.InputStream;
import java.util.Comparator;

public class TareaCell extends ListCell<Tarea> {

    // Color en base a prioridad
    private static String prioridadColor(int prioridad) {
        return switch (prioridad) {
            case 0  -> "#f85149";
            case 1 -> "#e3b341";
            case 2  -> "#3fb950";
            default      -> "#8b949e";
        };
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
        Label titulo = new Label(tarea.getNombre());
        titulo.getStyleClass().add("titulo-tarea");
        titulo.setMaxWidth(180);

        // Orden de prioridad y su color asociado
        Region dot = new Region();
        dot.getStyleClass().add("prioridad-dot");
        dot.setStyle("-fx-background-color: " + prioridadColor(tarea.getPrioridad()) + ";");

        Label prioLabel = new Label(tarea.getPrioridad() + "");
        prioLabel.getStyleClass().add("prioridad-label");

        HBox prioRow = new HBox(6, dot, prioLabel);
        prioRow.setAlignment(Pos.CENTER_LEFT);

        // Imagen Avatar
        StackPane avatarPane;
        try (InputStream stream = HelloApplication.class
            .getResourceAsStream("/com/decroly/todotabla/images/user.png")) {

            Image img = new Image(stream);

            ImageView avatar = new ImageView(img);
            avatar.setFitWidth(28);
            avatar.setFitHeight(28);
            avatar.setPreserveRatio(true);
            avatar.setSmooth(true);

            Circle clip = new Circle(14, 14, 14);
            avatar.setClip(clip);

            avatarPane = new StackPane(avatar);

        } catch (Exception e) {

            // Fallback — initials circle
            String initial = tarea.getNombre().substring(0, 1).toUpperCase();
            Label initLabel = new Label(initial);
            initLabel.setStyle("-fx-text-fill: #e6edf3; -fx-font-size: 11px; -fx-font-weight: bold;");
            Circle bg = new Circle(14);
            bg.setFill(Color.web("#1f6feb"));
            avatarPane = new StackPane(bg, initLabel);
        }

        avatarPane.setPrefSize(28, 28);
        avatarPane.setMinSize(28, 28);
        avatarPane.setMaxSize(28, 28);

        // Fila de abajo, prioridad y avatar
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox bottomRow = new HBox(prioRow, spacer, avatarPane);
        bottomRow.setAlignment(Pos.CENTER);

        // Union del código
        VBox card = new VBox(8, titulo, bottomRow);
        card.getStyleClass().add("task-card");

        setGraphic(card);
        setStyle("-fx-background-color: transparent;");
    }
}
