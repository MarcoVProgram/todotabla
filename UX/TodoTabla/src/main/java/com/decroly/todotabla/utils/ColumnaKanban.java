package com.decroly.todotabla.utils;

import com.decroly.todotabla.model.Estado;
import com.decroly.todotabla.model.Tarea;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.ListView;


public record ColumnaKanban(Estado estado, ListView<Tarea> lvTareas, ObservableList<Tarea> olTareas, FilteredList<Tarea> flTareas) {}
