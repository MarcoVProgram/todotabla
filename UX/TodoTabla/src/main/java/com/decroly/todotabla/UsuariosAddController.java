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

/**
 * Controlador del formulario de creación de usuarios.
 * Valida el nombre, los apellidos y el correo electrónico introducidos,
 * comprueba que el correo no esté ya registrado e inserta el nuevo usuario en la base de datos.
 * El botón de creación permanece deshabilitado mientras algún campo esté vacío.
 */
public class UsuariosAddController implements Initializable {
    @FXML
    public Button btnAddUser;
    
    @FXML
    private TextField apellidosUsuarioCrear;

    @FXML
    private TextField emailUsuarioCrear;

    @FXML
    private TextField nombreUsuarioCrear;


    /**
     * Valida los campos del formulario, comprueba que el correo no exista previamente
     * e inserta el nuevo usuario en la base de datos si todos los datos son correctos.
     * Muestra notificaciones de éxito, advertencia o error según el resultado.
     */
    @FXML
    public void addUsuario() {

        Usuario u;

        boolean insertado = false;

        boolean nombreCorrecto = nombreUsuarioCrear.getText() != null && !nombreUsuarioCrear.getText().isBlank() &&
                Pattern.matches("^[A-Za-z 0-9Ññ_-]{2,30}$", nombreUsuarioCrear.getText());
        boolean apellidosCorrecto = apellidosUsuarioCrear.getText() != null && !apellidosUsuarioCrear.getText().isBlank() &&
                Pattern.matches("^[A-Za-z 0-9Ññ_-]{2,45}$", apellidosUsuarioCrear.getText());
        boolean emailCorrecto = emailUsuarioCrear.getText() != null && !emailUsuarioCrear.getText().isBlank() &&
            Pattern.matches("[A-Za-z-._0-9Ññ]+@[A-Za-z]+[.][A-Za-z]{2,4}", emailUsuarioCrear.getText().toUpperCase());

        if (nombreCorrecto && apellidosCorrecto && emailCorrecto) {
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
            if(insertado){
                Notificator.exito("Exito", "Se ha insertado correctamente el usuario");
                nombreUsuarioCrear.clear();
                apellidosUsuarioCrear.clear();
                emailUsuarioCrear.clear();
            }else{
                Notificator.error("Error", "No se pudo insertar al usuario.");
            }
        } else {

            String ayuda = "";

            if (nombreCorrecto) {
                ayuda += "\nPara rellenar correctamente el nombre: Usa letras, espacios y numeros entre 2 y 30.\n";
            }
            if (apellidosCorrecto) {
                ayuda += "\nPara rellenar correctamente los apellidos: Usa letras, espacios y numeros entre 2 y 45.\n";
            }
            ayuda += "\nPara rellenar correctamente el correo, siga la estructura 'texto@ejemplo.com'. Asegúrate que no exista.";

            Notificator.error("Formulario vacio o mal", 
            "Por favor rellene correctamente el formulario." + "\n" + 
            ayuda);
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
