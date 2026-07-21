package com.smartlibrary.model;

import com.smartlibrary.decorator.Membership;
import com.smartlibrary.observer.Observer;

public abstract class User implements Observer {
    protected int id;
    protected String name;
    protected Membership membership;
    protected int currentLoans;

    public User(String name, Membership membership) {
        this.name = name;
        this.membership = membership;
        this.currentLoans = 0;
    }

    public int getId()                   { return id; }
    public void setId(int id)            { this.id = id; }
    public boolean canBorrow()           { return currentLoans < membership.getMaxBooks(); }
    public void incrementLoans()         { currentLoans++; }
    public void decrementLoans()         { if (currentLoans > 0) currentLoans--; }
    public String getName()              { return name; }
    public Membership getMembership()    { return membership; }
    public int getCurrentLoans()         { return currentLoans; }
    public abstract Role getRole();
    public abstract String getRoleLabel();

    @Override
    public void update(String message) {
        System.out.println("[Notificacion] " + name + ": " + message);
    }

    @Override public String toString() { return name; }
}
