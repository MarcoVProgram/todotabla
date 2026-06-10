package com.decroly.todotabla;

import com.decroly.todotabla.model.Asignacion;
import com.decroly.todotabla.model.Integrante;
import com.decroly.todotabla.model.sql.AsignacionesBDD;
import com.decroly.todotabla.model.sql.IntegrantesBDD;
import com.decroly.todotabla.utils.AppErrorHandler;
import com.decroly.todotabla.utils.EstadoPrograma;
import com.decroly.todotabla.utils.Navigator;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;

/**
 * Controlador de la ventana de gestión de integrantes activos de un proyecto.
 * Muestra los integrantes activos y permite expulsarlos mediante un menú contextual,
 * lo que cierra su participación en el proyecto y archiva sus asignaciones activas.
 */
public class IntegrantesController implements Initializable {

    @FXML
    public ListView<Integrante> listViewIntegrantes;

    @FXML
    private Node root;

    @FXML
    public TextField buscarUsuario;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarContextMenu();
        updateLista();
    }

    /**
     * Recarga los integrantes activos del proyecto desde la base de datos
     * y actualiza la lista visible.
     */
    private void updateLista() {
        Map<Integer, Integrante> map = new HashMap<>();

        try {
            map = IntegrantesBDD.getIntegrantesActivos(
                    EstadoPrograma.getInstance().getProyectoActivo()
            );
        } catch (Exception e) {
            AppErrorHandler.manejar(e, "abrirVentanaIntegrantes");
        }

        ObservableList<Integrante> obsIntegrantesList =
                FXCollections.observableArrayList(map.values());

        obsIntegrantesList.addListener((ListChangeListener<Integrante>) change -> {
            listViewIntegrantes.refresh();
        });

        listViewIntegrantes.setItems(obsIntegrantesList);

        listViewIntegrantes.setCellFactory(integrantesList -> new ListCell<Integrante>() {

            @Override
            protected void updateItem(Integrante user, boolean empty) {
                super.updateItem(user, empty);

                if (empty || user == null) {

                    setGraphic(null);

                } else {

                    Label rol = new Label(user.getRol());
                    Label nombre = new Label(user.getIdUsuario().getNombre());

                    rol.getStyleClass().add("titulo-tarea");
                    nombre.getStyleClass().add("subTitulo-tarea");

                    Label fecha = new Label(String.valueOf(user.getFechaEntrada()));

                    fecha.getStyleClass().add("subTitulo2-tarea");

                    VBox card = new VBox(10, rol, nombre, fecha);

                    card.getStyleClass().add("task-card");

                    setGraphic(card);
                }
            }
        });
    }

    /**
     * Configura el menú contextual de la lista de integrantes.
     * Al hacer clic secundario sobre un integrante, ofrece la opción de expulsarlo,
     * lo que establece su fecha de salida y archiva sus asignaciones activas en el proyecto.
     */
    private void configurarContextMenu() {
        ContextMenu contextMenu = new ContextMenu();

        MenuItem cerrarItem  = new MenuItem();

        contextMenu.getItems().addAll(cerrarItem);

        cerrarItem.setOnAction(e -> {
            Integrante selected = listViewIntegrantes.getSelectionModel().getSelectedItem();
            if (selected == null) return;
            try {
                selected.setFechaSalida(LocalDate.now());
                IntegrantesBDD.actualizar(selected);

                Map<Integer, Asignacion> existente = AsignacionesBDD.getAsignacionsActivas(selected.getIdUsuario());
                for (Asignacion asignacion : existente.values()) {
                    if (asignacion.getIdTarea().getIdProyecto().equals(selected.getIdProyecto())) {
                        asignacion.setFechaFin(LocalDate.now());
                        AsignacionesBDD.actualizar(asignacion);
                    }
                }

            } catch (Exception ex) {
                AppErrorHandler.manejar(ex, "cerrarItem");
            }
            updateLista();
        });

        listViewIntegrantes.setOnMouseClicked(event -> {
            Node clickedNode = event.getPickResult().getIntersectedNode();
            while (clickedNode != null && !(clickedNode instanceof ListCell<?>)) {
                clickedNode = clickedNode.getParent();
            }

            if (!(clickedNode instanceof ListCell<?> cell) || cell.isEmpty()) {
                contextMenu.hide();
                return;
            }

            if (event.getButton() == MouseButton.SECONDARY) {

                cerrarItem.setText("👤 Expulsar Usuario");
                cerrarItem.setStyle("-fx-text-fill: #e3b341;");


                listViewIntegrantes.getSelectionModel().select((Integrante) cell.getItem());
                contextMenu.show(listViewIntegrantes, event.getScreenX(), event.getScreenY());

            }
        });
    }

    /**
     * Navega a la vista de gestión de usuarios del Kanban.
     *
     * @throws IOException si el fichero FXML no puede cargarse
     */
    @FXML
    private void irAUsuariosview() throws IOException { //abrir panel kanban
        Stage stage = (Stage) root.getScene().getWindow();
        Navigator.changeScene(stage, "/com/decroly/todotabla/usuarios-formUsuariosGestionKanban.fxml");
    }

    /**
     * Cierra la ventana secundaria actual.
     *
     * @throws IOException si ocurre un error al cerrar la ventana
     */
    @FXML
    private void Salir() throws IOException { //abrir panel kanban
        Stage stage = (Stage) root.getScene().getWindow();
        stage.close();
    }
}
