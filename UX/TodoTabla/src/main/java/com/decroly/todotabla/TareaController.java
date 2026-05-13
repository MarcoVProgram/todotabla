package com.decroly.todotabla;

import com.decroly.todotabla.model.Proyecto;
import com.decroly.todotabla.model.Tarea;
import com.decroly.todotabla.model.sql.TareasBDD;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.*;

public class TareaController implements Initializable {

    //PESTAÑA TAREA
    @FXML
    private Button addTarea;

    @FXML
    private Button filterTarea;

    @FXML
    private Button buscarTarea;

    @FXML
    public Label nombreProyecto;

    @FXML
    public TextField nombreTarea;

    @FXML
    public ComboBox<Integer> comboBoxPrioridadTarea;



    //lista tareas
    List<Tarea> tareas = new ArrayList<>();
    ObservableList<Tarea> obsTareas = FXCollections.observableList(tareas);

    public void initialize(URL url, ResourceBundle rb) {
        actualizarTareas();

        ObservableList<Integer> prioridades = FXCollections.observableList(new LinkedList<>());

        for (int i = 0; i <= 100; i++) {
            prioridades.add(i);
        }
        comboBoxPrioridadTarea.setItems(prioridades);

    }

    private void actualizarTareas() {
        obsTareas.addAll(Objects.requireNonNull(TareasBDD.getTareas()).values());
    }

    //--------AGREGAR TAREA-------------
    @FXML
    private boolean addTarea(){
        boolean added = false;

        //obtener valores campos
        String nombre = nombreTarea.getText();
        int prioridad = comboBoxPrioridadTarea.getSelectionModel().getSelectedItem();

        //valores extra necesarios
        Proyecto idProyecto = null;

        try {TareasBDD.insertar(new Tarea(nombre, prioridad, null, idProyecto));
            this.actualizarTareas();
        } catch (Exception e) {
            (new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK)).show();
        }


        return added;
    }



}
