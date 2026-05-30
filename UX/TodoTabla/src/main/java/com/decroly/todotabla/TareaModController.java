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
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.*;

public class TareaModController implements Initializable {

    @FXML
    public TextField nombreTareaFormCrear;

    @FXML
    public Spinner<Integer> prioridadTareaFormCrear;

    @FXML
    public ListView<Tarea> listViewTareas;


    private ObservableList<Tarea> listaTareas;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        listarTareas();

        listViewTareas.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if(newValue != null){
                        nombreTareaFormCrear.setText(newValue.getNombre());
                        prioridadTareaFormCrear.getValueFactory().setValue(newValue.getPrioridad());
                        
                    }else{
                        Notificator.error("No se actualizo la tarea", "El valor antiguo de la tarea y el nuevo coincide");
                    }

                }
        );

    }

    private void listarTareas() {
        try {
            listaTareas = FXCollections.observableList(
                    new ArrayList<>(TareasBDD.getTareas(
                            EstadoPrograma.getInstance().getProyectoActivo()
                    ).values())
            );
        }  catch (Exception e) {
            AppErrorHandler.manejar(e, "getTareas");
            listaTareas = FXCollections.observableArrayList();
        }

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

        prioridadTareaFormCrear.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1)
        );

    }

    private void actualizarTareas() {
        Map<Integer, Tarea> todasTareasDelProyecto;

        try {
            todasTareasDelProyecto = TareasBDD.getTareas(
                    EstadoPrograma.getInstance().getProyectoActivo()
            );
        }  catch (Exception e) {
            AppErrorHandler.manejar(e, "actualizarTareas");
            todasTareasDelProyecto = null;
        }

        if (todasTareasDelProyecto != null) {
            listaTareas.setAll(todasTareasDelProyecto.values());
            listViewTareas.refresh();
        }

    }

    // TODO No se como cambiar la asignacion sin meter muchas cosas en la ventana
    public void modTarea(ActionEvent event) {
        boolean actualizarExito = true;

        String nombre = nombreTareaFormCrear.getText();
        int prioridad = prioridadTareaFormCrear.getValue();

        Tarea tarea = listViewTareas.getSelectionModel().getSelectedItem();

        if (tarea == null) {
            Notificator.error("Error", "Selecciona una tarea");
            return;
        }

        tarea.setNombre(nombre);
        tarea.setPrioridad(prioridad);

        try {
            actualizarExito = TareasBDD.actualizar(tarea);
        } catch (Exception e) {
            AppErrorHandler.manejar(e, "actualizarTarea");
            actualizarExito = false;
        }

        if (actualizarExito) {
            Notificator.exito("Actualización de Tarea", "Se ha actualizado la tarea correctamente");
            this.actualizarTareas();
        } else {
            Notificator.error("Actualización de Tarea", "No se pudo modificar la tarea");
        }
    }

}
