/* CONSULTAS Y VISTAS SQL */
USE todotabla;

/* 1. VER EL CONTADOR DE LAS TAREAS Y TODOS LOS USUARIOS QUE SIGAN EN EL PROYECTO DE CADA PROYECTO */
DROP VIEW IF EXISTS estadisticasProyectos;
CREATE VIEW estadisticasProyectos AS 
SELECT p.titulo, COUNT(DISTINCT t.id) as 'Tareas', COUNT(DISTINCT i.id) AS 'Usuarios' FROM proyecto p 
JOIN tarea t ON p.id = t.proyecto_id
JOIN integrante i ON p.id = i.proyecto_id
WHERE i.fecha_salida IS NULL
GROUP BY p.id
;
SELECT * FROM estadisticasProyectos;

/* 2. VER EN CUANTOS PROYECTOS Y CUANTAS TAREAS TIENE HECHAS CADA USUARIO */
DROP VIEW IF EXISTS estadisticasUsuarios;
CREATE VIEW estadisticasUsuarios AS
SELECT u.nombre, u.apellidos, COUNT(DISTINCT p.id) as 'Proyectos Involucrados', COUNT(DISTINCT t.id) AS 'Tareas Pendientes', COUNT(DISTINCT t2.id) AS 'Tareas Hechas'
FROM usuario u
LEFT JOIN asignacion a ON u.id = a.usuario_id
LEFT JOIN tarea t ON a.tarea_ID = t.id AND t.estado != 'Done'
LEFT JOIN asignacion a2 ON u.id = a2.usuario_id
LEFT JOIN tarea t2 ON t2.id = a2.tarea_id AND t2.estado = 'Done'
LEFT JOIN integrante i ON u.id = i.usuario_id
JOIN proyecto p ON p.id = i.proyecto_id
WHERE i.fecha_salida IS NULL
AND a.fecha_fin IS NULL
GROUP BY u.id
;
SELECT * FROM estadisticasUsuarios;

/* 3. VER CUANTAS TAREAS ESTAN SIN ASIGNAR */
DROP VIEW IF EXISTS tareasSueltas;
CREATE VIEW tareasSueltas AS
SELECT DISTINCT t.id, t.nombre, t.estado
FROM tarea t
LEFT JOIN asignacion a ON a.tarea_id = t.id
WHERE a.fecha_fin IS NOT NULL
ORDER BY t.estado ASC
;
SELECT * FROM tareasSueltas;


/* 4. VER TODAS LAS TAREAS DE LOS USUARIOS */
DROP VIEW IF EXISTS tareasUsuarios;
CREATE VIEW tareasUsuarios AS
SELECT t.id AS 'ID Tarea', t.nombre AS 'Nombre Tarea', t.estado, u.nombre, u.apellidos, u.id AS 'ID Usuario'
FROM tarea t
JOIN asignacion a ON a.tarea_id = t.id
JOIN usuario u ON u.id = a.usuario_id
WHERE a.fecha_fin IS NULL
ORDER BY t.estado ASC
;
SELECT * FROM tareasUsuarios;


/* 5. VER TODAS LAS TAREAS DE TODOS LOS PROYECTOS ACTIVOS */
DROP VIEW IF EXISTS tareasProyectosActivos;
CREATE VIEW tareasProyectosActivos AS
SELECT p.titulo AS 'Proyecto Titulo', t.id AS 'ID Tarea', t.nombre AS 'Nombre Tarea', t.estado
FROM tarea t
JOIN proyecto p ON t.proyecto_id = p.id
WHERE p.fecha_cierre IS NULL
ORDER BY p.titulo ASC
;
SELECT * FROM tareasProyectosActivos;


/* 6. VER TODAS LAS TAREAS DE TODOS LOS PROYECTOS PASADOS */
DROP VIEW IF EXISTS tareasProyectosArchivados;
CREATE VIEW tareasProyectosArchivados AS
SELECT p.titulo AS 'Proyecto Titulo', p.fecha_creacion AS 'Fecha Creacion Proyecto', p.fecha_cierre AS 'Fecha Archivación Proyecto', t.id AS 'ID Tarea', t.nombre AS 'Nombre Tarea', t.estado
FROM tarea t
JOIN proyecto p ON t.proyecto_id = p.id
WHERE p.fecha_cierre IS NOT NULL
ORDER BY p.titulo ASC
;
SELECT * FROM tareasProyectosArchivados;


