package com.decroly.todotabla;

import com.decroly.todotabla.model.Estado;
import com.decroly.todotabla.model.Proyecto;
import com.decroly.todotabla.model.Tarea;
import com.decroly.todotabla.model.Usuario;
import com.decroly.todotabla.model.sql.ProyetosBDD;

import com.decroly.todotabla.model.sql.TareasBDD;
import com.decroly.todotabla.model.sql.UsuariosBDD;
import com.decroly.todotabla.utils.AppErrorHandler;
import com.decroly.todotabla.utils.EstadoPrograma;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.chrono.IsoChronology;
import java.util.Map;
import java.util.ResourceBundle;

public class ProyectoController implements Initializable{

    @FXML
    private static TextField tituloProyecto;

    public static TextField getTituloProyecto() {
        return tituloProyecto;
    }

    @FXML
    private static DatePicker fechaProyecto;

    public static DatePicker getFechaProyecto() {
        return fechaProyecto;
    }

    @FXML
    private Button anadirUsuariosBtn;


    @FXML
    private Button crearProyecto;


    private static Stage ventanaSecundaria;

    public static Stage getVentanaSecundaria() {
        return ventanaSecundaria;
    }

//    @FXML
//    private Button addButtom;

    @FXML
    private void guardar() {
        Proyecto p = new Proyecto(
                tituloProyecto.getText(),
                fechaProyecto.getValue(),
                null
        );
        try {
            if (ProyetosBDD.insertar(p)) {
                (new Alert(Alert.AlertType.INFORMATION,
                        "Se ha creado correctamente",
                        ButtonType.OK
                )).showAndWait();
            }
        } catch (Exception ex) {
            AppErrorHandler.manejar(ex, "insertar");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if(fechaProyecto != null){
            fechaProyecto.setValue(LocalDate.now());
        }
    }

    @FXML
    private void abrirVentanaUsuarios() { //panel usuarios
        try {

            if(ventanaSecundaria != null && ventanaSecundaria.isShowing()){
                System.out.println("No se puede volver a abrir, hay una sesion existente");
                return;
            }

            // Cargar el archivo FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("usuarios-form.fxml"));
            Parent root = loader.load();

            // Crear una nueva ventana (Stage)
            ventanaSecundaria = new Stage();
            ventanaSecundaria.setTitle("Añadir usuarios");
            ventanaSecundaria.setScene(new Scene(root));

            ventanaSecundaria.setResizable(false);

            if(ventanaSecundaria.isFocused()){
                ventanaSecundaria.setAlwaysOnTop(true);
            }else{
                ventanaSecundaria.setAlwaysOnTop(false);
            }

//            listViewTareas.setItems(obsTareas);

            // Mostrar la ventana
            ventanaSecundaria.showAndWait();

            // TODO Hay que refrescar las listas del Kanban controller

        } catch (IOException e) {
            AppErrorHandler.manejar(e, "abrirVentanaUsuarios");
        }
    }
}
