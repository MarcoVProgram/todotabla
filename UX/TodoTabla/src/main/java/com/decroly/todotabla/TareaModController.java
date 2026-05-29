package com.decroly.todotabla;

import com.decroly.todotabla.model.Integrante;
import com.decroly.todotabla.model.Tarea;
import com.decroly.todotabla.model.Usuario;
import com.decroly.todotabla.model.sql.IntegrantesBDD;
import com.decroly.todotabla.model.sql.TareasBDD;
import com.decroly.todotabla.utils.AppErrorHandler;
import com.decroly.todotabla.utils.EstadoPrograma;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.*;

public class TareaModController implements Initializable {

    @FXML
    public TextField nombreTareaFormEditar;

    @FXML
    public Spinner<Integer> prioridadTareaFormCrear;

    public BorderPane personaPanelTareaForm;

    @FXML
    public ListView<Tarea> listViewTareas;
    private ObservableList<Tarea> listaTareas;

    @FXML
    public ListView<Usuario> listViewUsuarios;
    public ObservableList<Usuario> listaUsuarios;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        listarTareas();
        personaPanelTareaForm.setVisible(false);

        listViewUsuarios.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY || event.getClickCount() == 2) {
                personaPanelTareaForm.setVisible(false);
            }
        });

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
            listaTareas.addAll(todasTareasDelProyecto.values());
            listViewTareas.refresh();
        }

    }

    public void modTarea(ActionEvent event) {
        boolean actualizarExito = true;

        //obtener valores campos
        String nombre = nombreTareaFormEditar.getText();
        int prioridad = prioridadTareaFormCrear.getValue();

        ObservableList<Tarea> listaDeTareas = listViewTareas.getSelectionModel().getSelectedItems();

        for (Tarea tarea : listaDeTareas) {
            tarea.setNombre(nombre);
            tarea.setPrioridad(prioridad);

            try {
                actualizarExito = actualizarExito && TareasBDD.actualizar(tarea);
            } catch (Exception e) {
                AppErrorHandler.manejar(e, "actualizarTarea");
                actualizarExito = false;
            }
        }


        if (actualizarExito) {
            (new Alert(Alert.AlertType.INFORMATION,"Se edito correctamente", ButtonType.OK)).show();
            this.actualizarTareas();
        }
        else {
            (new Alert(Alert.AlertType.ERROR,"No se pudo editar", ButtonType.OK)).show();
        }
    }

    @FXML
    private void nadie() {
        listViewUsuarios.getSelectionModel().select(null);
        personaPanelTareaForm.setVisible(false);
    }

    @FXML
    private void abrirPersonaPanel() {
        personaPanelTareaForm.setVisible(true);
    }

    private void listarUsuarios() {
        listaUsuarios = FXCollections.observableList(new ArrayList<>());

        Map<Integer, Integrante> integrantes = IntegrantesBDD.getIntegrantes(
                EstadoPrograma.getInstance().getProyectoActivo()
        );

        if (integrantes != null) {
            Iterator<Integrante> integranteIterator =
                    integrantes.values().iterator();

            while (integranteIterator.hasNext()){
                Usuario user = integranteIterator.next().getIdUsuario();
                listaUsuarios.add(user);
            }
        }

        listViewUsuarios.setCellFactory(listaTareas -> new ListCell<>(){
            @Override
            protected void updateItem(Usuario u, boolean empty) {
                super.updateItem(u, empty);

                if (empty || u == null) {
                    setGraphic(null);
                    setText(null);
                    setStyle("-fx-background-color: transparent;");
                    return;
                }

                Label titulo = new Label(u.getNombre());
                titulo.getStyleClass().add("titulo-tarea");

                VBox card = new VBox(8, titulo);
                card.getStyleClass().add("kanban-list");

                setGraphic(card);

            }
        });

    }

}
