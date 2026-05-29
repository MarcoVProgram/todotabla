package com.decroly.todotabla;

import com.decroly.todotabla.model.Tarea;
import com.decroly.todotabla.model.sql.TareasBDD;
import com.decroly.todotabla.utils.AppErrorHandler;
import com.decroly.todotabla.utils.EstadoPrograma;
import com.decroly.todotabla.utils.Notificator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.ResourceBundle;

public class TareaRemoveController implements Initializable {

    @FXML
    public ListView<Tarea> listViewTareas;
    private ObservableList<Tarea> listaTareas;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        listarTareas();
    }

    private void listarTareas() {
        try {
            listaTareas = FXCollections.observableList(
                    new ArrayList<>(TareasBDD.getTareas(
                            EstadoPrograma.getInstance().getProyectoActivo()
                    ).values())
            );
        } catch (Exception e) {
            AppErrorHandler.manejar(e, "getTareas");
        }

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

        listViewTareas.setItems(listaTareas);
    }

    private void actualizarTareas() {
        Map<Integer, Tarea> todasTareasDelProyecto;
        try {
            todasTareasDelProyecto = TareasBDD.getTareas(
                    EstadoPrograma.getInstance().getProyectoActivo()
            );
        } catch (Exception e) {
            AppErrorHandler.manejar(e, "getTareas");
            todasTareasDelProyecto = null;
        }

        if (todasTareasDelProyecto != null) {
            listaTareas.addAll(todasTareasDelProyecto.values());
            listViewTareas.refresh();
        }

    }

    @FXML
    public void removeTarea(ActionEvent event) {
        boolean estado = false;
        ObservableList<Tarea> listaDeTareas = listViewTareas.getSelectionModel().getSelectedItems();

        for (Tarea t: listaDeTareas) {
            try {
                estado = TareasBDD.borrar(t);
            } catch (Exception e) {
                AppErrorHandler.manejar(e, "borrarTareas");
            }
        }

        if (estado) {
            Notificator.informar("Borrar Tarea", "Se ha borrado correctamente");
            actualizarTareas();
        } else {
            Notificator.advertencia("Borrar Tarea", "No se ha podido borrar");
        }
    }

}
