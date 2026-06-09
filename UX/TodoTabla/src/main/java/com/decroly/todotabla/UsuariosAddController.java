package com.decroly.todotabla;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import com.decroly.todotabla.model.Usuario;
import com.decroly.todotabla.model.sql.UsuariosBDD;
import com.decroly.todotabla.utils.AppErrorHandler;
import com.decroly.todotabla.utils.Notificator;

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

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

        Usuario u;

        boolean insertado = false;

        if (
            nombreUsuarioCrear.getText() != null && 
            !nombreUsuarioCrear.getText().isBlank() &&
            apellidosUsuarioCrear.getText() != null && 
            !apellidosUsuarioCrear.getText().isBlank() &&
            emailUsuarioCrear.getText() != null &&
            !emailUsuarioCrear.getText().isBlank() &&
            Pattern.matches(
                "[A-Za-z-._0-9Ññ]+@[A-Za-z]+[.][A-Za-z]{2,4}", 
                emailUsuarioCrear.getText().toUpperCase()
            ) && 
            Pattern.matches(
                "^[A-Za-z 0-9Ññ_-]{2,45}$", 
                apellidosUsuarioCrear.getText()
            ) &&
            Pattern.matches(
                "^[A-Za-z 0-9Ññ_-]{2,30}$", 
                nombreUsuarioCrear.getText()
            )
        )
        {
            u = new Usuario(
                    nombreUsuarioCrear.getText(),
                    apellidosUsuarioCrear.getText(),
                    emailUsuarioCrear.getText()
            );
        
            boolean yaExiste = true;

            try {
                yaExiste = UsuariosBDD.correoExiste(u.getEmail());
            } catch (Exception e) {
                AppErrorHandler.manejar(e, 
                    "UsuariosBDD.correoExiste(u.getEmail())");
            }

        if (!yaExiste){ 
                try {
                    insertado = UsuariosBDD.insertar(u);
                } catch (Exception e) {
                    AppErrorHandler.manejar(e, "UsuariosBDD.insertar(u);");
                }
            } else {
                Notificator.advertencia("Intento de duplicado", "Ese usuario ya existe.");
            }

            } else {

            String ayuda = "";

            ayuda += "\nPara rellenera correctamente  el nombre sin caracteres especiales.";
            ayuda += "\nPara rellenera correctamente los apellidos sin caracteres especiales.";
            ayuda += "\nPara la direccion correo ponga el nombre de usuario, el arroba y despues el dominio valido que le corresponada";

            Notificator.error("Formulario vacio o mal", 
            "Por favor rellene correctamente el formulario." + "\n" + 
            ayuda);
            }

        if(insertado){
            Notificator.exito("Exito", "Se ha insertado correctamente el usuario");
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
