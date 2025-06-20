// CypherScene.java
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class CypherScene extends JFrame{
    private Button fileNameButton;
    private Button imageFileButton;
    public Scene getScene(Stage stage, Scene mainScene) {
        Group root = new Group();
        Scene scene = new Scene(root, 1000, 1000, Color.LIGHTGREY);

        Image icon = new Image("ikonka.png");
        stage.getIcons().add(icon);        
        stage.setTitle("Secret Message here ;)");

        Text text = new Text("Cypher message process");
        text.setX(50);
        text.setY(100);
        text.setFont(Font.font("Verdana", 50));
        text.setFill(Color.BLACK);

        Line line = new Line(55, 110, 680, 110);
        line.setStrokeWidth(5);

        Image lock = new Image("lock.png");
        ImageView imageview = new ImageView(lock);
        imageview.setX(200);
        imageview.setY(550);
        imageview.setScaleX(1.75);
        imageview.setScaleY(1.75);

        Image arrow = new Image("right-arrow.png");
        ImageView imageview2 = new ImageView(arrow);
        imageview2.setX(265);
        imageview2.setY(75);
        imageview2.setScaleX(0.25);
        imageview2.setScaleY(0.25);

        Button button = new Button("Search for file");
        button.setLayoutX(130);
        button.setLayoutY(300);
        button.setPrefWidth(200);
        button.setPrefHeight(60);
        button.setFont(new Font("Arial", 20));

        button.setOnAction(e -> {
            
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File("."));

            int response = fileChooser.showSaveDialog(null);

            if(response == JFileChooser.APPROVE_OPTION){
                File selectedFile = fileChooser.getSelectedFile();
                System.out.println("Wybrano plik: " + selectedFile);

                // Ustaw przycisk z nazwą pliku
                fileNameButton.setText("📄 " + selectedFile.getName());
                fileNameButton.setUserData(selectedFile); // zapisz obiekt File do przycisku
                fileNameButton.setVisible(true);
            }
        });

        // Przycisk z nazwą pliku (widoczny tylko po wczytaniu)
        fileNameButton = new Button();
        fileNameButton.setVisible(false);
        fileNameButton.setLayoutX(130);
        fileNameButton.setLayoutY(370);
        fileNameButton.setFont(Font.font("Arial", 16));

        fileNameButton.setOnAction(e -> {
            if (fileNameButton.isDisabled()) return;
            fileNameButton.setDisable(true);
            showFileContentWindow(fileNameButton);
        });
        

        Button button2 = new Button("Search for image");
        button2.setLayoutX(700);
        button2.setLayoutY(300);
        button2.setPrefWidth(200);
        button2.setPrefHeight(60);
        button2.setFont(new Font("Arial", 20));

        button2.setOnAction(e -> {
            JFileChooser imageChooser = new JFileChooser();
            imageChooser.setCurrentDirectory(new File("."));

            int response = imageChooser.showSaveDialog(null);

            if(response == JFileChooser.APPROVE_OPTION){
                File selectedFile = imageChooser.getSelectedFile();
                System.out.println("Wybrano plik: " + selectedFile);

                // Ustaw przycisk z nazwą pliku
                imageFileButton.setText("📄 " + selectedFile.getName());
                imageFileButton.setUserData(selectedFile); // zapisz obiekt File do przycisku
                imageFileButton.setVisible(true);
            }
        });

        // Przycisk z nazwą pliku (widoczny tylko po wczytaniu)
        imageFileButton = new Button();
        imageFileButton.setVisible(false);
        imageFileButton.setLayoutX(700);
        imageFileButton.setLayoutY(370);
        imageFileButton.setFont(Font.font("Arial", 16));

        imageFileButton.setOnAction(e -> {
            if (imageFileButton.isDisabled()) return;
            imageFileButton.setDisable(true);
            showImageWindow(imageFileButton);
        });
        

        Button returnB = new Button("Return");
        returnB.setLayoutX(700);
        returnB.setLayoutY(800);
        returnB.setPrefWidth(200);
        returnB.setPrefHeight(60);
        returnB.setFont(new Font("Arial", 20));

        // >>> Wróć do sceny głównej po kliknięciu
        returnB.setOnAction(e -> stage.setScene(mainScene));

        Button buttonCode = new Button("Cypher");
        buttonCode.setLayoutX(465);
        buttonCode.setLayoutY(240);
        buttonCode.setPrefWidth(95);
        buttonCode.setPrefHeight(40);
        buttonCode.setFont(new Font("Arial", 20));
        //buttonCode.setVisible(false);

        root.getChildren().addAll(text, line, imageview, button, button2, returnB, imageview2, fileNameButton, imageFileButton, buttonCode);
        return scene;
    }



private void showFileContentWindow(Button sourceButton) {
    File file = (File) sourceButton.getUserData();
    if (file == null || !file.exists()) return;

    Stage popupStage = new Stage();
    popupStage.setTitle("Zawartość pliku: " + file.getName());
    TextArea area = new TextArea();
    area.setWrapText(true);
    area.setEditable(false);
    area.setFont(Font.font("Consolas", 14));


    //String content = FileService.loadText(file);


    StringBuilder content = new StringBuilder();

    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
        String line;
        while ((line = reader.readLine()) != null) {
            content.append(line).append("\n");
        }
    } catch (IOException ex) {
        content.append("Błąd podczas odczytu pliku: ").append(ex.getMessage());
    }




    area.setText(content.toString());

    int lineCount = content.toString().split("\n").length;
    int maxLineLength = content.toString().lines().mapToInt(String::length).max().orElse(80);

    int width = Math.min(1000, 50 + maxLineLength * 7);
    int height = Math.min(800, 40 + lineCount * 20);

    ScrollPane scrollPane = new ScrollPane(area);
    scrollPane.setFitToWidth(true);
    scrollPane.setFitToHeight(true);

    Scene popupScene = new Scene(scrollPane, width, height);
    popupStage.setScene(popupScene);
    popupStage.setResizable(false);

    popupStage.setOnHidden(ev -> sourceButton.setDisable(false));

    popupStage.show();
}

private void showImageWindow(Button sourceButton) {
    File file = (File) sourceButton.getUserData();
    if (file == null || !file.exists()) return;

    Stage popupStage = new Stage();
    popupStage.setTitle("Obraz: " + file.getName());



    // BufferedImage image = FileService.loadImage(file);

    Image image;
    try {
        image = new Image(file.toURI().toString());
    } catch (Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Błąd");
        alert.setHeaderText(null);
        alert.setContentText("Nie można załadować obrazu.");
        alert.showAndWait();
        sourceButton.setDisable(false);
        return;
    }





    ImageView imageView = new ImageView(image);
    imageView.setPreserveRatio(true);

    double maxWidth = 1000;
    double maxHeight = 800;

    if (image.getWidth() > maxWidth || image.getHeight() > maxHeight) {
        imageView.setFitWidth(maxWidth);
        imageView.setFitHeight(maxHeight);
    }

    ScrollPane scrollPane = new ScrollPane(imageView);
    scrollPane.setFitToWidth(true);
    scrollPane.setFitToHeight(true);

    Scene scene = new Scene(scrollPane);
    popupStage.setScene(scene);
    popupStage.setResizable(false);

    // Odblokuj przycisk po zamknięciu okna
    popupStage.setOnHidden(ev -> sourceButton.setDisable(false));

    popupStage.show();
}

}

