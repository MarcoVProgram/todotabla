package com.decroly.todotabla;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import com.decroly.todotabla.model.sql.*;
import com.decroly.todotabla.utils.AppErrorHandler;
import com.decroly.todotabla.utils.EstadoPrograma;
import com.decroly.todotabla.utils.Navigator;
import com.decroly.todotabla.utils.Notificator;
import com.decroly.todotabla.utils.cells.HistorialTareaCell;
import com.decroly.todotabla.utils.cells.UsuariosCell;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
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
    private ListView<Usuario> listViewAsignaciones; // Historial de asignaciones

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

        Stage ventanaSecundaria = MainController.getVentanaSecundaria();

        if(ventanaSecundaria != null && ventanaSecundaria.isShowing()){
                System.out.println("No se puede volver a abrir, hay una sesion existente");
                return;
        }

        // Cargar el archivo FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("usuarios-formAsignarTarea.fxml"));
            Parent root;
            try {
                root = loader.load();
            // Crear una nueva ventana (Stage)
            ventanaSecundaria = new Stage();
            ventanaSecundaria.setTitle("Añadir tarea");
            ventanaSecundaria.setScene(new Scene(root));

            ventanaSecundaria.setResizable(false);

            if (ventanaSecundaria.isFocused()){
                ventanaSecundaria.setAlwaysOnTop(true);
            }else{
                ventanaSecundaria.setAlwaysOnTop(false);
            }

//            listViewTareas.setItems(obsTareas);

            // Mostrar la ventana
            ventanaSecundaria.showAndWait();

                for (Usuario u : TareaAsignarController.getlistaAAsignar()) {

                    listaUsuarios.add(u);

                    System.out.println(u);
                }

                

                // listViewUsuarios.refresh();

            } catch (IOException e) {
                AppErrorHandler.manejar(e, "load the loader");
            }
    }

//    @FXML
//    private void desasignar() {
//        ObservableList<Usuario> usuariosRemove = listViewUsuarios.getSelectionModel().getSelectedItems();
//
//        for(Usuario u : usuariosRemove){
//         listaUsuarios.remove(u);
//        }
//
//            listViewUsuarios.refresh();
//            refrescarDatos();
//            listarAsignadosPasados();
//
//    }

//    @FXML
//    private void desasignar() {
//        List<Usuario> usuariosRemove = new ArrayList<>(listViewUsuarios.getSelectionModel().getSelectedItems());
//
//        if (usuariosRemove.isEmpty()) {
//            return;
//        }
//
//        for (Usuario u : usuariosRemove) {
//            listaUsuarios.remove(u);
//            // Nota: Aquí deberías incluir también la lógica para borrar de la Base de Datos
//            // si es que no la tienes ya delegada dentro de refrescarDatos() o en otro punto.
//        }
//
//        try {
//            
//            Asignacion a;
//            for(Asignacion asign : AsignacionesBDD.getAsignacionesActivas(EstadoPrograma.getInstance().getTareaActiva()).values()){
//                a = AsignacionesBDD.getAsignacion(asign.getId());
//                
//                if(usuariosRemove.contains(a.getIdUsuario()) && listViewUsuarios.getSelectionModel().getSelectedItems().contains(a.getId())){
//                    AsignacionesBDD.actualizar()
//                }
//            }
//
//            listViewUsuarios.refresh();
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//        refrescarDatos();
//        listarAsignados();
//
//        usuariosRemove.clear();
//    }

//    @FXML
//    private void desasignar() {
//        List<Usuario> usuariosALaborar = new ArrayList<>(listViewUsuarios.getSelectionModel().getSelectedItems());
//
//        if (usuariosALaborar.isEmpty()) {
//            return;
//        }
//
//        try {
//
//            Map<Integer, Asignacion> asignacionesActivas = AsignacionesBDD.getAsignacionesActivas(
//                    EstadoPrograma.getInstance().getTareaActiva()
//            );
//
//            if (asignacionesActivas != null) {
//                for (Asignacion asign : asignacionesActivas.values()) {
//
//                    if (usuariosALaborar.contains(asign.getIdUsuario())) {
//                        asign.setFechaFin(LocalDate.now());
//
//                        AsignacionesBDD.actualizar(asign);
//                    }
//                }
//            }
//
//        } catch (Exception e) {
//            AppErrorHandler.manejar(e, "Error al actualizar la base de datos al desasignar.");
//        }
//
//        refrescarDatos();
//        listarAsignados();
//        listarAsignadosPasados();
//    }

//    @FXML
//    private void desasignar() {
//        // 1. Clonamos los usuarios seleccionados (Tu idea original para evitar el error de índices)
//        List<Usuario> usuariosRemove = new ArrayList<>(listViewUsuarios.getSelectionModel().getSelectedItems());
//
//        if (usuariosRemove.isEmpty()) {
//            return;
//        }
//
//        // 2. RESPUESTA VISUAL INSTANTÁNEA: Borramos de la lista local primero
//        // Esto hace que los usuarios desaparezcan de la pantalla DE INMEDIATO sin congelarse
//        for (Usuario u : usuariosRemove) {
//            listaUsuarios.remove(u);
//        }
//        listViewUsuarios.refresh();
//
//        // 3. PROCESAR EN BASE DE DATOS (Optimizado para evitar consultas repetitivas)
//        try {
//            // Traemos las asignaciones activas UNA SOLA VEZ (Evita el bucle lento)
//            Map<Integer, Asignacion> asignacionesActivas = AsignacionesBDD.getAsignacionesActivas(
//                    EstadoPrograma.getInstance().getTareaActiva()
//            );
//
//            if (asignacionesActivas != null) {
//                for (Asignacion asign : asignacionesActivas.values()) {
//                    // Si el usuario de la asignación está entre los que eliminamos
//                    if (usuariosRemove.contains(asign.getIdUsuario())) {
//
//                        // Marcamos el fin de la asignación
//                        asign.setFechaFin(LocalDate.now());
//
//                        // Actualizamos en la BDD.
//                        // NOTA: Si tu método AsignacionesBDD permite actualizar en lote (batch), sería ideal.
//                        AsignacionesBDD.actualizar(asign);
//                    }
//                }
//            }
//        } catch (Exception e) {
//            AppErrorHandler.manejar(e, "Error al actualizar la base de datos.");
//        }
//
//        // 4. Refrescamos el resto de las listas secundarias al final
//        refrescarDatos();
//        listarAsignadosPasados();
//    }

