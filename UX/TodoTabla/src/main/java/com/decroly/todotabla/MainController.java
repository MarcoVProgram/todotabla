package com.decroly.todotabla;

import com.decroly.todotabla.model.sql.ProyetosBDD;
import com.decroly.todotabla.utils.AppErrorHandler;
import com.decroly.todotabla.utils.EstadoPrograma;
import com.decroly.todotabla.utils.Navigator;
import com.decroly.todotabla.model.*;
import com.decroly.todotabla.utils.cells.ProyectosCell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controlador de la vista principal de la aplicación.
 * Muestra los proyectos activos y archivados en pestañas separadas, permite filtrarlos
 * por título y ofrece un menú contextual para archivar, restaurar o eliminar proyectos.
 */
public class MainController implements Initializable {

    private List<Proyecto> proyectos = new ArrayList<>();
    ObservableList<Proyecto> obsProyectos = FXCollections.observableList(proyectos);

    @FXML
    private Node root;

    @FXML
    private Label contAbiertos;

    @FXML
    private Label contArchivados;

    @FXML
    private ImageView changeImage;
    
    @FXML
    private TextField isEstado;

    @FXML
    private Label proyectosAbiertos;

    @FXML
    private Label proyectosArchivados;

    @FXML
    private ListView<Proyecto> listViewProyectos;

    private List<Proyecto> proyectoListActivos = new LinkedList<>();
    private ObservableList<Proyecto> obsProyectoListActivos = FXCollections.observableList(proyectoListActivos);

    private List<Proyecto> proyectoListArchivados = new LinkedList<>();
    private ObservableList<Proyecto> obsProyectoListArchivados = FXCollections.observableList(proyectoListArchivados);
    
    private boolean mostrandoArchivados = false;

    private FilteredList<Proyecto> filteredProyectos;
    
    @FXML
    public void initialize(URL url, ResourceBundle rb) {
        configurarContextMenu();
        updateLists();
        isEstado.textProperty().addListener((observable) -> filtrarProyectos());
    }

