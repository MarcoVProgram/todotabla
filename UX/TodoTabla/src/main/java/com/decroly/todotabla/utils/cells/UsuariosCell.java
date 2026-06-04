package com.decroly.todotabla.utils.cells;

import com.decroly.todotabla.model.Usuario;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;

import java.util.Map;

public class UsuariosCell extends ListCell<Usuario> {

    private VBox card;
    private Label titulo;
    private Map<Integer, Label> additionalData;

    public UsuariosCell() {
        this.card = new VBox(8);
        this.titulo = new Label();

        this.card.getChildren().add(this.titulo);

        selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
            if (isNowSelected) {
                this.card.getStyleClass().setAll("user-card-selected");
            } else {
                this.card.getStyleClass().setAll("user-card");
            }
        });
    }

    public UsuariosCell(Map<Integer, Label> additionalData) {
        this.card = new VBox(8);
        this.titulo = new Label();
        this.additionalData = additionalData;

        this.card.getChildren().add(this.titulo);
        if (getItem() != null) {
            this.card.getChildren().add(this.additionalData.get(getItem().getId()));
        }

        selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
            if (isNowSelected) {
                this.card.getStyleClass().setAll("user-card-selected");
            } else {
                this.card.getStyleClass().setAll("user-card");
            }
        });
    }

    @Override
    protected void updateItem(Usuario u, boolean empty) {
        super.updateItem(u, empty);

        if (empty || u == null) {
            setGraphic(null);
            setText(null);
            return;
        }

        this.titulo.setText(u.getNombre());
        setGraphic(this.card);

        if (this.additionalData != null) {
            additionalData.get(getItem().getId()).setText(u.getApellidos() + ", " + u.getNombre());
        }

        this.titulo.getStyleClass().add("user-name");
        this.card.getStyleClass().add("user-card");
        setStyle("-fx-background-color: transparent;");
    }
}
