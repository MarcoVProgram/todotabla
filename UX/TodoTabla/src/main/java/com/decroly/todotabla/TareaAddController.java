package com.decroly.todotabla;

import com.decroly.todotabla.model.Integrante;
import com.decroly.todotabla.model.Proyecto;
import com.decroly.todotabla.model.Tarea;
import com.decroly.todotabla.model.Usuario;
import com.decroly.todotabla.model.sql.*;
import com.decroly.todotabla.utils.EstadoPrograma;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

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



    //lista tareas
    List<Usuario> tareas = new ArrayList<>();
    ObservableList<Usuario> obsTareas = FXCollections.observableList(tareas);

    public void initialize(URL url, ResourceBundle rb) {
        listViewUsuarios.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listarUsuarios();
    }

    private void listarUsuarios() {
        listaUsuarios = FXCollections.observableList(new ArrayList<>());

        Iterator<Integrante> integrantes =
                IntegrantesBDD.getIntegrantes(
                        EstadoPrograma.getInstance().getProyectoActivo()
                ).values().iterator();

        while (integrantes.hasNext()){
            Usuario user = integrantes.next().getIdUsuario();
            listaUsuarios.add(user);
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
        boolean added = false;

        //obtener valores campos
        String nombre = nombreTareaFormCrear.getText();
        int prioridad = PrioridadTareaFormCrear.getValue();

        //valores extra necesarios // TODO cambiar esto para seleccionar otros proyectos
        Proyecto idProyecto = EstadoPrograma.getInstance().getProyectoActivo();

        boolean insertarExito = TareasBDD.insertar(new Tarea(nombre, prioridad, 
                EstadosBDD.getEstado("Backlog"), EstadoPrograma.getInstance().getProyectoActivo()));
        if (insertarExito) {
            (new Alert(Alert.AlertType.INFORMATION,"Se añadio correctamente", ButtonType.OK)).show();
        }
        else {
            (new Alert(Alert.AlertType.ERROR,"No se pudo añadir", ButtonType.OK)).show();
        }
    }

}
