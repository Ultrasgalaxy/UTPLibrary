package com.smartlibrary.model;

import com.smartlibrary.interfaces.Borrowable;
import com.smartlibrary.observer.NotificationService;
import com.smartlibrary.observer.Observer;
import com.smartlibrary.state.AvailableState;
import com.smartlibrary.state.BookState;

public class Book implements Borrowable {
    private int id;
    private String title;
    private String author;
    private BookState state;
    private NotificationService notificationService;

    public Book(String title, String author) {
        this.title = title;
        this.author = author;
        this.state = new AvailableState();
        this.notificationService = new NotificationService();
    }

    public int getId()                   { return id; }
    public void setId(int id)            { this.id = id; }
    public void addReservationObserver(Observer o) { notificationService.addObserver(o); }
    public void setState(BookState s)    { this.state = s; }
    public BookState getState()          { return state; }
    public String getTitle()             { return title; }
    public String getAuthor()            { return author; }
    public NotificationService getNotificationService() { return notificationService; }

    @Override public void borrow()       { state.borrow(this); }
    @Override public void returnBook()   { state.returnBook(this); }
    @Override public void reserve()      { state.reserve(this); }
    @Override public String toString()   { return title + " — " + author; }
}
