package com.smartlibrary.ui.main;

import com.smartlibrary.facade.LibraryFacade;
import com.smartlibrary.model.Book;
import com.smartlibrary.model.Role;
import com.smartlibrary.model.User;
import com.smartlibrary.util.LoanDays;
import com.smartlibrary.util.LibrarySchedule;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;

public class MainController {

    @FXML private TabPane mainTabPane;
    @FXML private Tab tabIssue, tabReturn, tabAdmin;

    @FXML private ComboBox<Book>   cbBorrowBook;
    @FXML private ComboBox<String> cbBorrowDate;
    @FXML private ComboBox<String> cbBorrowSlot;
    @FXML private Label lblBorrowStatus;
    @FXML private Label lblDueDatePreview;
    @FXML private Label lblOverdueWarning;

    @FXML private ComboBox<Book>   cbReturnBook;
    @FXML private ComboBox<String> cbReturnDate;
    @FXML private ComboBox<String> cbReturnSlot;
    @FXML private Label lblReturnStatus;

    @FXML private Label  lblSessionName;
    @FXML private Label  lblSessionRole;
    @FXML private Button btnSideAddBook;
    @FXML private Button btnSideAddUser;
    @FXML private Button btnSideUsers;
    @FXML private MenuItem menuViewUsers;
    @FXML private Label  lblSeccionAcciones;

    private final LibraryFacade    facade         = AppContext.getFacade();
    private final List<LocalDate>  availableDates = LibrarySchedule.getAvailableDates();

    @FXML public void initialize() {
        applySessionUi();
        loadDateCombos();
        refreshCombos();
    }

    private void applySessionUi() {
        User current = AppContext.getCurrentUser();
        boolean isAdmin = current != null && current.getRole() == Role.ADMIN;
        if (current != null) {
            lblSessionName.setText(current.getName());
            lblSessionRole.setText(current.getRoleLabel());
        }
        if (isAdmin) {
            mainTabPane.getSelectionModel().select(tabAdmin);
        } else {
            mainTabPane.getTabs().remove(tabAdmin);
            btnSideAddBook.setVisible(false); btnSideAddBook.setManaged(false);
            btnSideAddUser.setVisible(false); btnSideAddUser.setManaged(false);
            lblSeccionAcciones.setVisible(false); lblSeccionAcciones.setManaged(false);
            // Solo un administrador puede ver la lista de cuentas de otros usuarios.
            btnSideUsers.setVisible(false); btnSideUsers.setManaged(false);
            menuViewUsers.setVisible(false);
            if (current != null && facade.hasOverdueLoans(current)) {
                lblOverdueWarning.setText(
                    "Tienes prestamo(s) vencido(s). Debes devolver antes de solicitar nuevos.");
                lblOverdueWarning.setVisible(true);
                lblOverdueWarning.setManaged(true);
            }
        }
    }

    private void loadDateCombos() {
        List<String> labels = availableDates.stream()
            .map(LibrarySchedule::formatDate).toList();
        cbBorrowDate.setItems(FXCollections.observableArrayList(labels));
        cbReturnDate.setItems(FXCollections.observableArrayList(labels));
    }

    @FXML private void handleBorrowDateSelected() {
        int idx = cbBorrowDate.getSelectionModel().getSelectedIndex();
        if (idx < 0) return;
        cbBorrowSlot.setItems(FXCollections.observableArrayList(
            LibrarySchedule.getSlots(availableDates.get(idx))));
        cbBorrowSlot.setPromptText("Seleccione una hora");
        updateDueDatePreview();
    }

    @FXML private void handleReturnDateSelected() {
        int idx = cbReturnDate.getSelectionModel().getSelectedIndex();
        if (idx < 0) return;
        cbReturnSlot.setItems(FXCollections.observableArrayList(
            LibrarySchedule.getSlots(availableDates.get(idx))));
        cbReturnSlot.setPromptText("Seleccione una hora");
    }

    private void updateDueDatePreview() {
        User current = AppContext.getCurrentUser();
        int idx = cbBorrowDate.getSelectionModel().getSelectedIndex();
        if (current == null || idx < 0) return;
        LocalDate pickup = availableDates.get(idx);
        int days = LoanDays.forUser(current);
        lblDueDatePreview.setText("Fecha limite de devolucion: " +
            pickup.plusDays(days) + "  (" + days + " dias segun tu tipo de cuenta)");
    }

