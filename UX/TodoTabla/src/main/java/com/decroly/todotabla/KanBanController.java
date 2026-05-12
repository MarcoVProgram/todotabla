package com.decroly.todotabla;

import com.decroly.todotabla.model.Tarea;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

import java.util.ArrayList;
import java.util.List;

public class KanBanController {


























    //LISTAS
    @FXML
    public ListView<Tarea> listViewBacklog;
    List<Tarea> tareasBacklog = new ArrayList<>();
    ObservableList<Tarea> obstareasBacklog = FXCollections.observableList(tareasBacklog);
    @FXML
    public ListView<Tarea> listViewProgress;
    List<Tarea> tareasInProgress = new ArrayList<>();
    ObservableList<Tarea> obstareasInProgress = FXCollections.observableList(tareasInProgress);
    @FXML
    public ListView<Tarea> listViewReview;
    List<Tarea> tareasInReview = new ArrayList<>();
    ObservableList<Tarea> obstareasInReview = FXCollections.observableList(tareasInReview);
    @FXML
    public ListView<Tarea> listViewDone;
    List<Tarea> tareasDone = new ArrayList<>();
    ObservableList<Tarea> obstareasDone = FXCollections.observableList(tareasDone);

}
