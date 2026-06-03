package com.decroly.todotabla;

import com.decroly.todotabla.model.Proyecto;
import com.decroly.todotabla.model.Tarea;
import com.decroly.todotabla.utils.AppErrorHandler;
import com.decroly.todotabla.utils.EstadoPrograma;
import com.decroly.todotabla.utils.Navigator;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class HistorialController implements Initializable {

    @FXML
    private ImageView returnBtn;

    private Tarea tareaActiva;
    private Proyecto proyectoActivo;

    @FXML
    private Label proyectoTitulo;

    @FXML
    private Label tareaHistorialNombre;

    @FXML
    private TextField nuevoNombreTarea;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tareaActiva = EstadoPrograma.getInstance().getTareaActiva();
        proyectoActivo = EstadoPrograma.getInstance().getProyectoActivo();
        proyectoTitulo.setText("🔒 " + proyectoActivo.getTitulo());
        tareaHistorialNombre.setText(tareaActiva.getNombre());
        nuevoNombreTarea.setText(tareaActiva.getNombre());
    }

    @FXML
    private void returnToMain() { //abrir pantalla principal (menú)
        Stage stage = (Stage) returnBtn.getScene().getWindow();
        try {
            Navigator.changeScene(stage, "/com/decroly/todotabla/main-view.fxml");
        } catch (Exception ex) {
            AppErrorHandler.manejar(ex, "returnToMain");
        }
    }

    @FXML
    private void returnToKanban() {
        Stage stage = (Stage) returnBtn.getScene().getWindow();
        try {
            Navigator.changeScene(stage, "/com/decroly/todotabla/kanban-view.fxml");
        } catch (Exception ex) {
            AppErrorHandler.manejar(ex, "returnToMain");
        }
    }
}
