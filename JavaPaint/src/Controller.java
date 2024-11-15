import java.io.File;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ColorPicker;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Controller {

    @FXML
    private Canvas imageContainer;

    private boolean isDrawing = false;

    @FXML
    private ColorPicker colorPicker;
    

    public void exitApp() {
       System.exit(0);
    }

    public void uploadFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        File file = fileChooser.showOpenDialog(getStage());
        if (file != null) {
            Image backgroundImage = new Image(file.toURI().toString());
            GraphicsContext gc = imageContainer.getGraphicsContext2D();

            gc.drawImage(backgroundImage, 0, 0, imageContainer.getWidth(), imageContainer.getHeight());
        }
    }

     private Stage getStage() {
        return (Stage) imageContainer.getScene().getWindow();
    }

    public void startDrawing() {
        if (!isDrawing) {
            isDrawing = true;
            imageContainer.setOnMouseClicked(event -> {
                double cordX = event.getX();
                double cordY = event.getY();

                GraphicsContext gc = imageContainer.getGraphicsContext2D();

                gc.setFill(colorPicker.getValue());

                gc.fillOval(cordX - 5, cordY - 5, 10, 10); 
            });

            imageContainer.setOnMouseDragged(event -> {
                double cordX = event.getX();
                double cordY = event.getY();

                GraphicsContext gc = imageContainer.getGraphicsContext2D();

                gc.setFill(colorPicker.getValue());

                gc.fillOval(cordX - 5, cordY - 5, 10, 10); 
            });
        } else {
            imageContainer.setOnMouseClicked(null);
            imageContainer.setOnMouseDragged(null);
            isDrawing = false;
        }
    }
}
