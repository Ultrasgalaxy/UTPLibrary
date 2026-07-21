package com.smartlibrary.ui.login;

import com.smartlibrary.model.User;
import com.smartlibrary.ui.main.AppContext;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.Optional;

public class LoginController {

    @FXML private TextField     fieldUsername;
    @FXML private PasswordField fieldPassword;
    @FXML private Label         lblError;

    @FXML
    public void initialize() {
        fieldPassword.setOnAction(e -> handleLogin());
    }

    @FXML
    private void handleLogin() {
        String username = fieldUsername.getText() == null ? "" : fieldUsername.getText().trim();
        String password = fieldPassword.getText();

        Optional<User> result = AppContext.getAuthService().authenticate(username, password);
        if (result.isEmpty()) {
            lblError.setText("Usuario o contrasena incorrectos.");
            fieldPassword.clear();
            return;
        }

        AppContext.setCurrentUser(result.get());
        openMainWindow();
    }

    private void openMainWindow() {
        try {
            Stage stage = (Stage) fieldUsername.getScene().getWindow();

            // Guardar estado actual antes de cambiar la escena
            AppContext.saveStageState(stage);

            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/smartlibrary/ui/main/main.fxml"));
            Parent root = loader.load();

            // Aplicar nueva escena SIN tocar posicion ni tamano
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("UTP Library — " + AppContext.getCurrentUser().getName());
            stage.setMinWidth(1080);
            stage.setMinHeight(680);

            // Restaurar estado: mismo tamano, misma posicion, mismo modo maximizado
            AppContext.restoreStageState(stage);

            // Si la ventana era de login (pequena), expandir a tamano de trabajo
            if (stage.getWidth() < 900) {
                stage.setWidth(1280);
                stage.setHeight(800);
                stage.centerOnScreen();
                AppContext.saveStageState(stage);
            }

        } catch (Exception e) {
            e.printStackTrace();
            lblError.setText("No se pudo abrir la aplicacion.");
        }
    }
}
