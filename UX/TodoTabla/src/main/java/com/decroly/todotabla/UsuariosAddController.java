package com.decroly.todotabla;

import java.util.regex.Pattern;

import com.decroly.todotabla.model.Usuario;
import com.decroly.todotabla.model.sql.UsuariosBDD;
import com.decroly.todotabla.utils.AppErrorHandler;
import com.decroly.todotabla.utils.Notificator;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class UsuariosAddController {

    @FXML
    private TextField apellidosUsuarioCrear;

    @FXML
    private TextField emailUsuarioCrear;

    @FXML
    private TextField nombreUsuarioCrear;

    @FXML
    public void addTarea() {

        

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
            Usuario u = new Usuario(
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
                    UsuariosBDD.insertar(u);
                    Notificator.exito("Usuario introducido", 
                    "El usuario se ha agregado correctamente");
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
    }
}
