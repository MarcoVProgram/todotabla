package com.decroly.todotabla.utils.cells;

import com.decroly.todotabla.model.Asignacion;
import com.decroly.todotabla.model.Estado;
import com.decroly.todotabla.model.Tarea;
import com.decroly.todotabla.model.sql.AsignacionesBDD;
import com.decroly.todotabla.model.sql.TareasBDD;
import com.decroly.todotabla.utils.AppErrorHandler;
import com.decroly.todotabla.utils.ColumnaKanban;
import com.decroly.todotabla.utils.constants.ColoresPrioridad;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorInput;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
/**
 * Celda interactiva de alta complejidad encargada del motor Drag and Drop (arrastrar y soltar)
 * de las tarjetas de tareas entre las columnas del tablero Kanban.
 * <p>
 * Orquesta la captura de gestos de ratón, la generación de snapshots semi-transparentes flotantes,
 * el recálculo dinámico de prioridades numéricas enteras (ordenación secuencial dentro de la lista)
 * y la persistencia transaccional inmediata en base de datos mediante llamadas al modelo SQL.
 * </p>
 *
 * @author Decroly
 * @version 1.0
 * @see com.decroly.todotabla.model.Tarea
 * @see com.decroly.todotabla.utils.ColumnaKanban
 */
public class TareaMovableCell extends ListCell<Tarea> {

    //Variables a dibujar
    private final Label titulo;
    private final Label estadoLabel;
    private final Region dot;
    private final VBox card;
    private final Label ownerLabel;
    private Circle bg;

    //Variables Mover cosas
    private double dragOffsetX;
    private double dragOffsetY;
    private final BorderPane root;
    private final Map<Estado, ColumnaKanban> columnMap;
    private ImageView ghost;
    private ColorInput tintInput;
    private Node lastHoveredCell;

    //Constructor
    public TareaMovableCell(BorderPane root, Map<Estado, ColumnaKanban> columnMap) {
        this.root = root;
        this.columnMap = columnMap;

        // Titulo
        titulo = new Label();
        titulo.getStyleClass().add("titulo-tarea");
        titulo.setMaxWidth(Double.MAX_VALUE);
        titulo.setWrapText(true);
        HBox.setHgrow(titulo, Priority.ALWAYS);

//        // Color Punto Prioridad
        dot = new Region();
        dot.getStyleClass().add("prioridad-dot");

        // Texto Prioridad
        estadoLabel = new Label();
        estadoLabel.getStyleClass().add("prioridad-label");

        HBox prioRow = new HBox(6, dot, estadoLabel);
        prioRow.setAlignment(Pos.CENTER_LEFT);

        // Label Letra User
        ownerLabel = new Label("");
        bg = new Circle(14);


        // Avatar - Dibuja o toma
        StackPane avatarPane = buildAvatarPane();

        // Final de la tarea
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox bottomRow = new HBox(prioRow, spacer, avatarPane);
        bottomRow.setAlignment(Pos.CENTER);

        // Tarea
        card = new VBox(8, titulo, bottomRow);
        card.getStyleClass().add("task-card");
        card.setMaxWidth(Double.MAX_VALUE);
        card.setPrefWidth(Region.USE_COMPUTED_SIZE);

        setMaxWidth(Double.MAX_VALUE);

        // Eventos
        this.setOnMousePressed(e -> onDragStart(e));
        this.setOnMouseDragged(e -> onDragMove(e));
        this.setOnMouseReleased(e -> onDragEnd(e));
    }

    // Metodo publico para ordenar tareas para estas celdas
    public static SortedList<Tarea> sorted(ObservableList<Tarea> source) {
        return new SortedList<>(source,
                Comparator.comparingInt(t ->
                        (t.getPrioridad())
                )
        );
    }

