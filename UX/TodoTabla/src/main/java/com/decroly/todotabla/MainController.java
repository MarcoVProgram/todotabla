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



    //VARIABLES AUXILIARES
    private static Stage ventanaSecundaria;

    public static Stage getVentanaSecundaria() {
        return ventanaSecundaria;
    }

    private int cont = 0;
    
    @FXML
    public void initialize(URL url, ResourceBundle rb) {

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

        listViewProyectos.setOnMouseClicked(event -> {
            if(event.getButton() == MouseButton.SECONDARY || event.getClickCount() == 2){
                try {
                    EstadoPrograma.getInstance().setProyectoActivo(listViewProyectos.getSelectionModel().getSelectedItem());
                    abrirVentanaPrincipal();

                } catch (IOException e) {
                    AppErrorHandler.manejar(e, "abrirVentanaPrincipal");
                }
            }
        });

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


//----------------DESPLAZAMIENTO ENTRE VENTANAS-------------
    @FXML
    private void abrirVentanaPrincipal() throws IOException { //abrir panel kanban
        Stage stage = (Stage) root.getScene().getWindow();
        Navigator.changeScene(stage, "/com/decroly/todotabla/kanban-view.fxml");
    }

    @FXML
    private void abrirVentanaProyecto() { //panel proyecto
        try {

            if(ventanaSecundaria != null && ventanaSecundaria.isShowing()){
                System.out.println("No se puede volver a abrir, hay una sesion existente");
                return;
            }

            // Cargar el archivo FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("proyecto-form.fxml"));
            Parent root = loader.load();

            // Crear una nueva ventana (Stage)
            ventanaSecundaria = new Stage();
            ventanaSecundaria.setTitle("Añadir proyecto");
            ventanaSecundaria.setScene(new Scene(root));

            ventanaSecundaria.setResizable(false);
            ventanaSecundaria.setAlwaysOnTop(false);

//            listViewTareas.setItems(obsTareas);

            // Mostrar la ventana
            ventanaSecundaria.showAndWait();

            updateLists();
            listViewProyectos.refresh();

        } catch (IOException e) {
            AppErrorHandler.manejar(e, "abrirVentanaProyecto (fxml)");
        }
    }
}