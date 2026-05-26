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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class KanBanController implements Initializable {
    @FXML
    private ImageView returnBtn;

    //LISTAS
    @FXML
    private ListView<Tarea> listViewBacklog;
    private List<Tarea> tareasBacklog = new ArrayList<>();
    private ObservableList<Tarea> obsTareasBacklog = FXCollections.observableList(tareasBacklog);

    @FXML
    private ListView<Tarea> listViewReady;
    private List<Tarea> tareasReady = new ArrayList<>();
    private ObservableList<Tarea> obsTareasReady = FXCollections.observableList(tareasReady);

    @FXML
    private ListView<Tarea> listViewProgress;
    private List<Tarea> tareasInProgress = new ArrayList<>();
    private ObservableList<Tarea> obsTareasProgress = FXCollections.observableList(tareasInProgress);

    @FXML
    private ListView<Tarea> listViewReview;
    private List<Tarea> tareasInReview = new ArrayList<>();
    private ObservableList<Tarea> obsTareasReview = FXCollections.observableList(tareasInReview);

    @FXML
    private ListView<Tarea> listViewDone;
    private List<Tarea> tareasDone = new ArrayList<>();
    ObservableList<Tarea> obsTareasDone = FXCollections.observableList(tareasDone);
    
    private List<Integrante> integrantes = new ArrayList<>();
    private ObservableList<Integrante> obsIntegrantes = FXCollections.observableList(integrantes);


    private Stage ventanaSecundaria = getVentanaSecundaria();

    private Proyecto proyectoSeleccionado;
    private List<Estado> estados;

    private ListView<Tarea>[] tareasOrdenadas;

    @FXML
    private BorderPane root;

    private static Stage getVentanaSecundaria() {
        return MainController.getVentanaSecundaria();
    }


    public void initialize(URL url, ResourceBundle rb) {
        estados = EstadosBDD.getEstados();
        tareasOrdenadas = new  ListView[estados.size()];
        proyectoSeleccionado = EstadoPrograma.getInstance().getProyectoActivo();
        actualizarTareas();
    }

    private void actualizarTareas() {
        for (Estado estado : estados) {
            switch (estado.getNombre()) {
                case "Pendiente":
                    tareasBacklog.clear();
                    tareasBacklog.addAll(TareasBDD.getTareas(estado, proyectoSeleccionado).values());
                    tareasOrdenadas[estado.getOrden()-1] = listViewBacklog;
                    break;
                case "En Curso":
                    tareasInProgress.clear();
                    tareasInProgress.addAll(TareasBDD.getTareas(estado, proyectoSeleccionado).values());
                    tareasOrdenadas[estado.getOrden()-1] = listViewProgress;
                    break;
                case "Completado":
                    tareasDone.clear();
                    tareasDone.addAll(TareasBDD.getTareas(estado, proyectoSeleccionado).values());
                    tareasOrdenadas[estado.getOrden()-1] = listViewDone;
                    break;
                case "En Revisión":
                    tareasInReview.clear();
                    tareasInReview.addAll(TareasBDD.getTareas(estado, proyectoSeleccionado).values());
                    tareasOrdenadas[estado.getOrden()-1] = listViewReview;
                    break;
                case "Desplegable":
                    tareasReady.clear();
                    tareasReady.addAll(TareasBDD.getTareas(estado, proyectoSeleccionado).values());
                    tareasOrdenadas[estado.getOrden()-1] = listViewReady;
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


        for (ListView<Tarea> listView : tareasOrdenadas) {
            listView.setItems(TareaCell.sorted(columnMap.get(listView)));
            listView.setCellFactory(tareaListView -> new TareaCell(root, columnMap) {});
        }
        /*listViewBacklog.setItems(TareaCell.sorted(obsTareasBacklog));
        listViewBacklog.setCellFactory(tareaListView -> new TareaCell(root, columnMap) {});

        listViewProgress.setItems(TareaCell.sorted(obsTareasProgress));
        listViewProgress.setCellFactory(tareaListView -> new TareaCell(root, columnMap) {});

        listViewDone.setItems(TareaCell.sorted(obsTareasDone));
        listViewDone.setCellFactory(tareaListView -> new TareaCell(root, columnMap) {});

        listViewReview.setItems(TareaCell.sorted(obsTareasReview));
        listViewReview.setCellFactory(tareaListView -> new TareaCell(root, columnMap) {});

        listViewReady.setItems(TareaCell.sorted(obsTareasReady));
        listViewReady.setCellFactory(tareaListView -> new TareaCell(root, columnMap) {});*/
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
