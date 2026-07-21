package com.smartlibrary.ui.main;

import com.smartlibrary.util.DemoDataSeeder;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        DemoDataSeeder.seed(AppContext.getFacade());

        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/com/smartlibrary/ui/login/login.fxml"));

        Scene scene = new Scene(loader.load(), 480, 660);
        stage.setTitle("UTP Library");
        stage.setScene(scene);
        stage.setMinWidth(420);
        stage.setMinHeight(580);

        // Guardar estado inicial para que los cambios de pantalla no lo alteren
        stage.setWidth(480);
        stage.setHeight(660);
        stage.centerOnScreen();
        AppContext.saveStageState(stage);

        stage.show();
    }
}
