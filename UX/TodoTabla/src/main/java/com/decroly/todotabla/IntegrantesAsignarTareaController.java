package com.decroly.todotabla;

import com.decroly.todotabla.model.Integrante;
import com.decroly.todotabla.model.Usuario;
import com.decroly.todotabla.model.sql.IntegrantesBDD;
import com.decroly.todotabla.model.sql.UsuariosBDD;
import com.decroly.todotabla.utils.AppErrorHandler;
import com.decroly.todotabla.utils.EstadoPrograma;
import com.decroly.todotabla.utils.Notificator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;

/**
 * Controlador de la ventana de selección de usuarios para añadirlos como integrantes temporales.
 * Muestra únicamente los usuarios que aún no pertenecen al proyecto activo.
 * Al hacer doble clic o clic secundario sobre un usuario, permite asignarle un rol
 * y añadirlo a la lista temporal de {@link EstadoPrograma}.
 */
public class IntegrantesAsignarTareaController implements Initializable {
    @FXML
    public ListView<Usuario> listViewUsuarios;

    List<Usuario> usuarioList = new ArrayList<>();
    ObservableList<Usuario> obsUsuarioList = FXCollections.observableList(usuarioList);

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
     * Si la carga falla, la lista se deja en el estado actual sin modificar.
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
}