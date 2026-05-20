package com.decroly.todotabla;

import com.decroly.todotabla.model.Proyecto;
import com.decroly.todotabla.model.sql.ProyetosBDD;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.time.LocalDate;
import java.time.chrono.IsoChronology;
import java.util.ResourceBundle;

public class ProyectoController implements Initializable{

    @FXML
    private TextField tituloProyecto;

    @FXML
    private DatePicker fechaProyecto;

//    @FXML
//    private Button addButtom;

    @FXML
    private void guardar() {
        Proyecto p = new Proyecto(
                tituloProyecto.getText(),
                fechaProyecto.getValue(),
                null
        );
        if (ProyetosBDD.insertar(p)) {
            (new Alert(Alert.AlertType.WARNING,
                    "No se ha podido crear",
                    ButtonType.OK
            )).showAndWait();
        };
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        fechaProyecto.setValue(LocalDate.now());
    }
}
