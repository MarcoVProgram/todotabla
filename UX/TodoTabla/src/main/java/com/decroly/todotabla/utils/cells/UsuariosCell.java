package com.decroly.todotabla.utils.cells;

import com.decroly.todotabla.model.Usuario;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;

import java.util.Map;

/**
 * Celda de lista que representa un {@link Usuario}.
 * Opcionalmente muestra información adicional por usuario a través de un mapa de etiquetas externas.
 */
public class UsuariosCell extends ListCell<Usuario> {

    private VBox card;
    private Label titulo;
    private Map<Integer, Label> additionalData;
    private Label extraInfo;

    /**
     * Crea una celda básica que muestra únicamente el nombre del usuario.
     */
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

    /**
     * Crea una celda que muestra el nombre del usuario junto con una etiqueta adicional
     * obtenida del mapa proporcionado.
     *
     * @param additionalData mapa de ID de usuario a {@link Label} con información extra a mostrar
     */
    public UsuariosCell(Map<Integer, Label> additionalData) {
        this.card = new VBox(8);
        this.titulo = new Label();
        this.additionalData = additionalData;

        this.card.getChildren().add(this.titulo);

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

        if (this.additionalData != null) {
            this.extraInfo = this.additionalData.get(getItem().getId());
            if (this.extraInfo != null){
                if(!this.card.getChildren().contains(this.extraInfo)) {
                    this.card.getChildren().add(this.extraInfo);
                }

                this.extraInfo.setStyle("-fx-text-fill: lightgray; -fx-font-size: 12px");
            }
        }

        this.titulo.getStyleClass().add("user-name");
        this.card.getStyleClass().add("user-card");
        setStyle("-fx-background-color: transparent;");
        setGraphic(this.card);
    }
}
