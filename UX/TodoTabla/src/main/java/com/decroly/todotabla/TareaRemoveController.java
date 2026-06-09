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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class TareaRemoveController implements Initializable {

    @FXML
    public ListView<Tarea> listViewTareas;
    @FXML
    public Button borrarBtn;
    
    private ObservableList<Tarea> listaObsTareas;
    private List<Tarea> listaTareas;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        listarTareas();
        
        borrarBtn.setDisable(true);

        final String[] t = {""};
        listViewTareas.getSelectionModel().selectedItemProperty().addListener((obs, oldTask, newTask) -> {
            if (newTask != null) {
                borrarBtn.setDisable(false);
            }
        });
        
        
    }

    private void listarTareas() {
        try {
            listaTareas = new ArrayList<>(TareasBDD.getTareas(
                    EstadoPrograma.getInstance().getProyectoActivo()
            ).values());
            listaObsTareas = FXCollections.observableList(
                    listaTareas
            );
        } catch (Exception e) {
            AppErrorHandler.manejar(e, "getTareas");
        }

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

        listViewTareas.setItems(listaObsTareas);
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
            listaTareas.clear();
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
                listaObsTareas.remove(t);
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
