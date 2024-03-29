package com.example.shipsgamegui;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Control;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;

public class ClientGUISettings {
    public static void startWindow(String fxmlFilePath, String windowTitle) {
        try{
            Stage stage = new Stage();

            FXMLLoader fxmlLoader = new FXMLLoader(ClientApplication.class.getResource(fxmlFilePath));
            Scene scene = new Scene(fxmlLoader.load(), 800, 600);

            stage.setMinWidth(800);
            stage.setMinHeight(600);
            stage.setMaxWidth(1000);
            stage.setMaxHeight(800);

            stage.setTitle(windowTitle);
            stage.setScene(scene);
            stage.show();

        }
        catch(IOException e){
            throw new RuntimeException();
        }
    }

    public static void initializeNewWindow(String fxmlFilePath, String windowTitle, Control control) {
        try{
            Stage stage = new Stage();

            FXMLLoader fxmlLoader = new FXMLLoader(ClientApplication.class.getResource(fxmlFilePath));
            Scene scene = new Scene(fxmlLoader.load(), 800, 600);

            stage.setMinWidth(800);
            stage.setMinHeight(600);
            stage.setMaxWidth(1000);
            stage.setMaxHeight(800);

            stage.setTitle(windowTitle);
            stage.setScene(scene);
            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent windowEvent) {
                    System.out.println("TEST");
                    try {
                        ClientSocketConnection.socket.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("TEST2");
                    System.exit(0);
                }
            });
            stage.show();

            Stage currentStage = (Stage) control.getScene().getWindow();
            currentStage.close();
        }
        catch(IOException e){
            throw new RuntimeException();
        }
    }

    public static void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
