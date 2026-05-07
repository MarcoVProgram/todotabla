DELIMITER $$
CREATE TRIGGER guardarHistorial AFTER UPDATE ON tarea FOR EACH ROW 
BEGIN 
	INSERT INTO historial_tareas(estado, tarea_id, fecha_cambio) SELECT NEW.estado, NEW.id, NOW();
END $$
DELIMITER ;