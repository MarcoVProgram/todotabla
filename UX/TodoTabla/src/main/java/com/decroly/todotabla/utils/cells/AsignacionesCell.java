package com.decroly.todotabla.utils.cells;

import com.decroly.todotabla.model.Asignacion;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;

import java.time.format.DateTimeFormatter;
import java.util.Map;

public class AsignacionesCell extends ListCell<Asignacion> {

    private VBox card;
    private Label titulo;
    private Label extraInfo;

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

        this.extraInfo.setStyle("-fx-text-fill: lightgray; -fx-font-size: 12px");

        this.titulo.getStyleClass().add("user-name");
        this.card.getStyleClass().add("user-card");
        setStyle("-fx-background-color: transparent;");
        setGraphic(this.card);

    }
}
