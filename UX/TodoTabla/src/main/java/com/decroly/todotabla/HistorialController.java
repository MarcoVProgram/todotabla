package com.decroly.todotabla;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;

import com.decroly.todotabla.model.Asignacion;
import com.decroly.todotabla.model.Estado;
import com.decroly.todotabla.model.HistorialTareas;
import com.decroly.todotabla.model.Integrante;
import com.decroly.todotabla.model.Proyecto;
import com.decroly.todotabla.model.Tarea;
import com.decroly.todotabla.model.Usuario;
import com.decroly.todotabla.model.sql.*;
import com.decroly.todotabla.utils.AppErrorHandler;
import com.decroly.todotabla.utils.EstadoPrograma;
import com.decroly.todotabla.utils.Navigator;
import com.decroly.todotabla.utils.Notificator;
import com.decroly.todotabla.utils.cells.AsignacionesCell;
import com.decroly.todotabla.utils.cells.HistorialTareaCell;
import com.decroly.todotabla.utils.cells.UsuariosCell;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

/**
 * Controlador de la vista de historial de una tarea.
 * Gestiona la visualización y edición del nombre y estado de la tarea activa,
 * el historial de cambios de estado, el historial de asignaciones y los usuarios actualmente asignados.
 */
public class HistorialController implements Initializable {

    @FXML
    private ImageView returnBtn;

    private Tarea tareaActiva;
    private Proyecto proyectoActivo;

    @FXML
    private Label proyectoTitulo;

    @FXML
    private Label tareaHistorialNombre;

    @FXML
    private TextField nuevoNombreTarea;

    @FXML
    private ListView<Usuario> listViewUsuarios;
    @FXML
    private Label circleEstado;

    private ObservableList<Usuario> listaUsuarios;
    private ObservableList<Asignacion> listaAsignacionesATarea;
    private ObservableList<Integrante> listaIntegrantesAProyecto;

    @FXML
    private ComboBox<String> estadoChoice;
    private ObservableList<String> listaEstados;
    private List<Estado> estados;

    @FXML
    private ListView<HistorialTareas> listViewPasado;
    @FXML
    private ListView<Asignacion> listViewAsignaciones;

    private ObservableList<HistorialTareas> listaHistorialTareas;
    private ObservableList<Asignacion> listaHistorialAsignaciones;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tareaActiva = EstadoPrograma.getInstance().getTareaActiva();
        proyectoActivo = EstadoPrograma.getInstance().getProyectoActivo();
        proyectoTitulo.setText("🔒 " + proyectoActivo.getTitulo());
        tareaHistorialNombre.setText(tareaActiva.getNombre());
        nuevoNombreTarea.setText(tareaActiva.getNombre());
        circleEstado.setStyle("-fx-background-color: " + tareaActiva.getEstado().getColor() + ";");

        try {
            estados = EstadosBDD.getEstados();
        } catch (Exception e) {
            AppErrorHandler.manejar(e, "getEstados");
            estados = new ArrayList<>();
        }

        listaEstados = FXCollections.observableArrayList(new LinkedList<>());

        Collections.sort(estados, Comparator.comparingInt(estado -> estado.getOrden()));

        for (Estado estado : estados) {
            listaEstados.add(estado.getNombre());
        }

        estadoChoice.setItems(listaEstados);
        estadoChoice.setValue(tareaActiva.getEstado().getNombre());
        estadoChoice.getStyleClass().add("choice-box-popup");

        listarAsignados();
        listarAsignadosPasados();
        listarEstadosPasados();

        listViewPasado.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listViewAsignaciones.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listViewUsuarios.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    /**
     * Navega de vuelta a la vista principal del menú.
     */
    @FXML
    private void returnToMain() {
        Stage stage = (Stage) returnBtn.getScene().getWindow();
        try {
            Navigator.changeScene(stage, "/com/decroly/todotabla/main-view.fxml");
        } catch (Exception ex) {
            AppErrorHandler.manejar(ex, "returnToMain");
        }
    }

    /**
     * Navega de vuelta a la vista del tablero Kanban.
     */
    @FXML
    private void returnToKanban() {
        Stage stage = (Stage) returnBtn.getScene().getWindow();
        try {
            Navigator.changeScene(stage, "/com/decroly/todotabla/kanban-view.fxml");
        } catch (Exception ex) {
            AppErrorHandler.manejar(ex, "returnToMain");
        }
    }

