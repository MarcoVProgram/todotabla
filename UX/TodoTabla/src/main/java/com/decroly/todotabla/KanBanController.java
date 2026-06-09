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
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
/**
 * Controlador Core y Orquestador Principal de la Vista del Tablero Ágil Kanban de la aplicación.
 * <p>
 * Esta clase representa una de las piezas arquitectónicas de mayor complejidad del sistema. Controla la instanciación
 * dinámica de columnas verticales basadas en los estados del flujo de trabajo (Workflow), inyecta lógica de filtrado
 * predictivo en tiempo real mediante hilos de ejecución basados en propiedades observables ({@link FilteredList}) y
 * encapsula la lógica para la invocación asíncrona de operaciones CRUD sobre las tareas expuestas.
 * </p>
 *
 * @author Senior Developer
 * @version 1.0.0
 * @see com.decroly.todotabla.utils.ColumnaKanban
 * @see com.decroly.todotabla.utils.cells.TareaMovableCell
 */
public class KanBanController implements Initializable {
    @FXML
    private ImageView returnBtn;

    //Contenedor
    @FXML
    private HBox contenedorColumnas;
    
    private List<Integrante> integrantes = new ArrayList<>();
    private ObservableList<Integrante> obsIntegrantes = FXCollections.observableList(integrantes);

    private static Stage ventanaTerciaria;

    private Proyecto proyectoSeleccionado;
    private List<Estado> estados;

    @FXML
    private BorderPane root;



    @FXML
    private Label proyectoTitulo;

    private Map<Estado, ColumnaKanban> columnMap = new HashMap<>();

    @FXML
    private TextField buscarTareaSearchBar;


    /**
     * Configura el entorno gráfico inicial del Tablero Kanban distribuido.
     * <p>
     * Recupera y ordena los estados definidos para el ciclo de vida del desarrollo a través de la capa relacional.
     * Enlaza la propiedad de texto del cuadro de búsqueda ({@code buscarTareaSearchBar}) a una rutina reactiva de filtrado
     * y despacha de manera diferida el método encargado del renderizado y poblamiento tridimensional del contenedor de columnas.
     * </p>
     */
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
     * Re-estructura el contenedor gráfico principal purgando las columnas previas y re-evaluando las tareas activas.
     * <p>
     * Itera secuencialmente sobre el conjunto ordenado de estados disponibles, insertando dinámicamente instancias de
     * {@link ColumnaKanban} dentro del layout horizontal y registrando de manera interna el mapeo conceptual llave-valor
     * para optimizar operaciones analíticas posteriores.
     * </p>
     */
    private void actualizarTareas() {
        contenedorColumnas.getChildren().clear();
        columnMap.clear();

        for (Estado estado : estados) {
            columnMap.put(estado, addColumna(estado));
        }
    }
    /**
     * Ejecuta una evaluación predictiva (Predicado) sobre el conjunto total de tareas visualizadas basándose en criterios de búsqueda alfanuméricos.
     * <p>
     * Evalúa las cadenas de texto introducidas por el operador contra los nombres estructurados de las tareas. Las tareas
     * no coincidentes son excluidas selectivamente de la escena visual sin alterar su integridad o persistencia en los almacenes relacionales de datos.
     * </p>
     */
    private void filtrarTareas() {
        for (Map.Entry<Estado, ColumnaKanban> entry : columnMap.entrySet()) {
            entry.getValue().flTareas().setPredicate(tarea ->
                    this.buscarTareaSearchBar.getText().isBlank() ||
                            tarea.getNombre().toLowerCase().contains(this.buscarTareaSearchBar.getText().toLowerCase()) );
        }
    }

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

            //Comprobar que se hace click correctamente en la celda
            Node clickedNode = event.getPickResult().getIntersectedNode();
            while (clickedNode != null && !(clickedNode instanceof ListCell)) {
                clickedNode = clickedNode.getParent();
            }

