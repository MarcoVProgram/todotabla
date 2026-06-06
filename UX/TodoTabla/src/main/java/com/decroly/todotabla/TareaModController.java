package com.decroly.todotabla;

import com.decroly.todotabla.model.Integrante;
import com.decroly.todotabla.model.Tarea;
import com.decroly.todotabla.model.Usuario;
import com.decroly.todotabla.model.sql.IntegrantesBDD;
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
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.*;

public class TareaModController implements Initializable {

    @FXML
    public TextField nombreTareaFormEditar;

    @FXML
    public Button editarBtn;
    @FXML
    public Button asignarBtn;
    
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
        
        editarBtn.setDisable(true);
        asignarBtn.setDisable(true);

        final String[] t = {""};
        listViewTareas.getSelectionModel().selectedItemProperty().addListener((obs, oldTask, newTask) -> {
                    if (newTask != null) {
                        nombreTareaFormEditar.setText(newTask.getNombre());
                        t[0] = newTask.getNombre();
                    } else {
                        nombreTareaFormEditar.clear();
                    }
                });
        
//        nombreTareaFormEditar.textProperty().addListener((obs, oldVal, newVal) ->{
//            editarBtn.setDisable(newVal == null && newVal.trim().isEmpty() && newVal.equalsIgnoreCase(oldVal));
//            asignarBtn.setDisable(newVal == null || newVal.trim().isEmpty() || newVal.equalsIgnoreCase(oldVal));
//        });

        nombreTareaFormEditar.textProperty().addListener((obs, oldVal, newVal) -> {
            editarBtn.setDisable(newVal == null || newVal.trim().isEmpty() || newVal.equals(t[0]));
            asignarBtn.setDisable(newVal == null || newVal.trim().isEmpty());
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

                    this.getStyleClass().add("task-card");
                    this.setStyle("-fx-border-color: white");

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
        boolean actualizarExito = false;

        //obtener valores campos
        String nombre = nombreTareaFormEditar.getText();

        ObservableList<Tarea> listaDeTareas = listViewTareas.getSelectionModel().getSelectedItems();

        actualizarExito = !listaDeTareas.isEmpty();
        for (Tarea tarea : listaDeTareas) {
            tarea.setNombre(nombre);

            try {
                actualizarExito = actualizarExito && TareasBDD.actualizar(tarea);
            } catch (Exception e) {
                AppErrorHandler.manejar(e, "actualizarTarea");
                actualizarExito = false;
            }
        }


        if (actualizarExito) {
            Notificator.exito("Actualización de Tarea", "Se ha actualizado la tarea correctamente");
            this.actualizarTareas();
        }
        else {
            Notificator.error("Actualización de Tarea", "No se pudo modificar la tarea");
        }
    }

    @FXML
    private void nadie() {
        listViewUsuarios.getSelectionModel().select(null);
        personaPanelTareaForm.setVisible(false);
    }

    @FXML
    private void abrirPersonaPanel() {
        listarUsuarios();
        personaPanelTareaForm.setVisible(true);
    }

    private void listarUsuarios() {
        listaUsuarios = FXCollections.observableList(new ArrayList<>());

        Map<Integer, Integrante> integrantes = null;
        try {
            integrantes = IntegrantesBDD.getIntegrantes(
                    EstadoPrograma.getInstance().getProyectoActivo()
            );
        } catch (Exception e) {
            AppErrorHandler.manejar(e, e.getCause().toString());
        }

        if (integrantes != null) {
            Iterator<Integrante> integranteIterator =
                    integrantes.values().iterator();

            while (integranteIterator.hasNext()){
                Usuario user = integranteIterator.next().getIdUsuario();
                listaUsuarios.add(user);
            }
        }

        listViewUsuarios.setItems(listaUsuarios);

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
