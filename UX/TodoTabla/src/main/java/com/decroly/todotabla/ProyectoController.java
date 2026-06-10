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

/**
 * Controlador del formulario de creación de un nuevo proyecto.
 * Persiste el proyecto en la base de datos, le asigna los integrantes temporales
 * acumulados en {@link EstadoPrograma} y limpia la lista temporal al finalizar.
 */
public class ProyectoController implements Initializable{

    @FXML
    private TextField tituloProyecto;

    @FXML
    private DatePicker fechaProyecto;

    @FXML
    private Node root;


    /**
     * Valida el formulario, inserta el proyecto en la base de datos, persiste los integrantes
     * temporales asociados y cierra la ventana secundaria.
     */
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

    /**
     * Navega a la vista de selección de usuarios para añadir integrantes al proyecto.
     *
     * @throws IOException si el fichero FXML no puede cargarse
     */
    @FXML
    private void irAUsuariosview() throws IOException {
        Stage stage = (Stage) root.getScene().getWindow();
        Navigator.changeScene(stage, "/com/decroly/todotabla/usuarios-formUsuarios.fxml");
    }

    /**
     * Cierra la ventana secundaria actual.
     *
     * @throws IOException si ocurre un error al cerrar la ventana
     */
    @FXML
    private void volverVentanaPrincipal() throws IOException {
        Stage stage = (Stage) root.getScene().getWindow();
        stage.close();
    }
}