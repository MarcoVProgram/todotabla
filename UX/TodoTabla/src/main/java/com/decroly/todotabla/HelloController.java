package com.decroly.todotabla;

import com.decroly.todotabla.model.Usuario;
import com.decroly.todotabla.model.sql.BDD;
import com.decroly.todotabla.model.sql.ProyetosBDD;
import com.decroly.todotabla.utils.EstadoPrograma;
import com.decroly.todotabla.utils.Navigator;
import com.decroly.todotabla.model.*;
import com.decroly.todotabla.utils.constants.opcionesBase;
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

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

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

    private List<Proyecto> proyectoListActivos = new ArrayList<>();
    private ObservableList<Proyecto> obsProyectoListActivos = FXCollections.observableList(proyectoListActivos);

    private List<Proyecto> proyectoListArchivados = new ArrayList<>();
    private ObservableList<Proyecto> obsProyectoListArchivados = FXCollections.observableList(proyectoListArchivados);

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

    private int cont = 0;
    
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

        List<Proyecto> allProyectos = new LinkedList<>();
        allProyectos.addAll(ProyetosBDD.getProyectos().values());


        listViewProyectos.getItems().addAll();

        for (Proyecto p : allProyectos) {
            boolean estaAbierto = false;

            if (p.getFechaCierre() != null) {
                try {
                    LocalDate fechaCierre = LocalDate.parse(String.valueOf(p.getFechaCierre()));
                    estaAbierto = !fechaCierre.isBefore(LocalDate.now()); // Incluye igualdad

                } catch (DateTimeParseException e) {
                    // Manejar formato incorrecto
                    estaAbierto = false;
                }
            }

            if (estaAbierto) {
                obsProyectoListActivos.add(p);
            } else {
                obsProyectoListArchivados.add(p);
            }

            listViewProyectos.setItems(obsProyectoListArchivados);
        }




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

        contAbiertos.setText(String.valueOf(contadorProyectosActivos()));
        contArchivados.setText(String.valueOf(contadorProyectosArchivados()));


        changeImage.setOnMouseClicked(event -> {

            String[] estado = {"Proyectos Abiertos", "Proyectos Archivados"};

            if (event.getButton() == MouseButton.PRIMARY) {
                cont++;

                if ((cont % 2) == 0) {
                    isEstado.setText(estado[0]);
                } else {
                    isEstado.setText(estado[1]);
                }
            }
        });

        proyectosAbiertos.setOnMouseClicked(event -> {

            String[] estado = {"Proyectos Abiertos", "Proyectos Archivados"};

            if (event.getButton() == MouseButton.PRIMARY) {
                cont++;

                if ((cont % 2) == 0) {
                    isEstado.setText(estado[0]);
                    listViewProyectos.setItems(obsProyectoListActivos);
                    proyectosAbiertos.getStyleClass().add("proyectosAbiertos");
                    proyectosAbiertos.getStyleClass().add("proyectoArchivadoDeseleccionado");
                } else {
                    isEstado.setText(estado[1]);
                    listViewProyectos.setItems(obsProyectoListArchivados);
                    proyectosAbiertos.getStyleClass().add("proyectosArchivados");
                    proyectosAbiertos.getStyleClass().add("proyectoAbiertoDeseleccionado");
                }
            }
        });

        proyectosArchivados.setOnMouseClicked(event -> {

            String[] estado = {"Proyectos Abiertos", "Proyectos Archivados"};

            if (event.getButton() == MouseButton.PRIMARY) {
                cont++;

                if ((cont % 2) == 0) {
                    isEstado.setText(estado[0]);
                    listViewProyectos.setItems(obsProyectoListActivos);
                } else {
                    isEstado.setText(estado[1]);
                    listViewProyectos.setItems(obsProyectoListArchivados);
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

            // TODO Hay que refrescar las listas del Kanban controller

        } catch (IOException e) {
            e.printStackTrace();
        }
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
                            EstadoPrograma.getInstance().setProyectoActivo(listViewProyectos.getSelectionModel().getSelectedItem());
                            abrirVentanaPrincipal();
                        } catch (IOException e) {
                            showAlert("Ocurrió un error inesperado y no se puede acceder al proyecto", "Cagaste");
                        }
                    }
                }
                /*case BORRAR_PROYECTO -> {
                    if(listViewProyectos.getSelectionModel().getSelectedItem() != null){
                        int id = listViewProyectos.getSelectionModel().getSelectedItem().getId();

                        obsProyectoList.remove(id);
                    }
                }*/
            }
        }
    }

    public int contadorProyectosActivos(){
        int contActivos = 0;

        for(Proyecto p : obsProyectoListActivos){
                if(p.getFechaCierre() == null){
                    contActivos++;
                }
            }

        return contActivos;
        }

    public int contadorProyectosArchivados(){
        int contArchivados = 0;

        for(Proyecto p : obsProyectoListArchivados) {
            if (p.getFechaCierre() != null) {
                contArchivados++;
            }
        }

        return contArchivados;
    }
}