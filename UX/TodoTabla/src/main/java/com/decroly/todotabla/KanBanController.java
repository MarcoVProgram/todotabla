package com.decroly.todotabla;

import com.decroly.todotabla.model.Estado;
import com.decroly.todotabla.model.*;
import com.decroly.todotabla.model.sql.EstadosBDD;
import com.decroly.todotabla.model.sql.TareasBDD;
import com.decroly.todotabla.utils.*;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class KanBanController implements Initializable {
    @FXML
    private ImageView returnBtn;

    //Contenedor
    @FXML
    private HBox contenedorColumnas;
    
    private List<Integrante> integrantes = new ArrayList<>();
    private ObservableList<Integrante> obsIntegrantes = FXCollections.observableList(integrantes);


    private Stage ventanaSecundaria = getVentanaSecundaria();


    private static Stage ventanaTerciaria;

    private Proyecto proyectoSeleccionado;
    private List<Estado> estados;

    @FXML
    private BorderPane root;

    private static Stage getVentanaSecundaria() {
        return MainController.getVentanaSecundaria();
    }

    @FXML
    private Label proyectoTitulo;

    private Map<Estado, ColumnaKanban> columnMap = new HashMap<>();



    public void initialize(URL url, ResourceBundle rb) {
        try {
            estados = EstadosBDD.getEstados();
        } catch (Exception ex) {
            AppErrorHandler.manejar(ex, "getEstados");
        }
        estados.sort(Comparator.comparingInt(Estado::getOrden));
        proyectoSeleccionado = EstadoPrograma.getInstance().getProyectoActivo();
        proyectoTitulo.setText("🔒 " + proyectoSeleccionado.getTitulo());

        actualizarTareas();
    }

    private void actualizarTareas() {
        contenedorColumnas.getChildren().clear();
        columnMap.clear();

        for (Estado estado : estados) {
            columnMap.put(estado, addColumna(estado));
        }
    }

    private void actualizarTareas(String regex) {
        contenedorColumnas.getChildren().clear();
        columnMap.clear();

        for (Estado estado : estados) {
            columnMap.put(estado, addColumna(estado, regex));
        }
    }

    private ColumnaKanban addColumna(Estado estado) {

        ObservableList<Tarea> items;

        try {
            items = FXCollections.observableArrayList(
                    TareasBDD.getTareas(estado, proyectoSeleccionado).values()
            );
        } catch (Exception ex) {
            AppErrorHandler.manejar(ex, "getTareas");
            items = FXCollections.observableArrayList();
        }

        ListView<Tarea> listView = constructorColumnas(estado, items);
        return new ColumnaKanban(estado, listView, items);
    }

    private ColumnaKanban addColumna(Estado estado, String regex) {
        ObservableList<Tarea> items;

        try {
            items = FXCollections.observableArrayList(
                    TareasBDD.getTareas(regex, proyectoSeleccionado, estado)
            );
        } catch (Exception ex) {
            AppErrorHandler.manejar(ex, "getTareas");
            items = FXCollections.observableArrayList();
        }
        ListView<Tarea> listView = constructorColumnas(estado, items);
        return new ColumnaKanban(estado, listView, items);
    }

    private ListView<Tarea> constructorColumnas(Estado  estado, ObservableList<Tarea> items) {
        // Circulo de Estado
        Circle dot = new Circle(4);
        dot.setStyle("-fx-fill: " + estado.getColor() + ";");

        // Titulo con contador
        Label title = new Label(estado.getNombre() + " [" + items.size() + "]");
        title.getStyleClass().add("column-title");

        // Actualización Automática
        items.addListener((ListChangeListener<Tarea>) c ->
                title.setText(estado.getNombre() + " [" + items.size() + "]")
        );

        // Titulo
        HBox titleRow = new HBox(6, dot, title);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        // ListView de Tarea
        ListView<Tarea> listView = new ListView<>(TareaCell.sorted(items));
        listView.setCellFactory(lv -> new TareaCell(root, columnMap));
        listView.getStyleClass().add("kanban-list");
        listView.setPrefHeight(579);
        listView.setPrefWidth(260);
        listView.setStyle("-fx-background-color: #0b0b0b;");
        VBox.setVgrow(listView, Priority.ALWAYS);

        // Columna creada
        VBox column = new VBox(titleRow, listView);
        column.getStyleClass().add("column");
        column.setStyle("-fx-background-color: #0b0b0b;");

        // Se añade a su hija
        contenedorColumnas.getChildren().add(column);
        return listView;
    }


    //----------------DESPLAZAMIENTO ENTRE VENTANAS-------------
    @FXML
    private void returnToMain() throws IOException { //abrir pantalla principal (menú)
        Stage stage = (Stage) returnBtn.getScene().getWindow();
        Navigator.changeScene(stage, "/com/decroly/todotabla/main-view.fxml");
    }

    @FXML
    private void abrirVentanaCrearTarea() { //panel tarea
        try {

            if(ventanaSecundaria != null && ventanaSecundaria.isShowing()){
                System.out.println("No se puede volver a abrir, hay una sesion existente");
                return;
            }

            // Cargar el archivo FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("tarea-view-create.fxml"));
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

            // Mostrar la ventana
            ventanaSecundaria.showAndWait();
            actualizarTareas();

        } catch (IOException e) {
            AppErrorHandler.manejar(e, "abrirVentanaCrearTarea");
        }
    }

    @FXML
    private void abrirVentanaEditarTarea() { //panel tarea
        try {

            if(ventanaSecundaria != null && ventanaSecundaria.isShowing()){
                System.out.println("No se puede volver a abrir, hay una sesion existente");
                return;
            }

            // Cargar el archivo FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("tarea-view-mod.fxml"));
            Parent root = loader.load();

            // Crear una nueva ventana (Stage)
            ventanaSecundaria = new Stage();
            ventanaSecundaria.setTitle("Editar tarea");
            ventanaSecundaria.setScene(new Scene(root));

            ventanaSecundaria.setResizable(false);

            // Mostrar la ventana
            ventanaSecundaria.showAndWait();
            actualizarTareas();

        } catch (IOException e) {
            AppErrorHandler.manejar(e,  "abrirVentanaEditarTarea (fxml)");
        }
    }

    @FXML
    private void abrirVentanaBorrarTarea() { //panel tarea
        try {

            if(ventanaSecundaria != null && ventanaSecundaria.isShowing()){
                System.out.println("No se puede volver a abrir, hay una sesion existente");
                return;
            }

            // Cargar el archivo FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("tarea-view-remove.fxml"));
            Parent root = loader.load();

            // Crear una nueva ventana (Stage)
            ventanaSecundaria = new Stage();
            ventanaSecundaria.setTitle("Borrar tarea");
            ventanaSecundaria.setScene(new Scene(root));

            ventanaSecundaria.setResizable(false);

            // Mostrar la ventana
            ventanaSecundaria.showAndWait();
            actualizarTareas();

        } catch (IOException e) {
            AppErrorHandler.manejar(e, "abrirVentanaEditarTarea");
        }
    }

//    @FXML
//    private void abrirVentanaUsuarios() { //panel gestión usuarios
//        try {
//
//            if(ventanaTerciaria != null && ventanaTerciaria.isShowing()){
//                System.out.println("No se puede volver a abrir, hay una sesion existente");
//                return;
//            }
//
//            // Cargar el archivo FXML
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("usuarios-form.fxml"));
//            Parent root = loader.load();
//
//            // Crear una nueva ventana (Stage)
//            ventanaTerciaria = new Stage();
//            ventanaTerciaria.setTitle("Gestionar usuarios");
//            ventanaTerciaria.setScene(new Scene(root));
//
//            ventanaTerciaria.setResizable(false);
//            ventanaTerciaria.setAlwaysOnTop(false);
//
////            listViewTareas.setItems(obsTareas);
//
//            // Mostrar la ventana
//            ventanaTerciaria.showAndWait();
//
//            // TODO Hay que refrescar las listas del Kanban controller
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }



    @FXML
    public void buscarTarea(ActionEvent event) {
        String name = ""; // Señalar barra de busqueda
        actualizarTareas(name);

    }
}
