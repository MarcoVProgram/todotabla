DROP DATABASE IF EXISTS todotabla;
CREATE DATABASE IF NOT EXISTS todotabla;
USE todotabla;



DROP TABLE IF EXISTS usuario,
                     proyecto,
                     integrante,
                     tarea,
                     estado,
                     historial_tareas;
                     
                     
CREATE TABLE estado (
	nombre 			VARCHAR(10) 	PRIMARY KEY,
	color 			VARCHAR(7)		NOT NULL
);

CREATE TABLE usuario (
	id				INT				AUTO_INCREMENT		PRIMARY KEY,
    nombre			VARCHAR(30)		NOT NULL,
    apellidos 		VARCHAR(45) 	NOT NULL,
    email			VARCHAR(45) 	UNIQUE
);

CREATE TABLE proyecto (
	id 				INT				AUTO_INCREMENT,
    titulo 			VARCHAR(45)		NOT NULL			UNIQUE,
    fecha_creacion 	DATE			NOT NULL,
    fecha_final 	DATE,
    
    CONSTRAINT Proyecto_PK PRIMARY KEY (id)
);

CREATE TABLE integrante (
	id				INT				AUTO_INCREMENT		PRIMARY KEY,
    rol				VARCHAR(45)		NOT NULL			DEFAULT "miembro",
    fecha_entrada 	DATE			NOT NULL,
    fecha_salida	VARCHAR(45),
    usuario_id		INT,
    proyecto_id		INT,
    
    FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE,
    FOREIGN KEY (proyecto_id) REFERENCES proyecto(id) ON DELETE CASCADE
);

CREATE TABLE tarea (
	id 				INT 			AUTO_INCREMENT		PRIMARY KEY,
    nombre 			VARCHAR(45) 	NOT NULL,
    usuario_ID 		INT,
    prioridad 		INT 			NOT NULL 			DEFAULT 0,
    estado 			VARCHAR(10),
    fecha_asignacion DATE,
    fecha_fin 		DATE,
    proyecto_ID 	INT,
    
    CONSTRAINT proyecto_tarea_FK FOREIGN KEY (proyecto_ID) REFERENCES proyecto(id) ON DELETE CASCADE,
    CONSTRAINT usuario_Tarea_FK FOREIGN KEY (usuario_ID) REFERENCES usuario(id) ON DELETE CASCADE,
    CONSTRAINT estado_Tarea_FK FOREIGN KEY (estado) REFERENCES estado(nombre) ON DELETE CASCADE
);

CREATE TABLE historial_tareas (
	id 				INT 			AUTO_INCREMENT 		PRIMARY KEY,
	estado			VARCHAR(10),
	tarea_id 		INT,
	fecha_cambio 	DATETIME		NOT NULL,
    
	FOREIGN KEY (estado) REFERENCES estado(nombre) ON DELETE CASCADE,
	FOREIGN KEY (tarea_id) REFERENCES tarea(id) ON DELETE CASCADE
);

-- ============================================================
-- INSERTS DE DATOS DE EJEMPLO
-- Proyectos: ERP Corporativo · App Móvil Clientes · Portal Web B2B
-- ============================================================
-- NOTA: "In Progress" tiene 11 caracteres y excede VARCHAR(10).
-- Se usa "InProgress" (10 chars) para cumplir con la restricción.
-- Ajusta el esquema con VARCHAR(11) si prefieres mantener el espacio.
-- ============================================================


-- ============================================================
-- ESTADO (5)
-- ============================================================
INSERT INTO estado (nombre, color) VALUES ('Backlog',    '#6B7280');
INSERT INTO estado (nombre, color) VALUES ('Ready',      '#3B82F6');
INSERT INTO estado (nombre, color) VALUES ('InProgress', '#F59E0B');
INSERT INTO estado (nombre, color) VALUES ('InReview',  '#8B5CF6');
INSERT INTO estado (nombre, color) VALUES ('Done',       '#10B981');


