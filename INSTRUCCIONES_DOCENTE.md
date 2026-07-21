# UTP Library — Instrucciones para ejecutar el .jar

## Requisitos previos
1. **Java 21** instalado. Verificar con:
   ```
   java -version
   ```
2. **MySQL Server 8** instalado y corriendo en `localhost:3306`.

## Paso 1 — Preparar la base de datos (una sola vez)
Ejecutar el archivo `preparar_evaluacion.sql` incluido en esta entrega. Opciones:

- **MySQL Workbench / DBeaver**: abrir el archivo y ejecutarlo completo (rayo / "Execute Script").
- **Consola**:
  ```
  mysql -u root -p < preparar_evaluacion.sql
  ```
  (pedira la contrasena del usuario `root` de tu propio MySQL, no la de la app)

Esto crea:
- Un usuario de MySQL dedicado para esta evaluacion (`utp_eval` / `EvalUTP2026!`) — no son
  credenciales personales del desarrollador, son solo para poder correr el programa.
- La base de datos `utp_library` con las tablas necesarias.
- Datos de prueba: 3 cuentas y 10 libros.

Si el usuario `utp_eval` ya existiera de un intento previo y no coincide la contrasena,
correr en su lugar:
```sql
ALTER USER 'utp_eval'@'localhost' IDENTIFIED BY 'EvalUTP2026!';
GRANT ALL PRIVILEGES ON utp_library.* TO 'utp_eval'@'localhost';
FLUSH PRIVILEGES;
```

## Paso 2 — Ejecutar la aplicacion
```
java -jar UTPLibrary.jar
```
Debe abrir directamente la pantalla de inicio de sesion.

## Cuentas de prueba
| Usuario     | Contrasena | Rol            |
|-------------|------------|----------------|
| admin       | admin123   | Administrador  |
| hsalazar    | 1234       | Profesor (alumno) |
| vcuba       | 1234       | Estudiante (alumno) |

## Notas
- El ejecutable esta compilado para **Windows** (incluye los componentes nativos
  de JavaFX para ese sistema operativo). No correra en Mac/Linux con este mismo archivo.
- Si aparece "Access denied" o cualquier error de conexion en la consola al iniciar sesion,
  confirmar que el Paso 1 se ejecuto sin errores y que el servicio de MySQL este activo.