    /**
     * Recarga todos los proyectos desde la base de datos, los clasifica en activos y archivados,
     * actualiza los contadores y configura los listeners de las pestañas y el botón de cambio de vista.
     */
    public void updateLists() {
        obsProyectos.clear();
        obsProyectoListActivos.clear();
        obsProyectoListArchivados.clear();

        try {
            obsProyectos.addAll(ProyetosBDD.getProyectos().values());
        } catch (Exception e) {
            AppErrorHandler.manejar(e, "getProyectos");
        }

        for (Proyecto p : obsProyectos) {

            if (p.getFechaCierre() == null || p.getFechaCierre().isAfter(LocalDate.now())) {
                obsProyectoListActivos.add(p);
            }else{
                obsProyectoListArchivados.add(p);
            }
        }

        setTabActivos();
        listViewProyectos.setCellFactory(proyectoListView -> new ProyectosCell());

        contAbiertos.setText(String.valueOf(obsProyectoListActivos.size()));
        contArchivados.setText(String.valueOf(obsProyectoListArchivados.size()));

        proyectosAbiertos.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) setTabActivos();
        });

        proyectosArchivados.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) setTabArchivados();
        });

        changeImage.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                if (mostrandoArchivados) setTabActivos();
                else setTabArchivados();
            }
        });
    }

    /**
     * Muestra la pestaña de proyectos activos con su lista filtrada y el botón de crear proyecto
     * como placeholder si la lista está vacía.
     */
    private void setTabActivos() {
        filteredProyectos = new FilteredList<>(obsProyectoListActivos, p -> true);
        listViewProyectos.setItems(filteredProyectos);
        proyectosAbiertos.getStyleClass().setAll("tab-proyectos", "tab-selected");
        proyectosArchivados.getStyleClass().setAll("tab-proyectos", "tab-deselected");
        mostrandoArchivados = false;
        Button addButton = new Button("+ Crear Nuevo Proyecto");
        addButton.getStyleClass().add("placeholder-add-button");
        addButton.setOnAction(e -> abrirVentanaProyecto());
        addButton.setAlignment(Pos.TOP_CENTER);
        listViewProyectos.setPlaceholder(addButton);
    }

    /**
     * Muestra la pestaña de proyectos archivados con su lista filtrada.
     */
    private void setTabArchivados() {
        filteredProyectos = new FilteredList<>(obsProyectoListArchivados, p -> true);
        listViewProyectos.setItems(filteredProyectos);
        proyectosArchivados.getStyleClass().setAll("tab-proyectos", "tab-selected");
        proyectosAbiertos.getStyleClass().setAll("tab-proyectos", "tab-deselected");
        mostrandoArchivados = true;
        listViewProyectos.setPlaceholder(null);
    }

    /**
     * Configura el menú contextual de la lista de proyectos.
     * Permite archivar o restaurar un proyecto con clic secundario,
     * y navegar al tablero Kanban con doble clic.
     */
    private void configurarContextMenu() {
        ContextMenu contextMenu = new ContextMenu();

        MenuItem cerrarItem  = new MenuItem();
        MenuItem eliminarItem = new MenuItem("❌ Borrar proyecto");

        eliminarItem.setStyle("-fx-text-fill: #f85149;");

        contextMenu.getItems().addAll(cerrarItem, eliminarItem);

        cerrarItem.setOnAction(e -> {
            Proyecto selected = listViewProyectos.getSelectionModel().getSelectedItem();
            if (selected == null) return;
            try {
                selected.setFechaCierre(mostrandoArchivados ? null : LocalDate.now());
                ProyetosBDD.actualizar(selected);
            } catch (Exception ex) {
                AppErrorHandler.manejar(ex, "cerrarItem");
            }
            updateLists();
        });

        eliminarItem.setOnAction(e -> {
            Proyecto selected = listViewProyectos.getSelectionModel().getSelectedItem();
            if (selected == null) return;
            try {
                ProyetosBDD.borrar(selected);
            } catch (Exception ex) {
                AppErrorHandler.manejar(ex, "eliminarItem");
            }
            updateLists();
        });

        listViewProyectos.setOnMouseClicked(event -> {
            Node clickedNode = event.getPickResult().getIntersectedNode();
            while (clickedNode != null && !(clickedNode instanceof ProyectosCell)) {
                clickedNode = clickedNode.getParent();
            }

            if (!(clickedNode instanceof ListCell<?> cell) || cell.isEmpty()) {
                contextMenu.hide();
                return;
            }

            if (event.getButton() == MouseButton.SECONDARY) {

                if (mostrandoArchivados) {
                    cerrarItem.setText("🔓 Abrir proyecto");
                    cerrarItem.setStyle("-fx-text-fill: #3fb950;");
                } else {
                    cerrarItem.setText("🔒 Archivar proyecto");
                    cerrarItem.setStyle("-fx-text-fill: #e3b341;");
                }

                listViewProyectos.getSelectionModel().select((Proyecto) cell.getItem());
                contextMenu.show(listViewProyectos, event.getScreenX(), event.getScreenY());

            } else if (event.getClickCount() == 2) {
                contextMenu.hide();
                try {
                    if (cell.getItem() == null) return;
                    EstadoPrograma.getInstance().setProyectoActivo((Proyecto) cell.getItem());
                    abrirVentanaPrincipal();
                } catch (IOException e) {
                    AppErrorHandler.manejar(e, "abrirVentanaPrincipal");
                }
            }
        });
    }

    /**
     * Aplica el predicado de búsqueda sobre la lista filtrada de proyectos
     * según el texto introducido en el campo de búsqueda.
     */
    private void filtrarProyectos() {
        filteredProyectos.setPredicate(proyecto ->
                    this.isEstado.getText().isBlank() ||
                            proyecto.getTitulo().toLowerCase().contains(this.isEstado.getText().toLowerCase()) );
    }

    /**
     * Navega al tablero Kanban del proyecto activo.
     *
     * @throws IOException si el fichero FXML no puede cargarse
     */
    @FXML
    private void abrirVentanaPrincipal() throws IOException {
        Stage stage = (Stage) root.getScene().getWindow();
        Navigator.changeScene(stage, "/com/decroly/todotabla/kanban-view.fxml");
    }

    /**
     * Abre la ventana secundaria de creación de proyecto y recarga la lista al cerrarla.
     */
    @FXML
    private void abrirVentanaProyecto() {
        try {
            String fxml = "proyecto-form.fxml";
            String title = "Añadir proyecto";

            Navigator.arbrirVentanaSecundaria(fxml, title, getClass());

            updateLists();
            listViewProyectos.refresh();

        } catch (IOException e) {
            AppErrorHandler.manejar(e, "abrirVentanaProyecto (fxml)");
        }
    }

    /**
     * Abre la ventana secundaria de creación de usuarios.
     */
    @FXML
    private void abrirVentanaUsuarios() {
        try {
            Navigator.arbrirVentanaSecundaria("usuario-create.fxml", "Crear usuarios", getClass());
        } catch (IOException e) {
            AppErrorHandler.manejar(e, "abrirVentanaUsuarios (fxml)");
        }
    }
}