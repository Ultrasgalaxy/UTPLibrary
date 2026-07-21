package com.smartlibrary.auth;

import com.smartlibrary.model.User;
import com.smartlibrary.repository.UsuarioRepository;
import java.util.Optional;

public class AuthService {
    private final UsuarioRepository repo = new UsuarioRepository();

    public Optional<User> authenticate(String username, String password) {
        return repo.findByCredentials(username, password);
    }
}