    /**
     * Recarga desde la base de datos las asignaciones activas, los integrantes activos,
     * el historial de estados y el historial de asignaciones de la tarea activa.
     */
    private void refrescarDatos() {
        Map<Integer, Asignacion> asignados = null;
        Map<Integer, Integrante> integrantes = null;
        Map<Integer, HistorialTareas> historialTareas = null;
        Map<Integer, Asignacion> historialAsignaciones = null;
        listaAsignacionesATarea = FXCollections.observableArrayList(new ArrayList<>());
        listaIntegrantesAProyecto = FXCollections.observableArrayList(new ArrayList<>());
        listaHistorialTareas = FXCollections.observableArrayList(new ArrayList<>());
        listaHistorialAsignaciones = FXCollections.observableArrayList(new ArrayList<>());

        try {
            asignados = AsignacionesBDD.getAsignacionesActivas(tareaActiva);
            integrantes = IntegrantesBDD.getIntegrantesActivos(proyectoActivo);
            historialTareas = HistorialTareasBDD.getHistorialTareas(tareaActiva);
            historialAsignaciones = AsignacionesBDD.getAsignaciones(tareaActiva);
        } catch (Exception e) {
            AppErrorHandler.manejar(e, e.getCause().toString());
        }

        if (asignados != null) {
            listaAsignacionesATarea.addAll(asignados.values());
        }
        if (integrantes != null) {
            listaIntegrantesAProyecto.addAll(integrantes.values());
        }
        if (historialTareas != null) {
            listaHistorialTareas.addAll(historialTareas.values());
        }
        if (historialAsignaciones != null) {
            listaHistorialAsignaciones.addAll(historialAsignaciones.values());
        }
    }

    /**
     * Carga y muestra en {@code listViewUsuarios} los usuarios actualmente asignados a la tarea,
     * junto con su rol en el proyecto.
     */
    private void listarAsignados() {
        listaUsuarios = FXCollections.observableList(new ArrayList<>());
        Map<Integer, Label> rols = new LinkedHashMap<>();

        refrescarDatos();

        if (listaAsignacionesATarea != null) {
            for (Asignacion asignacion : listaAsignacionesATarea) {
                Usuario user = asignacion.getIdUsuario();
                listaUsuarios.add(user);
                for (Integrante integrante : listaIntegrantesAProyecto) {
                    if (integrante.getIdUsuario().equals(user)) {
                        rols.put(user.getId(), new Label("Rol: " + integrante.getRol()));
                        break;
                    }
                }
            }
        }

        listViewUsuarios.setItems(listaUsuarios);
        listViewUsuarios.setCellFactory(listaAsignados -> new UsuariosCell(rols));
    }

    /**
     * Carga y muestra en {@code listViewAsignaciones} el historial completo de asignaciones
     * de la tarea, ordenado por fecha de asignación ascendente.
     */
    private void listarAsignadosPasados() {
        refrescarDatos();

        if (listaHistorialAsignaciones != null) {
            Collections.sort(listaHistorialAsignaciones, Comparator.comparingLong(h -> (h.getFechaAsignacion().toEpochDay())));
        }

        listViewAsignaciones.setItems(listaHistorialAsignaciones);
        listViewAsignaciones.setCellFactory(l -> new AsignacionesCell());
    }

    /**
     * Carga y muestra en {@code listViewPasado} el historial de cambios de estado de la tarea,
     * ordenado por fecha de cambio ascendente.
     */
    private void listarEstadosPasados() {
        refrescarDatos();

        if (listaHistorialTareas != null) {
            Collections.sort(listaHistorialTareas, Comparator.comparingLong(h -> (h.getFechaCambio().toEpochSecond(ZoneOffset.UTC))));
        }
        listViewPasado.setItems(listaHistorialTareas);
        listViewPasado.setCellFactory(l -> new HistorialTareaCell());
    }

    /**
     * Valida y persiste los cambios de nombre y estado introducidos por el usuario para la tarea activa.
     * Muestra un aviso si los nuevos valores ya existen en el proyecto.
     */
    @FXML
    private void actualizarTareaEdicion() {
        String newName  = nuevoNombreTarea.getText();
        String newEstado = estadoChoice.getValue();

        try {
            boolean tareaExiste = TareasBDD.tareaExiste(newName, proyectoActivo) != 0;
            boolean estadoEsIgual = newEstado.equalsIgnoreCase(tareaActiva.getEstado().getNombre());
            if (tareaExiste && estadoEsIgual) {
                Notificator.advertencia("No se pudo cambiar datos",
                        "Ambos datos existen en el proyecto, no tiene sentido cambiarlos");
                return;
            }
            if (!newName.isEmpty() && !tareaExiste) {
                tareaActiva.setNombre(newName);
                tareaHistorialNombre.setText(tareaActiva.getNombre());
            }
            if (!newEstado.isEmpty() && !estadoEsIgual) {
                tareaActiva.setEstado(EstadosBDD.getEstado(newEstado));
                circleEstado.setStyle("-fx-background-color: " + tareaActiva.getEstado().getColor() + ";");
            }
            TareasBDD.actualizar(tareaActiva);
            Notificator.exito("Cambios Guardados", "Se han realizado con éxito las modificaciones de la tarea");
        } catch (Exception e) {
            AppErrorHandler.manejar(e, "actualizarTareaEdicion");
        }
    }

