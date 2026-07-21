package com.smartlibrary.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Reservation {
    private int id;
    private User user;
    private Book book;
    private LocalDate pickupDate;
    private String    pickupSlot;
    private boolean   active;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public Reservation(User user, Book book, LocalDate pickupDate, String pickupSlot) {
        this.user       = user;
        this.book       = book;
        this.pickupDate = pickupDate;
        this.pickupSlot = pickupSlot;
        this.active     = true;
    }

    public int getId()                   { return id; }
    public void setId(int id)            { this.id = id; }
    public void cancel()                 { this.active = false; }
    public boolean isActive()            { return active; }
    public User getUser()                { return user; }
    public Book getBook()                { return book; }
    public LocalDate getPickupDate()     { return pickupDate; }
    public String getPickupSlot()        { return pickupSlot; }
    public String getPickupFormatted()   { return pickupDate.format(FMT) + " a las " + pickupSlot; }

    @Override public String toString() {
        return "[Reserva] " + user.getName() + " -> " + book.getTitle() +
               " | Recojo: " + getPickupFormatted() +
               (active ? " (activa)" : " (cancelada)");
    }
}
