package com.javafx;

import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ColorPicker;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
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

    public void saveImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save image");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG Files","*.png"));

        File file = fileChooser.showSaveDialog(getStage());
        if (file != null) {
            try {
                WritableImage writableImage = new WritableImage((int) imageContainer.getWidth(), (int) imageContainer.getHeight());
                imageContainer.snapshot(null, writableImage);
                // Get width and height from the WritableImage
            int width = (int) writableImage.getWidth();
            int height = (int) writableImage.getHeight();

            // Create a new BufferedImage with ARGB format
            BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

            // Get PixelReader from the WritableImage
            javafx.scene.image.PixelReader pixelReader = writableImage.getPixelReader();

            // Loop through each pixel in the WritableImage and copy the data to the BufferedImage
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    Color color = pixelReader.getColor(x, y);

                    // Extract the RGBA components from the JavaFX Color object
                    int red = (int) (color.getRed() * 255);
                    int green = (int) (color.getGreen() * 255);
                    int blue = (int) (color.getBlue() * 255);
                    int alpha = (int) (color.getOpacity() * 255);

                    // Combine the components into one ARGB value and set it in the BufferedImage
                    int argb = (alpha << 24) | (red << 16) | (green << 8) | blue;
                    bufferedImage.setRGB(x, y, argb);
                }
            }

            // Save the BufferedImage to the file using ImageIO
            ImageIO.write(bufferedImage, "PNG", file);
            } catch (IOException e) {
                System.err.println("Failed to save canvas");
            }
        } else {
            System.out.println("No file selected");
        }
    }

    public void startDrawing() {
        if (!isDrawing) {
           
            isDrawing = true;
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
        } else {
            imageContainer.setOnMouseDragged(null); 
            imageContainer.setOnMouseDragReleased(null);
            isDrawing = false;
        }
    }

    public void clearCanvas() {
        GraphicsContext gc = imageContainer.getGraphicsContext2D();
        gc.clearRect(0, 0, imageContainer.getWidth(), imageContainer.getHeight());
    }
}