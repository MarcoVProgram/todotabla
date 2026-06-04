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

public class IntegrantesController implements Initializable {
//    @FXML
//    public ListView<Usuario> listViewUsuarios;

    @FXML
    public ListView<Integrante> listViewIntegrantes;

//    List<Usuario> usuarioList = new ArrayList<>();
//    ObservableList<Usuario> obsUsuarioList = FXCollections.observableList(usuarioList);

    @FXML
    private Node root;

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
        //=================LISTA INTEGRANTES===================
        Map<Integer, Integrante> map = new HashMap<>();

        try {
            map = IntegrantesBDD.getIntegrantes(
                    EstadoPrograma.getInstance().getProyectoActivo()
            );
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }

        ObservableList<Integrante> obsIntegrantesList =
                FXCollections.observableArrayList(map.values());

        listViewIntegrantes.setItems(obsIntegrantesList);

        listViewIntegrantes.setCellFactory(integrantesList -> new ListCell<Integrante>() {

            @Override
            protected void updateItem(Integrante user, boolean empty) {
                super.updateItem(user, empty);

                if (empty || user == null) {

                    setGraphic(null);

                } else {

                    // Título
                    Label rol = new Label(user.getRol());
                    Label nombre = new Label(user.getIdUsuario().getNombre());

                    rol.getStyleClass().add("titulo-tarea");
                    nombre.getStyleClass().add("subTitulo-tarea");

                    // Fecha entrada proyecto
                    Label fecha = new Label(String.valueOf(user.getFechaEntrada()));

                    fecha.getStyleClass().add("subTitulo2-tarea");

                    // Card completa
                    VBox card = new VBox(10, rol, nombre, fecha);

                    card.getStyleClass().add("task-card");

                    setGraphic(card);
                }
            }
        });

//        //===========LISTA USUARIOS================
//
//        Set<Integer> idsIntegrantes = new HashSet<>();
//
//        for (Integrante i : obsIntegrantesList) {
//            idsIntegrantes.add(i.getIdUsuario().getId());
//        }
//
//        obsUsuarioList.clear();
//
//        try {
//            for (Usuario u : UsuariosBDD.getUsuarios().values()) {
//                if (!idsIntegrantes.contains(u.getId())) {
//                    obsUsuarioList.add(u);
//                }
//            }
//        } catch (Exception e) {
//            AppErrorHandler.manejar(e, "getUsuarios");
//        }
//
//        listViewUsuarios.setItems(obsUsuarioList);

    }

//        Map<Integer, Integrante> integrantesList = IntegrantesBDD.getIntegrantes(EstadoPrograma.getInstance().getProyectoActivo());
//        ObservableMap<Integer, Integrante> obsIntegrantesList = FXCollections.observableMap(integrantesList);
//        else{
////            ProyectoController.getAnadirUsuariosBtn().setDisable(true);
////            ProyectoController.getCrearProyecto().setDisable(true);
//        }


//    private void actualizarUsuarios() {
//        Map<Integer, Usuario> todosUsuarios;
//        try {
//            todosUsuarios = UsuariosBDD.getUsuarios();
//        } catch (Exception e) {
//            AppErrorHandler.manejar(e, "getUsuarios");
//            todosUsuarios = null;
//        }
//        if (todosUsuarios != null) {
//            listViewUsuarios.refresh();
//        }
//    }


    @FXML
    private void irAUsuariosview() throws IOException { //abrir panel kanban
        Stage stage = (Stage) root.getScene().getWindow();
        Navigator.changeScene(stage, "/com/decroly/todotabla/usuarios-formUsuariosGestionKanban.fxml");
    }

    @FXML
    private void Salir() throws IOException { //abrir panel kanban
        Stage stage = (Stage) root.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void irAIntegrantesview() throws IOException { //abrir panel kanban
        Stage stage = (Stage) root.getScene().getWindow();
        Navigator.changeScene(stage, "/com/decroly/todotabla/usuarios-formIntegrantes.fxml");
    }
}
