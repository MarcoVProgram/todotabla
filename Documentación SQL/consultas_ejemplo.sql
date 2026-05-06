USE todotabla;

DROP VIEW IF EXISTS a1;

/* CONSULTAS BBDD */

CREATE VIEW a1 AS 
SELECT p.titulo, t.Tareas, i.Usuarios FROM proyecto p 
INNER JOIN (SELECT t.proyecto_ID, count(t.id) AS Tareas FROM tarea t GROUP BY t.proyecto_ID) t ON p.id = t.proyecto_ID
INNER JOIN (SELECT i.proyecto_id, count(i.id) AS Usuarios FROM integrante i GROUP BY i.proyecto_id) i ON p.id = i.proyecto_id
GROUP BY p.id
;