-- ============================================================
-- usuario (8)
-- ============================================================
INSERT INTO usuario (nombre, apellidos, email) VALUES ('Carlos',    'García López',     'carlos.garcia@empresa.com');
INSERT INTO usuario (nombre, apellidos, email) VALUES ('María',     'Fernández Ruiz',   'maria.fernandez@empresa.com');
INSERT INTO usuario (nombre, apellidos, email) VALUES ('Alejandro', 'Martínez Sanz',    'alejandro.martinez@empresa.com');
INSERT INTO usuario (nombre, apellidos, email) VALUES ('Laura',     'Pérez Jiménez',    'laura.perez@empresa.com');
INSERT INTO usuario (nombre, apellidos, email) VALUES ('Pablo',     'Rodríguez Moreno', 'pablo.rodriguez@empresa.com');
INSERT INTO usuario (nombre, apellidos, email) VALUES ('Sara',      'López Navarro',    'sara.lopez@empresa.com');
INSERT INTO usuario (nombre, apellidos, email) VALUES ('Diego',     'Sánchez Torres',   'diego.sanchez@empresa.com');
INSERT INTO usuario (nombre, apellidos, email) VALUES ('Ana',       'Gómez Herrera',    'ana.gomez@empresa.com');


-- ============================================================
-- PROYECTO (3)
-- ============================================================
INSERT INTO proyecto (titulo, fecha_creacion, fecha_final) VALUES ('ERP Corporativo',    '2024-01-15', '2024-12-31');
INSERT INTO proyecto (titulo, fecha_creacion, fecha_final) VALUES ('App Móvil Clientes', '2024-03-01', '2024-09-30');
INSERT INTO proyecto (titulo, fecha_creacion, fecha_final) VALUES ('Portal Web B2B',     '2024-02-01', '2024-11-30');


-- ============================================================
-- INTEGRANTE (12)
-- Proyecto 1 (ERP):    usuarios 1, 2, 3, 4
-- Proyecto 2 (App):    usuarios 1, 5, 6, 7
-- Proyecto 3 (Portal): usuarios 2, 3, 7, 8
-- ============================================================

-- Proyecto 1: ERP Corporativo
INSERT INTO integrante (rol, fecha_entrada, fecha_salida, usuario_id, proyecto_id) VALUES ('Project Manager', '2024-01-15', NULL, 1, 1);
INSERT INTO integrante (rol, fecha_entrada, fecha_salida, usuario_id, proyecto_id) VALUES ('Desarrollador',   '2024-01-15', NULL, 2, 1);
INSERT INTO integrante (rol, fecha_entrada, fecha_salida, usuario_id, proyecto_id) VALUES ('Desarrollador',   '2024-01-15', NULL, 3, 1);
INSERT INTO integrante (rol, fecha_entrada, fecha_salida, usuario_id, proyecto_id) VALUES ('Tester',          '2024-01-20', NULL, 4, 1);

-- Proyecto 2: App Móvil Clientes
INSERT INTO integrante (rol, fecha_entrada, fecha_salida, usuario_id, proyecto_id) VALUES ('Project Manager', '2024-03-01', NULL, 1, 2);
INSERT INTO integrante (rol, fecha_entrada, fecha_salida, usuario_id, proyecto_id) VALUES ('Desarrollador',   '2024-03-01', NULL, 5, 2);
INSERT INTO integrante (rol, fecha_entrada, fecha_salida, usuario_id, proyecto_id) VALUES ('Diseñador',       '2024-03-01', NULL, 6, 2);
INSERT INTO integrante (rol, fecha_entrada, fecha_salida, usuario_id, proyecto_id) VALUES ('Desarrollador',   '2024-03-05', NULL, 7, 2);

