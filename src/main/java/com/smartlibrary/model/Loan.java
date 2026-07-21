package com.smartlibrary.model;

import com.smartlibrary.util.LoanDays;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Loan {
    private int id;
    private User user;
    private Book book;
    private LocalDate pickupDate;
    private String    pickupSlot;
    private LocalDate dueDate;
    private LocalDate returnedDate;
    private boolean   returned;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public Loan(User user, Book book, LocalDate pickupDate, String pickupSlot) {
        this.user       = user;
        this.book       = book;
        this.pickupDate = pickupDate;
        this.pickupSlot = pickupSlot;
        this.dueDate    = pickupDate.plusDays(LoanDays.forUser(user));
        this.returned   = false;
    }

    public int getId()                   { return id; }
    public void setId(int id)            { this.id = id; }
    public void markReturned()           { this.returned = true; this.returnedDate = LocalDate.now(); }
    public boolean isReturned()          { return returned; }
    public boolean isOverdue()           { return !returned && LocalDate.now().isAfter(dueDate); }
    public User getUser()                { return user; }
    public Book getBook()                { return book; }
    public LocalDate getPickupDate()     { return pickupDate; }
    public String getPickupSlot()        { return pickupSlot; }
    public LocalDate getDueDate()        { return dueDate; }
    public LocalDate getReturnedDate()   { return returnedDate; }
    public String getPickupFormatted()   { return pickupDate.format(FMT) + " a las " + pickupSlot; }
    public String getDueDateFormatted()  { return dueDate.format(FMT); }

    @Override public String toString() {
        return "[Prestamo] " + user.getName() + " -> " + book.getTitle() +
               " | Recojo: " + getPickupFormatted() +
               " | Devolucion: " + getDueDateFormatted() +
               (returned ? " (devuelto)" : isOverdue() ? " [VENCIDO]" : "");
    }
}
