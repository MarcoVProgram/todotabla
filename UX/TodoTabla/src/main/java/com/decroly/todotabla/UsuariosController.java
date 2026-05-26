package com.decroly.todotabla;

import com.decroly.todotabla.model.Proyecto;
import com.decroly.todotabla.model.Tarea;
import com.decroly.todotabla.model.Usuario;
import com.decroly.todotabla.model.sql.TareasBDD;
import com.decroly.todotabla.model.sql.UsuariosBDD;
import com.decroly.todotabla.utils.EstadoPrograma;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class UsuariosController implements Initializable {
    @FXML
    public ListView<Usuario> listViewUsuarios;
    List<Usuario> usuarioList = new ArrayList<>();
    ObservableList<Usuario> obsUsuarioList = FXCollections.observableList(usuarioList);

    @FXML
    public TextField buscarUsuario;

    private static Stage ventanaSecundaria;

    public static Stage getVentanaSecundaria() {
        return ventanaSecundaria;
    }



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        obsUsuarioList.addAll(UsuariosBDD.getUsuarios().values());
        listViewUsuarios.setItems(obsUsuarioList);

        actualizarUsuarios();

        listViewUsuarios.setCellFactory(usuarioList -> new ListCell<Usuario>() {

            @Override
            protected void updateItem(Usuario user, boolean empty) {
                super.updateItem(user, empty);

                if (empty || user == null) {

                    setGraphic(null);

                } else {

                    // Título
                    Label titulo = new Label(user.getNombre());
                    Label subTitulo = new Label(user.getApellidos());

                    titulo.getStyleClass().add("titulo-tarea");
                    subTitulo.getStyleClass().add("subTitulo-tarea");

                    // Fecha fin
                    Label email = new Label(user.getEmail());

                    email.getStyleClass().add("subTitulo2-tarea");

                    // Card completa
                    VBox card = new VBox(10, titulo, subTitulo, email);

                    card.getStyleClass().add("task-card");

                    setGraphic(card);
                }
            }
        });

        listViewUsuarios.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

//        listViewUsuarios.setOnMouseClicked(event -> {
//            if(event.getButton() == MouseButton.SECONDARY || event.getClickCount() == 2){
//                try {
//                    abrirVentanaPrincipal();
//
//                } catch (IOException e) {
//                    showAlert("Ocurrió un error inesperado y no se puede acceder al proyecto", "Cagaste");
//                }
//            }
//        });
    }


    private void actualizarUsuarios() {
        Map<Integer, Usuario> todosUsuarios = UsuariosBDD.getUsuarios();
        if (todosUsuarios != null) {
            listViewUsuarios.refresh();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }




}
