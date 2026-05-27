package com.decroly.todotabla;

import com.decroly.todotabla.model.*;
import com.decroly.todotabla.model.sql.*;
import com.decroly.todotabla.utils.EstadoPrograma;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

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
    public Spinner<Integer> PrioridadTareaFormCrear;


    @FXML
    public ListView<Usuario> listViewUsuarios;
    private ObservableList<Usuario> listaUsuarios;


    public void initialize(URL url, ResourceBundle rb) {
        listViewUsuarios.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listarUsuarios();
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

        // TODO rehacer esto para hacer lo con usuarios recomendablemente
        /*listViewUsuarios.setCellFactory(listaTareas -> new ListCell<Tarea>() {
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
        });*/

    }

    //--------AGREGAR TAREA-------------
    @FXML
    private void addTarea(){

        //obtener valores campos
        String nombre = nombreTareaFormCrear.getText();
        int prioridad = PrioridadTareaFormCrear.getValue();

        List<Usuario> usuariosSeleccionados = listViewUsuarios.getSelectionModel().getSelectedItems();


        //valores extra necesarios
        Proyecto idProyecto = EstadoPrograma.getInstance().getProyectoActivo();

        Tarea tareo = new Tarea(
                nombre, prioridad,
                EstadosBDD.getEstado("Backlog"), idProyecto
        );
        boolean insertarExito = TareasBDD.insertar(tareo);
        if (insertarExito) {
            for (Usuario u: usuariosSeleccionados) {
                Asignacion a = new Asignacion(u, tareo, LocalDate.now(), LocalDate.MAX); // TODO No se como asignar la fecha de fin
                insertarExito = (insertarExito && AsignacionesBDD.insertar(a));
            }
        }
        if (insertarExito) {
            (new Alert(Alert.AlertType.INFORMATION,"Se añadio correctamente", ButtonType.OK)).show();
        }
        else {
            (new Alert(Alert.AlertType.ERROR,"No se pudo añadir", ButtonType.OK)).show();
        }
    }

}
