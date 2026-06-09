package com.decroly.todotabla;

import java.net.URL;
import java.util.*;

import com.decroly.todotabla.model.Integrante;
import com.decroly.todotabla.model.Usuario;
import com.decroly.todotabla.model.sql.IntegrantesBDD;
import com.decroly.todotabla.utils.AppErrorHandler;
import com.decroly.todotabla.utils.EstadoPrograma;
import com.decroly.todotabla.utils.cells.UsuariosCell;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
/**
 * Controlador especializado en la lógica pura de re-asignación dinámica de operarios sobre tareas en ejecución.
 * <p>
 * Actúa de manera atómica limpiando estructuras volátiles del singleton del sistema e inyectando listados de
 * integrantes filtrados reactivamente por su estado de activación operativa dentro del proyecto activo.
 * </p>
 *
 * @author Senior Developer
 * @version 1.0.0
 */
public class TareaAsignarController implements Initializable {
    
    @FXML
    private TextField buscarUsuario;

    @FXML
    private ListView<Usuario> listViewUsuarios;

    @FXML
    private AnchorPane root;

    private List<Usuario> listaAAsignar;
    /**
     * Inicializa los buffers volátiles e inicia la hidratación de datos de integrantes activos.
     * <p>
     * Ejecuta una purga de seguridad sobre los arrays temporales de {@link EstadoPrograma} para prevenir colisiones
     * de referencias de memoria y despacha el cargado del listado maestro observable mediante {@code getObsIntegrantesList()}.
     * </p>
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        EstadoPrograma.getInstance().setIntegrantesTemp(null);
        getObsIntegrantesList();
    }
    /**
     * Consulta, estructura y encapsula las entidades de usuario válidas para asignación en el contenedor visual.
     * <p>
     * Interroga a {@code IntegrantesBDD.getIntegrantesActivos()}, extrae los mapeos lógicos de usuario subyacentes,
     * los introduce en una colección de tipo {@link ObservableList} y asocia una factoría de celdas customizada ({@link UsuariosCell}).
     * </p>
     */
    private void getObsIntegrantesList() {
        Map<Integer, Integrante> map = new HashMap<>();

        try {
            map = IntegrantesBDD.getIntegrantesActivos(
                    EstadoPrograma.getInstance().getProyectoActivo()
            );
        } catch (Exception e) {
            AppErrorHandler.manejar(e, "getObsIntegrantesList");
        }

        ArrayList<Usuario> al = new ArrayList<>();
        for (Integrante i : map.values()) {
            
            al.add(i.getIdUsuario());
        }

        ObservableList<Usuario> obsIntegrantesList =
                FXCollections.observableArrayList(al);

        listViewUsuarios.setItems(obsIntegrantesList);
        listViewUsuarios.setCellFactory(cell -> new UsuariosCell());
    }

    @FXML
    public void salir(MouseEvent event) {
        Stage stage = (Stage) root.getScene().getWindow();
        stage.close();
    }
    /**
     * Confirma la selección del operario actual y la exporta al ámbito temporal del programa para su uso transaccional.
     *
     * @param event El evento de acción originado desde el control de aceptación de la UI.
     */
    @FXML
    public void asignarUsuarios(ActionEvent event) {
        listaAAsignar = listViewUsuarios.getSelectionModel().getSelectedItems();
        EstadoPrograma.getInstance().setUsuariosTemp(listaAAsignar);
        Stage stage = (Stage) root.getScene().getWindow();
        stage.close();
    }
}