    /**
     * Abre la ventana secundaria de selección de personas para asignarlas a la tarea activa.
     * Una vez cerrada la ventana, inserta las nuevas asignaciones evitando duplicados.
     */
    @FXML
    private void abrirVentanaPersonas() {

        try {
            String fxml = "usuarios-formAsignarTarea.fxml";
            String titulo = "Añadir Personas";

            Navigator.arbrirVentanaSecundaria(fxml, titulo, getClass());

            List<Usuario> lAAsignar = EstadoPrograma.getInstance().getUsuariosTemp();

            if (lAAsignar != null) {
                Map<Integer, Asignacion> aa = AsignacionesBDD.getAsignacionesActivas(tareaActiva);
                Map<Integer, Usuario> ua = new LinkedHashMap<>();
                for (Asignacion asignacion  : aa.values()) {
                    ua.put(asignacion.getIdUsuario().getId(), asignacion.getIdUsuario());
                }
                for (Usuario u : lAAsignar) {
                    if (ua.containsValue(u)) {
                        continue;
                    }
                    try {
                        Asignacion asignacion = new Asignacion(u, tareaActiva, LocalDate.now(), null);
                        AsignacionesBDD.insertar(asignacion);
                    } catch (Exception e) {
                        AppErrorHandler.manejar(e, "insertarAsignacion");
                    }
                }
            }

        } catch (IOException e) {
            AppErrorHandler.manejar(e, "load the loader");
        } catch (Exception e) {
            AppErrorHandler.manejar(e, "getAsignacionesActivas");
        }
        listarAsignados();
        listarAsignadosPasados();
    }

    /**
     * Archiva las asignaciones activas de los usuarios seleccionados en {@code listViewUsuarios}
     * estableciendo su fecha de fin al día actual.
     */
    @FXML
    private void desasignarIntegrantesSeleccionados() {
        try {
            int cambios = 0;
            refrescarDatos();
            for (Usuario u : listViewUsuarios.getSelectionModel().getSelectedItems()) {
                for (Asignacion activo : listaAsignacionesATarea) {
                    if (activo.getIdUsuario().equals(u)) {
                        activo.setFechaFin(LocalDate.now());
                        AsignacionesBDD.actualizar(activo);
                        cambios++;
                    }
                }
            }
            if (cambios > 0) {
                Notificator.exito("Desasignacion Realizada", "Se han archivado " + cambios + " asignaciones de usuarios");
            } else {
                Notificator.informar("Sin cambios", "No se han archivado datos");
            }
        } catch (Exception e) {
            AppErrorHandler.manejar(e, "borrar Integrantes seleccionados");
        }
        listarAsignados();
        listarAsignadosPasados();
    }

    /**
     * Elimina de la base de datos los registros del historial de estados seleccionados
     * en {@code listViewPasado}.
     */
    @FXML
    private void desasignarHistorialesSeleccionados() {
        try {
            int cambios = 0;
            refrescarDatos();

            for (HistorialTareas historial : listViewPasado.getSelectionModel().getSelectedItems()) {
                HistorialTareasBDD.borrar(historial);
                cambios++;
            }

            if (cambios > 0) {
                Notificator.exito("Borrado Realizado", "Se han borrado " + cambios + " historiales");
            } else {
                Notificator.informar("Sin cambios", "No se han borrado datos");
            }
        } catch (Exception e) {
            AppErrorHandler.manejar(e, "borrar Historiales seleccionados");
        }
        listarEstadosPasados();
    }

    /**
     * Elimina de la base de datos las asignaciones seleccionadas en {@code listViewAsignaciones}.
     * Solo permite borrar asignaciones ya finalizadas; las activas se omiten con un aviso.
     */
    @FXML
    private void desasignarAsignacionesSeleccionados() {
        try {
            int cambios = 0;
            refrescarDatos();

            for (Asignacion todaAsignacion : listViewAsignaciones.getSelectionModel().getSelectedItems()) {
                if (listaAsignacionesATarea.contains(todaAsignacion)) {
                    Notificator.advertencia("No se puede borrar asignacion", "Solo se pueden borrar asignaciones terminadas");
                    continue;
                }
                AsignacionesBDD.borrar(todaAsignacion);
                cambios++;
            }

            if (cambios > 0) {
                Notificator.exito("Borrado Realizado", "Se han borrado " + cambios + " asignaciones");
            } else {
                Notificator.informar("Sin cambios", "No se han borrado datos");
            }
        } catch (Exception e) {
            AppErrorHandler.manejar(e, "borrar Asignaciones seleccionadas");
        }
        listarAsignadosPasados();
    }
}
