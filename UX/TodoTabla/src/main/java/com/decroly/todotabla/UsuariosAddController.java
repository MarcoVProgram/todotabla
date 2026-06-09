package com.decroly.todotabla;

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

    private Usuario ultimoIntroducido = new Usuario("", "", "");

    @FXML
    public void addTarea() {

        System.out.println(nombreUsuarioCrear.getText() + "/n" +
                    apellidosUsuarioCrear.getText() + "/n" +
                    emailUsuarioCrear.getText());

        if (
            nombreUsuarioCrear.getText() != null && 
            !nombreUsuarioCrear.getText().isBlank() &&
            apellidosUsuarioCrear.getText() != null && 
            !apellidosUsuarioCrear.getText().isBlank() &&
            emailUsuarioCrear.getText() != null &&
            !emailUsuarioCrear.getText().isBlank()
            )
        {
            Usuario u = new Usuario(
                    nombreUsuarioCrear.getText(),
                    apellidosUsuarioCrear.getText(),
                    emailUsuarioCrear.getText()
            );

            if (!(u.getEmail().equals(ultimoIntroducido.getEmail()))){ 
                try {
                    UsuariosBDD.insertar(u);
                    Notificator.exito("Usuario introducido", 
                    "El usuario se ha agregado correctamente");
                    ultimoIntroducido = u;
                } catch (Exception e) {
                    AppErrorHandler.manejar(e, "UsuariosBDD.insertar(u);");
                }
            } else {
                Notificator.advertencia("Intento de duplicado", "Ese usuario ya existe.");
            }
        } else {
            Notificator.error("Formulario vacio o mal", 
            "Por favor rellene correctamente el formulario");
        }
    }
}