    // Metodo de mostrar tareas correcto
    @Override
    protected void updateItem(Tarea tarea, boolean empty) {
        super.updateItem(tarea, empty);

        if (empty || tarea == null) {
            setGraphic(null);
            setText(null);
            setStyle("-fx-background-color: transparent;");
            return;
        }

        // Título
        titulo.setText(tarea.getNombre());

        // Color asociado a prioridad
        dot.setStyle("-fx-background-color: " + tarea.getEstado().getColor() + ";");
        estadoLabel.setText(tarea.getEstado().getNombre());

        // Avatar Letra
        Map<Integer, Asignacion> asignados;
        try {
            asignados = AsignacionesBDD.getAsignaciones(tarea);
        } catch (Exception e) {
            AppErrorHandler.manejar(e, "getAsignaciones");
            asignados = new HashMap<>();
        }

        String initials = "";

        boolean hasActiveAsignees = false;
        for  (Map.Entry<Integer, Asignacion> entry : asignados.entrySet()) {
            if (entry.getValue().getIdUsuario() != null && entry.getValue().getFechaFin() == null) {
                hasActiveAsignees = true;
                if (initials.length() != 0) {
                    initials += "|";
                }
                initials += entry.getValue().getIdUsuario().getNombre().substring(0, 1).toUpperCase();
            }
        }
        ownerLabel.setText(initials);

        // Avatar Color
        if (!hasActiveAsignees) {
            bg.setFill(Color.TRANSPARENT);
        }
        else {
            bg.setFill(Color.web(ColoresPrioridad.getColores(tarea.getPrioridad())));
        }

        setGraphic(card);
        setStyle("-fx-background-color: transparent;");
    }

    private StackPane buildAvatarPane() {
        bg.setFill(Color.web("#1f6feb"));

        ownerLabel.setStyle("-fx-text-fill: #e6edf3; -fx-font-size: 11px; -fx-font-weight: bold;");

        StackPane pane = new StackPane(bg, ownerLabel);
        pane.setPrefSize(28, 28);
        pane.setMinSize(28, 28);
        pane.setMaxSize(28, 28);

        return pane;
    }

