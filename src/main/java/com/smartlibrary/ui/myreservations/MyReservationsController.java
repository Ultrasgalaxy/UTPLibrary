package com.smartlibrary.ui.myreservations;

import com.smartlibrary.model.Reservation;
import com.smartlibrary.model.User;
import com.smartlibrary.ui.main.AppContext;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.List;

public class MyReservationsController {

    @FXML private TableView<Reservation> tableReservations;
    @FXML private TableColumn<Reservation, String> colBook;
    @FXML private TableColumn<Reservation, String> colAuthor;
    @FXML private TableColumn<Reservation, String> colPickup;
    @FXML private Label lblCount;
    @FXML private Label lblStatus;
    @FXML private Button btnCancel;

    @FXML
    public void initialize() {
        colBook.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getBook().getTitle()));
        colAuthor.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getBook().getAuthor()));
        colPickup.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPickupFormatted()));

        tableReservations.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSel, newSel) -> btnCancel.setDisable(newSel == null));

        loadData();
    }

    @FXML private void handleRefresh() { loadData(); }

    @FXML
    private void handleCancel() {
        Reservation selected = tableReservations.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
            "¿Cancelar la reserva de \"" + selected.getBook().getTitle() + "\"?",
            ButtonType.CANCEL, ButtonType.OK);
        confirm.setHeaderText("Cancelar reserva");
        var result = confirm.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) return;

        User user = AppContext.getCurrentUser();
        String msg = AppContext.getFacade().cancelReservation(user, selected.getBook());
        lblStatus.setText(msg);
        loadData();
    }

    @FXML
    private void handleClose() {
        ((Stage) btnCancel.getScene().getWindow()).close();
    }

    private void loadData() {
        User user = AppContext.getCurrentUser();
        List<Reservation> mine = AppContext.getFacade().getReservationsForUser(user);
        tableReservations.setItems(FXCollections.observableArrayList(mine));
        lblCount.setText(mine.size() + " reserva(s)");
        btnCancel.setDisable(true);
    }
}
