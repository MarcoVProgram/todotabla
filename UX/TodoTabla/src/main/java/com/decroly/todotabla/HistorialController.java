package com.decroly.todotabla;

import java.io.IOException;
import java.net.URL;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import com.decroly.todotabla.model.Asignacion;
import com.decroly.todotabla.model.Estado;
import com.decroly.todotabla.model.HistorialTareas;
import com.decroly.todotabla.model.Integrante;
import com.decroly.todotabla.model.Proyecto;
import com.decroly.todotabla.model.Tarea;
import com.decroly.todotabla.model.Usuario;
import com.decroly.todotabla.model.sql.AsignacionesBDD;
import com.decroly.todotabla.model.sql.EstadosBDD;
import com.decroly.todotabla.model.sql.HistorialTareasBDD;
import com.decroly.todotabla.model.sql.IntegrantesBDD;
import com.decroly.todotabla.model.sql.TareasBDD;
import com.decroly.todotabla.utils.AppErrorHandler;
import com.decroly.todotabla.utils.EstadoPrograma;
import com.decroly.todotabla.utils.Navigator;
import com.decroly.todotabla.utils.Notificator;
import com.decroly.todotabla.utils.cells.HistorialTareaCell;
import com.decroly.todotabla.utils.cells.UsuariosCell;
import com.decroly.todotabla.utils.Navigator;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
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
    private ListView listViewUsuarios; // ¿Que clase de objeto usa?
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
    private ListView<Usuario> listViewAsignaciones;

    private ObservableList<HistorialTareas> listaHistorialTareas;
    private ObservableList<Asignacion> listaHistorialAsignaciones;

    private ObservableList<Usuario> listaUsuariosAsignados;


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
        Map<Integer, Label> fechas = new LinkedHashMap<>();
        listaUsuariosAsignados = FXCollections.observableList(new ArrayList<>());

        refrescarDatos();

        if (listaHistorialAsignaciones != null) {

            Collections.sort(listaHistorialAsignaciones, Comparator.comparingLong(h -> (h.getFechaAsignacion().toEpochDay())));
            for (Asignacion asignacion : listaHistorialAsignaciones) {
                Usuario user = asignacion.getIdUsuario();
                listaUsuariosAsignados.add(user);
                String duracion = "Desde " + asignacion.getFechaAsignacion().format(DateTimeFormatter.ISO_LOCAL_DATE) + " hasta ";
                if (asignacion.getFechaFin() != null) {
                    duracion += asignacion.getFechaFin().format(DateTimeFormatter.ISO_LOCAL_DATE);
                } else {
                    duracion += "hoy";
                }
                fechas.put(user.getId(), new Label(duracion));
            }
        }

        listViewAsignaciones.setItems(listaUsuariosAsignados);
        listViewAsignaciones.setCellFactory(l -> new UsuariosCell(fechas));
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
        MainController.getVentanaSecundaria();
        Stage stage = new Stage();
        try {
            Navigator.changeScene(stage, "/com/decroly/todotabla/usuarios-formAsignarTarea.fxml"); // TODO Poner ruta al view correspondiente
        } catch (IOException e) {
            AppErrorHandler.manejar(e, e.getCause().toString());
        } 
    }

    /* private void listarUsuarios() {
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
        listViewUsuarios.setCellFactory(listaTareas -> new UsuariosCell());

    } */
}
