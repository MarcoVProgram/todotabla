package com.decroly.todotabla;

import com.decroly.todotabla.model.Integrante;
import com.decroly.todotabla.model.Usuario;
import com.decroly.todotabla.model.sql.IntegrantesBDD;
import com.decroly.todotabla.model.sql.UsuariosBDD;
import com.decroly.todotabla.utils.AppErrorHandler;
import com.decroly.todotabla.utils.EstadoPrograma;
import com.decroly.todotabla.utils.Navigator;
import com.decroly.todotabla.utils.Notificator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;

public class IntegrantesAsignarTareaController implements Initializable {
    @FXML
    public ListView<Usuario> listViewUsuarios;
    @FXML
    public ListView listViewIntegrantes;

    @FXML
    private Node root;

    List<Usuario> usuarioList = new ArrayList<>();
    ObservableList<Usuario> obsUsuarioList = FXCollections.observableList(usuarioList);



    @FXML
    public TextField buscarUsuario;

    @FXML
    public Button anadirBtnParticipantes;

    private static Stage ventanaSecundaria;

    public static Stage getVentanaSecundaria() {
        return ventanaSecundaria;
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        actualizarUsuarios();

        //==============MODIFICAR LISTVIEW USUARIOS===================
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

//        if(ProyectoController.getTituloProyecto() != null) {
//            ProyectoController.getAnadirUsuariosBtn().setDisable(false);
//            ProyectoController.getCrearProyecto().setDisable(false);

        //si click derecho o doble click izq, mostrar popup para seleccionar rol

        //==============CREAR POPUP AL CLICKEAR USUARIO PARA AÑADIR===================
//        listViewUsuarios.setOnMouseClicked(event -> {
//            if (event.getButton() == MouseButton.SECONDARY || event.getClickCount() == 2) {
//                //Popup combobox
//                List<String> roles = List.of(
//                        "Product Owner",
//                        "Scrum Master",
//                        "Developer",
//                        "Tester",
//                        "Designer",
//                        "DevOps",
//                        "Stakeholder"
//                );
//
//                ChoiceDialog<String> dialog =
//                        new ChoiceDialog<>("Developer", roles);
//
//                dialog.setTitle("Seleccionar Rol");
//                dialog.setHeaderText("Asignar rol al usuario");
//
//                Optional<String> result = dialog.showAndWait();
//
//                result.ifPresent(rol -> {
//                    System.out.println("Rol seleccionado: " + rol);
//
//
//                    if (rol != null) {
//                        Integrante i = new Integrante(rol, LocalDate.now(), null, listViewUsuarios.getSelectionModel().getSelectedItem(), EstadoPrograma.getInstance().getProyectoActivo());
//                        Integrante exist = null;
//                        try {
//                            IntegrantesBDD.insertar(i);
//                            exist = IntegrantesBDD.getIntegrante(i.getId());
//
//                        } catch (Exception e) {
//                            e.getStackTrace();
//                        }
//
//                        if (exist != null) {
//                            Notificator.exito("Exito", "Se insertó correctamente al usuario " + listViewUsuarios.getSelectionModel().getSelectedItem().getNombre()
//                                    + ", al proyecto actual ");
//
//                        } else {
//                            Notificator.error("Error", "Ocurrió un error inesperado al intentar insertar al usuario " + listViewUsuarios.getSelectionModel().getSelectedItem().getNombre()
//                                    + ", al proyecto actual ");
//                        }
//                    }
//                });
//            }
//        });

        listViewUsuarios.setOnMouseClicked(event -> {

            Usuario seleccionado = listViewUsuarios.getSelectionModel().getSelectedItem();

            if (seleccionado == null) return;

            if (event.getButton() == MouseButton.SECONDARY || event.getClickCount() == 2) {

                List<String> roles = List.of(
                        "Product Owner",
                        "Scrum Master",
                        "Developer",
                        "Tester",
                        "Designer",
                        "DevOps",
                        "Stakeholder"
                );

                ChoiceDialog<String> dialog = new ChoiceDialog<>("Developer", roles);
                dialog.setTitle("Seleccionar Rol");
                dialog.setHeaderText("Asignar rol al usuario");

                Optional<String> result = dialog.showAndWait();

                result.ifPresent(rol -> {


                    try {

                        Integrante i = new Integrante(
                                rol,
                                LocalDate.now(),
                                null,
                                seleccionado,
                                null
                        );

                        EstadoPrograma.getInstance().getIntegrantesTemp().add(i);

                        Notificator.exito(
                                "Éxito",
                                "Integrante añadido temporalmente"
                        );

                    } catch (Exception e) {
                        AppErrorHandler.manejar(e, "insertar integrante");

                        Notificator.error(
                                "Error",
                                "No se pudo añadir el usuario"
                        );
                    }
                });
            }
        });



        //=================LISTA INTEGRANTES===================
        Map<Integer, Integrante> map = null;

        try {
            map = IntegrantesBDD.getIntegrantes(
                    EstadoPrograma.getInstance().getProyectoActivo()
            );
        } catch (Exception e) {
            Notificator.advertencia("Mapa vacio", "Creando nuevo mapa");
            map = new HashMap<>();
        }

        ObservableList<Integrante> obsIntegrantesList =
                FXCollections.observableArrayList(map.values());

        //===========LISTA USUARIOS================

        Set<Integer> idsIntegrantes = new HashSet<>();

        for (Integrante i : obsIntegrantesList) {
            idsIntegrantes.add(i.getIdUsuario().getId());
        }

        obsUsuarioList.clear();

        try {
            for (Usuario u : UsuariosBDD.getUsuarios().values()) {
                if (!idsIntegrantes.contains(u.getId())) {
                    obsUsuarioList.add(u);
                }
            }
        } catch (Exception e) {
            AppErrorHandler.manejar(e, "getUsuarios");
        }

        listViewUsuarios.setItems(obsUsuarioList);

    }

//        Map<Integer, Integrante> integrantesList = IntegrantesBDD.getIntegrantes(EstadoPrograma.getInstance().getProyectoActivo());
//        ObservableMap<Integer, Integrante> obsIntegrantesList = FXCollections.observableMap(integrantesList);
//        else{
////            ProyectoController.getAnadirUsuariosBtn().setDisable(true);
////            ProyectoController.getCrearProyecto().setDisable(true);
//        }


    private void actualizarUsuarios() {
        Map<Integer, Usuario> todosUsuarios;
        try {
            todosUsuarios = UsuariosBDD.getUsuarios();
        } catch (Exception e) {
            AppErrorHandler.manejar(e, "getUsuarios");
            todosUsuarios = null;
        }
        if (todosUsuarios != null) {
            listViewUsuarios.refresh();
        }
    }

    @FXML
    private void volverVentanaPrincipal() throws IOException { //abrir panel kanban
        Stage stage = (Stage) root.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void irAIntegrantesview() throws IOException { //abrir panel kanban
        Stage stage = (Stage) root.getScene().getWindow();
        Navigator.changeScene(stage, "/com/decroly/todotabla/usuarios-formIntegrantes.fxml");
    }

    @FXML
    private void irACrearProyectoView() throws IOException { //abrir panel kanban
        Stage stage = (Stage) root.getScene().getWindow();
        Navigator.changeScene(stage, "/com/decroly/todotabla/proyecto-form.fxml");
    }
}
