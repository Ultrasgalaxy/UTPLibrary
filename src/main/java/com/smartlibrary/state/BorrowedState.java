package com.smartlibrary.state;
import com.smartlibrary.model.Book;
public class BorrowedState implements BookState {
    @Override public void borrow(Book book) {
        System.out.println("El libro ya esta prestado.");
    }
    @Override public void returnBook(Book book) {
        book.setState(new AvailableState());
        book.getNotificationService().notifyObservers(
            "'" + book.getTitle() + "' esta disponible nuevamente.");
        System.out.println(book.getTitle() + " devuelto.");
    }
    @Override public void reserve(Book book) {
        System.out.println("No se puede reservar: el libro esta prestado.");
    }
}
