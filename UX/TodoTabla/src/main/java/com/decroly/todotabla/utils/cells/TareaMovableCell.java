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
 * Celda de lista arrastrable que representa una {@link Tarea} en el tablero Kanban.
 * Permite mover tareas entre columnas mediante drag and drop, mostrando un fantasma visual
 * durante el arrastre y actualizando la prioridad y el estado al soltar.
 */

public class TareaMovableCell extends ListCell<Tarea> {

    private final Label titulo;
    private final Label estadoLabel;
    private final Region dot;
    private final VBox card;
    private final Label ownerLabel;
    private Circle bg;

    private double dragOffsetX;
    private double dragOffsetY;
    private final BorderPane root;
    private final Map<Estado, ColumnaKanban> columnMap;
    private ImageView ghost;
    private ColorInput tintInput;
    private Node lastHoveredCell;

    /**
     * Crea una celda arrastrable vinculada al contenedor raíz y al mapa de columnas del tablero.
     *
     * @param root      el {@link BorderPane} raíz sobre el que se renderiza el fantasma de arrastre
     * @param columnMap mapa que asocia cada {@link Estado} con su {@link ColumnaKanban} correspondiente
     */
    public TareaMovableCell(BorderPane root, Map<Estado, ColumnaKanban> columnMap) {
        this.root = root;
        this.columnMap = columnMap;

        titulo = new Label();
        titulo.getStyleClass().add("titulo-tarea");
        titulo.setMaxWidth(Double.MAX_VALUE);
        titulo.setWrapText(true);
        HBox.setHgrow(titulo, Priority.ALWAYS);

        dot = new Region();
        dot.getStyleClass().add("prioridad-dot");

        estadoLabel = new Label();
        estadoLabel.getStyleClass().add("prioridad-label");

        HBox prioRow = new HBox(6, dot, estadoLabel);
        prioRow.setAlignment(Pos.CENTER_LEFT);

        ownerLabel = new Label("");
        bg = new Circle(14);


        StackPane avatarPane = buildAvatarPane();

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox bottomRow = new HBox(prioRow, spacer, avatarPane);
        bottomRow.setAlignment(Pos.CENTER);

        card = new VBox(8, titulo, bottomRow);
        card.getStyleClass().add("task-card");
        card.setMaxWidth(Double.MAX_VALUE);
        card.setPrefWidth(Region.USE_COMPUTED_SIZE);

        setMaxWidth(Double.MAX_VALUE);

        this.setOnMousePressed(e -> onDragStart(e));
        this.setOnMouseDragged(e -> onDragMove(e));
        this.setOnMouseReleased(e -> onDragEnd(e));
    }

    /**
     * Devuelve una lista ordenada por prioridad ascendente a partir de una lista observable de tareas.
     *
     * @param source la lista observable de {@link Tarea} a ordenar
     * @return una {@link SortedList} ordenada por prioridad
     */
    public static SortedList<Tarea> sorted(ObservableList<Tarea> source) {
        return new SortedList<>(source,
                Comparator.comparingInt(t ->
                        (t.getPrioridad())
                )
        );
    }

    @Override
    protected void updateItem(Tarea tarea, boolean empty) {
        super.updateItem(tarea, empty);

        if (empty || tarea == null) {
            setGraphic(null);
            setText(null);
            setStyle("-fx-background-color: transparent;");
            return;
        }

        titulo.setText(tarea.getNombre());

        dot.setStyle("-fx-background-color: " + tarea.getEstado().getColor() + ";");
        estadoLabel.setText(tarea.getEstado().getNombre());

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

    /**
     * Inicia el arrastre de la tarjeta, genera el fantasma visual y lo añade al contenedor raíz.
     *
     * @param e el evento de ratón que origina el arrastre
     */
    private void onDragStart(MouseEvent e) {
        if (getItem() == null) return;
        if (e.getButton() == MouseButton.SECONDARY) return;

        Bounds cardBounds = card.localToScene(card.getBoundsInLocal());
        this.dragOffsetX = e.getSceneX() - cardBounds.getMinX();
        this.dragOffsetY = e.getSceneY() - cardBounds.getMinY();

        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        WritableImage snapshot = card.snapshot(params, null);

        ghost = new ImageView(snapshot);
        ghost.setManaged(false);
        ghost.setMouseTransparent(true);
        ghost.setOpacity(0.85);

        tintInput = new ColorInput(0, 0, snapshot.getWidth(), snapshot.getHeight(), Color.TRANSPARENT);
        Blend blend = new Blend(BlendMode.SRC_ATOP);
        blend.setTopInput(tintInput);
        ghost.setEffect(blend);

        Point2D rootPos = root.sceneToLocal(cardBounds.getMinX(), cardBounds.getMinY());
        ghost.setLayoutX(rootPos.getX());
        ghost.setLayoutY(rootPos.getY());

        root.getChildren().add(ghost);

        card.setOpacity(0.3);
    }

    /**
     * Actualiza la posición del fantasma durante el arrastre y resalta la celda sobre la que se sitúa.
     *
     * @param e el evento de ratón durante el movimiento
     */
    private void onDragMove(MouseEvent e) {
        if (ghost == null) return;
        if (e.getButton() == MouseButton.SECONDARY) return;

        if (lastHoveredCell != null) {
            lastHoveredCell.getStyleClass().remove("ghostChosen");
            lastHoveredCell = null;
        }

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

            if (tareaFocused != null && tareaFocused.getItem() != null) {
                tareaFocused.getStyleClass().add("ghostChosen");
                lastHoveredCell = tareaFocused;
            }
        }
        else {
            tintInput.setPaint(Color.TRANSPARENT);
        }
    }

