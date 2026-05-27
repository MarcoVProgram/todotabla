package com.decroly.todotabla;

import com.decroly.todotabla.model.Tarea;
import com.decroly.todotabla.model.sql.TareasBDD;
import com.decroly.todotabla.utils.EstadoPrograma;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;

public class TareaModController implements Initializable {

    @FXML
    public TextField nombreTareaFormEditar;

    @FXML
    public Spinner<Integer> prioridadTareaFormCrear;

    @FXML
    public ListView<Tarea> listViewTareas;
    private ObservableList<Tarea> listaTareas;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        listarTareas();

    }

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
        Map<Integer, Tarea> todasTareasDelProyecto = TareasBDD.getTareas(
                EstadoPrograma.getInstance().getProyectoActivo()
        );

        if (todasTareasDelProyecto != null) {
            listaTareas.addAll(todasTareasDelProyecto.values());
            listViewTareas.refresh();
        }

    }

    // TODO No se como cambiar la asignacion sin meter muchas cosas en la ventana
    public void modTarea(ActionEvent event) {
        boolean actualizarExito = true;

        //obtener valores campos
        String nombre = nombreTareaFormEditar.getText();
        int prioridad = prioridadTareaFormCrear.getValue();

        ObservableList<Tarea> listaDeTareas = listViewTareas.getSelectionModel().getSelectedItems();

        for (Tarea tarea : listaDeTareas) {
            tarea.setNombre(nombre);
            tarea.setPrioridad(prioridad);

            actualizarExito = actualizarExito && TareasBDD.actualizar(tarea);
        }


        if (actualizarExito) {
            (new Alert(Alert.AlertType.INFORMATION,"Se edito correctamente", ButtonType.OK)).show();
            this.actualizarTareas();
        }
        else {
            (new Alert(Alert.AlertType.ERROR,"No se pudo editar", ButtonType.OK)).show();
        }
    }

}
