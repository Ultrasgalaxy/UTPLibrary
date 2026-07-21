package com.smartlibrary.state;
import com.smartlibrary.model.Book;
public class AvailableState implements BookState {
    @Override public void borrow(Book book) {
        book.setState(new BorrowedState());
        System.out.println("Libro prestado.");
    }
    @Override public void returnBook(Book book) {
        System.out.println("El libro ya esta disponible.");
    }
    @Override public void reserve(Book book) {
        book.setState(new ReservedState());
        System.out.println("Libro reservado.");
    }
}
