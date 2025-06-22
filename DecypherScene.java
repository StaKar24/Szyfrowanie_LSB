import java.io.File;
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
import javafx.stage.Stage;
import javafx.scene.control.TextField;


public class DecypherScene extends JFrame{
    private Button imageFileButton2;
    private String decodedMessage = "";

    public Scene getScene(Stage stage, Scene mainScene) {
        Group root = new Group();
        Scene scene = new Scene(root, 1000, 1000, Color.LIGHTGREY);

        Image icon = new Image("ikonka.png");
        stage.getIcons().add(icon);        
        stage.setTitle("Secret Message here ;)");

        Text text = new Text("Decypher image process");
        text.setX(50);
        text.setY(100);
        text.setFont(Font.font("Verdana", 50));
        text.setFill(Color.BLACK);

        Line line = new Line(55, 110, 680, 110);
        line.setStrokeWidth(5);

        Image unlock = new Image("unlock.png");
        ImageView imageview = new ImageView(unlock);
        imageview.setX(65);
        imageview.setY(400);
        imageview.setScaleX(0.7);
        imageview.setScaleY(0.7);

        Button button = new Button("Search for image");
        button.setLayoutX(380);
        button.setLayoutY(300);
        button.setPrefWidth(200);
        button.setPrefHeight(60);
        button.setFont(new Font("Arial", 20));

button.setOnAction(e -> {
    JFileChooser imageChooser = new JFileChooser();
    imageChooser.setCurrentDirectory(new File("."));

    int response = imageChooser.showSaveDialog(null);

    if (response == JFileChooser.APPROVE_OPTION) {
        File selectedFile = imageChooser.getSelectedFile();
        System.out.println("Wybrano plik: " + selectedFile);

        // przypisz do pola klasy
        imageToDecypher = selectedFile;

        // Ustaw przycisk z nazwą pliku
        imageFileButton2.setText("📄 " + selectedFile.getName());
        imageFileButton2.setUserData(selectedFile); // zapisz obiekt File do przycisku
        imageFileButton2.setVisible(true);
    }
});

        // Przycisk z nazwą pliku (widoczny tylko po wczytaniu)
        imageFileButton2 = new Button();
        imageFileButton2.setVisible(false);
        imageFileButton2.setLayoutX(380);
        imageFileButton2.setLayoutY(370);
        imageFileButton2.setFont(Font.font("Arial", 16));

        imageFileButton2.setOnAction(e -> {
            if (imageFileButton2.isDisabled()) return;
            imageFileButton2.setDisable(true);
            showImageWindow(imageFileButton2);
        });



        TextField fileNameField = new TextField();
        fileNameField.setPromptText("Wpisz nazwę pliku");
        fileNameField.setLayoutX(380);
        fileNameField.setLayoutY(460);
        fileNameField.setPrefWidth(260);
        fileNameField.setFont(Font.font("Arial", 16));
        fileNameField.setVisible(false); // <<< Ukryj na start

        // 🔹 Przycisk zapisu
        Button saveButton = new Button("Zapisz wiadomość ");
        saveButton.setLayoutX(380);
        saveButton.setLayoutY(500);
        saveButton.setPrefWidth(200);
        saveButton.setPrefHeight(40);
        saveButton.setFont(new Font("Arial", 16));
        saveButton.setVisible(false); // <<< Ukryj na start


        saveButton.setOnAction(e -> {
            File file = new File(fileNameField.getText());
            if (fileNameField.getText().isEmpty()) {
                System.err.println("Blad w podaniu nazwy pliku!");
                return;
            }
            if (decodedMessage == null || decodedMessage.isEmpty()) {
                System.err.println("Blad, brak wiadomosci do zapisania!");
                return;
            }
            try {
                FileService.saveText(decodedMessage, file );
            } catch (IOException ex) {
                System.err.println("Blad, nie udalo się zapisac pliku: " + ex.getMessage());
            }
        });


        Button buttonDeCode = new Button("Decypher");
        buttonDeCode.setLayoutX(425);
        buttonDeCode.setLayoutY(240);
        buttonDeCode.setPrefWidth(115);
        buttonDeCode.setPrefHeight(40);
        buttonDeCode.setFont(new Font("Arial", 20));
        //buttonDeCode.setVisible(false);

        buttonDeCode.setOnAction(e -> {
            try {
                    decodedMessage = Steganography.extractMessage(imageToDecypher); // 🔹 ZAPISZ do zmiennej
                    System.out.println(decodedMessage);
                    //System.out.println("tez dziala");
                    fileNameField.setVisible(true);
                    saveButton.setVisible(true);
                } catch (IOException ef) {
                    System.out.println("Blad przy szyfrowaniu pliku: " + ef.getMessage());
                }
        });

        Button returnB = new Button("Return");
        returnB.setLayoutX(700);
        returnB.setLayoutY(800);
        returnB.setPrefWidth(200);
        returnB.setPrefHeight(60);
        returnB.setFont(new Font("Arial", 20));

        // >>> Wróć do sceny głównej po kliknięciu
        returnB.setOnAction(e -> stage.setScene(mainScene));

        root.getChildren().addAll(text, line, imageview, button, returnB, imageFileButton2, buttonDeCode, fileNameField, saveButton);
        return scene;
    }

    private File imageToDecypher;

    private void showImageWindow(Button sourceButton) {
    File file = (File) sourceButton.getUserData();
    if (file == null || !file.exists()) return;

    Stage popupStage = new Stage();
    popupStage.setTitle("Obraz: " + file.getName());

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

    // Skalowanie maksymalne (np. do 1000x800)
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
