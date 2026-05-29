USE todotabla;

DELIMITER $$
CREATE TRIGGER guardarHistorialCambio AFTER UPDATE ON tarea FOR EACH ROW 
BEGIN 
	INSERT INTO historial_tareas(estado, tarea_id, fecha_cambio) VALUES (NEW.estado, NEW.id, NOW());
END $$
DELIMITER ;

DELIMITER $$
CREATE TRIGGER guardarHistorialInsercion AFTER INSERT ON tarea FOR EACH ROW 
BEGIN 
	INSERT INTO historial_tareas(estado, tarea_id, fecha_cambio) VALUES (NEW.estado, NEW.id, NOW());
END $$
DELIMITER ;


UPDATE tarea SET estado='InProgress' WHERE id=1;
SELECT t.nombre, t.estado AS 'estado actual', h.* FROM tarea t
JOIN historial_tareas h ON t.id = h.tarea_id
WHERE t.id = 1
ORDER BY h.fecha_cambio;