package com.decroly.todotabla.utils.cells;

import com.decroly.todotabla.model.Usuario;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;

import java.util.Map;

/**
 * Celda personalizada para la representación visual de entidades {@link Usuario} dentro de un JavaFX {@link javafx.scene.control.ListView}.
 * <p>
 * Diseñada para soportar vistas de tarjetas dinámicas, permite inyectar información complementaria en tiempo de ejecución
 * mediante mapas de datos adicionales (por ejemplo, el rol o el estado de asignación de ese usuario en un contexto específico).
 * </p>
 *
 * @author Decroly
 * @version 1.0
 */
public class UsuariosCell extends ListCell<Usuario> {

    /** Contenedor vertical principal que organiza la estructura visual de la tarjeta del usuario. */
    private VBox card;
    /** Etiqueta de texto destinada a mostrar el nombre del usuario. */
    private Label titulo;
    /** Diccionario opcional indexado por ID de usuario que contiene etiquetas de información secundaria o métricas. */
    private Map<Integer, Label> additionalData;
    /** Almacena transitoriamente la etiqueta de información complementaria asignada a la instancia actual del ítem. */
    private Label extraInfo;

    /**
     * Constructor base por defecto. Inicializa los componentes visuales mínimos de la tarjeta
     * e implementa los escuchadores (listeners) de selección para alternar los estilos CSS de JavaFX.
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
     * Constructor avanzado que permite vincular un mapa de metadatos adicionales a los usuarios.
     * Útil para añadir información dependiente del contexto sin acoplar el POJO original de dominio.
     *
     * @param additionalData Mapa cuyas claves son los IDs de los usuarios y los valores son objetos {@link Label} formateados.
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

    /**
     * Ciclo de vida interno de JavaFX. Se ejecuta de forma automática para actualizar o limpiar el contenido
     * de la celda en función del reciclaje de nodos de la lista.
     *
     * @param u     La instancia del {@link Usuario} asignada a la posición de la celda.
     * @param empty Bandera booleana que indica si la celda actual representa un slot vacío en la lista.
     */
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
            if (this.extraInfo != null) {
                if (!this.card.getChildren().contains(this.extraInfo)) {
                    this.card.getChildren().add(this.extraInfo);
                }
                this.extraInfo.setStyle("-fx-text-fill: lightgray; -fx-font-size: 12px");
            }
        }

        setGraphic(card);
    }
}