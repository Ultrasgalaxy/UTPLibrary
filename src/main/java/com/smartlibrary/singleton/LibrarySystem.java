package com.smartlibrary.singleton;

import com.smartlibrary.model.Book;
import com.smartlibrary.model.User;
import java.util.ArrayList;
import java.util.List;

/**
 * Singleton que mantiene el estado en memoria durante la sesion.
 * La persistencia real esta en MySQL a traves de los repositorios.
 */
public class LibrarySystem {

    private LibrarySystem() {
        books = new ArrayList<>();
        users = new ArrayList<>();
    }

    private static class Holder {
        private static final LibrarySystem INSTANCE = new LibrarySystem();
    }

    public static LibrarySystem getInstance() { return Holder.INSTANCE; }

    private List<Book> books;
    private List<User> users;

    public boolean addBook(Book book) {
        if (book == null) return false;
        boolean exists = books.stream().anyMatch(b ->
            b.getTitle().equalsIgnoreCase(book.getTitle()) &&
            b.getAuthor().equalsIgnoreCase(book.getAuthor()));
        if (!exists) { books.add(book); return true; }
        return false;
    }

    public boolean addUser(User user) {
        if (user == null) return false;
        boolean exists = users.stream().anyMatch(u ->
            u.getName().equalsIgnoreCase(user.getName()));
        if (!exists) { users.add(user); return true; }
        return false;
    }

    public List<Book> getBooks() { return books; }
    public List<User> getUsers() { return users; }

    public Book findBookByTitle(String title) {
        return books.stream()
            .filter(b -> b.getTitle().equalsIgnoreCase(title))
            .findFirst().orElse(null);
    }
}