    /**
     * Finaliza el arrastre, mueve la tarea a la columna destino y elimina el fantasma visual.
     *
     * @param e el evento de ratón al soltar
     */
    private void onDragEnd(MouseEvent e) {
        if (ghost == null) return;
        if (getItem() == null) return;
        if (e.getButton() == MouseButton.SECONDARY) return;

        ColumnaKanban colChosen = getColumna(e.getScreenX(), e.getScreenY());
        if (colChosen != null) {
            ListCell<Tarea> cellChosen = getHoveredListCell(colChosen, e.getScreenX(), e.getScreenY());

            if (cellChosen != null) {
                cellChosen.getStyleClass().remove("ghostChosen");
            }

            Tarea tareaFocused = getHoveredTarea(colChosen, e.getScreenX(), e.getScreenY());
            moverTarea(colChosen, tareaFocused);
        }

        root.getChildren().remove(ghost);

        card.setOpacity(1);
    }

    /**
     * Devuelve la {@link ColumnaKanban} sobre la que se encuentra el cursor en pantalla.
     *
     * @param screenX coordenada X del cursor en pantalla
     * @param screenY coordenada Y del cursor en pantalla
     * @return la columna bajo el cursor, o {@code null} si no hay ninguna
     */
    private ColumnaKanban getColumna(double screenX, double screenY) {
        return columnMap.values().stream()
                .filter(col -> col.lvTareas().localToScreen(col.lvTareas().getBoundsInLocal()).contains(screenX, screenY))
                .findFirst()
                .orElse(null);
    }

    /**
     * Devuelve la celda de lista sobre la que se encuentra el cursor dentro de una columna.
     *
     * @param colHover la columna en la que buscar
     * @param screenX  coordenada X del cursor en pantalla
     * @param screenY  coordenada Y del cursor en pantalla
     * @return la {@link ListCell} bajo el cursor, o {@code null} si no hay ninguna
     */
    private ListCell<Tarea> getHoveredListCell(ColumnaKanban colHover, double screenX, double screenY) {
        return colHover.lvTareas().lookupAll(".list-cell").stream()
                .filter(node -> node.localToScreen(node.getBoundsInLocal()).contains(screenX, screenY))
                .findFirst()
                .map(node -> (ListCell<Tarea>) node)
                .orElse(null);
    }

    /**
     * Devuelve la {@link Tarea} de la celda sobre la que se encuentra el cursor dentro de una columna.
     *
     * @param colHover la columna en la que buscar
     * @param screenX  coordenada X del cursor en pantalla
     * @param screenY  coordenada Y del cursor en pantalla
     * @return la tarea bajo el cursor, o {@code null} si no hay ninguna
     */
    private Tarea getHoveredTarea(ColumnaKanban colHover, double screenX, double screenY) {
        return colHover.lvTareas().lookupAll(".list-cell").stream()
                .filter(node -> node.localToScreen(node.getBoundsInLocal()).contains(screenX, screenY))
                .findFirst()
                .map(node -> (ListCell<Tarea>) node)
                .map(ListCell::getItem)
                .orElse(null);
    }

    /**
     * Mueve la tarea actual a la columna destino, actualizando su estado y prioridad.
     * Si se indica una tarea de referencia, la tarea insertada adopta su prioridad;
     * en caso contrario se coloca al final de la columna destino.
     *
     * @param destino     la columna a la que se mueve la tarea
     * @param tareaChosen la tarea de referencia para determinar la posición de inserción,
     *                    o {@code null} para insertar al final
     */
    private void moverTarea(ColumnaKanban destino, Tarea tareaChosen) {
        if (destino != null) {
            Tarea t = getItem();
            ColumnaKanban partida = columnMap.get(t.getEstado());

            if (tareaChosen != null) {
                t.setPrioridad(tareaChosen.getPrioridad());
            } else {
                try {
                    t.setPrioridad(TareasBDD.getMayorPrioridad(t.getIdProyecto())+1);
                } catch (Exception ex) {
                    AppErrorHandler.manejar(ex, "getMayorPrioridad");
                }
            }

            t.setEstado(destino.estado());
            partida.olTareas().remove(t);
            
            int indiceInsercion = Math.max(Math.min(t.getPrioridad(), destino.olTareas().size()), 0);
            destino.olTareas().add(indiceInsercion, t);

            actualizarLista(partida);
            actualizarLista(destino);
        }
    }

    /**
     * Reasigna las prioridades de todas las tareas de una columna según su posición actual
     * y persiste los cambios en la base de datos.
     *
     * @param columnaEstadoKanban la columna cuyas tareas se van a actualizar
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
