package com.decroly.todotabla;

import com.decroly.todotabla.model.Estado;
import com.decroly.todotabla.model.*;
import com.decroly.todotabla.model.sql.EstadosBDD;
import com.decroly.todotabla.model.sql.TareasBDD;
import com.decroly.todotabla.utils.EstadoPrograma;
import com.decroly.todotabla.utils.Navigator;
import com.decroly.todotabla.utils.TareaCell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

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
    List<Estado> estados;

    @FXML
    private BorderPane root;

    private static Stage getVentanaSecundaria() {
        return HelloController.getVentanaSecundaria();
    }


    public void initialize(URL url, ResourceBundle rb) {
        estados = EstadosBDD.getEstados();
        proyectoSeleccionado = EstadoPrograma.getInstance().getProyectoActivo();
        actualizarTareas();
    }

    private void actualizarTareas() {
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
                case "Done":
                    tareasDone.clear();
                    tareasDone.addAll(TareasBDD.getTareas(estado, proyectoSeleccionado).values());
                    break;
                case "InReview":
                    tareasInReview.clear();
                    tareasInReview.addAll(TareasBDD.getTareas(estado, proyectoSeleccionado).values());
                    break;
                case "Ready":
                    tareasReady.clear();
                    tareasReady.addAll(TareasBDD.getTareas(estado, proyectoSeleccionado).values());
                    break;
                default:
                    // TODO nuevos estados
                    break;
            }
        }
        Map<ListView<Tarea>, ObservableList<Tarea>> columnMap = new LinkedHashMap<>();
        columnMap.put(listViewBacklog, obsTareasBacklog);
        columnMap.put(listViewReady, obsTareasReady);
        columnMap.put(listViewProgress, obsTareasProgress);
        columnMap.put(listViewReview, obsTareasReview);
        columnMap.put(listViewDone, obsTareasDone);


        listViewBacklog.setItems(TareaCell.sorted(obsTareasBacklog));
        listViewBacklog.setCellFactory(tareaListView -> new TareaCell(root, columnMap) {});

        listViewProgress.setItems(TareaCell.sorted(obsTareasProgress));
        listViewProgress.setCellFactory(tareaListView -> new TareaCell(root, columnMap) {});

        listViewDone.setItems(TareaCell.sorted(obsTareasDone));
        listViewDone.setCellFactory(tareaListView -> new TareaCell(root, columnMap) {});

        listViewReview.setItems(TareaCell.sorted(obsTareasReview));
        listViewReview.setCellFactory(tareaListView -> new TareaCell(root, columnMap) {});

        listViewReady.setItems(TareaCell.sorted(obsTareasReady));
        listViewReady.setCellFactory(tareaListView -> new TareaCell(root, columnMap) {});
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

            if(ventanaSecundaria.isFocused()){
                ventanaSecundaria.setAlwaysOnTop(true);
            }else{
                ventanaSecundaria.setAlwaysOnTop(false);
            }

//            listViewTareas.setItems(obsTareas);

            actualizarTareas();
            // Mostrar la ventana
            ventanaSecundaria.showAndWait();
            actualizarTareas();

            // TODO Hay que refrescar las listas del Kanban controller

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    public void buscarTarea(ActionEvent event) {
        String name = ""; // Señalar barra de busqueda
        obsTareasReady.addAll(TareasBDD.getTarea(name, EstadoPrograma.getInstance().getProyectoActivo()));

    }


}
