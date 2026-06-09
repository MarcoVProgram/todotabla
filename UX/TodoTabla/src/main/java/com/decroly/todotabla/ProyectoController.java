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
 * Controlador transaccional a cargo del formulario de definición, alta y parametrización de Proyectos.
 * <p>
 * Maneja la captura de metadatos de proyectos (títulos, cronogramas y fechas límite), implementa validaciones
 * lógicas previas a la persistencia y encapsula la lógica para realizar confirmaciones atómicas (Commits) de inserción de
 * recursos e integrantes en relaciones de bases de datos de carácter muchos-a-muchos (N:M).
 * </p>
 *
 * @author Senior Developer
 * @version 1.0.0
 */
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

    /**
     * Despacha el guardado e inserción persistente de la información estructurada del proyecto.
     * <p>
     * Recupera los valores crudos de los formularios gráficos de la vista, instancia el modelo de negocio {@link Proyecto}
     * y ejecuta una operación de escritura de tipo INSERT a través de {@code ProyetosBDD.insertar()}. Si la base de datos devuelve
     * una clave primaria válida, asocia y confirma concurrentemente el listado temporal de integrantes antes de retornar
     * el flujo gráfico a la ventana predecesora.
     * </p>
     */
    @FXML
    private void guardar() {
        Proyecto proyecto = new Proyecto(
                    tituloProyecto.getText(),
                    LocalDate.now(),
                    fechaProyecto.getValue()
            );
        try {
            int index = ProyetosBDD.insertar(proyecto);
            if (index != -1) {
                proyecto = new Proyecto(
                        index,
                    tituloProyecto.getText(),
                    LocalDate.now(),
                    fechaProyecto.getValue()
            );
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

    /**
     * Setea por defecto el estado de las marcas de tiempo iniciales del formulario al momento cronológico actual.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if(fechaProyecto != null){
            fechaProyecto.setValue(LocalDate.now());
        }
    }

    /**
     * Invoca y expone de forma aislada la ventana modal para asociar recursos humanos temporales a la transacción de este proyecto.
     */
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
