package com.decroly.todotabla;

import com.decroly.todotabla.model.Usuario;
import com.decroly.todotabla.model.sql.UsuariosBDD;
import com.decroly.todotabla.utils.AppErrorHandler;
import com.decroly.todotabla.utils.Notificator;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class UsuariosAddController implements Initializable {
    @FXML
    public Button btnAddUser;
    
    @FXML
    private TextField apellidosUsuarioCrear;

    @FXML
    private TextField emailUsuarioCrear;

    @FXML
    private TextField nombreUsuarioCrear;

    @FXML
    public void addUsuario() {
        Usuario u = new Usuario(
                nombreUsuarioCrear.getText(),
                apellidosUsuarioCrear.getText(),
                emailUsuarioCrear.getText()
        );
        
        boolean insertado = false;
        try {
            insertado = UsuariosBDD.insertar(u);
        } catch (Exception e) {
            AppErrorHandler.manejar(e, "UsuariosBDD.insertar(u);");
        }

            if(insertado){
                Notificator.exito("Exito", "Se ha insertado correctamente al usuario " + u.getNombre());
                nombreUsuarioCrear.clear();
                apellidosUsuarioCrear.clear();
                emailUsuarioCrear.clear();
            }else{
                Notificator.error("Error", "No se pudo insertar al usuario.");
            }

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnAddUser.setDisable(true);

        ChangeListener<String> campos = (obs, oldVal, newVal)->{
            boolean algunVacio = nombreUsuarioCrear.getText().trim().isEmpty()
                    || apellidosUsuarioCrear.getText().trim().isEmpty()
                    || emailUsuarioCrear.getText().trim().isEmpty();

            btnAddUser.setDisable(algunVacio);
        };
        
        nombreUsuarioCrear.textProperty().addListener(campos);
        apellidosUsuarioCrear.textProperty().addListener(campos);
        emailUsuarioCrear.textProperty().addListener(campos);
    }
}