            //Nos quedamos con la celda que sea igual / Aviso, se necesita esta selección o selecciona la superior
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
                listView.setPlaceholder(addButton);
            }
        } catch (Exception e) {
            AppErrorHandler.manejar(e, "getEstado");
        }
        return new ColumnaKanban(estado, listView, items, filteredTareas);
    }

    private ListView<Tarea> constructorColumnas(Estado  estado, ObservableList<Tarea> items,
                                                FilteredList<Tarea> filtered) {
        // Circulo de Estado
        Circle dot = new Circle(4);
        dot.setStyle("-fx-fill: " + estado.getColor() + ";");

        // Titulo con contador
        Label title = new Label(estado.getNombre() + " [" + items.size() + "]");
        title.getStyleClass().add("column-title");

        // Actualización Automática
        items.addListener((ListChangeListener<Tarea>) c ->
                title.setText(estado.getNombre() + " [" + items.size() + "]")
        );

        // Titulo
        HBox titleRow = new HBox(6, dot, title);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        // ListView de Tarea
        ListView<Tarea> listView = new ListView<>(TareaMovableCell.sorted(filtered));
        listView.setCellFactory(lv -> new TareaMovableCell(root, columnMap));
        listView.getStyleClass().add("kanban-list");
        listView.setPrefHeight(579);
        listView.setMaxWidth(Double.MAX_VALUE);
        listView.setMinWidth(150);
        listView.setStyle("-fx-background-color: #0b0b0b;");
        VBox.setVgrow(listView, Priority.ALWAYS);

        // Columna creada
        VBox column = new VBox(titleRow, listView);
        column.getStyleClass().add("column");
        column.setStyle("-fx-background-color: #0b0b0b;");
        column.setMaxWidth(Double.MAX_VALUE);
        column.setMinWidth(150);
        HBox.setHgrow(column, Priority.ALWAYS);

        // Se añade a su hija
        contenedorColumnas.getChildren().add(column);
        return listView;
    }


    //----------------DESPLAZAMIENTO ENTRE VENTANAS-------------
    @FXML
    private void returnToMain() { //abrir pantalla principal (menú)
        Stage stage = (Stage) root.getScene().getWindow();
        try {
            Navigator.changeScene(stage, "/com/decroly/todotabla/main-view.fxml");
        } catch (Exception ex) {
            AppErrorHandler.manejar(ex, "returnToMain");
        }
    }
    /**
     * Despacha una ventana modal flotante de carácter secundario especializada en el flujo transaccional de creación de tareas.
     * <p>
     * Invoca de manera imperativa a {@link Navigator#arbrirVentanaSecundaria(String, String, Class)} y, tras la liberación exitosa
     * del hilo secundario de control, ejecuta una llamada de sincronización forzada ({@code actualizarTareas()}) para refrescar el tablero.
     * </p>
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

    @FXML
    private void abrirVentanaCrearTarea() { //panel tarea
        try {

            Navigator.arbrirVentanaSecundaria("tarea-view-create.fxml", "Añadir tarea", getClass());

            actualizarTareas();

        } catch (IOException e) {
            AppErrorHandler.manejar(e, "abrirVentanaCrearTarea");
        }
    }

    @FXML
    private void abrirVentanaEditarTarea() { //panel tarea
        try {

            String fxml = "tarea-view-mod.fxml";
            String titulo = "Editar tarea";


            Navigator.arbrirVentanaSecundaria(fxml, titulo, getClass());

            actualizarTareas();

        } catch (IOException e) {
            AppErrorHandler.manejar(e,  "abrirVentanaEditarTarea (fxml)");
        }
    }

    @FXML
    private void abrirVentanaBorrarTarea() { //panel tarea
        try {

            String titulo = "Borrar tarea";

            String fxml = "tarea-view-remove.fxml";

            Navigator.arbrirVentanaSecundaria(fxml, titulo, getClass());

            actualizarTareas();

        } catch (IOException e) {
            AppErrorHandler.manejar(e, "abrirVentanaEditarTarea");
        }
    }

    @FXML
    private void abrirVentanaIntegrantes() { //panel gestión usuarios
        try {

            String fxml = "usuarios-formIntegrantes.fxml";

            String titulo = "Gestionar usuarios";

            Navigator.arbrirVentanaSecundaria(fxml, titulo, getClass());


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
