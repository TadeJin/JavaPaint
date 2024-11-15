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

    private double previousX;
    private double previousY;

    private boolean first = true;
    

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
            drawLine();
        } else {
            imageContainer.setOnMouseClicked(null);  
            imageContainer.setOnMouseDragged(null); 
            imageContainer.setOnMouseDragReleased(null);
            isDrawing = false;
        }
    }

    private void drawLine() {
        imageContainer.setOnMouseDragged(event -> {
            if (first) {
                previousX = event.getX();
                previousY = event.getY();
                first = false;
            }
            double cordX = event.getX();
            double cordY = event.getY();
    
            GraphicsContext gc = imageContainer.getGraphicsContext2D();
    
            gc.setStroke(colorPicker.getValue());
            gc.setLineWidth(2);
    
            gc.strokeLine(previousX, previousY, cordX, cordY);
    
            previousX = cordX;
            previousY = cordY;
        });

        imageContainer.setOnMouseReleased(event ->  {
            first = true;
        });
    }

    public void clearCanvas() {
        GraphicsContext gc = imageContainer.getGraphicsContext2D();
        gc.clearRect(0, 0, imageContainer.getWidth(), imageContainer.getHeight());
    }
}
