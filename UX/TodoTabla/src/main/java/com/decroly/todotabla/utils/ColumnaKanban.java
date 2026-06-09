package com.decroly.todotabla.utils;

import com.decroly.todotabla.model.Estado;
import com.decroly.todotabla.model.Tarea;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.ListView;

/**
 * Estructura de datos inmutable (Record) que acopla un estado lógico del flujo Kanban con sus respectivos
 * componentes de control y colecciones visuales de JavaFX.
 * <p>
 * Facilita el transporte unificado de los contenedores de la UI por columna y simplifica los cálculos de
 * ordenación, filtrado interactivo dinámico y el mapeo rápido en el ciclo de vida del Drag and Drop.
 * </p>
 *
 * @param estado   Instancia del dominio {@link Estado} que define y da nombre a la columna (ej: "To Do", "In Progress").
 * @param lvTareas Componente de control visual nativo {@link ListView} encargado de renderizar físicamente las tarjetas en la interfaz.
 * @param olTareas Lista almacén observable subyacente que sincroniza las tareas activas asociadas al estado de la columna.
 * @param flTareas Envoltura condicional {@link FilteredList} que permite realizar búsquedas en tiempo real sobre el contenedor sin alterar la colección raíz.
 * * @author Decroly
 * @version 1.0
 */public record ColumnaKanban(Estado estado, ListView<Tarea> lvTareas, ObservableList<Tarea> olTareas, FilteredList<Tarea> flTareas) {}
