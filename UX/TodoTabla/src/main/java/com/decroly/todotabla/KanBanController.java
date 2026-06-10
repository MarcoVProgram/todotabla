package com.decroly.todotabla;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import com.decroly.todotabla.model.Estado;
import com.decroly.todotabla.model.Integrante;
import com.decroly.todotabla.model.Proyecto;
import com.decroly.todotabla.model.Tarea;
import com.decroly.todotabla.model.sql.EstadosBDD;
import com.decroly.todotabla.model.sql.TareasBDD;
import com.decroly.todotabla.utils.AppErrorHandler;
import com.decroly.todotabla.utils.ColumnaKanban;
import com.decroly.todotabla.utils.EstadoPrograma;
import com.decroly.todotabla.utils.Navigator;
import com.decroly.todotabla.utils.cells.TareaMovableCell;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

/**
 * Controlador principal del tablero Kanban.
 * Genera dinámicamente una columna por cada {@link Estado} registrado en la base de datos,
 * gestiona el arrastre de tareas entre columnas y permite filtrar tareas por nombre en tiempo real.
 */
public class KanBanController implements Initializable {

    @FXML
    private HBox contenedorColumnas;
    
    private List<Integrante> integrantes = new ArrayList<>();
    private ObservableList<Integrante> obsIntegrantes = FXCollections.observableList(integrantes);


    private Proyecto proyectoSeleccionado;
    private List<Estado> estados;

    @FXML
    private BorderPane root;



    @FXML
    private Label proyectoTitulo;

    private Map<Estado, ColumnaKanban> columnMap = new HashMap<>();

    @FXML
    private TextField buscarTareaSearchBar;



    public void initialize(URL url, ResourceBundle rb) {
        try {
            estados = EstadosBDD.getEstados();
        } catch (Exception ex) {
            AppErrorHandler.manejar(ex, "getEstados");
        }
        estados.sort(Comparator.comparingInt(Estado::getOrden));
        proyectoSeleccionado = EstadoPrograma.getInstance().getProyectoActivo();
        proyectoTitulo.setText("🔒 " + proyectoSeleccionado.getTitulo());

        this.buscarTareaSearchBar.textProperty().addListener((observable) -> filtrarTareas());

        actualizarTareas();
    }

    /**
     * Reconstruye todas las columnas del tablero limpiando el contenedor
     * y regenerando una columna por cada estado disponible.
     */
    private void actualizarTareas() {
        contenedorColumnas.getChildren().clear();
        columnMap.clear();

        for (Estado estado : estados) {
            columnMap.put(estado, addColumna(estado));
        }
    }

    /**
     * Aplica el predicado de búsqueda sobre la {@link FilteredList} de cada columna
     * según el texto introducido en la barra de búsqueda.
     */
    private void filtrarTareas() {
        for (Map.Entry<Estado, ColumnaKanban> entry : columnMap.entrySet()) {
            entry.getValue().flTareas().setPredicate(tarea ->
                    this.buscarTareaSearchBar.getText().isBlank() ||
                            tarea.getNombre().toLowerCase().contains(this.buscarTareaSearchBar.getText().toLowerCase()) );
        }
    }

    /**
     * Crea y configura una columna Kanban para el estado indicado.
     * Carga sus tareas desde la base de datos, configura el arrastre y,
     * si el estado es "pendiente", añade un botón de creación de tarea como placeholder.
     *
     * @param estado el estado que representa esta columna
     * @return la {@link ColumnaKanban} construida
     */
    private ColumnaKanban addColumna(Estado estado) {

        ObservableList<Tarea> items;
        FilteredList<Tarea> filteredTareas;

        try {
            items = FXCollections.observableArrayList(
                    TareasBDD.getTareas(estado, proyectoSeleccionado).values()
            );

        } catch (Exception ex) {
            AppErrorHandler.manejar(ex, "getTareas");
            items = FXCollections.observableArrayList();
        }
        filteredTareas = new FilteredList<>(items, tarea -> true);

        ListView<Tarea> listView = constructorColumnas(estado, items, filteredTareas);

        listView.setOnMouseClicked(event -> {
            if (event.getButton() != MouseButton.SECONDARY && event.getClickCount() != 2) return;

            Node clickedNode = event.getPickResult().getIntersectedNode();
            while (clickedNode != null && !(clickedNode instanceof ListCell)) {
                clickedNode = clickedNode.getParent();
            }

            if (clickedNode instanceof ListCell<?> cell && !cell.isEmpty()) {
                Tarea selected = (Tarea) cell.getItem();
                if (selected == null) return;
                EstadoPrograma.getInstance().setTareaActiva(selected);
                abrirVentanaHistorialTareas();
            }
        });

        try {
            if (estado.getNombre().equalsIgnoreCase("pendiente")) {
                Button addButton = new Button("+ Crear Nueva Tarea");
                addButton.getStyleClass().add("placeholder-add-button");
                addButton.setOnAction(e -> abrirVentanaCrearTarea());
                addButton.setAlignment(Pos.TOP_CENTER);
                listView.setPlaceholder(addButton);
            }
        } catch (Exception e) {
            AppErrorHandler.manejar(e, "getEstado");
        }
        return new ColumnaKanban(estado, listView, items, filteredTareas);
    }