-- Proyecto 3: Portal Web B2B
INSERT INTO integrante (rol, fecha_entrada, fecha_salida, usuario_id, proyecto_id) VALUES ('Project Manager', '2024-02-01', NULL, 2, 3);
INSERT INTO integrante (rol, fecha_entrada, fecha_salida, usuario_id, proyecto_id) VALUES ('Desarrollador',   '2024-02-01', NULL, 3, 3);
INSERT INTO integrante (rol, fecha_entrada, fecha_salida, usuario_id, proyecto_id) VALUES ('Desarrollador',   '2024-02-05', NULL, 7, 3);
INSERT INTO integrante (rol, fecha_entrada, fecha_salida, usuario_id, proyecto_id) VALUES ('Diseñador',       '2024-02-05', NULL, 8, 3);


-- ============================================================
-- TAREA (24) — 8 por proyecto
-- prioridad: 0=ninguna · 1=baja · 2=media · 3=alta
-- ============================================================

-- Proyecto 1: ERP Corporativo
INSERT INTO tarea (nombre, usuario_ID, prioridad, estado, fecha_asignacion, fecha_fin, proyecto_ID)
  VALUES ('Análisis de requisitos',      1, 3, 'Done',       '2024-01-20', '2024-02-10', 1);
INSERT INTO tarea (nombre, usuario_ID, prioridad, estado, fecha_asignacion, fecha_fin, proyecto_ID)
  VALUES ('Diseño de base de datos',     2, 3, 'Done',       '2024-02-11', '2024-03-01', 1);
INSERT INTO tarea (nombre, usuario_ID, prioridad, estado, fecha_asignacion, fecha_fin, proyecto_ID)
  VALUES ('Módulo de facturación',       3, 3, 'Done',       '2024-03-05', '2024-04-30', 1);
INSERT INTO tarea (nombre, usuario_ID, prioridad, estado, fecha_asignacion, fecha_fin, proyecto_ID)
  VALUES ('Módulo de inventario',        4, 2, 'InReview',  '2024-04-01',  NULL,        1);
INSERT INTO tarea (nombre, usuario_ID, prioridad, estado, fecha_asignacion, fecha_fin, proyecto_ID)
  VALUES ('Módulo de RRHH',              2, 2, 'InProgress', '2024-04-15',  NULL,        1);
INSERT INTO tarea (nombre, usuario_ID, prioridad, estado, fecha_asignacion, fecha_fin, proyecto_ID)
  VALUES ('Integración con contabilidad',3, 1, 'Ready',      '2024-05-01',  NULL,        1);
INSERT INTO tarea (nombre, usuario_ID, prioridad, estado, fecha_asignacion, fecha_fin, proyecto_ID)
  VALUES ('Panel de administración',     1, 2, 'Backlog',    '2024-05-10',  NULL,        1);
INSERT INTO tarea (nombre, usuario_ID, prioridad, estado, fecha_asignacion, fecha_fin, proyecto_ID)
  VALUES ('Testing y QA global',         4, 1, 'Backlog',     NULL,         NULL,        1);

-- Proyecto 2: App Móvil Clientes
INSERT INTO tarea (nombre, usuario_ID, prioridad, estado, fecha_asignacion, fecha_fin, proyecto_ID)
  VALUES ('Wireframes y prototipo',      5, 3, 'Done',       '2024-03-05', '2024-03-25', 2);
INSERT INTO tarea (nombre, usuario_ID, prioridad, estado, fecha_asignacion, fecha_fin, proyecto_ID)
  VALUES ('Diseño UI/UX',               6, 3, 'Done',       '2024-03-20', '2024-04-15', 2);
INSERT INTO tarea (nombre, usuario_ID, prioridad, estado, fecha_asignacion, fecha_fin, proyecto_ID)
  VALUES ('Autenticación de usuarios',   7, 3, 'Done',       '2024-04-01', '2024-04-30', 2);
INSERT INTO tarea (nombre, usuario_ID, prioridad, estado, fecha_asignacion, fecha_fin, proyecto_ID)
  VALUES ('Catálogo de productos',       5, 2, 'InReview',  '2024-04-20',  NULL,        2);
