package com.decroly.todotabla;

import com.decroly.todotabla.model.Usuario;
import com.decroly.todotabla.model.sql.BDD;
import com.decroly.todotabla.utils.Navigator;
import com.decroly.todotabla.model.*;
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
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class HelloController implements Initializable {
    //lista miembros

    List<Usuario> usuarios = new ArrayList<>();
    ObservableList<Usuario> obsUsuarios = FXCollections.observableList(usuarios);

    //lista tareas
    List<Tarea> tareas = new ArrayList<>();
    ObservableList<Tarea> obsTareas = FXCollections.observableList(tareas);

    List<Usuario> miembros = new ArrayList<>();
    ObservableList<Usuario> obsMiembros = FXCollections.observableList(miembros);

    
    //lista proyectos
    List<Proyecto> proyectos = new ArrayList<>();
    ObservableList<Proyecto> obsProyectos = FXCollections.observableList(proyectos);
    
    //PESTAÑA INICIO
    @FXML
    private Button panelKanbanBtn;

    @FXML
    private Node root;

    //VARIABLES AUXILIARES
    private static Stage ventanaSecundaria;

    public static Stage getVentanaSecundaria() {
        return ventanaSecundaria;
    }

    //FORMATTER
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

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
    }


//----------------DESPLAZAMIENTO ENTRE VENTANAS-------------
    @FXML
    private void abrirVentanaPrincipal() throws IOException { //abrir panel kanban
        Stage stage = (Stage) root.getScene().getWindow();
        Navigator.changeScene(stage, "/com/decroly/todotabla/kanban-view.fxml");
    }
}