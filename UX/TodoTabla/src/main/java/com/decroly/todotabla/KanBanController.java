package com.decroly.todotabla;

import com.decroly.todotabla.model.Estado;
import com.decroly.todotabla.model.Tarea;
import com.decroly.todotabla.model.sql.EstadosBDD;
import com.decroly.todotabla.model.sql.TareasBDD;
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
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
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


    private Stage ventanaSecundaria = getVentanaSecundaria();

    private static Stage getVentanaSecundaria() {
        return HelloController.getVentanaSecundaria();
    }


    public void initialize(URL url, ResourceBundle rb) {
        List<Estado> estados = EstadosBDD.getEstados();
        for (Estado estado : estados) {
            switch (estado.getNombre()) {
                case "Backlog":
                    tareasBacklog.clear();
                    tareasBacklog.addAll(TareasBDD.getTareas(estado).values());
                    break;
                case "InProgress":
                    tareasInProgress.clear();
                    tareasInProgress.addAll(TareasBDD.getTareas(estado).values());
                    break;
                case "Ready":
                    tareasReady.clear();
                    tareasReady.addAll(TareasBDD.getTareas(estado).values());
                    break;
                case "InReview":
                    tareasInReview.clear();
                    tareasInReview.addAll(TareasBDD.getTareas(estado).values());
                    break;
                case "Done":
                    tareasDone.clear();
                    tareasDone.addAll(TareasBDD.getTareas(estado).values());
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

                    Label titulo = new Label(tarea.getNombre());
                    titulo.getStyleClass().add("titulo-tarea");

                    Label prioridad = new Label("Prioridad: " + tarea.getPrioridad());

                    VBox card = new VBox(8, titulo, prioridad);
                    card.getStyleClass().add("kanban-list");

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

                    Label titulo = new Label(tarea.getNombre());
                    titulo.getStyleClass().add("titulo-tarea");

                    Label prioridad = new Label("Prioridad: " + tarea.getPrioridad());

                    VBox card = new VBox(8, titulo, prioridad);
                    card.getStyleClass().add("kanban-list");

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

                    Label titulo = new Label(tarea.getNombre());
                    titulo.getStyleClass().add("titulo-tarea");

                    Label prioridad = new Label("Prioridad: " + tarea.getPrioridad());

                    VBox card = new VBox(8, titulo, prioridad);
                    card.getStyleClass().add("kanban-list");

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

                    Label titulo = new Label(tarea.getNombre());
                    titulo.getStyleClass().add("titulo-tarea");

                    Label prioridad = new Label("Prioridad: " + tarea.getPrioridad());

                    VBox card = new VBox(8, titulo, prioridad);
                    card.getStyleClass().add("kanban-list");

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

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    public void editarTarea(ActionEvent event) {
        System.out.println("Caca \n");
    }
}
