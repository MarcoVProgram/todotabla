/* CONSULTAS Y VISTAS SQL */
USE todotabla;

/* 1. VER TODAS LAS TAREAS Y TODOS LOS USUARIOS QUE SIGAN EN EL PROYECTO DE CADA PROYECTO */
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
JOIN asignacion a ON u.id = a.usuario_id
JOIN tarea t ON a.tarea_ID = t.id
JOIN asignacion a2 ON u.id = a2.usuario_id
JOIN tarea t2 ON t2.id = a2.tarea_id
JOIN integrante i ON u.id = i.usuario_id
JOIN proyecto p ON p.id = i.proyecto_id
WHERE i.fecha_salida IS NULL
AND a.fecha_fin IS NULL
AND t.estado != 'Done'
AND t2.estado = 'Done'
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
SELECT p.titulo AS 'Proyecto Titulo', t.id AS 'ID Tarea', t.nombre AS 'Nombre Tarea', t.estado
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


/* 8. ESTADOS POR */