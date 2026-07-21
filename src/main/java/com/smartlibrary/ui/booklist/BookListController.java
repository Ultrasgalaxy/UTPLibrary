package com.smartlibrary.ui.booklist;

import com.smartlibrary.model.Book;
import com.smartlibrary.model.Role;
import com.smartlibrary.model.User;
import com.smartlibrary.ui.main.AppContext;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class BookListController {
    @FXML private TableView<Book>         tableBooks;
    @FXML private TableColumn<Book,String> colTitle;
    @FXML private TableColumn<Book,String> colAuthor;
    @FXML private TableColumn<Book,String> colStatus;
    @FXML private TextField fieldSearch;
    @FXML private Label lblCount;
    @FXML private Button btnDelete;
    @FXML private Label lblDeleteStatus;

    @FXML public void initialize() {
        colTitle.setCellValueFactory(c  -> new SimpleStringProperty(c.getValue().getTitle()));
        colAuthor.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAuthor()));
        colStatus.setCellValueFactory(c -> new SimpleStringProperty(
            c.getValue().getState().getClass().getSimpleName().replace("State", "")));
        loadData(AppContext.getFacade().getAllBooks());

        // Solo el administrador puede eliminar libros.
        User current = AppContext.getCurrentUser();
        boolean isAdmin = current != null && current.getRole() == Role.ADMIN;
        btnDelete.setVisible(isAdmin);
        btnDelete.setManaged(isAdmin);
        btnDelete.setDisable(true);
        tableBooks.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSel, newSel) -> btnDelete.setDisable(newSel == null));
    }

    @FXML private void handleRefresh() {
        loadData(AppContext.getFacade().getAllBooks());
        fieldSearch.clear();
        lblDeleteStatus.setText("");
    }

    @FXML private void handleSearch() {
        String q = fieldSearch.getText().toLowerCase();
        List<Book> filtered = AppContext.getFacade().getAllBooks().stream()
            .filter(b -> b.getTitle().toLowerCase().contains(q) ||
                         b.getAuthor().toLowerCase().contains(q))
            .collect(Collectors.toList());
        loadData(filtered);
    }

    @FXML private void handleDelete() {
        Book selected = tableBooks.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
            "¿Eliminar \"" + selected.getTitle() + "\" del catalogo?\n\n" +
            "Esto tambien borrara el historial de prestamos/reservas ya cerrados de este libro. " +
            "Esta accion no se puede deshacer.",
            ButtonType.CANCEL, ButtonType.OK);
        confirm.setHeaderText("Eliminar libro");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) return;

        String msg = AppContext.getFacade().deleteBook(selected);
        lblDeleteStatus.setText(msg);
        loadData(AppContext.getFacade().getAllBooks());
    }

    private void loadData(List<Book> books) {
        tableBooks.setItems(FXCollections.observableArrayList(books));
        lblCount.setText("Total: " + books.size() + " libro(s)");
    }
}
