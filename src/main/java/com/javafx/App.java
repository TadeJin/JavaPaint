package com.javafx;

import java.io.IOException;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) {

        try {
           
            Parent root = FXMLLoader.load(getClass().getResource("MainScene.fxml"));
            Scene scene = new Scene(root,700,500);

            primaryStage.setTitle("JavaPaint");
            primaryStage.setScene(scene);

            primaryStage.widthProperty().addListener((obs,oldWidth,newWidth) -> {
                controller.resizeCanvasWidth(newWidth.doubleValue(),oldWidth.doubleValue());
            });

            primaryStage.heightProperty().addListener((obs,oldHeight,newHeight) -> {
                controller.resizeCanvasHeight(newHeight.doubleValue(),oldHeight.doubleValue());
            });

            primaryStage.setMinWidth(750);
            primaryStage.setMinHeight(550);

            primaryStage.setMaxWidth(1920);
            primaryStage.setMaxHeight(1080);

            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

 public static void main(String[] args) {
        launch(args);
    }
}
