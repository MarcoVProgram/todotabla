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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class TareaAsignarController implements Initializable {
    
    @FXML
    private TextField buscarUsuario;

    @FXML
    private ListView<Usuario> listViewUsuarios;

    @FXML
    private AnchorPane root;

    private List<Usuario> listaAAsignar;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        EstadoPrograma.getInstance().setIntegrantesTemp(null);
        getObsIntegrantesList();
    }

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

        listViewUsuarios.setItems(obsIntegrantesList);
        listViewUsuarios.setCellFactory(cell -> new UsuariosCell());
    }

    @FXML
    public void salir(MouseEvent event) {
        Stage stage = (Stage) root.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void asignarUsuarios(ActionEvent event) {
        listaAAsignar = listViewUsuarios.getSelectionModel().getSelectedItems();
        EstadoPrograma.getInstance().setUsuariosTemp(listaAAsignar);
        Stage stage = (Stage) root.getScene().getWindow();
        stage.close();
    }
}
