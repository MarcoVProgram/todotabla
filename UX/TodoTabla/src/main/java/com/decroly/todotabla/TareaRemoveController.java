package com.decroly.todotabla;

import com.decroly.todotabla.model.Tarea;
import com.decroly.todotabla.model.sql.TareasBDD;
import com.decroly.todotabla.utils.AppErrorHandler;
import com.decroly.todotabla.utils.EstadoPrograma;
import com.decroly.todotabla.utils.Notificator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Controlador de la ventana de eliminación de tareas.
 * Muestra las tareas del proyecto activo en una lista filtrable y permite
 * eliminar las seleccionadas de la base de datos.
 * El botón de borrado permanece deshabilitado mientras no haya ninguna tarea seleccionada.
 */
public class TareaRemoveController implements Initializable {

    @FXML
    public ListView<Tarea> listViewTareas;
    @FXML
    public Button borrarBtn;
    
    private ObservableList<Tarea> listaObsTareas;
    private FilteredList<Tarea> filteredTareas;
    private List<Tarea> listaTareas;

    @FXML
    private TextField buscarTareaEliminar;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        listarTareas();
        
        borrarBtn.setDisable(true);

        listViewTareas.getSelectionModel().selectedItemProperty().addListener((obs, oldTask, newTask) -> {
            if (newTask != null) {
                borrarBtn.setDisable(false);
            } else {
                borrarBtn.setDisable(true);
            }
        });

        this.buscarTareaEliminar.textProperty().addListener((obs) -> filtrarTareas());
    }

    /**
     * Carga las tareas del proyecto activo desde la base de datos y las muestra en la lista.
     */
    private void listarTareas() {
        try {
            listaTareas = new ArrayList<>(TareasBDD.getTareas(
                    EstadoPrograma.getInstance().getProyectoActivo()
            ).values());
            listaObsTareas = FXCollections.observableList(listaTareas);
        } catch (Exception e) {
            AppErrorHandler.manejar(e, "getTareas");
        }

        filteredTareas = new FilteredList<>(listaObsTareas, e -> true);
        listViewTareas.setItems(filteredTareas);
        listViewTareas.setCellFactory(listaTareas ->  new ListCell<Tarea>() {
            @Override
            protected void updateItem(Tarea tarea, boolean empty) {
                super.updateItem(tarea, empty);

                if (empty || tarea == null) {
                    this.setStyle("-fx-background-color: transparent");
                    setGraphic(null);
                } else {

                    Label titulo = new Label(tarea.getNombre());
                    titulo.getStyleClass().add("titulo-tarea");

                    VBox card = new VBox(8, titulo);
                    card.getStyleClass().add("kanban-list");

                    this.getStyleClass().add("task-card");

                    this.setStyle("-fx-background-color: #161b22");
                    setGraphic(card);
                }
            }
        });
    }

    /**
     * Aplica el predicado de búsqueda sobre la lista filtrada de tareas
     * según el texto introducido en el campo de búsqueda.
     */
    private void filtrarTareas() {
        filteredTareas.setPredicate(proyecto ->
                    this.buscarTareaEliminar.getText().isBlank() ||
                            proyecto.getNombre().toLowerCase().contains(this.buscarTareaEliminar.getText().toLowerCase()) );
    }

    /**
     * Recarga las tareas del proyecto activo desde la base de datos y refresca la vista.
     */
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
            listaTareas.clear();
            listaTareas.addAll(todasTareasDelProyecto.values());
            listViewTareas.refresh();
        }

    }

    /**
     * Elimina de la base de datos las tareas seleccionadas en la lista
     * y muestra una notificación con el resultado de la operación.
     */
    @FXML
    public void removeTarea() {
        boolean estado = false;
        List<Tarea> listaDeTareas = listViewTareas.getSelectionModel().getSelectedItems();

        for (Tarea t: listaDeTareas) {
            try {
                estado = TareasBDD.borrar(t);
                listaObsTareas.remove(t);
            } catch (Exception e) {
                AppErrorHandler.manejar(e, "borrarTareas");
            }
        }

        if (estado) {
            Notificator.exito("Borrar Tarea", "Se ha borrado correctamente");
            actualizarTareas();
        } else {
            Notificator.advertencia("Borrar Tarea", "No se ha podido borrar");
        }
    }
}
