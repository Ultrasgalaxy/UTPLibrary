# UTP Library

Sistema de gestión de biblioteca desarrollado en **JavaFX** con persistencia en **MySQL**, inspirado en los servicios reales de la Biblioteca de la Universidad Tecnológica del Perú (catálogo por carrera, préstamos, reservas y sedes).

## Características principales

- **Login con detección automática de rol.** No se elige "alumno" o "administrador" al iniciar sesión: el sistema lo determina según el tipo de cuenta asociado a las credenciales.
- **Panel de Administración** (solo visible para cuentas con rol `ADMIN`): estadísticas del catálogo, libros más solicitados, préstamos vencidos y accesos rápidos de gestión.
- **Autoservicio para alumnos y docentes:** solicitar préstamo, devolver un libro y ver el historial propio — cada cuenta ve únicamente su propia información, nunca la de otras cuentas.
- **Reglas de negocio por tipo de usuario:** los docentes tienen un plazo de préstamo mayor (14 días) que los estudiantes (7 días), manteniendo el mismo límite de libros simultáneos.
- **Horarios de recojo dinámicos:** solo se muestran las franjas horarias del día que todavía no han pasado; si ya no queda ninguna disponible hoy, se salta directamente al día siguiente.
- **Empaquetado como ejecutable independiente:** `.jar` autocontenido (JavaFX + driver de MySQL incluidos), ejecutable con `java -jar` sin necesidad de Maven ni de un IDE.

## Patrones de diseño implementados

| Patrón | Uso en el proyecto |
|---|---|
| **Singleton** | `LibrarySystem` — instancia única de acceso a los datos en memoria |
| **Facade** | `LibraryFacade` — punto de entrada único para préstamos, devoluciones y reservas |
| **Factory Method** | `UserFactory` — creación de `Student`, `Teacher` y `Librarian` |
| **State** | `BookState` (`Available`, `Borrowed`, `Reserved`) — ciclo de vida de un libro |
| **Decorator** | `Membership` (`Basic`, `Premium`) — límites de préstamo según tipo de cuenta |
| **Observer** | `NotificationService` — notificaciones sobre libros reservados |

## Requisitos

- Java 21
- Maven
- MySQL Server 8

## Cómo ejecutar en desarrollo

```bash
mvn clean javafx:run
```

## Cómo generar el ejecutable independiente

```bash
mvn clean package
```

Esto genera `target/UTPLibrary.jar`, que se puede ejecutar en cualquier equipo Windows con Java 21 instalado, sin Maven ni IDE:

```bash
java -jar UTPLibrary.jar
```

> Antes de la primera ejecución, es necesario preparar la base de datos. Ver `preparar_evaluacion.sql` e `INSTRUCCIONES_DOCENTE.md` para el paso a paso completo.

## Cuentas de prueba

| Usuario | Contraseña | Rol |
|---|---|---|
| `admin` | `admin123` | Administrador |
| `hsalazar` | `1234` | Profesor (alumno) |
| `vcuba` | `1234` | Estudiante (alumno) |

## Estructura del proyecto

```
com.smartlibrary
├── model        → Book, User, Role, Student, Teacher, Librarian, Loan, Reservation
├── singleton    → LibrarySystem
├── facade       → LibraryFacade
├── factory      → UserFactory
├── decorator    → BasicMembership, PremiumMembership
├── observer     → Subject, Observer, NotificationService
├── state        → BookState, AvailableState, BorrowedState, ReservedState
├── auth         → AuthService
├── db           → DatabaseConnection
├── repository   → LibroRepository, UsuarioRepository, PrestamoRepository, ReservaRepository
├── util         → LoanDays, LibrarySchedule, DemoDataSeeder
└── ui           → main, login, admin, addbook, adduser, booklist, userlist, loanlist, reservelist
```