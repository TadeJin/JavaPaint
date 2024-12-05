package com.javafx;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ColorPicker;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Controller {

    @FXML
    private Canvas imageContainer;

    private boolean isDrawing = false;

    @FXML
    private Line line;

    @FXML
    private ColorPicker colorPicker;

    @FXML
    private VBox mainContainer;

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
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files","*.png", "*.jpg", "*.jpeg"));

        File file = fileChooser.showSaveDialog(getStage());
        if (file != null) {
            try {
                WritableImage writableImage = new WritableImage((int) imageContainer.getWidth(), (int) imageContainer.getHeight());
                imageContainer.snapshot(null, writableImage);
            int width = (int) writableImage.getWidth();
            int height = (int) writableImage.getHeight();

            BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

            javafx.scene.image.PixelReader pixelReader = writableImage.getPixelReader();

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    Color color = pixelReader.getColor(x, y);

                    int red = (int) (color.getRed() * 255);
                    int green = (int) (color.getGreen() * 255);
                    int blue = (int) (color.getBlue() * 255);
                    int alpha = (int) (color.getOpacity() * 255);

                    int argb = (alpha << 24) | (red << 16) | (green << 8) | blue;
                    bufferedImage.setRGB(x, y, argb);
                }
            }

            ImageIO.write(bufferedImage, "PNG", file);
            } catch (IOException e) {
                System.err.println("Failed to save canvas");
            }
        } else {
            System.out.println("No file selected");
        }
    }

    public void invertColors() {
        WritableImage writableImage = new WritableImage((int) imageContainer.getWidth(), (int) imageContainer.getHeight());
        imageContainer.snapshot(null, writableImage);
        int width = (int) writableImage.getWidth();
        int height = (int) writableImage.getHeight();

        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        javafx.scene.image.PixelReader pixelReader = writableImage.getPixelReader();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = pixelReader.getColor(x, y);

                int red = 255 - (int) (color.getRed() * 255);
                int green = 255 - (int) (color.getGreen() * 255);
                int blue = 255 - (int) (color.getBlue() * 255);
                int alpha = (int) (color.getOpacity() * 255);
                
                int argb = (alpha << 24) | (red << 16) | (green << 8) | blue;
                bufferedImage.setRGB(x, y, argb);
            }
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Image backgroundImage = null;

        try {
            ImageIO.write(bufferedImage, "png", outputStream);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
            backgroundImage = new javafx.scene.image.Image(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        GraphicsContext gc = imageContainer.getGraphicsContext2D();

        gc.drawImage(backgroundImage, 0, 0, imageContainer.getWidth(), imageContainer.getHeight());
    }

    public void startDrawing() {
        if (!isDrawing) {
           
            isDrawing = true;

            imageContainer.setCursor(Cursor.CROSSHAIR);

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
            imageContainer.setCursor(Cursor.DEFAULT);
            isDrawing = false;
        }
    }

    public void clearCanvas() {
        GraphicsContext gc = imageContainer.getGraphicsContext2D();
        gc.clearRect(0, 0, imageContainer.getWidth(), imageContainer.getHeight());
    }

    public void resizeCanvasWidth(Double windowWidth) {
        imageContainer.setWidth(windowWidth - 125);
        line.setStartX(windowWidth - 650);
        line.setEndX(windowWidth - 650);
    }

    public void resizeCanvasHeight(Double windowHeight) {
        imageContainer.setHeight(windowHeight-20);
        line.setStartY(-14);
        line.setEndY(windowHeight);
    }
}
