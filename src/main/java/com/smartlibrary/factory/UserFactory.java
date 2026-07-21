package com.smartlibrary.factory;

import com.smartlibrary.model.Librarian;
import com.smartlibrary.model.Student;
import com.smartlibrary.model.Teacher;
import com.smartlibrary.model.User;

public class UserFactory {

    public static User createUser(String type, String name) {
        if (type == null || name == null || name.isBlank())
            throw new IllegalArgumentException("Tipo y nombre no pueden estar vacios.");
        return switch (type.toLowerCase()) {
            case "estudiante", "student"        -> new Student(name);
            case "profesor",   "teacher"        -> new Teacher(name);
            case "bibliotecario", "librarian"   -> new Librarian(name);
            default -> throw new IllegalArgumentException("Tipo desconocido: " + type);
        };
    }
}
