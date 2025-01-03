package com.javafx;

import java.io.IOException;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) {

        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("MainScene.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root,700,500);

            Controller controller = loader.getController();

            primaryStage.setTitle("JavaPaint");
            primaryStage.setScene(scene);

            primaryStage.widthProperty().addListener((obs,oldWidth,newWidth) -> {
                controller.resizeCanvasWidth(newWidth.doubleValue(),oldWidth.doubleValue());
            });

            primaryStage.heightProperty().addListener((obs,oldHeight,newHeight) -> {
                controller.resizeCanvasHeight(newHeight.doubleValue(),oldHeight.doubleValue());
            });

            controller.setFirstIndex();

            KeyCombination ctrlZ = new KeyCodeCombination(javafx.scene.input.KeyCode.Z, KeyCombination.CONTROL_DOWN);
            scene.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
                if (ctrlZ.match(event)) {
                    controller.rollbackCanvas();
                }
            });

            KeyCombination ctrlY = new KeyCodeCombination(javafx.scene.input.KeyCode.Y, KeyCombination.CONTROL_DOWN);
            scene.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
                if (ctrlY.match(event)) {
                    controller.fwdCanvas();
                }
            });

            KeyCombination ctrlA = new KeyCodeCombination(javafx.scene.input.KeyCode.A);
            scene.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
                if (ctrlA.match(event)) {
                    controller.generateImage();;
                }
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
