package com.decroly.todotabla.utils.cells;

import com.decroly.todotabla.model.HistorialTareas;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

import java.time.format.DateTimeFormatter;

public class HistorialTareaCell extends ListCell<HistorialTareas> {

    private VBox card;
    private Label titulo;
    private HBox hbox;
    private Circle circle;
    private Label estado;


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

    @Override
    protected void updateItem(HistorialTareas h, boolean empty) {
        super.updateItem(h, empty);

        if (empty || h == null) {
            setGraphic(null);
            setText(null);
            return;
        }

        this.titulo.setText("Change: " + h.getFechaCambio().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        this.circle.setStyle("-fx-fill: " + h.getEstado().getColor()+";");
        this.estado.setText(h.getEstado().getNombre());

        this.titulo.getStyleClass().add("titulo-tarea");
        this.card.getStyleClass().add("user-card");
        this.estado.setStyle("-fx-text-fill: lightgray;");

        setStyle("-fx-background-color: transparent;");
        setGraphic(this.card);
    }
}
