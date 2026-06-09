package com.decroly.todotabla;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
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
import javafx.collections.SetChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

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
    private ListView<Usuario> listViewUsuarios; // ¿Que clase de objeto usa?
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
    private ListView<Asignacion> listViewAsignaciones; // Historial de asignaciones

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

        listarAsignados();
        listarAsignadosPasados();
        listarEstadosPasados();

        listViewPasado.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listViewAsignaciones.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listViewUsuarios.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    @FXML
    private void returnToMain() { //abrir pantalla principal (menú)
        Stage stage = (Stage) returnBtn.getScene().getWindow();
        try {
            Navigator.changeScene(stage, "/com/decroly/todotabla/main-view.fxml");
        } catch (Exception ex) {
            AppErrorHandler.manejar(ex, "returnToMain");
        }
    }

    @FXML
    private void returnToKanban() {
        Stage stage = (Stage) returnBtn.getScene().getWindow();
        try {
            Navigator.changeScene(stage, "/com/decroly/todotabla/kanban-view.fxml");
        } catch (Exception ex) {
            AppErrorHandler.manejar(ex, "returnToMain");
        }
    }

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

    private void listarAsignadosPasados() {
        refrescarDatos();

        if (listaHistorialAsignaciones != null) {
            Collections.sort(listaHistorialAsignaciones, Comparator.comparingLong(h -> (h.getFechaAsignacion().toEpochDay())));
        }

        listViewAsignaciones.setItems(listaHistorialAsignaciones);
        listViewAsignaciones.setCellFactory(l -> new AsignacionesCell());
    }

    private void listarEstadosPasados() {
        refrescarDatos();

        if (listaHistorialTareas != null) {
            Collections.sort(listaHistorialTareas, Comparator.comparingLong(h -> (h.getFechaCambio().toEpochSecond(ZoneOffset.UTC))));
        }
        listViewPasado.setItems(listaHistorialTareas);
        listViewPasado.setCellFactory(l -> new HistorialTareaCell());
    }

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

    // Para signar seria preferible abrir otro panel que este oculto o una ventana nueva

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