//    @FXML
//    private void desasignar() {
//        // 1. Tu lógica original (con el fix de la copia para evitar el error de índices)
//        List<Usuario> usuariosRemove = new ArrayList<>(listViewUsuarios.getSelectionModel().getSelectedItems());
//
//        if (usuariosRemove.isEmpty()) {
//            return;
//        }
//
//        try {
//            // 2. Insertamos la Base de Datos aquí mismo de forma directa
//            int idTarea = EstadoPrograma.getInstance().getTareaActiva().getId();
//
//            for (Usuario u : usuariosRemove) {
//                // Quitamos de la lista local (tu código original)
//                listaUsuarios.remove(u);
//
//                // LLAMADA DIRECTA: Le pasamos el ID del usuario y de la tarea
//                AsignacionesBDD.darDeBajaAsignacion(u.getId(), idTarea);
//            }
//
//            // 3. Tu refresco original de la interfaz
//            listViewUsuarios.refresh();
//            refrescarDatos();
//            listarAsignadosPasados();
//            listarAsignados();
//
//        } catch (Exception e) {
//            AppErrorHandler.manejar(e, "Error al desasignar el usuario en la Base de Datos.");
//        }
//    }

//    @FXML
//    private void desasignar() {
//        // 1. Clonamos la selección para evitar el error de índices
//        List<Usuario> usuariosRemove = new ArrayList<>(listViewUsuarios.getSelectionModel().getSelectedItems());
//
//        if (usuariosRemove.isEmpty()) {
//            return;
//        }
//
//        // 2. RESPUESTA VISUAL INSTANTÁNEA: Modificamos la interfaz primero
//        // Al hacer esto antes de la BDD, el usuario ve que se borran EN MILISEGUNDOS. Cero retraso.
//        for (Usuario u : usuariosRemove) {
//            listaUsuarios.remove(u);
//        }
//        listViewUsuarios.refresh();
//
//        // 3. LA BASE DE DATOS SE EJECUTA DESPUÉS (En segundo plano)
//        int idTarea = EstadoPrograma.getInstance().getTareaActiva().getId();
//
//        // Creamos un hilo secundario para que la consulta no bloquee la pantalla
//        new Thread(() -> {
//            try {
//                // El bucle de la BDD se ejecuta aquí sin molestar al usuario
//                for (Usuario u : usuariosRemove) {
//                    AsignacionesBDD.darDeBajaAsignacion(u.getId(), idTarea);
//                }
//
//                // 4. ACTUALIZACIÓN FINAL DE SEGUNDAS LISTAS
//                // Como ya terminó la BDD, le pedimos a JavaFX que actualice el historial
//                javafx.application.Platform.runLater(() -> {
//                    refrescarDatos();
//                    listarAsignadosPasados();
//                    listarAsignados();
//                });
//
//            } catch (Exception e) {
//                // Si la base de datos falla en segundo plano, avisamos sin romper la app
//                javafx.application.Platform.runLater(() -> {
//                    AppErrorHandler.manejar(e, "La operación falló en el servidor, pero la vista se actualizó localmente.");
//                });
//            }
//        }).start(); // ¡Arranca el proceso en segundo plano!
//    }

    @FXML
    private void desasignar() {
        // 1. Clonamos la selección para evitar errores de índices
        List<Usuario> usuariosRemove = new ArrayList<>(listViewUsuarios.getSelectionModel().getSelectedItems());

        if (usuariosRemove.isEmpty()) {
            return;
        }

        try {
            int idTarea = EstadoPrograma.getInstance().getTareaActiva().getId();

            // 2. ÚNICA CONSULTA A LA BDD: Rápida y directa al grano
            for (Usuario u : usuariosRemove) {
                AsignacionesBDD.darDeBajaAsignacion(u.getId(), idTarea);
            }

            // 3. ACTUALIZACIÓN LOCAL MÍNIMA (Reemplaza a refrescarDatos)
            for (Usuario u : usuariosRemove) {
                // Lo quitamos de la lista visual de la izquierda (Actuales)
                listaUsuarios.remove(u);

                // Buscamos su asignación en la lista local para actualizarle la fecha de fin
                // de esta forma el historial se enterará del cambio sin ir a la BDD
                for (Asignacion asign : listaHistorialAsignaciones) {
                    if (asign.getIdUsuario().equals(u) && asign.getFechaFin() == null) {
                        asign.setFechaFin(LocalDate.now());
                        break;
                    }
                }
            }

            // 4. REDIBUJAR LA INTERFAZ
            listViewUsuarios.refresh();

            // En vez de recargar todo de la BDD, solo volvemos a pintar el historial
            // con los datos que ya editamos en el paso anterior
            listarAsignadosPasados();

        } catch (Exception e) {
            AppErrorHandler.manejar(e, "Error al desasignar.");
        }
    }


}
