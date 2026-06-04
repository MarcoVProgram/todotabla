package com.decroly.todotabla;

import com.decroly.todotabla.model.*;
import com.decroly.todotabla.model.sql.AsignacionesBDD;
import com.decroly.todotabla.model.sql.EstadosBDD;
import com.decroly.todotabla.model.sql.IntegrantesBDD;
import com.decroly.todotabla.model.sql.TareasBDD;
import com.decroly.todotabla.utils.AppErrorHandler;
import com.decroly.todotabla.utils.EstadoPrograma;
import com.decroly.todotabla.utils.Navigator;
import com.decroly.todotabla.utils.Notificator;
import com.decroly.todotabla.utils.cells.UsuariosCell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.net.URL;
import java.util.*;

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
    private ListView listViewUsuarios;
    @FXML
    private Circle circleEstado;

    private ObservableList<Usuario> listaUsuarios;
    private ObservableList<Asignacion> listaAsignacionesATarea;
    private ObservableList<Integrante> listaIntegrantesAProyecto;

    @FXML
    private TextField getNuevoNombreTarea;

    @FXML
    private ComboBox<String> estadoChoice;
    private ObservableList<String> listaEstados;
    private List<Estado> estados;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tareaActiva = EstadoPrograma.getInstance().getTareaActiva();
        proyectoActivo = EstadoPrograma.getInstance().getProyectoActivo();
        proyectoTitulo.setText("🔒 " + proyectoActivo.getTitulo());
        tareaHistorialNombre.setText(tareaActiva.getNombre());
        nuevoNombreTarea.setText(tareaActiva.getNombre());
        circleEstado.setStyle("-fx-fill: " + tareaActiva.getEstado().getColor() + ";");

        try {
            estados = EstadosBDD.getEstados();
        } catch (Exception e) {
            AppErrorHandler.manejar(e, "getEstados");
        }

        listaEstados = FXCollections.observableArrayList(new LinkedList<>());

        for (Estado estado : estados) {
            listaEstados.add(estado.getNombre());
        }
        estadoChoice.setItems(listaEstados);
        estadoChoice.setValue(tareaActiva.getEstado().getNombre());

        listarAsignados();
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
        listaAsignacionesATarea = FXCollections.observableArrayList(new ArrayList<>());
        listaIntegrantesAProyecto = FXCollections.observableArrayList(new ArrayList<>());

        try {
            asignados = AsignacionesBDD.getAsignaciones(tareaActiva);
            integrantes = IntegrantesBDD.getIntegrantes(proyectoActivo);
        } catch (Exception e) {
            AppErrorHandler.manejar(e, e.getCause().toString());
        }

        if (asignados != null) {
            listaAsignacionesATarea.addAll(asignados.values());
        }
        if (integrantes != null) {
            listaIntegrantesAProyecto.addAll(integrantes.values());
        }
    }

    private void listarAsignados() {
        listaUsuarios = FXCollections.observableList(new ArrayList<>());
        Map<Integer, Label> rols = new LinkedHashMap<>();

        refrescarDatos();

        System.out.println(listaAsignacionesATarea);
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
                circleEstado.setStyle("-fx-fill: " + tareaActiva.getEstado().getColor() + ";");
            }
            TareasBDD.actualizar(tareaActiva);
            Notificator.exito("Cambios Guardados", "Se han realizado con éxito las modificaciones de la tarea");
        } catch (Exception e) {
            AppErrorHandler.manejar(e, "actualizarTareaEdicion");
        }
    }
}