INSERT INTO tarea (nombre, usuario_ID, prioridad, estado, fecha_asignacion, fecha_fin, proyecto_ID)
  VALUES ('Carrito de compras',          7, 2, 'InProgress', '2024-05-01',  NULL,        2);
INSERT INTO tarea (nombre, usuario_ID, prioridad, estado, fecha_asignacion, fecha_fin, proyecto_ID)
  VALUES ('Pasarela de pagos',           6, 3, 'InProgress', '2024-05-01',  NULL,        2);
INSERT INTO tarea (nombre, usuario_ID, prioridad, estado, fecha_asignacion, fecha_fin, proyecto_ID)
  VALUES ('Notificaciones push',         5, 1, 'Ready',      '2024-05-10',  NULL,        2);
INSERT INTO tarea (nombre, usuario_ID, prioridad, estado, fecha_asignacion, fecha_fin, proyecto_ID)
  VALUES ('Testing en dispositivos',     6, 1, 'Backlog',     NULL,         NULL,        2);

-- Proyecto 3: Portal Web B2B
INSERT INTO tarea (nombre, usuario_ID, prioridad, estado, fecha_asignacion, fecha_fin, proyecto_ID)
  VALUES ('Definición de arquitectura',  2, 3, 'Done',       '2024-02-05', '2024-02-28', 3);
INSERT INTO tarea (nombre, usuario_ID, prioridad, estado, fecha_asignacion, fecha_fin, proyecto_ID)
  VALUES ('Diseño visual y branding',    8, 2, 'Done',       '2024-03-01', '2024-03-31', 3);
INSERT INTO tarea (nombre, usuario_ID, prioridad, estado, fecha_asignacion, fecha_fin, proyecto_ID)
  VALUES ('Sistema de autenticación',    3, 3, 'Done',       '2024-03-15', '2024-04-20', 3);
INSERT INTO tarea (nombre, usuario_ID, prioridad, estado, fecha_asignacion, fecha_fin, proyecto_ID)
  VALUES ('Gestión de pedidos',          7, 3, 'InReview',  '2024-04-10',  NULL,        3);
INSERT INTO tarea (nombre, usuario_ID, prioridad, estado, fecha_asignacion, fecha_fin, proyecto_ID)
  VALUES ('Dashboard de métricas',       8, 2, 'InProgress', '2024-04-25',  NULL,        3);
INSERT INTO tarea (nombre, usuario_ID, prioridad, estado, fecha_asignacion, fecha_fin, proyecto_ID)
  VALUES ('Integración API proveedores', 3, 2, 'InProgress', '2024-05-01',  NULL,        3);
INSERT INTO tarea (nombre, usuario_ID, prioridad, estado, fecha_asignacion, fecha_fin, proyecto_ID)
  VALUES ('Módulo de reportes',          8, 1, 'Ready',      '2024-05-05',  NULL,        3);
INSERT INTO tarea (nombre, usuario_ID, prioridad, estado, fecha_asignacion, fecha_fin, proyecto_ID)
  VALUES ('Optimización SEO',            2, 1, 'Backlog',     NULL,         NULL,        3);


-- ============================================================
-- historial_tareas (48) — 2 entradas por tarea
-- Muestra la transición al estado actual de cada tarea
-- ============================================================

-- Tarea 1 (Done) — Análisis de requisitos
INSERT INTO historial_tareas (estado, tarea_id, fecha_cambio) VALUES ('Backlog',    1, '2024-01-20 09:00:00');
INSERT INTO historial_tareas (estado, tarea_id, fecha_cambio) VALUES ('Done',       1, '2024-02-10 17:00:00');

-- Tarea 2 (Done) — Diseño de base de datos
INSERT INTO historial_tareas (estado, tarea_id, fecha_cambio) VALUES ('Backlog',    2, '2024-02-11 09:00:00');
INSERT INTO historial_tareas (estado, tarea_id, fecha_cambio) VALUES ('Done',       2, '2024-03-01 18:00:00');

