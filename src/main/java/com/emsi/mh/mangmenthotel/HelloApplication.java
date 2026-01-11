package com.emsi.mh.mangmenthotel;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        com.emsi.mh.mangmenthotel.util.DatabaseInitializer.initialize();

        FXMLLoader fxmlLoader = new FXMLLoader(
                HelloApplication.class.getResource("/com/emsi/mh/mangmenthotel/view/login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 400, 400);
        stage.setTitle("Hotel Login");
        stage.setScene(scene);
        stage.show();
    }
}
