package com.smartlibrary.util;

import com.smartlibrary.model.Librarian;
import com.smartlibrary.model.Teacher;
import com.smartlibrary.model.User;

/**
 * Plazos de devolucion segun normas UTP Piura.
 * Estudiante: 7 dias  (max 2 libros)
 * Docente:   14 dias  (max 2 libros)
 */
public final class LoanDays {
    private LoanDays() {}

    public static int forUser(User user) {
        if (user instanceof Teacher)   return 14;
        if (user instanceof Librarian) return 0;  // No presta
        return 7; // Student y cualquier otro
    }

    public static int maxBooksForRole(User user) {
        if (user instanceof Teacher)   return 2;
        if (user instanceof Librarian) return 0;
        return 2;
    }
}
