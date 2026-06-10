package com.decroly.todotabla;

import com.decroly.todotabla.model.*;
import com.decroly.todotabla.model.sql.*;
import com.decroly.todotabla.utils.AppErrorHandler;
import com.decroly.todotabla.utils.EstadoPrograma;
import com.decroly.todotabla.utils.Notificator;
import com.decroly.todotabla.utils.cells.UsuariosCell;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.time.LocalDate;
import java.util.*;

/**
 * Controlador del formulario de creación de una nueva tarea.
 * Permite introducir el nombre de la tarea, seleccionar los integrantes a asignar
 * y persistir tanto la tarea como sus asignaciones en la base de datos.
 * El botón de creación permanece deshabilitado mientras el campo de nombre esté vacío.
 */
public class TareaAddController implements Initializable {

    @FXML
    private Button addTarea;

    @FXML
    public TextField nombreTareaFormCrear;

    @FXML
    private ListView<Usuario> listViewIntegrantes;
    private List<Usuario> misUsuarios = new ArrayList<>();
    private ObservableList<Usuario> listaUsuarios;



    public void initialize(URL url, ResourceBundle rb) {
        listViewIntegrantes.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        addTarea.setDisable(true);
        ChangeListener<String> campo = (obs, oldVal, newVal)->{
            boolean vacio = nombreTareaFormCrear.getText().trim().isEmpty();

            addTarea.setDisable(vacio);
        };

        nombreTareaFormCrear.textProperty().addListener(campo);
        
        listarUsuarios();
    }

    /**
     * Carga los integrantes activos del proyecto activo y los muestra en la lista de selección.
     */
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
        listViewIntegrantes.setCellFactory(listaUsuarios -> new UsuariosCell());
    }

    /**
     * Valida el formulario, inserta la tarea en estado "Pendiente" y crea las asignaciones
     * para cada usuario seleccionado. Muestra notificaciones de éxito o error según el resultado.
     */
    @FXML
    private void addTarea(){

        String nombre = nombreTareaFormCrear.getText();

        List<Usuario> usuariosSeleccionados = listViewIntegrantes.getSelectionModel().getSelectedItems();


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
            nombreTareaFormCrear.clear();

            listViewIntegrantes.getSelectionModel().clearSelection();
        }
    }

}
