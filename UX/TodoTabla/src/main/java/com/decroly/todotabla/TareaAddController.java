package com.decroly.todotabla;

import com.decroly.todotabla.model.*;
import com.decroly.todotabla.model.sql.*;
import com.decroly.todotabla.utils.AppErrorHandler;
import com.decroly.todotabla.utils.EstadoPrograma;
import com.decroly.todotabla.utils.Notificator;
import com.decroly.todotabla.utils.cells.UsuariosCell;
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
/**
 * Controlador de operaciones destinado a gobernar el formulario de creación, asignación inicial
 * de metadatos y priorización de tareas individuales.
 * <p>
 * Expone facilidades para realizar selecciones múltiples simultáneas (Multiple Selection Mode) de operarios,
 * enlazando de forma segura de manera transaccional la inserción de una tarea con sus respectivas entidades dependientes de
 * asignación ({@code Asignacion}) bajo un único bloque lógico de control de errores.
 * </p>
 *
 * @author Senior Developer
 * @version 1.0.0
 */
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


    /**
     * Prepara el entorno transaccional del formulario de alta de tareas.
     * <p>
     * Configura el {@link SelectionMode#MULTIPLE} sobre el componente estructurado de integrantes para permitir
     * asignaciones masivas iniciales e inicia la recuperación asíncrona de recursos humanos.
     * </p>
     */
    public void initialize(URL url, ResourceBundle rb) {
        listViewIntegrantes.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listarUsuarios();
    }
    /**
     * Recupera y mapea los usuarios que se encuentran capacitados para tomar responsabilidades en el proyecto en curso.
     * <p>
     * Extrae información relacional desde la base de datos a través del singleton de contexto de ejecución, poblando de
     * forma segura la interfaz de usuario en el hilo principal de renderizado de JavaFX.
     * </p>
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
