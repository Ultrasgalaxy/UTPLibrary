package com.smartlibrary.state;
import com.smartlibrary.model.Book;
public interface BookState {
    void borrow(Book book);
    void returnBook(Book book);
    void reserve(Book book);
}