    @FXML private void handleBorrow() {
        User user   = AppContext.getCurrentUser();
        Book book   = cbBorrowBook.getValue();
        int  dateIdx= cbBorrowDate.getSelectionModel().getSelectedIndex();
        String slot = cbBorrowSlot.getValue();
        if (book == null)  { lblBorrowStatus.setText("Seleccione un libro."); return; }
        if (dateIdx < 0)   { lblBorrowStatus.setText("Seleccione la fecha de recojo."); return; }
        if (slot == null)  { lblBorrowStatus.setText("Seleccione la hora de recojo."); return; }
        lblBorrowStatus.setText(facade.borrowBook(user, book, availableDates.get(dateIdx), slot));
        refreshCombos();
    }

    @FXML private void handleClearBorrow() {
        cbBorrowBook.setValue(null); cbBorrowDate.setValue(null);
        cbBorrowSlot.getItems().clear(); cbBorrowSlot.setPromptText("Primero elija una fecha");
        lblBorrowStatus.setText(""); lblDueDatePreview.setText("");
    }

    @FXML private void handleReturn() {
        User user = AppContext.getCurrentUser(); Book book = cbReturnBook.getValue();
        if (book == null) { lblReturnStatus.setText("Seleccione un libro."); return; }
        lblReturnStatus.setText(facade.returnBook(user, book));
        refreshCombos();
    }

    @FXML private void handleReserve() {
        User user    = AppContext.getCurrentUser();
        Book book    = cbReturnBook.getValue();
        int  dateIdx = cbReturnDate.getSelectionModel().getSelectedIndex();
        String slot  = cbReturnSlot.getValue();
        if (book == null) { lblReturnStatus.setText("Seleccione un libro."); return; }
        if (dateIdx < 0)  { lblReturnStatus.setText("Seleccione la fecha de recojo."); return; }
        if (slot == null) { lblReturnStatus.setText("Seleccione la hora de recojo."); return; }
        lblReturnStatus.setText(facade.reserveBook(user, book, availableDates.get(dateIdx), slot));
        refreshCombos();
    }

    /** Abre "Mis Reservas": la propia persona ve solo sus reservas activas y las cancela
     *  directamente ahi, sin tener que indicar manualmente usuario/libro. */
    @FXML private void handleViewMyReservations() {
        openDialog("/com/smartlibrary/ui/myreservations/my_reservations.fxml", "Mis Reservas");
        refreshCombos();
    }

    @FXML private void handleClearReturn() {
        cbReturnBook.setValue(null);
        cbReturnDate.setValue(null); cbReturnSlot.getItems().clear();
        cbReturnSlot.setPromptText("Primero elija fecha"); lblReturnStatus.setText("");
    }

    @FXML private void handleExit()             { System.exit(0); }
    @FXML private void handleAddBook()          { openDialog("/com/smartlibrary/ui/addbook/add_book.fxml",   "Agregar Libro");     refreshCombos(); }
    @FXML private void handleAddUser()          { openDialog("/com/smartlibrary/ui/adduser/add_user.fxml",   "Registrar Usuario"); refreshCombos(); }
    @FXML private void handleViewBooks()        { openDialog("/com/smartlibrary/ui/booklist/book_list.fxml", "Lista de Libros"); }
    @FXML private void handleViewUsers()        { openDialog("/com/smartlibrary/ui/userlist/user_list.fxml", "Lista de Usuarios"); }
    @FXML private void handleViewLoans()        { openDialog("/com/smartlibrary/ui/loanlist/loan_list.fxml", "Prestamos Activos"); }
    @FXML private void handleViewReservations() { openDialog("/com/smartlibrary/ui/reservelist/reserve_list.fxml", "Reservas Activas"); }

    @FXML private void handleLogout() {
        try {
            Stage stage = (Stage) mainTabPane.getScene().getWindow();
            AppContext.saveStageState(stage); // recordar tamano/posicion/maximizado actual
            AppContext.logout();
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/smartlibrary/ui/login/login.fxml"));
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.setTitle("UTP Library");
            stage.setMinWidth(420); stage.setMinHeight(580);
            // No se toca ancho/alto/posicion/maximizado: la ventana se mantiene
            // exactamente como el usuario la dejo, sin importar la accion realizada.
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void refreshCombos() {
        cbBorrowBook.getItems().setAll(facade.getAllBooks());
        cbReturnBook.getItems().setAll(facade.getAllBooks());
    }

    private void openDialog(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle(title);
            dialog.setScene(new Scene(loader.load()));
            dialog.showAndWait();
        } catch (Exception e) { e.printStackTrace(); }
    }
}
