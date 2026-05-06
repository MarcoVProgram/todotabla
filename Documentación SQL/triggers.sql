USE todotabla;

-- Human write trigger

DELIMITER $$
CREATE TRIGGER guardarHistorial AFTER INSERT ON tarea FOR EACH ROW 
BEGIN 
	INSERT INTO historial_tareas SELECT NEW.estado, NEW.id, NOW();
END $$

DELIMITER $$
CREATE TRIGGER guardarHistorial AFTER UPDATE ON tarea FOR EACH ROW 
BEGIN 
	INSERT INTO historial_tareas SELECT NEW.estado, NEW.id, NOW();
END $$