    /**
     * Construye visualmente la columna con su encabezado, lista de tareas y estilos,
     * y la añade al contenedor de columnas.
     *
     * @param estado        el estado asociado a la columna
     * @param items         la lista observable de tareas de la columna
     * @param filtered      la lista filtrada aplicada sobre {@code items}
     * @return la {@link ListView} de tareas construida
     */
    private ListView<Tarea> constructorColumnas(Estado  estado, ObservableList<Tarea> items,
                                                FilteredList<Tarea> filtered) {
        Circle dot = new Circle(4);
        dot.setStyle("-fx-fill: " + estado.getColor() + ";");

        Label title = new Label(estado.getNombre() + " [" + items.size() + "]");
        title.getStyleClass().add("column-title");

        items.addListener((ListChangeListener<Tarea>) c ->
                title.setText(estado.getNombre() + " [" + items.size() + "]")
        );

        HBox titleRow = new HBox(6, dot, title);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        ListView<Tarea> listView = new ListView<>(TareaMovableCell.sorted(filtered));
        listView.setCellFactory(lv -> new TareaMovableCell(root, columnMap));
        listView.getStyleClass().add("kanban-list");
        listView.setPrefHeight(579);
        listView.setMaxWidth(Double.MAX_VALUE);
        listView.setMinWidth(150);
        listView.setStyle("-fx-background-color: #0b0b0b;");
        VBox.setVgrow(listView, Priority.ALWAYS);

        VBox column = new VBox(titleRow, listView);
        column.getStyleClass().add("column");
        column.setStyle("-fx-background-color: #0b0b0b;");
        column.setMaxWidth(Double.MAX_VALUE);
        column.setMinWidth(150);
        HBox.setHgrow(column, Priority.ALWAYS);

        contenedorColumnas.getChildren().add(column);
        return listView;
    }


    /**
     * Navega de vuelta a la vista principal del menú.
     */
    @FXML
    private void returnToMain() {
        Stage stage = (Stage) root.getScene().getWindow();
        try {
            Navigator.changeScene(stage, "/com/decroly/todotabla/main-view.fxml");
        } catch (Exception ex) {
            AppErrorHandler.manejar(ex, "returnToMain");
        }
    }

    /**
     * Navega a la vista de historial de la tarea actualmente seleccionada.
     */
    @FXML
    private void abrirVentanaHistorialTareas() {
        Stage stage = (Stage) root.getScene().getWindow();
        try {
            Navigator.changeScene(stage, "/com/decroly/todotabla/historial-view.fxml");
        } catch (Exception ex) {
            AppErrorHandler.manejar(ex, "abrirVentanaHistorialTareas");
        }
    }

    /**
     * Abre la ventana secundaria de creación de tarea y recarga el tablero al cerrarla.
     */
    @FXML
    private void abrirVentanaCrearTarea() {
        try {

            Navigator.arbrirVentanaSecundaria("tarea-view-create.fxml", "Añadir tarea", getClass());

            actualizarTareas();

        } catch (IOException e) {
            AppErrorHandler.manejar(e, "abrirVentanaCrearTarea");
        }
    }

    /**
     * Abre la ventana secundaria de eliminación de tarea y recarga el tablero al cerrarla.
     */
    @FXML
    private void abrirVentanaBorrarTarea() {
        try {

            String titulo = "Borrar tarea";

            String fxml = "tarea-view-remove.fxml";

            Navigator.arbrirVentanaSecundaria(fxml, titulo, getClass());

            actualizarTareas();

        } catch (IOException e) {
            AppErrorHandler.manejar(e, "abrirVentanaEditarTarea");
        }
    }

    /**
     * Abre la ventana secundaria de gestión de integrantes del proyecto activo.
     */
    @FXML
    private void abrirVentanaIntegrantes() {
        try {

            String fxml = "usuarios-formIntegrantes.fxml";

            String titulo = "Gestionar usuarios";

            Navigator.arbrirVentanaSecundaria(fxml, titulo, getClass());


        } catch (IOException e) {
            AppErrorHandler.manejar(e, "abrirVentanaIntegrantes");
        }
    }
}
