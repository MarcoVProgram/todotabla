<<<<<<< HEAD
DELIMITER $$
=======
USE todotabla;

-- Human write trigger

DELIMITER $$
CREATE TRIGGER guardarHistorial AFTER INSERT ON tarea FOR EACH ROW 
BEGIN 
	INSERT INTO historial_tareas SELECT NEW.estado, NEW.id, NOW();
END $$

DELIMITER $$
>>>>>>> origin/mayo
CREATE TRIGGER guardarHistorial AFTER UPDATE ON tarea FOR EACH ROW 
BEGIN 
	INSERT INTO historial_tareas(estado, tarea_id, fecha_cambio) VALUE (NEW.estado, NEW.id, NOW());
END $$
DELIMITER ;

UPDATE tarea SET estado='InProgress' WHERE id=1;
SELECT t.nombre, t.estado AS 'estado actual', h.* FROM tarea t
JOIN historial_tareas h ON t.id = h.tarea_id
WHERE t.id = 1
ORDER BY h.fecha_cambio;