package com.decroly.todotabla.utils;

import com.decroly.todotabla.model.Estado;
import com.decroly.todotabla.model.Tarea;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.ListView;

/**
 * Representa una columna del tablero Kanban.
 * Agrupa el {@link Estado} asociado, la vista de lista, la lista observable subyacente
 * y la lista filtrada utilizada para la búsqueda en tiempo real.
 *
 * @param estado   el estado que identifica esta columna
 * @param lvTareas la {@link ListView} que renderiza las tareas de la columna
 * @param olTareas la {@link ObservableList} que actúa como fuente de datos
 * @param flTareas la {@link FilteredList} aplicada sobre {@code olTareas} para el filtrado
 */
public record ColumnaKanban(Estado estado, ListView<Tarea> lvTareas, ObservableList<Tarea> olTareas, FilteredList<Tarea> flTareas) {}
