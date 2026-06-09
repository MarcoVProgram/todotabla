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
/**
 * Controlador de interfaz de usuario encargado del flujo de captura de datos,
 * validación reactiva en cliente y alta formal de nuevas entidades de tipo {@link Usuario}.
 * <p>
 * Esta clase implementa un enfoque de diseño defensivo en el frontend mediante la evaluación
 * en tiempo real de las propiedades de texto de los campos de entrada, asegurando que no se
 * deleguen peticiones de inserción con datos vacíos o inconsistentes a la capa de persistencia.
 * </p>
 *
 * @author Senior Developer
 * @version 1.0.0
 * @see javafx.fxml.Initializable
 * @see com.decroly.todotabla.model.sql.UsuariosBDD
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
     * Procesa la captura de metadatos del formulario, instancia el modelo de negocio y despacha la transacción de inserción.
     * <p>
     * Extrae los valores sanitizados de los campos de texto, construye un objeto {@link Usuario} e interroga
     * de manera síncrona a la capa de persistencia relacional a través del método {@code UsuariosBDD.insertar(u)}.
     * En caso de una confirmación (commit) exitosa, emite una notificación visual mediante {@link Notificator#exito}
     * y purga de forma atómica los campos del formulario para permitir flujos continuos de alta de datos; en caso contrario,
     * desvía el control del flujo hacia el subsistema de alertas de error.
     * </p>
     */
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
    /**
     * Inicializa las propiedades de control y los escuchadores reactivos de la vista de creación.
     * <p>
     * Registra un objeto unificado de tipo {@link ChangeListener} sobre las propiedades de texto
     * ({@code textProperty()}) de los campos de entrada de la UI (nombre, apellidos y email).
     * El listener evalúa de forma perezosa (lazy) la presencia de cadenas vacías o compuestas únicamente
     * por espacios en blanco, mutando el estado de inhabilitación ({@code setDisable}) del botón de inserción
     * para mitigar errores de validación en tiempo de ejecución.
     * </p>
     *
     * @param location  La ubicación utilizada para resolver rutas relativas para el objeto raíz.
     * @param resources Los recursos utilizados para localizar el objeto raíz.
     */
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
