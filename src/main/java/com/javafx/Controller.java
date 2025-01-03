package com.javafx;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import javafx.scene.control.Button;



import javafx.scene.control.TextArea;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ColorPicker;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.Menu;

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

    @FXML
    private Button rollBackBut;

    @FXML
    private Button fwdBut;

    @FXML
    private TextArea historyBox;

    private String historyText;

    @FXML
    private Menu aboutMenu;

    @FXML
    private Button aboutButton;

    @FXML
    private Menu exitMenu;

    @FXML
    private Button exitButton;


    private ArrayList<WritableImage> previousCanvasContent = new ArrayList<WritableImage>();

    private int currentIndex = 0;

    public void exitApp() {
        System.exit(0);
    }

    public void setFirstIndex() {
        WritableImage writableImage = new WritableImage(1920, 1080);
        checkStepButValidity();

        PixelWriter pixelWriter = writableImage.getPixelWriter();

        for (int y = 0; y < 1080; y++) {
            for (int x = 0; x < 1920; x++) {
                pixelWriter.setColor(x, y, Color.WHITE);
            }
        }

        previousCanvasContent.add(writableImage);
        historyBox.setEditable(false);
        historyBox.setFocusTraversable(false);
        historyText = "App started successfully.";
        historyBox.setText(historyText);
        historyBox.setWrapText(true);
    }

    public void highlightAboutMenu() {
        aboutMenu.setStyle("-fx-background-color: rgb(0,150,201);");
        aboutButton.setStyle("-fx-text-fill: white;-fx-background-color: transparent;-fx-padding: -2;");
    }

    public void removeHighlightFromAboutMenu() {
        aboutMenu.setStyle("-fx-background-color: transparent;");
        aboutButton.setStyle("-fx-text-fill: black;-fx-background-color: transparent;-fx-padding: -2;");
    }

    public void highlightExitMenu() {
        exitMenu.setStyle("-fx-background-color: rgb(0,150,201);");
        exitButton.setStyle("-fx-text-fill: white;-fx-background-color: transparent;-fx-padding: -2;");
    }

    public void removeHighlightFromExitMenu() {
        exitMenu.setStyle("-fx-background-color: transparent;");
        exitButton.setStyle("-fx-text-fill: black;-fx-background-color: transparent;-fx-padding: -2;");
    }

    private void addHistoryText(String text) {
        historyText += "\n--------------   \n" + text;
        historyBox.setText(historyText);
        historyBox.appendText("");
    }

    public void uploadFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg" , "*.bmp")
        );

        File file = fileChooser.showOpenDialog(getStage());
        if (file != null) {
            Image backgroundImage = new Image(file.toURI().toString());
            GraphicsContext gc = imageContainer.getGraphicsContext2D();

            gc.drawImage(backgroundImage, 0, 0, imageContainer.getWidth(), imageContainer.getHeight());
        }
        saveCanvas();
        addHistoryText("Image uploaded.");
    }

     private Stage getStage() {
        return (Stage) imageContainer.getScene().getWindow();
    }

    public void saveImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save image");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files","*.png", "*.jpg", "*.jpeg", "*.bmp"));

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
                addHistoryText("Image saved.");
            } catch (IOException e) {
                System.err.println("Failed to save canvas");
            }
        } else {
            System.out.println("No file selected");
        }
        
    }

    private void saveCanvas() {
        WritableImage writableImage = new WritableImage(1920, 1080);
        imageContainer.snapshot(null, writableImage);
        if (!previousCanvasContent.isEmpty()) {
            if (previousCanvasContent.size() < 10) {
                previousCanvasContent.add(writableImage);
            } else {
                previousCanvasContent.remove(0);
                previousCanvasContent.add(writableImage);
            }
        } else {
            previousCanvasContent.add(writableImage);
        }

        if (currentIndex < 10) {
            currentIndex++;
        }

        for (int i = currentIndex + 1; i < previousCanvasContent.size(); i++) {
            previousCanvasContent.remove(i);
        }
        checkStepButValidity();
    }

    private void showHiddenCanvasContent() {
        if (!previousCanvasContent.isEmpty()) {

            GraphicsContext gc = imageContainer.getGraphicsContext2D();

            gc.drawImage(previousCanvasContent.get(currentIndex), 0, 0, previousCanvasContent.get(currentIndex).getWidth(), previousCanvasContent.get(currentIndex).getHeight());
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

                // Extract the RGBA components from the JavaFX Color object
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
        saveCanvas();
        addHistoryText("Invert filter applied.");
    }

    public void startDrawing() {
        if (!isDrawing) {
           
            isDrawing = true;
            addHistoryText("Started drawing.");

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
                saveCanvas();
            });
        } else {
            imageContainer.setOnMouseDragged(null); 
            imageContainer.setOnMouseDragReleased(null);
            imageContainer.setCursor(Cursor.DEFAULT);
            isDrawing = false;
            addHistoryText("Stopped drawing.");
        }
    }

    public void clearCanvas() {
        GraphicsContext gc = imageContainer.getGraphicsContext2D();
        gc.clearRect(0, 0, imageContainer.getWidth(), imageContainer.getHeight());
        saveCanvas();
        addHistoryText("Canvas cleared.");
    }

    public void resizeCanvasWidth(Double windowWidth, Double oldWidth) {
        if (windowWidth > oldWidth) {
            imageContainer.setWidth(windowWidth - 125);
            line.setStartX(windowWidth - 650);
            line.setEndX(windowWidth - 650);
            showHiddenCanvasContent();
        } else {
            imageContainer.setWidth(windowWidth - 125);
            line.setStartX(windowWidth - 650);
            line.setEndX(windowWidth - 650);
        }
    }

    public void resizeCanvasHeight(Double windowHeight, Double oldHeight) {
        if (windowHeight > oldHeight) {
            imageContainer.setHeight(windowHeight - 20);
            historyBox.setPrefHeight(windowHeight - 260);
            line.setStartY(-14);
            line.setEndY(windowHeight);
            showHiddenCanvasContent();
        } else {
            imageContainer.setHeight(windowHeight - 20);
            historyBox.setPrefHeight(windowHeight - 260);
            line.setStartY(-14);
            line.setEndY(windowHeight);
        }
    }

    public void rollbackCanvas() {
        if (currentIndex != 0) {
            rollBackBut.setDisable(false);
            GraphicsContext gc = imageContainer.getGraphicsContext2D();

            currentIndex--;
            gc.drawImage(previousCanvasContent.get(currentIndex), 0, 0, previousCanvasContent.get(currentIndex).getWidth(), previousCanvasContent.get(currentIndex).getHeight());
            addHistoryText("Undo.");
        }
        checkStepButValidity();
    }

    public void fwdCanvas() {
        if (currentIndex + 1 < previousCanvasContent.size()) {
            GraphicsContext gc = imageContainer.getGraphicsContext2D();

            currentIndex++;
            gc.drawImage(previousCanvasContent.get(currentIndex), 0, 0, previousCanvasContent.get(currentIndex).getWidth(), previousCanvasContent.get(currentIndex).getHeight());
            addHistoryText("Reverted.");
        }
        checkStepButValidity();
    }

    public void generateImage() {
            
        int width = (int) imageContainer.getWidth();
        int height = (int) imageContainer.getHeight();
        Random random = new Random();

        WritableImage image = new WritableImage(width, height);
        PixelWriter writer = image.getPixelWriter();

        double freqRed = random.nextDouble() * 0.05 + 0.01; 
        double freqGreen = random.nextDouble() * 0.05 + 0.01;
        double freqBlue = random.nextDouble() * 0.05 + 0.01; 
        double phaseRed = random.nextDouble() * Math.PI * 2;
        double phaseGreen = random.nextDouble() * Math.PI * 2; 
        double phaseBlue = random.nextDouble() * Math.PI * 2;

        // Generate colors based on randomized equations
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double dx = x - width / 2.0;
                double dy = y - height / 2.0;
                double distance = Math.sqrt(dx * dx + dy * dy);

                // Use randomized parameters for color generation
                double red = (Math.sin(distance * freqRed + phaseRed) + 1) * 128;
                double green = (Math.sin(distance * freqGreen + phaseGreen) + 1) * 128;
                double blue = (Math.cos(distance * freqBlue + phaseBlue) + 1) * 128;

                Color color = Color.rgb((int) red, (int) green, (int) blue);
                writer.setColor(x, y, color);
            }
        }

        GraphicsContext gc = imageContainer.getGraphicsContext2D();

        gc.drawImage(image, 0, 0, imageContainer.getWidth(), imageContainer.getHeight());
        saveCanvas();
        addHistoryText("Random image generated.");
    }

    public void checkStepButValidity() {
        if (currentIndex == 0) {
            rollBackBut.setDisable(true);
        } else {
            rollBackBut.setDisable(false);
        }

        if (currentIndex + 1 >= previousCanvasContent.size() || previousCanvasContent.isEmpty()) {
            fwdBut.setDisable(true);
        } else {
            fwdBut.setDisable(false);
        }
    }

    public void showAbout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("PopUp.fxml"));
            Parent popupContent = loader.load();

            TextArea textArea = (TextArea) popupContent.lookup("#popUpTxtArea");
            if (textArea != null) {
                textArea.setEditable(false);
                textArea.setFocusTraversable(false);
            }

            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setTitle("About");

            popupStage.setResizable(false);

            Button closeBut = (Button) popupContent.lookup("#closeButton");
            closeBut.setOnAction(e -> {
                popupStage.close();
            });

            Scene popupScene = new Scene(popupContent);
            popupStage.setScene(popupScene);
            popupStage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
