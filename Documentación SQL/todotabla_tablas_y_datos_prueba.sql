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
    fecha_cierre 	DATE,
    
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
    prioridad 		INT 			NOT NULL 			DEFAULT 0,
    estado 			VARCHAR(10),
    proyecto_ID 	INT,
    
    CONSTRAINT proyecto_tarea_FK FOREIGN KEY (proyecto_ID) REFERENCES proyecto(id) ON DELETE CASCADE,
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

CREATE TABLE asignacion (
	id 				INT 			AUTO_INCREMENT 		PRIMARY KEY,
    usuario_ID		INT,
    tarea_ID		INT,
	fecha_asignacion DATE			NOT NULL,
    fecha_fin 		DATE,
    
    CONSTRAINT usuario_Asignacion_FK FOREIGN KEY (usuario_ID) REFERENCES usuario(id) ON DELETE CASCADE,
    CONSTRAINT tarea_Asignacion_FK FOREIGN KEY (tarea_ID) REFERENCES tarea(id) ON DELETE CASCADE
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
INSERT INTO proyecto (titulo, fecha_creacion, fecha_cierre) VALUES ('ERP Corporativo',    '2024-01-15', '2024-12-31');
INSERT INTO proyecto (titulo, fecha_creacion, fecha_cierre) VALUES ('App Móvil Clientes', '2024-03-01', '2024-09-30');
INSERT INTO proyecto (titulo, fecha_creacion, fecha_cierre) VALUES ('Portal Web B2B',     '2024-02-01', '2024-11-30');


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

-- ============================================================
-- TAREA (24) — esquema actualizado
-- Eliminadas: miembro_ID, fecha_asignacion, fecha_fin
-- La asignación de miembros se gestiona ahora en tabla asignacion
-- ============================================================

-- Proyecto 1: ERP Corporativo
INSERT INTO tarea (nombre, prioridad, estado, proyecto_ID) VALUES ('Análisis de requisitos',       3, 'Done',       1);
INSERT INTO tarea (nombre, prioridad, estado, proyecto_ID) VALUES ('Diseño de base de datos',      3, 'Done',       1);
INSERT INTO tarea (nombre, prioridad, estado, proyecto_ID) VALUES ('Módulo de facturación',        3, 'Done',       1);
INSERT INTO tarea (nombre, prioridad, estado, proyecto_ID) VALUES ('Módulo de inventario',         2, 'InReview',  1);
INSERT INTO tarea (nombre, prioridad, estado, proyecto_ID) VALUES ('Módulo de RRHH',               2, 'InProgress', 1);
INSERT INTO tarea (nombre, prioridad, estado, proyecto_ID) VALUES ('Integración con contabilidad', 1, 'Ready',      1);
INSERT INTO tarea (nombre, prioridad, estado, proyecto_ID) VALUES ('Panel de administración',      2, 'Backlog',    1);
INSERT INTO tarea (nombre, prioridad, estado, proyecto_ID) VALUES ('Testing y QA global',          1, 'Backlog',    1);

-- Proyecto 2: App Móvil Clientes
INSERT INTO tarea (nombre, prioridad, estado, proyecto_ID) VALUES ('Wireframes y prototipo',    3, 'Done',       2);
INSERT INTO tarea (nombre, prioridad, estado, proyecto_ID) VALUES ('Diseño UI/UX',              3, 'Done',       2);
INSERT INTO tarea (nombre, prioridad, estado, proyecto_ID) VALUES ('Autenticación de usuarios', 3, 'Done',       2);
INSERT INTO tarea (nombre, prioridad, estado, proyecto_ID) VALUES ('Catálogo de productos',     2, 'InReview',  2);
INSERT INTO tarea (nombre, prioridad, estado, proyecto_ID) VALUES ('Carrito de compras',        2, 'InProgress', 2);
INSERT INTO tarea (nombre, prioridad, estado, proyecto_ID) VALUES ('Pasarela de pagos',         3, 'InProgress', 2);
INSERT INTO tarea (nombre, prioridad, estado, proyecto_ID) VALUES ('Notificaciones push',       1, 'Ready',      2);
INSERT INTO tarea (nombre, prioridad, estado, proyecto_ID) VALUES ('Testing en dispositivos',   1, 'Backlog',    2);

-- Proyecto 3: Portal Web B2B
INSERT INTO tarea (nombre, prioridad, estado, proyecto_ID) VALUES ('Definición de arquitectura',  3, 'Done',       3);
INSERT INTO tarea (nombre, prioridad, estado, proyecto_ID) VALUES ('Diseño visual y branding',    2, 'Done',       3);
INSERT INTO tarea (nombre, prioridad, estado, proyecto_ID) VALUES ('Sistema de autenticación',    3, 'Done',       3);
INSERT INTO tarea (nombre, prioridad, estado, proyecto_ID) VALUES ('Gestión de pedidos',          3, 'InReview',  3);
INSERT INTO tarea (nombre, prioridad, estado, proyecto_ID) VALUES ('Dashboard de métricas',       2, 'InProgress', 3);
INSERT INTO tarea (nombre, prioridad, estado, proyecto_ID) VALUES ('Integración API proveedores', 2, 'InProgress', 3);
INSERT INTO tarea (nombre, prioridad, estado, proyecto_ID) VALUES ('Módulo de reportes',          1, 'Ready',      3);
INSERT INTO tarea (nombre, prioridad, estado, proyecto_ID) VALUES ('Optimización SEO',            1, 'Backlog',    3);


-- ============================================================
-- ASIGNACION (28)
-- 24 asignaciones base (una por tarea) +
--  4 extra en tareas colaborativas de alta prioridad
--
-- usuario_ID referencia los mismos IDs que miembro:
--   1-Carlos · 2-María · 3-Alejandro · 4-Laura
--   5-Pablo  · 6-Sara  · 7-Diego    · 8-Ana
-- ============================================================

-- ── Proyecto 1: ERP Corporativo ─────────────────────────────

-- Tarea 1 — Análisis de requisitos (Done)
INSERT INTO asignacion (usuario_ID, tarea_ID, fecha_asignacion, fecha_fin) VALUES (1, 1,  '2024-01-20', '2024-02-10');

-- Tarea 2 — Diseño de base de datos (Done)
INSERT INTO asignacion (usuario_ID, tarea_ID, fecha_asignacion, fecha_fin) VALUES (2, 2,  '2024-02-11', '2024-03-01');

-- Tarea 3 — Módulo de facturación (Done) [colaborativa: Alejandro + Carlos como revisor]
INSERT INTO asignacion (usuario_ID, tarea_ID, fecha_asignacion, fecha_fin) VALUES (3, 3,  '2024-03-05', '2024-04-30');
INSERT INTO asignacion (usuario_ID, tarea_ID, fecha_asignacion, fecha_fin) VALUES (1, 3,  '2024-04-10', '2024-04-30');

-- Tarea 4 — Módulo de inventario (InReview)
INSERT INTO asignacion (usuario_ID, tarea_ID, fecha_asignacion, fecha_fin) VALUES (4, 4,  '2024-04-01',  NULL);

-- Tarea 5 — Módulo de RRHH (InProgress)
INSERT INTO asignacion (usuario_ID, tarea_ID, fecha_asignacion, fecha_fin) VALUES (2, 5,  '2024-04-15',  NULL);

-- Tarea 6 — Integración con contabilidad (Ready)
INSERT INTO asignacion (usuario_ID, tarea_ID, fecha_asignacion, fecha_fin) VALUES (3, 6,  '2024-05-01',  NULL);

-- Tarea 7 — Panel de administración (Backlog)
INSERT INTO asignacion (usuario_ID, tarea_ID, fecha_asignacion, fecha_fin) VALUES (1, 7,  '2024-05-10',  NULL);

-- Tarea 8 — Testing y QA global (Backlog)
INSERT INTO asignacion (usuario_ID, tarea_ID, fecha_asignacion, fecha_fin) VALUES (4, 8,  '2024-05-15',  NULL);

-- ── Proyecto 2: App Móvil Clientes ──────────────────────────

-- Tarea 9 — Wireframes y prototipo (Done)
INSERT INTO asignacion (usuario_ID, tarea_ID, fecha_asignacion, fecha_fin) VALUES (5, 9,  '2024-03-05', '2024-03-25');

-- Tarea 10 — Diseño UI/UX (Done)
INSERT INTO asignacion (usuario_ID, tarea_ID, fecha_asignacion, fecha_fin) VALUES (6, 10, '2024-03-20', '2024-04-15');

-- Tarea 11 — Autenticación de usuarios (Done) [colaborativa: Diego + Pablo como apoyo]
INSERT INTO asignacion (usuario_ID, tarea_ID, fecha_asignacion, fecha_fin) VALUES (7, 11, '2024-04-01', '2024-04-30');
INSERT INTO asignacion (usuario_ID, tarea_ID, fecha_asignacion, fecha_fin) VALUES (5, 11, '2024-04-15', '2024-04-30');

-- Tarea 12 — Catálogo de productos (InReview)
INSERT INTO asignacion (usuario_ID, tarea_ID, fecha_asignacion, fecha_fin) VALUES (5, 12, '2024-04-20',  NULL);

-- Tarea 13 — Carrito de compras (InProgress)
INSERT INTO asignacion (usuario_ID, tarea_ID, fecha_asignacion, fecha_fin) VALUES (7, 13, '2024-05-01',  NULL);

-- Tarea 14 — Pasarela de pagos (InProgress)
INSERT INTO asignacion (usuario_ID, tarea_ID, fecha_asignacion, fecha_fin) VALUES (6, 14, '2024-05-01',  NULL);

-- Tarea 15 — Notificaciones push (Ready)
INSERT INTO asignacion (usuario_ID, tarea_ID, fecha_asignacion, fecha_fin) VALUES (5, 15, '2024-05-10',  NULL);

-- Tarea 16 — Testing en dispositivos (Backlog)
INSERT INTO asignacion (usuario_ID, tarea_ID, fecha_asignacion, fecha_fin) VALUES (6, 16, '2024-05-15',  NULL);

-- ── Proyecto 3: Portal Web B2B ───────────────────────────────

-- Tarea 17 — Definición de arquitectura (Done)
INSERT INTO asignacion (usuario_ID, tarea_ID, fecha_asignacion, fecha_fin) VALUES (2, 17, '2024-02-05', '2024-02-28');

-- Tarea 18 — Diseño visual y branding (Done)
INSERT INTO asignacion (usuario_ID, tarea_ID, fecha_asignacion, fecha_fin) VALUES (8, 18, '2024-03-01', '2024-03-31');

-- Tarea 19 — Sistema de autenticación (Done) [colaborativa: Alejandro + Diego como apoyo]
INSERT INTO asignacion (usuario_ID, tarea_ID, fecha_asignacion, fecha_fin) VALUES (3, 19, '2024-03-15', '2024-04-20');
INSERT INTO asignacion (usuario_ID, tarea_ID, fecha_asignacion, fecha_fin) VALUES (7, 19, '2024-04-01', '2024-04-20');

-- Tarea 20 — Gestión de pedidos (InReview) [colaborativa: Diego + Alejandro como revisor]
INSERT INTO asignacion (usuario_ID, tarea_ID, fecha_asignacion, fecha_fin) VALUES (7, 20, '2024-04-10',  NULL);
INSERT INTO asignacion (usuario_ID, tarea_ID, fecha_asignacion, fecha_fin) VALUES (3, 20, '2024-04-25',  NULL);

-- Tarea 21 — Dashboard de métricas (InProgress)
INSERT INTO asignacion (usuario_ID, tarea_ID, fecha_asignacion, fecha_fin) VALUES (8, 21, '2024-04-25',  NULL);

-- Tarea 22 — Integración API proveedores (InProgress)
INSERT INTO asignacion (usuario_ID, tarea_ID, fecha_asignacion, fecha_fin) VALUES (3, 22, '2024-05-01',  NULL);

-- Tarea 23 — Módulo de reportes (Ready)
INSERT INTO asignacion (usuario_ID, tarea_ID, fecha_asignacion, fecha_fin) VALUES (8, 23, '2024-05-05',  NULL);

-- Tarea 24 — Optimización SEO (Backlog)
INSERT INTO asignacion (usuario_ID, tarea_ID, fecha_asignacion, fecha_fin) VALUES (2, 24, '2024-05-10',  NULL);


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

-- Tarea 4 (InReview) — Módulo de inventario
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

-- Tarea 12 (InReview) — Catálogo de productos
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

-- Tarea 20 (InReview) — Gestión de pedidos
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
INSERT INTO historial_tareas (estado, tarea_id, fecha_cambio) VALUES ('Backlog',    24, '2024-05-13 09:00:00');