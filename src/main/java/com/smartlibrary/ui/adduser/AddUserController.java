package com.smartlibrary.ui.adduser;

import com.smartlibrary.factory.UserFactory;
import com.smartlibrary.model.User;
import com.smartlibrary.ui.main.AppContext;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class AddUserController {
    @FXML private TextField     fieldName;
    @FXML private TextField     fieldUsername;
    @FXML private PasswordField fieldPassword;
    @FXML private ComboBox<String> cbUserType;
    @FXML private Label         lblStatus;
    @FXML private Label         lblMembershipInfo;

    @FXML public void initialize() {
        cbUserType.setItems(FXCollections.observableArrayList(
            "Estudiante", "Profesor", "Bibliotecario"));
        cbUserType.setOnAction(e -> updateInfo());
    }

    private void updateInfo() {
        String t = cbUserType.getValue();
        if (t == null) return;
        lblMembershipInfo.setText(switch (t) {
            case "Estudiante"    -> "Membresia Basica — max 3 libros | Plazo: 7 dias";
            case "Profesor"      -> "Membresia Premium — max 8 libros | Plazo: 14 dias";
            case "Bibliotecario" -> "Acceso administrativo — no realiza prestamos";
            default -> "";
        });
    }

    @FXML private void handleSave() {
        String name     = fieldName.getText().trim();
        String username = fieldUsername.getText().trim();
        String password = fieldPassword.getText();
        String type     = cbUserType.getValue();

        if (name.isEmpty() || username.isEmpty() || password.isEmpty() || type == null) {
            lblStatus.setText("Todos los campos son obligatorios.");
            return;
        }
        try {
            // createUser solo necesita tipo y nombre
            User user = UserFactory.createUser(type, name);
            // username y password se pasan aparte a la Facade para guardar en BD
            lblStatus.setText(AppContext.getFacade().addUser(user, username, password));
            fieldName.clear(); fieldUsername.clear();
            fieldPassword.clear(); cbUserType.setValue(null);
            lblMembershipInfo.setText("");
        } catch (IllegalArgumentException ex) {
            lblStatus.setText("Error: " + ex.getMessage());
        }
    }

    @FXML private void handleCancel() {
        ((Stage) fieldName.getScene().getWindow()).close();
    }
}
