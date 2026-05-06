-- Human write trigger

DELIMITER $$
CREATE TRIGGER guardarHistorial AFTER INSERT ON estado FOR EACH ROW 
BEGIN 
	INSERT INTO historial_tareas SELECT NEW.estado, NEW.id, NOW();
END $$