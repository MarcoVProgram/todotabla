package com.decroly.todotabla;

import com.decroly.todotabla.model.Integrante;
import com.decroly.todotabla.model.Proyecto;
import com.decroly.todotabla.model.Tarea;
import com.decroly.todotabla.model.Usuario;
import com.decroly.todotabla.model.sql.IntegrantesBDD;
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
import javafx.stage.Popup;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;

public class UsuariosController implements Initializable {
    @FXML
    public ListView<Usuario> listViewUsuarios;
    List<Usuario> usuarioList = new ArrayList<>();
    ObservableList<Usuario> obsUsuarioList = FXCollections.observableList(usuarioList);

    @FXML
    public TextField buscarUsuario;

    @FXML
    public Button anadirBtnParticipantes;
    
    @FXML
    public ListView<Integrante> integrantesLV;
    List<Integrante> integrantesList = new ArrayList<>();
    ObservableList<Integrante> obsIntegrantesList = FXCollections.observableList(integrantesList);
    

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

        listViewUsuarios.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);


        //si click derecho o doble click izq, mostrar popup para seleccionar rol
        listViewUsuarios.setOnMouseClicked(event -> {
            if(event.getButton() == MouseButton.SECONDARY || event.getClickCount() == 2){
                    //Popup combobox
                    List<String> roles = List.of(
                            "Product Owner",
                            "Scrum Master",
                            "Developer",
                            "Tester",
                            "Designer",
                            "DevOps",
                            "Stakeholder"
                    );

                    ChoiceDialog<String> dialog =
                            new ChoiceDialog<>("Developer", roles);

                    dialog.setTitle("Seleccionar Rol");
                    dialog.setHeaderText("Asignar rol al usuario");

                    Optional<String> result = dialog.showAndWait();
                    


                    result.ifPresent(rol -> {
                        System.out.println("Rol seleccionado: " + rol);
                        final Integrante i;

                        i = new Integrante(rol, ProyectoController.getFechaProyecto().getValue(), null, 
                                listViewUsuarios.getSelectionModel().getSelectedItem(), EstadoPrograma.getInstance().getProyectoActivo());

                        IntegrantesBDD.insertar(i);

                        Integrante i2 = IntegrantesBDD.getIntegrante(i.getId());
                        
                        if(i2 != null && i2.getId() != -1){
                            showAlert("Exito","Se ingreso correctamente al usuario " + UsuariosBDD.getUsuario(listViewUsuarios.getSelectionModel().getSelectedItem().getId() + 
                                    " con nombre " + listViewUsuarios.getSelectionModel().getSelectedItem().getNombre() + " al proyecto actual " + EstadoPrograma.getInstance().getProyectoActivo().getTitulo()));

                        }else{
                            showAlert("Error", "No se pudo agregar correctamente el integrante " + listViewUsuarios.getSelectionModel().getSelectedItem().getNombre() + " al proyecto actual " + EstadoPrograma.getInstance().getProyectoActivo().getTitulo());
                        }
                    });
            }
        });
        integrantesLV.setItems(obsIntegrantesList);
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
