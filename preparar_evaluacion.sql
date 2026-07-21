-- ============================================================================
--  UTP Library - Preparacion de base de datos para evaluacion
-- ============================================================================
--  Ejecutar este script UNA SOLA VEZ en MySQL (Workbench, DBeaver, o consola
--  "mysql -u root -p < preparar_evaluacion.sql") antes de correr el .jar.
--  Requiere MySQL Server 8 instalado y accesible en localhost:3306.
-- ============================================================================

-- 1) Usuario y base de datos dedicados para esta evaluacion
--    (no son las credenciales personales del desarrollador)
CREATE USER IF NOT EXISTS 'utp_eval'@'localhost' IDENTIFIED BY 'EvalUTP2026!';

CREATE DATABASE IF NOT EXISTS utp_library
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

GRANT ALL PRIVILEGES ON utp_library.* TO 'utp_eval'@'localhost';
FLUSH PRIVILEGES;

USE utp_library;

-- 2) Esquema (igual al usado por la aplicacion)
CREATE TABLE IF NOT EXISTS usuarios (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    nombre          VARCHAR(150) NOT NULL,
    username        VARCHAR(50)  NOT NULL UNIQUE,
    password        VARCHAR(100) NOT NULL,
    tipo            ENUM('Estudiante','Profesor','Bibliotecario') NOT NULL DEFAULT 'Estudiante'
);

CREATE TABLE IF NOT EXISTS libros (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    titulo          VARCHAR(200) NOT NULL,
    autor           VARCHAR(150) NOT NULL,
    estado          ENUM('Disponible','Prestado','Reservado') DEFAULT 'Disponible',
    fecha_registro  DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS prestamos (
    id                INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id        INT NOT NULL,
    libro_id          INT NOT NULL,
    fecha_recojo      DATE NOT NULL,
    hora_recojo       VARCHAR(20) NOT NULL,
    fecha_limite      DATE NOT NULL,
    fecha_devolucion  DATE NULL,
    estado            ENUM('Activo','Devuelto') NOT NULL DEFAULT 'Activo',
    CONSTRAINT fk_prestamo_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    CONSTRAINT fk_prestamo_libro   FOREIGN KEY (libro_id)   REFERENCES libros(id)
);

CREATE TABLE IF NOT EXISTS reservas (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id      INT NOT NULL,
    libro_id        INT NOT NULL,
    fecha_recojo    DATE NOT NULL,
    hora_recojo     VARCHAR(20) NOT NULL,
    estado          ENUM('Activa','Cancelada') NOT NULL DEFAULT 'Activa',
    CONSTRAINT fk_reserva_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    CONSTRAINT fk_reserva_libro   FOREIGN KEY (libro_id)   REFERENCES libros(id)
);

-- 3) Datos de demostracion (2 cuentas por rol para probar login y permisos)
INSERT INTO usuarios (nombre, username, password, tipo) VALUES
('Marcela Rios Vega',   'admin',      'admin123', 'Bibliotecario'),
('Hugo Salazar Prado',  'hsalazar',   '1234',     'Profesor'),
('Valeria Cuba Ramos',  'vcuba',      '1234',     'Estudiante');

INSERT INTO libros (titulo, autor, estado) VALUES
('Clean Code: A Handbook of Agile Software Craftsmanship',         'Robert C. Martin',                    'Disponible'),
('Ingenieria de Software',                                         'Ian Sommerville',                     'Disponible'),
('Design Patterns: Elements of Reusable Object-Oriented Software', 'Gamma, Helm, Johnson, Vlissides',      'Disponible'),
('Investigacion de Operaciones',                                   'Hamdy A. Taha',                       'Disponible'),
('Administracion de Operaciones',                                  'Jay Heizer, Barry Render',             'Disponible'),
('Fundamentos de Marketing',                                       'Philip Kotler, Gary Armstrong',        'Disponible'),
('El Sistema Juridico: Introduccion al Derecho',                   'Marcial Rubio Correa',                 'Disponible'),
('Derecho Constitucional Peruano',                                 'Cesar Landa Arroyo',                   'Disponible'),
('Fundamentos del Diseno',                                         'Wucius Wong',                          'Disponible'),
('Arquitectura: Forma, Espacio y Orden',                           'Francis D.K. Ching',                   'Disponible');
