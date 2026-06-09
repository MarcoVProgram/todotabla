package com.decroly.todotabla;

import com.decroly.todotabla.model.sql.ProyetosBDD;
import com.decroly.todotabla.utils.AppErrorHandler;
import com.decroly.todotabla.utils.EstadoPrograma;
import com.decroly.todotabla.utils.Navigator;
import com.decroly.todotabla.model.*;
import com.decroly.todotabla.utils.cells.ProyectosCell;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    //lista miembros

    private List<Usuario> usuarios = new ArrayList<>();
    ObservableList<Usuario> obsUsuarios = FXCollections.observableList(usuarios);

    //lista tareas
    private List<Tarea> tareas = new ArrayList<>();
    ObservableList<Tarea> obsTareas = FXCollections.observableList(tareas);

    private List<Usuario> miembros = new ArrayList<>();
    ObservableList<Usuario> obsMiembros = FXCollections.observableList(miembros);

    
    //lista proyectos
    private List<Proyecto> proyectos = new ArrayList<>();
    ObservableList<Proyecto> obsProyectos = FXCollections.observableList(proyectos);

    //PESTAÑA INICIO
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

    private int cont = 0;
    
    @FXML
    public void initialize(URL url, ResourceBundle rb) {
        configurarContextMenu();
        updateLists();
    }

    public void updateLists() {
        List<Proyecto> allProyectos = new LinkedList<>();
        obsProyectoListActivos.clear();
        obsProyectoListArchivados.clear();

        try {
            allProyectos.addAll(ProyetosBDD.getProyectos().values());
        } catch (Exception e) {
            AppErrorHandler.manejar(e, "getProyectos");
        }

        for (Proyecto p : allProyectos) {

            if (p.getFechaCierre() == null) {
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

    private void setTabActivos() {
        listViewProyectos.setItems(obsProyectoListActivos);
        proyectosAbiertos.getStyleClass().setAll("tab-proyectos", "tab-selected");
        proyectosArchivados.getStyleClass().setAll("tab-proyectos", "tab-deselected");
        mostrandoArchivados = false;
        Button addButton = new Button("+ Crear Nuevo Proyecto");
        addButton.getStyleClass().add("placeholder-add-button");
        addButton.setOnAction(e -> abrirVentanaProyecto());
        listViewProyectos.setPlaceholder(addButton);
    }

    private void setTabArchivados() {
        listViewProyectos.setItems(obsProyectoListArchivados);
        proyectosArchivados.getStyleClass().setAll("tab-proyectos", "tab-selected");
        proyectosAbiertos.getStyleClass().setAll("tab-proyectos", "tab-deselected");
        mostrandoArchivados = true;
        listViewProyectos.setPlaceholder(null);
    }

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
                    cerrarItem.setText("🔒 Cerrar proyecto");
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


//----------------DESPLAZAMIENTO ENTRE VENTANAS-------------
    @FXML
    private void abrirVentanaPrincipal() throws IOException { //abrir panel kanban
        Stage stage = (Stage) root.getScene().getWindow();
        Navigator.changeScene(stage, "/com/decroly/todotabla/kanban-view.fxml");
    }

    @FXML
    private void abrirVentanaProyecto() { //panel proyecto
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

    @FXML
    private void abrirVentanaUsuarios() {
        try {
            Navigator.arbrirVentanaSecundaria("usuarios-create.fxml", "Crear usuarios", getClass());
        } catch (IOException e) {
            AppErrorHandler.manejar(e, "abrirVentanaUsuarios (fxml)");
        }
    }
}