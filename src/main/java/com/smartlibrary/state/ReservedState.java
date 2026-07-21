package com.smartlibrary.state;
import com.smartlibrary.model.Book;
public class ReservedState implements BookState {
    @Override public void borrow(Book book) {
        book.setState(new BorrowedState());
        System.out.println("Libro reservado ahora prestado.");
    }
    @Override public void returnBook(Book book) {
        book.setState(new AvailableState());
        book.getNotificationService().notifyObservers(
            "'" + book.getTitle() + "' esta disponible nuevamente.");
        System.out.println(book.getTitle() + " devuelto desde reserva.");
    }
    @Override public void reserve(Book book) {
        System.out.println("El libro ya tiene una reserva activa.");
    }
}