-- Tarea 3 (Done) — Módulo de facturación
INSERT INTO historial_tareas (estado, tarea_id, fecha_cambio) VALUES ('Ready',      3, '2024-03-05 08:30:00');
INSERT INTO historial_tareas (estado, tarea_id, fecha_cambio) VALUES ('Done',       3, '2024-04-30 16:00:00');

-- Tarea 4 (In Review) — Módulo de inventario
INSERT INTO historial_tareas (estado, tarea_id, fecha_cambio) VALUES ('InProgress', 4, '2024-04-01 09:00:00');
INSERT INTO historial_tareas (estado, tarea_id, fecha_cambio) VALUES ('InReview',  4, '2024-04-25 11:00:00');

-- Tarea 5 (InProgress) — Módulo de RRHH
INSERT INTO historial_tareas (estado, tarea_id, fecha_cambio) VALUES ('Backlog',    5, '2024-04-15 09:00:00');
INSERT INTO historial_tareas (estado, tarea_id, fecha_cambio) VALUES ('InProgress', 5, '2024-04-20 10:00:00');

-- Tarea 6 (Ready) — Integración con contabilidad
INSERT INTO historial_tareas (estado, tarea_id, fecha_cambio) VALUES ('Backlog',    6, '2024-05-01 09:00:00');
INSERT INTO historial_tareas (estado, tarea_id, fecha_cambio) VALUES ('Ready',      6, '2024-05-03 14:00:00');

-- Tarea 7 (Backlog) — Panel de administración
INSERT INTO historial_tareas (estado, tarea_id, fecha_cambio) VALUES ('Backlog',    7, '2024-05-10 09:00:00');
INSERT INTO historial_tareas (estado, tarea_id, fecha_cambio) VALUES ('Backlog',    7, '2024-05-14 09:00:00');

-- Tarea 8 (Backlog) — Testing y QA global
INSERT INTO historial_tareas (estado, tarea_id, fecha_cambio) VALUES ('Backlog',    8, '2024-05-15 09:00:00');
INSERT INTO historial_tareas (estado, tarea_id, fecha_cambio) VALUES ('Backlog',    8, '2024-05-17 09:00:00');

-- Tarea 9 (Done) — Wireframes y prototipo
INSERT INTO historial_tareas (estado, tarea_id, fecha_cambio) VALUES ('Backlog',    9, '2024-03-05 08:00:00');
INSERT INTO historial_tareas (estado, tarea_id, fecha_cambio) VALUES ('Done',       9, '2024-03-25 17:00:00');

-- Tarea 10 (Done) — Diseño UI/UX
INSERT INTO historial_tareas (estado, tarea_id, fecha_cambio) VALUES ('Ready',      10, '2024-03-20 09:00:00');
INSERT INTO historial_tareas (estado, tarea_id, fecha_cambio) VALUES ('Done',       10, '2024-04-15 16:30:00');

-- Tarea 11 (Done) — Autenticación de usuarios
INSERT INTO historial_tareas (estado, tarea_id, fecha_cambio) VALUES ('InProgress', 11, '2024-04-01 09:00:00');
INSERT INTO historial_tareas (estado, tarea_id, fecha_cambio) VALUES ('Done',       11, '2024-04-30 18:00:00');

-- Tarea 12 (In Review) — Catálogo de productos
INSERT INTO historial_tareas (estado, tarea_id, fecha_cambio) VALUES ('Ready',      12, '2024-04-20 09:00:00');
INSERT INTO historial_tareas (estado, tarea_id, fecha_cambio) VALUES ('InReview',  12, '2024-05-05 14:00:00');

-- Tarea 13 (InProgress) — Carrito de compras
INSERT INTO historial_tareas (estado, tarea_id, fecha_cambio) VALUES ('Ready',      13, '2024-05-01 09:00:00');
INSERT INTO historial_tareas (estado, tarea_id, fecha_cambio) VALUES ('InProgress', 13, '2024-05-06 10:00:00');

