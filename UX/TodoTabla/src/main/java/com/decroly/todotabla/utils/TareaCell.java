package com.decroly.todotabla.utils;

import com.decroly.todotabla.model.Asignacion;
import com.decroly.todotabla.model.Estado;
import com.decroly.todotabla.model.Tarea;
import com.decroly.todotabla.model.sql.AsignacionesBDD;
import com.decroly.todotabla.utils.constants.ColoresPrioridad;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorInput;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class TareaCell extends ListCell<Tarea> {

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

    //Constructor
    public TareaCell(BorderPane root, Map<Estado, ColumnaKanban> columnMap) {
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

    private void onDragMove(MouseEvent e) {
        // Si el Fantasma no es Null
        if (ghost == null) return;

        // Mover el fantasma
        Point2D rootPos = root.sceneToLocal(
            e.getSceneX() - dragOffsetX,
            e.getSceneY() - dragOffsetY
        );

        ghost.setLayoutX(rootPos.getX());
        ghost.setLayoutY(rootPos.getY());

        double sceneHeight = root.getScene().getHeight();
        int band = (int) Math.min(8, (e.getSceneY() / sceneHeight) * 9);//Formula a ajustar para colores
        //tintInput.setPaint(Color.web(ColoresPrioridad.getColores(band)).deriveColor(0, 1, 1, 0.4));

    }

    private void onDragEnd(MouseEvent e) {
        // realizar los updates en base al resultado

        // Adios fantasma
        root.getChildren().remove(ghost);

        // Carta vuelta a ser full
        card.setOpacity(1);
    }

    /*private ListView<Tarea> getTargetListView(double screenX, double screenY) {
        return columnMap.keySet().stream()
                .filter(lv -> lv.localToScreen(lv.getBoundsInLocal()).contains(screenX, screenY))
                .findFirst()
                .orElse(null);
    }*/
}
