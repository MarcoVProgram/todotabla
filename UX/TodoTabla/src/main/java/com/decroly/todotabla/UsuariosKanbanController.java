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

/**
 * Controlador de la vista de gestión de usuarios del Kanban.
 * Muestra los usuarios que aún no son integrantes del proyecto activo
 * y permite incorporarlos al proyecto asignándoles un rol directamente desde esta vista.
 */
public class UsuariosKanbanController implements Initializable {

    @FXML
    public ListView<Usuario> listViewUsuarios;

    @FXML
    private Node root;

    List<Usuario> usuarioList = new ArrayList<>();
    ObservableList<Usuario> obsUsuarioList = FXCollections.observableList(usuarioList);



    @FXML
    public TextField buscarUsuario;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        actualizarUsuarios();

        listViewUsuarios.setCellFactory(usuarioList -> new ListCell<Usuario>() {

            @Override
            protected void updateItem(Usuario user, boolean empty) {
                super.updateItem(user, empty);

                if (empty || user == null) {

                    setGraphic(null);

                } else {

                    Label titulo = new Label(user.getNombre());
                    Label subTitulo = new Label(user.getApellidos());

                    titulo.getStyleClass().add("titulo-tarea");
                    subTitulo.getStyleClass().add("subTitulo-tarea");

                    Label email = new Label(user.getEmail());

                    email.getStyleClass().add("subTitulo2-tarea");

                    VBox card = new VBox(10, titulo, subTitulo, email);

                    card.getStyleClass().add("task-card");

                    setGraphic(card);
                }
            }
        });

        listViewUsuarios.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        listViewUsuarios.setOnMouseClicked(event -> {
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

                ChoiceBox<String> choiceBox = new ChoiceBox<>();
                choiceBox.getItems().addAll(roles);
                choiceBox.setValue("Developer");
                choiceBox.getStyleClass().add("choice-box-choice");

                Dialog<String> dialog = new Dialog<>();
                dialog.setTitle("Seleccionar Rol");
                dialog.setHeaderText("Asignar rol al usuario");

                DialogPane dialogPane = dialog.getDialogPane();
                dialogPane.setContent(choiceBox);
                dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
                dialogPane.getStyleClass().add("dialog-info");

                dialog.setOnShown(e ->
                    dialogPane.getScene().getStylesheets().add(
                        getClass().getResource("/com/decroly/todotabla/style.css").toExternalForm()
                    )
                );

                dialog.setResultConverter(btn ->
                    btn == ButtonType.OK ? choiceBox.getValue() : null
                );

                dialog.showAndWait().ifPresent(rol -> {
                    Usuario seleccionado = listViewUsuarios.getSelectionModel().getSelectedItem();

                    Integrante i = new Integrante(
                            rol,
                            LocalDate.now(),
                            null,
                            seleccionado,
                            EstadoPrograma.getInstance().getProyectoActivo()
                    );

                    try {
                        IntegrantesBDD.insertar(i);
                        Notificator.exito(
                                "Éxito",
                                "Se insertó correctamente al usuario " + seleccionado.getNombre() + " al proyecto actual"
                        );
                    } catch (Exception e) {
                        AppErrorHandler.manejar(e, "insertar integrante");
                        Notificator.error(
                                "Error",
                                "No se pudo insertar al usuario " + seleccionado.getNombre()
                        );
                    }
                });
            }
        });

        Map<Integer, Integrante> map = null;

        try {
            map = IntegrantesBDD.getIntegrantesActivos(
                    EstadoPrograma.getInstance().getProyectoActivo()
            );
        } catch (Exception e) {
            Notificator.advertencia("Mapa vacio", "Creando nuevo mapa");
            map = new HashMap<>();
        }

        ObservableList<Integrante> obsIntegrantesList =
                FXCollections.observableArrayList(map.values());

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

    /**
     * Refresca la vista de la lista de usuarios.
     */
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

    /**
     * Navega a la vista de gestión de integrantes.
     *
     * @throws IOException si el fichero FXML no puede cargarse
     */
    @FXML
    private void irAIntegrantesview() throws IOException {
        Stage stage = (Stage) root.getScene().getWindow();
        Navigator.changeScene(stage, "/com/decroly/todotabla/usuarios-formIntegrantes.fxml");
    }
}