    private void onDragStart(MouseEvent e) {
        //Salir si no es valido
        if (getItem() == null) return;
        if (e.getButton() == MouseButton.SECONDARY) return;

        // Guardado de posicion inicial
        Bounds cardBounds = card.localToScene(card.getBoundsInLocal());
        this.dragOffsetX = e.getSceneX() - cardBounds.getMinX();
        this.dragOffsetY = e.getSceneY() - cardBounds.getMinY();

        // Toma una radiografia de la tarea
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        WritableImage snapshot = card.snapshot(params, null);

        // Construccion de la tarea fantasma
        ghost = new ImageView(snapshot);
        ghost.setManaged(false);
        ghost.setMouseTransparent(true);
        ghost.setOpacity(0.85);

        tintInput = new ColorInput(0, 0, snapshot.getWidth(), snapshot.getHeight(), Color.TRANSPARENT);
        Blend blend = new Blend(BlendMode.SRC_ATOP);
        blend.setTopInput(tintInput);
        ghost.setEffect(blend);

        // Fantasma sobre la original
        Point2D rootPos = root.sceneToLocal(cardBounds.getMinX(), cardBounds.getMinY());
        ghost.setLayoutX(rootPos.getX());
        ghost.setLayoutY(rootPos.getY());

        root.getChildren().add(ghost);

        // Transparencia original
        card.setOpacity(0.3);
    }

//    private void onDragMove(MouseEvent e) {
//        // Si el Fantasma no es Null
//        if (ghost == null) return;
//        if (e.getButton() == MouseButton.SECONDARY) return;
//
//        //Limpiar estilos
//        if (lastHoveredCell != null) {
//            lastHoveredCell.getStyleClass().remove("ghostChosen");
//        }
//
//        // Mover el fantasma
//        Point2D rootPos = root.sceneToLocal(
//            e.getSceneX() - dragOffsetX,
//            e.getSceneY() - dragOffsetY
//        );
//
//        ghost.setLayoutX(rootPos.getX());
//        ghost.setLayoutY(rootPos.getY());
//
//        ColumnaKanban colHover = getColumna(e.getScreenX(), e.getScreenY());
//        if  (colHover != null) {
//            tintInput.setPaint(Color.web(colHover.estado().getColor(), 0.4));
//            ListCell<Tarea> tareaFocused = getHoveredListCell(colHover, e.getScreenX(), e.getScreenY());
//            if (tareaFocused != null && tareaFocused.getItem() != null) {
//                tareaFocused.getStyleClass().add("ghostChosen");
//                lastHoveredCell = tareaFocused;
//            } else {
//                lastHoveredCell = null;
//            }
//        }
//        else {
//            tintInput.setPaint(Color.TRANSPARENT);
//        }
//    }
private void onDragMove(MouseEvent e) {
    // Si el Fantasma no es Null
    if (ghost == null) return;
    if (e.getButton() == MouseButton.SECONDARY) return;

    // Limpiar estilos previos con seguridad
    if (lastHoveredCell != null) {
        lastHoveredCell.getStyleClass().remove("ghostChosen");
        lastHoveredCell = null; // Lo reiniciamos
    }

    // Mover el fantasma
    Point2D rootPos = root.sceneToLocal(
            e.getSceneX() - dragOffsetX,
            e.getSceneY() - dragOffsetY
    );

    ghost.setLayoutX(rootPos.getX());
    ghost.setLayoutY(rootPos.getY());

    ColumnaKanban colHover = getColumna(e.getScreenX(), e.getScreenY());
    if (colHover != null) {
        tintInput.setPaint(Color.web(colHover.estado().getColor(), 0.4));
        ListCell<Tarea> tareaFocused = getHoveredListCell(colHover, e.getScreenX(), e.getScreenY());

        // Cambiado aquí: Solo añadimos el estilo visual si la celda existe
        if (tareaFocused != null && tareaFocused.getItem() != null) {
            tareaFocused.getStyleClass().add("ghostChosen");
            lastHoveredCell = tareaFocused;
        }
    }
    else {
        tintInput.setPaint(Color.TRANSPARENT);
    }
}

//    private void onDragEnd(MouseEvent e) {
//        // realizar los updates en base al resultado
//        if (ghost == null) return;
//        if (getItem() == null) return;
//        if (e.getButton() == MouseButton.SECONDARY) return;
//
//        ColumnaKanban colChosen = getColumna(e.getScreenX(), e.getScreenY());
//        if  (colChosen != null) {
//            ListCell<Tarea> cellChosen = getHoveredListCell(colChosen, e.getScreenX(), e.getScreenY());
//            cellChosen.getStyleClass().remove("ghostChosen");
//            Tarea tareaFocused = getHoveredTarea(colChosen, e.getScreenX(), e.getScreenY());
//            moverTarea(colChosen, tareaFocused);
//        }
//
//        // Adios fantasma
//        root.getChildren().remove(ghost);
//
//        // Carta vuelta a ser full
//        card.setOpacity(1);
//    }

    private void onDragEnd(MouseEvent e) {
        // realizar los updates en base al resultado
        if (ghost == null) return;
        if (getItem() == null) return;
        if (e.getButton() == MouseButton.SECONDARY) return;

        ColumnaKanban colChosen = getColumna(e.getScreenX(), e.getScreenY());
        if (colChosen != null) {
            ListCell<Tarea> cellChosen = getHoveredListCell(colChosen, e.getScreenX(), e.getScreenY());

            // --- AQUÍ ESTÁ EL CAMBIO DEFENSIVO ---
            // Solo intentamos quitar la clase si realmente se encontró una celda bajo el cursor
            if (cellChosen != null) {
                cellChosen.getStyleClass().remove("ghostChosen");
            }

            Tarea tareaFocused = getHoveredTarea(colChosen, e.getScreenX(), e.getScreenY());
            moverTarea(colChosen, tareaFocused);
        }

        // Adios fantasma
        root.getChildren().remove(ghost);

        // Carta vuelta a ser full
        card.setOpacity(1);
    }

