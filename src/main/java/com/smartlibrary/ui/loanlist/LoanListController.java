package com.smartlibrary.ui.loanlist;

import com.smartlibrary.model.Loan;
import com.smartlibrary.ui.main.AppContext;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.util.List;

public class LoanListController {
    @FXML private TableView<Loan>         tableLoans;
    @FXML private TableColumn<Loan,String> colUser;
    @FXML private TableColumn<Loan,String> colBook;
    @FXML private TableColumn<Loan,String> colDate;
    @FXML private TableColumn<Loan,String> colDue;
    @FXML private TableColumn<Loan,String> colOverdue;
    @FXML private Label lblCount;

    @FXML public void initialize() {
        colUser.setCellValueFactory(c    -> new SimpleStringProperty(c.getValue().getUser().getName()));
        colBook.setCellValueFactory(c    -> new SimpleStringProperty(c.getValue().getBook().getTitle()));
        colDate.setCellValueFactory(c    -> new SimpleStringProperty(c.getValue().getPickupFormatted()));
        colDue.setCellValueFactory(c     -> new SimpleStringProperty(c.getValue().getDueDateFormatted()));
        colOverdue.setCellValueFactory(c -> new SimpleStringProperty(
            c.getValue().isOverdue() ? "VENCIDO" : "Al dia"));
        loadData();
    }

    @FXML private void handleRefresh() { loadData(); }

    private void loadData() {
        List<Loan> loans = AppContext.getFacade().getActiveLoans();
        tableLoans.setItems(FXCollections.observableArrayList(loans));
        lblCount.setText("Total: " + loans.size() + " prestamo(s) activo(s)");
    }
}
