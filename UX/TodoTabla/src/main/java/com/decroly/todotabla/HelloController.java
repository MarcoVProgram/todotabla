package com.decroly.todotabla;

import com.decroly.todotabla.model.Usuario;
import com.decroly.todotabla.model.sql.BDD;
import com.decroly.todotabla.model.sql.ProyetosBDD;
import com.decroly.todotabla.utils.EstadoPrograma;
import com.decroly.todotabla.utils.Navigator;
import com.decroly.todotabla.model.*;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
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
import javafx.util.Duration;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.decroly.todotabla.model.sql.*;
import org.w3c.dom.events.MouseEvent;

public class HelloController implements Initializable {
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
    private Button panelKanbanBtn;

    @FXML
    private Node root;

    @FXML
    private ListView<Proyecto> listViewProyectos;
        List<Proyecto> proyectoList = new ArrayList<>();
        ObservableList<Proyecto> obsProyectoList = FXCollections.observableList(proyectoList);

    public ListView<Proyecto> getListViewProyectos() {
        return listViewProyectos;
    }

    @FXML
    private ComboBox<opcionesBase> comboBoxOpcion;
        List<opcionesBase> opcionesBaseList = new ArrayList<>();

        ObservableList<opcionesBase> obsOpcionesBase = FXCollections.observableList(opcionesBaseList);

    public ComboBox<opcionesBase> getComboBoxOpcion() {
        return comboBoxOpcion;
    }



    //VARIABLES AUXILIARES
    private static Stage ventanaSecundaria;

    public static Stage getVentanaSecundaria() {
        return ventanaSecundaria;
    }



    @FXML
    public void initialize(URL url, ResourceBundle rb) {
        try {
            Connection x = BDD.getConnection();
            x.close();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "No se ha podido conectar a la base de datos, saliendo", ButtonType.CLOSE);
            alert.showAndWait();
            Platform.exit();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "No se ha podido acceder a los archivos de configuracion, saliendo", ButtonType.CLOSE);
            alert.showAndWait();
            Platform.exit();
        } catch (URISyntaxException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Ha habido un fallo al convertir URL a URI, saliendo", ButtonType.CLOSE);
            alert.showAndWait();
            Platform.exit();
        } catch (ClassNotFoundException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "No se ha podido encontrar driver de JDBC, saliendo", ButtonType.CLOSE);
            alert.showAndWait();
            Platform.exit();
        }


        comboBoxOpcion.getItems().addAll(opcionesBase.values());
        listViewProyectos.setItems(obsProyectoList);

        obsProyectoList.addAll(ProyetosBDD.getProyectos().values());


//        PauseTransition delay = new PauseTransition(Duration.seconds(2));
//
//        delay.setOnFinished(event -> {
//            comprobarProyecto();
//        });
//
//        delay.play();
        listViewProyectos.setCellFactory(proyectoListView -> new ListCell<Proyecto>() {

            @Override
            protected void updateItem(Proyecto p, boolean empty) {
                super.updateItem(p, empty);

                if (empty || p == null) {

                    setGraphic(null);

                } else {

                    // Título
                    Label titulo = new Label(p.getTitulo());
                    titulo.getStyleClass().add("titulo-tarea");

                    // Fecha fin
                    Label inicio = new Label("Fecha inicio: " + p.getFechaCreacion());

                     //Fecha fin
                    Label fin = new Label("Fecha fin: " + p.getFechaCierre());

                    if(fin.getText().contentEquals("Fecha fin: " + null)){
                        fin.setText("");
                    }

                    // Card completa
                    VBox card = new VBox(10, titulo, inicio, fin);

                    card.getStyleClass().add("task-card");

                    setGraphic(card);
                }
            }
        });

        listViewProyectos.setOnMouseClicked(event -> {
            if(event.getButton() == MouseButton.SECONDARY || event.getClickCount() == 2){
                try {
                    EstadoPrograma.getInstance().setProyectoActivo(listViewProyectos.getSelectionModel().getSelectedItem());
                    abrirVentanaPrincipal();
                } catch (IOException e) {
                    showAlert("Ocurrió un error inesperado y no se puede acceder al proyecto", "Cagaste");
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

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    @FXML
    private void comprobarProyecto(){
        if(comboBoxOpcion.getSelectionModel().getSelectedItem() != null){
            switch (comboBoxOpcion.getSelectionModel().getSelectedItem()){
                case VER_KANBAN -> {
                    if(listViewProyectos.getSelectionModel().getSelectedItem() != null){
                        try {
                            KanBanController.proyectoSeleccionado = listViewProyectos.getSelectionModel().getSelectedItem();
                            abrirVentanaPrincipal();
                        } catch (IOException e) {
                            showAlert("Ocurrió un error inesperado y no se puede acceder al proyecto", "Cagaste");
                        }
                    }
                }
                case BORRAR_PROYECTO -> {
                    if(listViewProyectos.getSelectionModel().getSelectedItem() != null){
                        int id = listViewProyectos.getSelectionModel().getSelectedItem().getId();

                        obsProyectoList.remove(id);
                    }
                }
            }
        }
    }


}