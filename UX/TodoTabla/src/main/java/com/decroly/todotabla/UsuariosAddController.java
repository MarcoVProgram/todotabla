package com.decroly.todotabla;

import com.decroly.todotabla.model.Usuario;
import com.decroly.todotabla.model.sql.UsuariosBDD;
import com.decroly.todotabla.utils.AppErrorHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class UsuariosAddController {

    @FXML
    private TextField apellidosUsuarioCrear;

    @FXML
    private TextField emailUsuarioCrear;

    @FXML
    private TextField nombreUsuarioCrear;

    @FXML
    public void addTarea(MouseEvent event) {
        Usuario u = new Usuario(
                nombreUsuarioCrear.getText(),
                apellidosUsuarioCrear.getText(),
                emailUsuarioCrear.getText()
        );
        try {
            UsuariosBDD.insertar(u);
        } catch (Exception e) {
            AppErrorHandler.manejar(e, "UsuariosBDD.insertar(u);");
        }
    }
}
