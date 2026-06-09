package com.decroly.todotabla;

import com.decroly.todotabla.model.*;
import com.decroly.todotabla.model.sql.IntegrantesBDD;
import com.decroly.todotabla.model.sql.ProyetosBDD;

import com.decroly.todotabla.utils.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class ProyectoController implements Initializable{

    @FXML
    private TextField tituloProyecto;

    @FXML
    private DatePicker fechaProyecto;

    @FXML
    private Button anadirUsuariosBtn;

    @FXML
    private Node root;


    @FXML
    private Button crearProyecto;


//    @FXML
//    private Button addButtom;


    @FXML
    private void guardar() {
        Proyecto proyecto = new Proyecto(
                    tituloProyecto.getText(),
                    LocalDate.now(),
                    fechaProyecto.getValue());
        try {
            int index = ProyetosBDD.insertar(proyecto);
            if (index != -1) {
                proyecto = new Proyecto(
                        index,
                        tituloProyecto.getText(),
                        LocalDate.now(),
                        fechaProyecto.getValue());
            }
        } catch (Exception ex) {
            AppErrorHandler.manejar(ex, "insertar");
        }

        EstadoPrograma.getInstance().setProyectoActivo(proyecto);

        for (Integrante i : EstadoPrograma.getInstance().getIntegrantesTemp()) {
            i.setIdProyecto(proyecto);

            try {
                IntegrantesBDD.insertar(i);
                proyecto.getIntegrantesTemp().add(i);
                
            } catch (Exception e) {
                AppErrorHandler.manejar(e, "insertar integrante en commit proyecto");
            }
        }
        
        EstadoPrograma.getInstance().getIntegrantesTemp().clear();

        try {
            volverVentanaPrincipal();

        } catch (IOException e) {
            AppErrorHandler.manejar(e, "volverVentanaPrincipal");
        }
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }


    @FXML
    private void abrirVentanaUsuarios() { //panel usuarios
        try {

            String fxml = "usuarios-formIntegrantes.fxml";
            String titulo = "Añadir usuarios";

            Navigator.arbrirVentanaSecundaria(fxml, titulo, this.getClass());

        } catch (IOException e) {
            AppErrorHandler.manejar(e, "abrirVentanaUsuarios");
        }
    }

    @FXML
    private void irAUsuariosview() throws IOException { //abrir panel kanban
        Stage stage = (Stage) root.getScene().getWindow();
        Navigator.changeScene(stage, "/com/decroly/todotabla/usuarios-formUsuarios.fxml");
    }

    @FXML
    private void volverVentanaPrincipal() throws IOException { //abrir panel kanban
        Stage stage = (Stage) root.getScene().getWindow();
        stage.close();
    }


}
