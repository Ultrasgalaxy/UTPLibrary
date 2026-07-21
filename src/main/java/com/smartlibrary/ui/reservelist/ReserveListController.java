package com.smartlibrary.ui.reservelist;

import com.smartlibrary.model.Reservation;
import com.smartlibrary.ui.main.AppContext;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.util.List;

public class ReserveListController {
    @FXML private TableView<Reservation>         tableReserves;
    @FXML private TableColumn<Reservation,String> colUser;
    @FXML private TableColumn<Reservation,String> colBook;
    @FXML private TableColumn<Reservation,String> colDate;
    @FXML private TableColumn<Reservation,String> colActive;
    @FXML private Label lblCount;

    @FXML public void initialize() {
        colUser.setCellValueFactory(c   -> new SimpleStringProperty(c.getValue().getUser().getName()));
        colBook.setCellValueFactory(c   -> new SimpleStringProperty(c.getValue().getBook().getTitle()));
        colDate.setCellValueFactory(c   -> new SimpleStringProperty(c.getValue().getPickupFormatted()));
        colActive.setCellValueFactory(c -> new SimpleStringProperty(
            c.getValue().isActive() ? "Activa" : "Cancelada"));
        loadData();
    }

    @FXML private void handleRefresh() { loadData(); }

    private void loadData() {
        List<Reservation> res = AppContext.getFacade().getActiveReservations();
        tableReserves.setItems(FXCollections.observableArrayList(res));
        lblCount.setText("Total: " + res.size() + " reserva(s) activa(s)");
    }
}