    private ColumnaKanban getColumna(double screenX, double screenY) {
        return columnMap.values().stream()
                .filter(col -> col.lvTareas().localToScreen(col.lvTareas().getBoundsInLocal()).contains(screenX, screenY))
                .findFirst()
                .orElse(null);
    }

    private ListCell<Tarea> getHoveredListCell(ColumnaKanban colHover, double screenX, double screenY) {
        return colHover.lvTareas().lookupAll(".list-cell").stream()
                .filter(node -> node.localToScreen(node.getBoundsInLocal()).contains(screenX, screenY))
                .findFirst()
                .map(node -> (ListCell<Tarea>) node)
                .orElse(null);
    }

    private Tarea getHoveredTarea(ColumnaKanban colHover, double screenX, double screenY) {
        return colHover.lvTareas().lookupAll(".list-cell").stream()
                .filter(node -> node.localToScreen(node.getBoundsInLocal()).contains(screenX, screenY))
                .findFirst()
                .map(node -> (ListCell<Tarea>) node)
                .map(ListCell::getItem)
                .orElse(null);
    }
/** ... (Propiedades de UI, nodos visuales e inyecciones de mapas de columnas)*/

    /**
     * Mueve lógicamente una tarea desde su columna Kanban de origen hacia una nueva columna de destino,
     * reconfigurando su prioridad ordinal de inserción según la tarjeta que se encuentre en la posición de soltado.
     *
     * @param destino     La instancia contenedora {@link ColumnaKanban} de destino.
     * @param tareaChosen La tarea de referencia sobre la cual se hace el drop para calcular la posición exacta.
     */
    private void moverTarea(ColumnaKanban destino, Tarea tareaChosen) {
        if (destino != null) {
            Tarea t = getItem();
            ColumnaKanban partida = columnMap.get(t.getEstado());

            if (tareaChosen != null) {
                t.setPrioridad(tareaChosen.getPrioridad());
            } else {
//                try {
//                    t.setPrioridad(TareasBDD.getMayorPrioridad(t.getIdProyecto())+1);
//                } catch (Exception ex) {
//                    AppErrorHandler.manejar(ex, "getMayorPrioridad");
//                }
                t.setPrioridad(0);
            }

            t.setEstado(destino.estado());
            partida.olTareas().remove(t);
            
//            destino.olTareas().add(Math.max(Math.min(t.getPrioridad(), destino.olTareas().size()), 0), t);
            int indiceInsercion = Math.max(Math.min(t.getPrioridad(), destino.olTareas().size()), 0);
            destino.olTareas().add(indiceInsercion, t);

            actualizarLista(partida);
            actualizarLista(destino);
        }
    }
    /**
     * Recorre secuencialmente la lista observable de una columna Kanban tras un movimiento,
     * normalizando los índices enteros de propiedad 'prioridad' de todas sus tareas de forma correlativa (0, 1, 2...)
     * y sincronizando dicho estado con el servidor mediante sentencias batch asíncronas.
     *
     * @param columnaEstadoKanban Columna Kanban cuyo orden secuencial de elementos internos se desea consolidar.
     */
    private void actualizarLista(ColumnaKanban columnaEstadoKanban) {
        for (int i = 0; i < columnaEstadoKanban.olTareas().size(); i++) {
            columnaEstadoKanban.olTareas().get(i).setPrioridad(i);
            try {
                TareasBDD.actualizar(columnaEstadoKanban.olTareas().get(i));
            } catch (Exception ex) {
                AppErrorHandler.manejar(ex, "actualizar");
            }
        }
    }
}
