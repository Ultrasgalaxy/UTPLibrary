package com.smartlibrary.facade;

import com.smartlibrary.model.*;
import com.smartlibrary.repository.*;
import com.smartlibrary.util.LoanDays;

import java.time.LocalDate;
import java.util.List;

public class LibraryFacade {

    private final LibroRepository    libroRepo    = new LibroRepository();
    private final UsuarioRepository  usuarioRepo  = new UsuarioRepository();
    private final PrestamoRepository prestamoRepo = new PrestamoRepository();
    private final ReservaRepository  reservaRepo  = new ReservaRepository();

    // ── LIBROS ────────────────────────────────────────────────────────────────

    public String addBook(Book book) {
        if (libroRepo.titleAuthorExists(book.getTitle(), book.getAuthor()))
            return "El libro ya existe en el catalogo.";
        libroRepo.save(book);
        return "Libro agregado: " + book.getTitle();
    }

    public List<Book> getAllBooks() {
        return libroRepo.findAll();
    }

    /** Elimina un libro. Se bloquea si esta actualmente prestado o reservado,
     *  para no perder la trazabilidad de un prestamo en curso. */
    public String deleteBook(Book book) {
        boolean disponible = book.getState().getClass().getSimpleName().equals("AvailableState");
        if (!disponible)
            return "ERROR: No se puede eliminar '" + book.getTitle() +
                   "' porque tiene un prestamo o reserva activa. Debe devolverse o liberarse primero.";
        boolean ok = libroRepo.delete(book.getId());
        return ok ? "Libro eliminado: " + book.getTitle()
                  : "ERROR: No se pudo eliminar el libro (revise que no tenga historial asociado).";
    }

    // ── USUARIOS ──────────────────────────────────────────────────────────────

    public String addUser(User user, String username, String password) {
        if (usuarioRepo.usernameExists(username))
            return "El usuario '" + username + "' ya existe.";
        usuarioRepo.save(user, username, password);
        return "Usuario registrado: " + user.getName();
    }

    public List<User> getAllUsers() {
        return usuarioRepo.findAll();
    }

    // ── PRESTAMOS ─────────────────────────────────────────────────────────────

    public String borrowBook(User user, Book book,
                             LocalDate pickupDate, String pickupSlot) {

        if (LoanDays.forUser(user) == 0)
            return "Los administradores no realizan prestamos.";

        if (prestamoRepo.tieneVencidos(user.getId()))
            return "ERROR: Tienes prestamo(s) vencido(s). Debes devolver antes de solicitar nuevos.";

        int activos = prestamoRepo.countActivosByUsuario(user.getId());
        if (activos >= LoanDays.maxBooksForRole(user))
            return "ERROR: Limite de prestamos alcanzado (" +
                   LoanDays.maxBooksForRole(user) + " libros simultaneos).";

        Loan loan = new Loan(user, book, pickupDate, pickupSlot);
        prestamoRepo.save(loan);
        book.borrow();

        return "Prestamo registrado correctamente.\n" +
               "Recojo: " + loan.getPickupFormatted() + "\n" +
               "Devolucion limite: " + loan.getDueDateFormatted();
    }

    public String returnBook(User user, Book book) {
        List<Loan> activos = prestamoRepo.findByUsuario(user.getId());
        Loan loan = activos.stream()
            .filter(l -> l.getBook().getId() == book.getId())
            .findFirst().orElse(null);

        if (loan == null)
            return "ERROR: " + user.getName() +
                   " no tiene prestado '" + book.getTitle() + "'.";

        prestamoRepo.marcarDevuelto(loan.getId(), book.getId());
        book.returnBook();
        user.decrementLoans();
        return user.getName() + " devolvio: " + book.getTitle();
    }

    public String reserveBook(User user, Book book,
                              LocalDate pickupDate, String pickupSlot) {

        if (LoanDays.forUser(user) == 0)
            return "Los administradores no realizan reservas.";

        if (reservaRepo.libroTieneReservaActiva(book.getId()))
            return "ERROR: '" + book.getTitle() + "' ya tiene una reserva activa.";

        Reservation res = new Reservation(user, book, pickupDate, pickupSlot);
        reservaRepo.save(res);
        book.reserve();

        return "Reserva registrada correctamente.\n" +
               "Recojo: " + res.getPickupFormatted();
    }

    /** Permite que el propio alumno/docente cancele su solicitud de reserva,
     *  liberando el libro para que vuelva a estar disponible (y, por tanto,
     *  eliminable por un administrador si asi lo requiere). */
    public String cancelReservation(User user, Book book) {
        var reserva = reservaRepo.findActivaByUsuarioYLibro(user.getId(), book.getId());
        if (reserva.isEmpty())
            return "ERROR: " + user.getName() + " no tiene una reserva activa para '" + book.getTitle() + "'.";

        boolean ok = reservaRepo.cancelar(reserva.get().getId());
        if (!ok)
            return "ERROR: No se pudo cancelar la reserva.";

        libroRepo.updateEstado(book.getId(), "Disponible");
        book.returnBook(); // ReservedState -> AvailableState
        return "Reserva cancelada: '" + book.getTitle() + "' vuelve a estar disponible.";
    }

    // ── CONSULTAS ─────────────────────────────────────────────────────────────

    public List<Loan>        getActiveLoans()        { return prestamoRepo.findActivos(); }
    public List<Reservation> getActiveReservations() { return reservaRepo.findActivas(); }

    /** Reservas activas de un unico usuario (para el panel "Mis Reservas"). */
    public List<Reservation> getReservationsForUser(User user) {
        return reservaRepo.findActivasPorUsuario(user.getId());
    }
    public List<Loan>        getOverdueLoans()       { return prestamoRepo.findVencidos(); }

    public boolean hasOverdueLoans(User user) {
        return prestamoRepo.tieneVencidos(user.getId());
    }

    // ── ESTADISTICAS (Panel Admin) ────────────────────────────────────────────
    public int countBooks()     { return libroRepo.findAll().size(); }
    public int countUsers()     { return usuarioRepo.findAll().size(); }
    public int countActive()    { return prestamoRepo.findActivos().size(); }
    public int countOverdue()   { return prestamoRepo.findVencidos().size(); }
    public int countAvailable() {
        return (int) libroRepo.findAll().stream()
            .filter(b -> b.getState().getClass().getSimpleName().equals("AvailableState"))
            .count();
    }
    public int countBorrowed() {
        return (int) libroRepo.findAll().stream()
            .filter(b -> b.getState().getClass().getSimpleName().equals("BorrowedState"))
            .count();
    }
    public int countReserved() {
        return (int) libroRepo.findAll().stream()
            .filter(b -> b.getState().getClass().getSimpleName().equals("ReservedState"))
            .count();
    }
}
