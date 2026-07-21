package com.smartlibrary.model;
import com.smartlibrary.decorator.BasicMembership;
import com.smartlibrary.decorator.PremiumMembership;
public class Librarian extends User {
    public Librarian(String name) { super(name, new PremiumMembership(new PremiumMembership(new BasicMembership()))); }
    @Override public Role getRole()      { return Role.ADMIN; }
    @Override public String getRoleLabel(){ return "Administrador"; }
}
