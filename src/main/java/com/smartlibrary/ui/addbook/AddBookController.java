package com.smartlibrary.ui.addbook;

import com.smartlibrary.model.Book;
import com.smartlibrary.ui.main.AppContext;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddBookController {
    @FXML private TextField fieldTitle;
    @FXML private TextField fieldAuthor;
    @FXML private Label     lblStatus;

    @FXML private void handleSave() {
        String title  = fieldTitle.getText().trim();
        String author = fieldAuthor.getText().trim();
        if (title.isEmpty() || author.isEmpty()) {
            lblStatus.setText("Titulo y autor son obligatorios.");
            return;
        }
        String result = AppContext.getFacade().addBook(new Book(title, author));
        lblStatus.setText(result);
        if (result.startsWith("Libro agregado")) { fieldTitle.clear(); fieldAuthor.clear(); }
    }

    @FXML private void handleCancel() {
        ((Stage) fieldTitle.getScene().getWindow()).close();
    }
}
