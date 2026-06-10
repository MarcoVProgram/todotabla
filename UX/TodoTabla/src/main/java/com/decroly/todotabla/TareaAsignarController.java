package com.decroly.todotabla;

import java.net.URL;
import java.util.*;

import com.decroly.todotabla.model.Integrante;
import com.decroly.todotabla.model.Usuario;
import com.decroly.todotabla.model.sql.IntegrantesBDD;
import com.decroly.todotabla.utils.AppErrorHandler;
import com.decroly.todotabla.utils.EstadoPrograma;
import com.decroly.todotabla.utils.cells.UsuariosCell;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Controlador de la ventana de selección de usuarios para asignarlos a la tarea activa.
 * Muestra los integrantes activos del proyecto y permite filtrarlos por nombre.
 * Al confirmar, almacena la selección en {@link EstadoPrograma} y cierra la ventana.
 */
public class TareaAsignarController implements Initializable {

    @FXML
    private ListView<Usuario> listViewUsuarios;

    @FXML
    private AnchorPane root;

    private List<Usuario> listaAAsignar;

    private FilteredList<Usuario> filteredIntegranteList;


    @FXML
    private TextField buscarUsuario;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        EstadoPrograma.getInstance().setIntegrantesTemp(null);
        getObsIntegrantesList();
        buscarUsuario.textProperty().addListener((observable) -> filtarUsers());
    }

    /**
     * Carga los integrantes activos del proyecto activo y los muestra en la lista filtrable.
     */
    private void getObsIntegrantesList() {
        Map<Integer, Integrante> map = new HashMap<>();

        try {
            map = IntegrantesBDD.getIntegrantesActivos(
                    EstadoPrograma.getInstance().getProyectoActivo()
            );
        } catch (Exception e) {
            AppErrorHandler.manejar(e, "getObsIntegrantesList");
        }

        ArrayList<Usuario> al = new ArrayList<>();
        for (Integrante i : map.values()) {
            
            al.add(i.getIdUsuario());
        }

        ObservableList<Usuario> obsIntegrantesList =
                FXCollections.observableArrayList(al);
        filteredIntegranteList = new FilteredList<>(obsIntegrantesList,  p -> true);


        listViewUsuarios.setItems(filteredIntegranteList);
        listViewUsuarios.setCellFactory(cell -> new UsuariosCell());
    }

    /**
     * Aplica el predicado de búsqueda sobre la lista filtrada de usuarios
     * según el texto introducido en el campo de búsqueda.
     */
    private void filtarUsers() {
        filteredIntegranteList.setPredicate(proyecto ->
                    this.buscarUsuario.getText().isBlank() ||
                            proyecto.getNombre().toLowerCase().contains(this.buscarUsuario.getText().toLowerCase()) );
    }

    /**
     * Cierra la ventana secundaria sin guardar ninguna selección.
     */
    @FXML
    public void salir() {
        Stage stage = (Stage) root.getScene().getWindow();
        stage.close();
    }

    /**
     * Almacena los usuarios seleccionados en {@link EstadoPrograma} y cierra la ventana.
     */
    @FXML
    public void asignarUsuarios() {
        listaAAsignar = listViewUsuarios.getSelectionModel().getSelectedItems();
        EstadoPrograma.getInstance().setUsuariosTemp(listaAAsignar);
        Stage stage = (Stage) root.getScene().getWindow();
        stage.close();
    }
}
