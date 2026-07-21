package com.smartlibrary.model;
import com.smartlibrary.decorator.BasicMembership;
public class Student extends User {
    public Student(String name) { super(name, new BasicMembership()); }
    @Override public Role getRole()      { return Role.ALUMNO; }
    @Override public String getRoleLabel(){ return "Estudiante"; }
}
