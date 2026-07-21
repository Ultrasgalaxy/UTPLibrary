package com.smartlibrary.ui.userlist;

import com.smartlibrary.model.User;
import com.smartlibrary.ui.main.AppContext;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.util.List;
import java.util.stream.Collectors;

public class UserListController {
    @FXML private TableView<User>         tableUsers;
    @FXML private TableColumn<User,String>  colName;
    @FXML private TableColumn<User,String>  colType;
    @FXML private TableColumn<User,String>  colMembership;
    @FXML private TableColumn<User,Integer> colLoans;
    @FXML private TableColumn<User,Integer> colMax;
    @FXML private TextField fieldSearch;
    @FXML private Label lblCount;

    @FXML public void initialize() {
        colName.setCellValueFactory(c       -> new SimpleStringProperty(c.getValue().getName()));
        colType.setCellValueFactory(c       -> new SimpleStringProperty(c.getValue().getRoleLabel()));
        colMembership.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getMembership().getDescription()));
        colLoans.setCellValueFactory(c      -> new SimpleIntegerProperty(c.getValue().getCurrentLoans()).asObject());
        colMax.setCellValueFactory(c        -> new SimpleIntegerProperty(c.getValue().getMembership().getMaxBooks()).asObject());
        loadData(AppContext.getFacade().getAllUsers());
    }

    @FXML private void handleRefresh() { loadData(AppContext.getFacade().getAllUsers()); fieldSearch.clear(); }

    @FXML private void handleSearch() {
        String q = fieldSearch.getText().toLowerCase();
        loadData(AppContext.getFacade().getAllUsers().stream()
            .filter(u -> u.getName().toLowerCase().contains(q))
            .collect(Collectors.toList()));
    }

    private void loadData(List<User> users) {
        tableUsers.setItems(FXCollections.observableArrayList(users));
        lblCount.setText("Total: " + users.size() + " usuario(s)");
    }
}
