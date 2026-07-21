package com.smartlibrary.ui.admin;

import com.smartlibrary.facade.LibraryFacade;
import com.smartlibrary.model.Loan;
import com.smartlibrary.ui.main.AppContext;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AdminPanelController {

    @FXML private Label lblTotalBooks;
    @FXML private Label lblAvailableBooks;
    @FXML private Label lblBorrowedBooks;
    @FXML private Label lblReservedBooks;
    @FXML private Label lblTotalUsers;
    @FXML private Label lblOverdueCount;

    @FXML private TableView<Loan>         tableOverdue;
    @FXML private TableColumn<Loan,String> colOverdueUser;
    @FXML private TableColumn<Loan,String> colOverdueBook;
    @FXML private TableColumn<Loan,String> colOverdueDue;

    @FXML public void initialize() {
        colOverdueUser.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getUser().getName()));
        colOverdueBook.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getBook().getTitle()));
        colOverdueDue.setCellValueFactory(c  -> new SimpleStringProperty(c.getValue().getDueDateFormatted()));
        loadData();
    }

    @FXML private void handleRefresh() { loadData(); }

    private void loadData() {
        LibraryFacade f = AppContext.getFacade();
        lblTotalBooks.setText(String.valueOf(f.countBooks()));
        lblAvailableBooks.setText(String.valueOf(f.countAvailable()));
        lblBorrowedBooks.setText(String.valueOf(f.countBorrowed()));
        lblReservedBooks.setText(String.valueOf(f.countReserved()));
        lblTotalUsers.setText(String.valueOf(f.countUsers()));
        lblOverdueCount.setText(String.valueOf(f.countOverdue()));
        tableOverdue.setItems(FXCollections.observableArrayList(f.getOverdueLoans()));
    }

    @FXML private void handleAddBook() { openDialog("/com/smartlibrary/ui/addbook/add_book.fxml",   "Agregar Libro"); loadData(); }
    @FXML private void handleAddUser() { openDialog("/com/smartlibrary/ui/adduser/add_user.fxml",   "Registrar Usuario"); loadData(); }
    @FXML private void handleViewBooks(){ openDialog("/com/smartlibrary/ui/booklist/book_list.fxml", "Lista de Libros"); }
    @FXML private void handleViewUsers(){ openDialog("/com/smartlibrary/ui/userlist/user_list.fxml", "Lista de Usuarios"); }

    private void openDialog(String path, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
            Stage d = new Stage();
            d.initModality(Modality.APPLICATION_MODAL);
            d.setTitle(title);
            d.setScene(new Scene(loader.load()));
            d.showAndWait();
        } catch (Exception e) { e.printStackTrace(); }
    }
}
