package com.decroly.todotabla;

import com.decroly.todotabla.model.Estado;
import com.decroly.todotabla.model.*;
import com.decroly.todotabla.model.sql.EstadosBDD;
import com.decroly.todotabla.model.sql.TareasBDD;
import com.decroly.todotabla.utils.EstadoPrograma;
import com.decroly.todotabla.utils.Navigator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class KanBanController implements Initializable {
    @FXML
    private ImageView returnBtn;

    //LISTAS
    @FXML
    private ListView<Tarea> listViewBacklog;
    List<Tarea> tareasBacklog = new ArrayList<>();
    ObservableList<Tarea> obsTareasBacklog = FXCollections.observableList(tareasBacklog);

    @FXML
    private ListView<Tarea> listViewReady;
    List<Tarea> tareasReady = new ArrayList<>();
    ObservableList<Tarea> obsTareasReady = FXCollections.observableList(tareasReady);

    @FXML
    private ListView<Tarea> listViewProgress;
    List<Tarea> tareasInProgress = new ArrayList<>();
    ObservableList<Tarea> obsTareasProgress = FXCollections.observableList(tareasInProgress);

    @FXML
    private ListView<Tarea> listViewReview;
    List<Tarea> tareasInReview = new ArrayList<>();
    ObservableList<Tarea> obsTareasReview = FXCollections.observableList(tareasInReview);

    @FXML
    private ListView<Tarea> listViewDone;
    List<Tarea> tareasDone = new ArrayList<>();
    ObservableList<Tarea> obsTareasDone = FXCollections.observableList(tareasDone);
    
    List<Integrante> integrantes = new ArrayList<>();
    ObservableList<Integrante> obsIntegrantes = FXCollections.observableList(integrantes);


    private Stage ventanaSecundaria = getVentanaSecundaria();

    private Proyecto proyectoSeleccionado;

    private static Stage getVentanaSecundaria() {
        return HelloController.getVentanaSecundaria();
    }


    public void initialize(URL url, ResourceBundle rb) {
        List<Estado> estados = EstadosBDD.getEstados();
        proyectoSeleccionado = EstadoPrograma.getInstance().getProyectoActivo();
        for (Estado estado : estados) {
            switch (estado.getNombre()) {
                case "Backlog":
                    tareasBacklog.clear();
                    tareasBacklog.addAll(TareasBDD.getTareas(estado, proyectoSeleccionado).values());
                    break;
                case "InProgress":
                    tareasInProgress.clear();
                    tareasInProgress.addAll(TareasBDD.getTareas(estado, proyectoSeleccionado).values());
                    break;
                case "Ready":
                    tareasReady.clear();
                    tareasReady.addAll(TareasBDD.getTareas(estado, proyectoSeleccionado).values());
                    break;
                case "InReview":
                    tareasInReview.clear();
                    tareasInReview.addAll(TareasBDD.getTareas(estado, proyectoSeleccionado).values());
                    break;
                case "Done":
                    tareasDone.clear();
                    tareasDone.addAll(TareasBDD.getTareas(estado, proyectoSeleccionado).values());
                    break;
                default:
                    // TODO nuevos estados
                    break;
            }
        }

        listViewBacklog.setItems(obsTareasBacklog);
        listViewBacklog.setCellFactory(tareaListView -> new ListCell<Tarea>() {
            @Override
            protected void updateItem(Tarea tarea, boolean empty) {
                super.updateItem(tarea, empty);

                if (empty || tarea == null) {
                    setGraphic(null);

                } else {
                    // Título
                    Label titulo = new Label(tarea.getNombre());
                    titulo.getStyleClass().add("titulo-tarea");

                    // Prioridad
                    Label prioridad = new Label("Prioridad: " + tarea.getPrioridad());

                    // Imagen integrante
                    ImageView avatar = new ImageView(
                            new Image(
                                    HelloApplication.class
                                            .getResource("/com/decroly/todotabla/images/user.png")
                                            .toExternalForm()
                            )
                    );

                    avatar.setFitWidth(40);
                    avatar.setFitHeight(40);

                    // Hacerla circular
                    Circle clip = new Circle(20, 20, 20);
                    avatar.setClip(clip);

                    // Parte superior de la card
                    HBox top = new HBox(10, avatar, titulo);

                    // Card completa
                    VBox card = new VBox(10, top, prioridad);

                    card.getStyleClass().add("task-card");

                    setGraphic(card);
                }
            }
        });

        listViewReady.setItems(obsTareasReady);
        listViewReady.setCellFactory(tareaListView -> new ListCell<Tarea>() {
            @Override
            protected void updateItem(Tarea tarea, boolean empty) {
                super.updateItem(tarea, empty);

                if (empty || tarea == null) {
                    setGraphic(null);

                } else {
                    // Título
                    Label titulo = new Label(tarea.getNombre());
                    titulo.getStyleClass().add("titulo-tarea");

                    // Prioridad
                    Label prioridad = new Label("Prioridad: " + tarea.getPrioridad());

                    // Imagen integrante
                    ImageView avatar = new ImageView(
                            new Image(
                                    HelloApplication.class
                                            .getResource("/com/decroly/todotabla/images/user.png")
                                            .toExternalForm()
                            )
                    );

                    avatar.setFitWidth(40);
                    avatar.setFitHeight(40);

                    // Hacerla circular
                    Circle clip = new Circle(20, 20, 20);
                    avatar.setClip(clip);

                    // Parte superior de la card
                    HBox top = new HBox(10, avatar, titulo);

                    // Card completa
                    VBox card = new VBox(10, top, prioridad);

                    card.getStyleClass().add("task-card");

                    setGraphic(card);
                }
            }
        });

        //listView tareas progress
        listViewProgress.setItems(obsTareasProgress);
        listViewProgress.setCellFactory(obstareasInProgress -> new ListCell<Tarea>() {

            @Override
            protected void updateItem(Tarea tarea, boolean empty) {
                super.updateItem(tarea, empty);

                if (empty || tarea == null) {
                    setGraphic(null);
                } else {
// Título
                    Label titulo = new Label(tarea.getNombre());
                    titulo.getStyleClass().add("titulo-tarea");

                    // Prioridad
                    Label prioridad = new Label("Prioridad: " + tarea.getPrioridad());

                    // Imagen integrante
                    ImageView avatar = new ImageView(
                            new Image(
                                    HelloApplication.class
                                            .getResource("/com/decroly/todotabla/images/user.png")
                                            .toExternalForm()
                            )
                    );

                    avatar.setFitWidth(40);
                    avatar.setFitHeight(40);

                    // Hacerla circular
                    Circle clip = new Circle(20, 20, 20);
                    avatar.setClip(clip);

                    // Parte superior de la card
                    HBox top = new HBox(10, avatar, titulo);

                    // Card completa
                    VBox card = new VBox(10, top, prioridad);

                    card.getStyleClass().add("task-card");

                    setGraphic(card);
                }
            }
        });

        //listView tareas review
        listViewReview.setItems(obsTareasReview);
        listViewReview.setCellFactory(obstareasInReview -> new ListCell<Tarea>() {

            @Override
            protected void updateItem(Tarea tarea, boolean empty) {
                super.updateItem(tarea, empty);

                if (empty || tarea == null) {
                    setGraphic(null);
                } else {
// Título
                    Label titulo = new Label(tarea.getNombre());
                    titulo.getStyleClass().add("titulo-tarea");

                    // Prioridad
                    Label prioridad = new Label("Prioridad: " + tarea.getPrioridad());

                    // Imagen integrante
                    ImageView avatar = new ImageView(
                            new Image(
                                    HelloApplication.class
                                            .getResource("/com/decroly/todotabla/images/user.png")
                                            .toExternalForm()
                            )
                    );

                    avatar.setFitWidth(40);
                    avatar.setFitHeight(40);

                    // Hacerla circular
                    Circle clip = new Circle(20, 20, 20);
                    avatar.setClip(clip);

                    // Parte superior de la card
                    HBox top = new HBox(10, avatar, titulo);

                    // Card completa
                    VBox card = new VBox(10, top, prioridad);

                    card.getStyleClass().add("task-card");

                    setGraphic(card);
                }
            }
        });

        //listView tareas done
        listViewDone.setItems(obsTareasDone);
        listViewDone.setCellFactory(obstareasDone -> new ListCell<Tarea>() {

            @Override
            protected void updateItem(Tarea tarea, boolean empty) {
                super.updateItem(tarea, empty);

                if (empty || tarea == null) {
                    setGraphic(null);
                } else {
// Título
                    Label titulo = new Label(tarea.getNombre());
                    titulo.getStyleClass().add("titulo-tarea");

                    // Prioridad
                    Label prioridad = new Label("Prioridad: " + tarea.getPrioridad());

                    // Imagen integrante
                    ImageView avatar = new ImageView(
                            new Image(
                                    HelloApplication.class
                                            .getResource("/com/decroly/todotabla/images/user.png")
                                            .toExternalForm()
                            )
                    );

                    avatar.setFitWidth(40);
                    avatar.setFitHeight(40);

                    // Hacerla circular
                    Circle clip = new Circle(20, 20, 20);
                    avatar.setClip(clip);

                    // Parte superior de la card
                    HBox top = new HBox(10, avatar, titulo);

                    // Card completa
                    VBox card = new VBox(10, top, prioridad);

                    card.getStyleClass().add("task-card");

                    setGraphic(card);
                }
            }
        });
    }




    //----------------DESPLAZAMIENTO ENTRE VENTANAS-------------
    @FXML
    private void returnToMain() throws IOException { //abrir pantalla principal (menú)
        Stage stage = (Stage) returnBtn.getScene().getWindow();
        Navigator.changeScene(stage, "/com/decroly/todotabla/main-view.fxml");
    }

    @FXML
    private void abrirVentanaTarea() { //panel tarea
        try {

            if(ventanaSecundaria != null && ventanaSecundaria.isShowing()){
                System.out.println("No se puede volver a abrir, hay una sesion existente");
                return;
            }

            // Cargar el archivo FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("tarea-view.fxml"));
            Parent root = loader.load();

            // Crear una nueva ventana (Stage)
            ventanaSecundaria = new Stage();
            ventanaSecundaria.setTitle("Añadir tarea");
            ventanaSecundaria.setScene(new Scene(root));

            ventanaSecundaria.setResizable(false);
            ventanaSecundaria.setAlwaysOnTop(false);

//            listViewTareas.setItems(obsTareas);

            // Mostrar la ventana
            ventanaSecundaria.showAndWait();

            // TODO Hay que refrescar las listas del Kanban controller

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    public void buscarTarea(ActionEvent event) {
        String name = ""; // Señalar barra de busqueda
        obsTareasReady.addAll(TareasBDD.getTarea(name));

    }


}