-- Tarea 14 (InProgress) — Pasarela de pagos
INSERT INTO historial_tareas (estado, tarea_id, fecha_cambio) VALUES ('Backlog',    14, '2024-05-01 09:00:00');
INSERT INTO historial_tareas (estado, tarea_id, fecha_cambio) VALUES ('InProgress', 14, '2024-05-08 09:00:00');

-- Tarea 15 (Ready) — Notificaciones push
INSERT INTO historial_tareas (estado, tarea_id, fecha_cambio) VALUES ('Backlog',    15, '2024-05-10 09:00:00');
INSERT INTO historial_tareas (estado, tarea_id, fecha_cambio) VALUES ('Ready',      15, '2024-05-12 11:00:00');

-- Tarea 16 (Backlog) — Testing en dispositivos
INSERT INTO historial_tareas (estado, tarea_id, fecha_cambio) VALUES ('Backlog',    16, '2024-05-15 09:00:00');
INSERT INTO historial_tareas (estado, tarea_id, fecha_cambio) VALUES ('Backlog',    16, '2024-05-17 09:00:00');

-- Tarea 17 (Done) — Definición de arquitectura
INSERT INTO historial_tareas (estado, tarea_id, fecha_cambio) VALUES ('Backlog',    17, '2024-02-05 08:00:00');
INSERT INTO historial_tareas (estado, tarea_id, fecha_cambio) VALUES ('Done',       17, '2024-02-28 17:00:00');

-- Tarea 18 (Done) — Diseño visual y branding
INSERT INTO historial_tareas (estado, tarea_id, fecha_cambio) VALUES ('Backlog',    18, '2024-03-01 09:00:00');
INSERT INTO historial_tareas (estado, tarea_id, fecha_cambio) VALUES ('Done',       18, '2024-03-31 16:00:00');

-- Tarea 19 (Done) — Sistema de autenticación
INSERT INTO historial_tareas (estado, tarea_id, fecha_cambio) VALUES ('Ready',      19, '2024-03-15 09:00:00');
INSERT INTO historial_tareas (estado, tarea_id, fecha_cambio) VALUES ('Done',       19, '2024-04-20 17:30:00');

-- Tarea 20 (In Review) — Gestión de pedidos
INSERT INTO historial_tareas (estado, tarea_id, fecha_cambio) VALUES ('InProgress', 20, '2024-04-10 09:00:00');
INSERT INTO historial_tareas (estado, tarea_id, fecha_cambio) VALUES ('InReview',  20, '2024-04-30 14:00:00');

-- Tarea 21 (InProgress) — Dashboard de métricas
INSERT INTO historial_tareas (estado, tarea_id, fecha_cambio) VALUES ('Backlog',    21, '2024-04-25 09:00:00');
INSERT INTO historial_tareas (estado, tarea_id, fecha_cambio) VALUES ('InProgress', 21, '2024-05-02 10:00:00');

-- Tarea 22 (InProgress) — Integración API proveedores
INSERT INTO historial_tareas (estado, tarea_id, fecha_cambio) VALUES ('Ready',      22, '2024-05-01 09:00:00');
INSERT INTO historial_tareas (estado, tarea_id, fecha_cambio) VALUES ('InProgress', 22, '2024-05-07 11:00:00');

-- Tarea 23 (Ready) — Módulo de reportes
INSERT INTO historial_tareas (estado, tarea_id, fecha_cambio) VALUES ('Backlog',    23, '2024-05-05 09:00:00');
INSERT INTO historial_tareas (estado, tarea_id, fecha_cambio) VALUES ('Ready',      23, '2024-05-07 14:00:00');

-- Tarea 24 (Backlog) — Optimización SEO
INSERT INTO historial_tareas (estado, tarea_id, fecha_cambio) VALUES ('Backlog',    24, '2024-05-10 09:00:00');
INSERT INTO historial_tareas (estado, tarea_id, fecha_cambio) VALUES ('InProgress',    24, '2024-05-10 11:00:00');