/* 7. ESTADOS CON CADA TAREA */
DROP VIEW IF EXISTS estadosPorTarea;
CREATE VIEW estadosPorTarea AS
SELECT e.nombre AS 'Estado', COUNT(t.id) AS 'Cantidad'
FROM estado e
JOIN tarea t ON t.estado = e.nombre
GROUP BY e.nombre
;
SELECT * FROM estadosPorTarea;


/* 8. HISTORIAL DE CAMBIOS DE UNA TAREA */
SELECT t.nombre, t.estado AS 'estado actual', h.* FROM tarea t
JOIN historial_tareas h ON t.id = h.tarea_id
WHERE t.id = 1
ORDER BY h.fecha_cambio;


/* 9. VER TAREAS DE ALTA PRIORIDAD SIN COMPLETAR Y SUS ASIGNADOS, ASI COMO SU PROYECTO */
SELECT p.titulo AS 'Proyecto', t.nombre AS 'Tarea', t.estado AS 'Estado Actual', u.nombre AS 'Nombre Responsable', u.apellidos AS 'Apellidos Responsable'
FROM tarea t
JOIN asignacion a ON t.id = a.tarea_id
JOIN usuario u ON u.id = a.usuario_id
JOIN proyecto p ON p.id = t.proyecto_id
WHERE t.prioridad >= 3
AND t.estado != 'Done'
AND a.fecha_fin IS NULL;


/* 10. INTEGRANTES CON SU ROL EN CADA PROYECTO */
DROP VIEW IF EXISTS integrantesYsuRol;
CREATE VIEW integrantesYsuRol AS
SELECT p.titulo AS 'Proyecto', 	CONCAT(u.nombre, ' ', u.apellidos) AS 'Miembro', i.rol, i.fecha_entrada
FROM proyecto p
JOIN integrante i ON i.proyecto_id = p.id
JOIN usuario u ON u.id = i.usuario_id
WHERE i.fecha_salida IS NULL
ORDER BY p.titulo ASC
;
SELECT * FROM integrantesYsuRol;

/* 11. PUNTUACION DE EFICIENCIA */
/* PARA MEDIR COMO DE EFECTIVOS SON LOS MIEMBROS DE UN PROYECTO, VAMOS A REALIZAR LA SIGUIENTE FORMULA: */
/* EFICIENCIA: (TAREAS HECHAS * 10) - (DIAS * 0.5) + (CAMBIOS DE ESTADO * 1.5)*/
SELECT CONCAT(u.nombre, ' ', u.apellidos) AS 'Miembro', 
	((COUNT(DISTINCT t2.id)*10)
    - (AVG(TIMESTAMPDIFF(WEEK, a.fecha_asignacion,COALESCE(a.fecha_fin, p.fecha_cierre)))*0.2)
    + (COUNT(DISTINCT h.id)) * 2)
    AS 'Puntuación'
FROM proyecto p
JOIN integrante i ON i.proyecto_id = p.id
JOIN usuario u ON u.id = i.usuario_id
LEFT JOIN asignacion a ON a.usuario_id = u.id AND a.tarea_id IN (SELECT id FROM tarea WHERE proyecto_id = p.id)
LEFT JOIN asignacion a2 ON a2.usuario_id = u.id AND a2.tarea_id IN (SELECT id FROM tarea WHERE proyecto_id = p.id)
LEFT JOIN tarea t ON t.id = a.tarea_id AND t.proyecto_id = p.id
LEFT JOIN tarea t2 ON t2.id = a2.tarea_id AND t2.proyecto_id = p.id AND t2.estado = 'Done'
LEFT JOIN historial_tareas h ON h.tarea_id = t.id
WHERE p.id = 1
GROUP BY u.id;


SELECT u.nombre, u.apellidos, p.titulo
FROM proyecto p
JOIN integrante i ON i.proyecto_id = p.id
JOIN usuario u ON u.id = i.usuario_id
WHERE p.id = 1;

