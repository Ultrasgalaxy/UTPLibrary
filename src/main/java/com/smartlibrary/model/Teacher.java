package com.smartlibrary.model;
import com.smartlibrary.decorator.BasicMembership;
import com.smartlibrary.decorator.PremiumMembership;
public class Teacher extends User {
    public Teacher(String name) { super(name, new PremiumMembership(new BasicMembership())); }
    @Override public Role getRole()      { return Role.ALUMNO; }
    @Override public String getRoleLabel(){ return "Docente"; }
}
