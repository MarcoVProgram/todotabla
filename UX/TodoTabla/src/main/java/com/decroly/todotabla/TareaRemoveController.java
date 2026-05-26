package com.decroly.todotabla;

import com.decroly.todotabla.model.Tarea;
import com.decroly.todotabla.model.sql.TareasBDD;
import com.decroly.todotabla.utils.EstadoPrograma;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.Map;

public class TareaRemoveController {

    @FXML
    public ListView<Tarea> listViewTareas;
    private ObservableList<Tarea> listaTareas;

    private void listarTareas() {
        listaTareas = FXCollections.observableList(
                new ArrayList<>(TareasBDD.getTareas(
                        EstadoPrograma.getInstance().getProyectoActivo()
                ).values())
        );

        listViewTareas.setItems(listaTareas);
        listViewTareas.setCellFactory(listaTareas ->  new ListCell<Tarea>() {
            @Override
            protected void updateItem(Tarea tarea, boolean empty) {
                super.updateItem(tarea, empty);

                if (empty || tarea == null) {
                    setGraphic(null);
                } else {

                    Label titulo = new Label(tarea.getNombre());
                    titulo.getStyleClass().add("titulo-tarea");

                    VBox card = new VBox(8, titulo);
                    card.getStyleClass().add("kanban-list");

                    setGraphic(card);
                }
            }
        });

    }

    private void actualizarTareas() {
        Map<Integer, Tarea> todasTareasDelUProyecto = TareasBDD.getTareas(
                EstadoPrograma.getInstance().getProyectoActivo()
        );

        if (todasTareasDelUProyecto != null) {
            listaTareas.addAll((TareasBDD.getTareas()).values());
            listViewTareas.refresh();
        }

    }

    @FXML
    public void removeTarea(ActionEvent event) {
        boolean estado = false;
        ObservableList<Tarea> listaDeTareas = listViewTareas.getSelectionModel().getSelectedItems();

        (new Alert(Alert.AlertType.INFORMATION,
                "Se borro correctamente",
                ButtonType.OK
        )).show();

        for (Tarea t: listaDeTareas) {
            estado = TareasBDD.borrar(t);
        }

        if (estado) {
            (new Alert(Alert.AlertType.INFORMATION,
                    "Se borro correctamente",
                    ButtonType.OK
            )).show();
            actualizarTareas();
        } else {
            (new Alert(Alert.AlertType.WARNING,
                    "No se ha podido borrar",
                    ButtonType.OK
            )).showAndWait();
        }
    }
}
