package com.decroly.todotabla;

import com.decroly.todotabla.model.*;
import com.decroly.todotabla.model.sql.*;
import com.decroly.todotabla.utils.AppErrorHandler;
import com.decroly.todotabla.utils.EstadoPrograma;
import com.decroly.todotabla.utils.Notificator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Border;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.time.LocalDate;
import java.util.*;

public class TareaAddController implements Initializable { // TODO Comprobar su funcinamiento

    //PESTAÑA TAREA
//    @FXML
//    private Button addTarea;

    @FXML
    public TextField nombreTareaFormCrear;

    @FXML
    private ListView<Usuario> listViewIntegrantes;
    private List<Usuario> misUsuarios = new ArrayList<>();
    private ObservableList<Usuario> listaUsuarios;



    public void initialize(URL url, ResourceBundle rb) {
        listViewIntegrantes.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listarUsuarios();
    }

    private void listarUsuarios() {
        listaUsuarios = FXCollections.observableList(misUsuarios);

        Map<Integer, Integrante> integrantes;
        try {
            integrantes = IntegrantesBDD.getIntegrantes(
                    EstadoPrograma.getInstance().getProyectoActivo()
            );
        } catch (Exception e) {
            AppErrorHandler.manejar(e, "getIntegrantes");
            integrantes = null;
        }

        if (integrantes != null) {
            Iterator<Integrante> integranteIterator =
                    integrantes.values().iterator();

            while (integranteIterator.hasNext()){
                Usuario user = integranteIterator.next().getIdUsuario();
                misUsuarios.add(user);
            }
        }

        listViewIntegrantes.setItems(listaUsuarios);
        listViewIntegrantes.setCellFactory(listaTareas -> new ListCell<>(){
            @Override
            protected void updateItem(Usuario u, boolean empty) {
                super.updateItem(u, empty);

                if (empty || u == null) {
                    this.setStyle("-fx-background-color: transparent;");
                    this.setGraphic(null);
                    this.setText(null);
                    return;
                }

                Label titulo = new Label(u.getNombre());
                titulo.getStyleClass().add("titulo-tarea");

                VBox card = new VBox(8, titulo);
                card.getStyleClass().add("kanban-list");

                this.getStyleClass().add("task-card");

                this.setStyle("-fx-background-color: #161b22");

                setGraphic(card);

            }
        });

    }

    //--------AGREGAR TAREA-------------
    @FXML
    private void addTarea(){

        //obtener valores campos
        String nombre = nombreTareaFormCrear.getText();

        List<Usuario> usuariosSeleccionados = listViewIntegrantes.getSelectionModel().getSelectedItems();


        //valores extra necesarios
        Proyecto idProyecto = EstadoPrograma.getInstance().getProyectoActivo();
        int prioridad;
        try {
            prioridad = TareasBDD.getMayorPrioridad(idProyecto);
        } catch (Exception e) {
            AppErrorHandler.manejar(e, "getMayorPrioridad");
            prioridad = 0;
        }

        Tarea tareo;
        try {
            tareo = new Tarea(
                    nombre, prioridad,
                    EstadosBDD.getEstado("Pendiente"), idProyecto
            );
        } catch (Exception e) {
            AppErrorHandler.manejar(e, "getEstado");
            tareo = null;
        }

        int key = -1;
        if (tareo != null) {
            try {
                key = TareasBDD.insertar(tareo);
            } catch (Exception e) {
                AppErrorHandler.manejar(e, "insertar");
            }
        }

        boolean insertarExito = (key != -1);
        if (insertarExito) {
            try {
                tareo = TareasBDD.getTarea(key);
            for (Usuario u: usuariosSeleccionados) {
                Asignacion a = new Asignacion(u, tareo, LocalDate.now(), LocalDate.MAX); // TODO No se como asignar la fecha de fin
                try {
                    insertarExito = (insertarExito && AsignacionesBDD.insertar(a));
                } catch (Exception e) {
                    AppErrorHandler.manejar(e, "insertar");
                    insertarExito = false;
                    break;
                }
            }
            } catch (Exception e) {
                AppErrorHandler.manejar(e, "Obtener la tarea");
            }
        }
        if (insertarExito) {
            Notificator.exito("Creación de Tarea", "Se ha creado la tarea con éxito, puede cerrar esta ventana");
        }
    }

}
