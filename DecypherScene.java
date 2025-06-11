import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class DecypherScene {
    public Scene getScene(Stage stage, Scene mainScene) {
        Group root = new Group();
        Scene scene = new Scene(root, 1000, 1000, Color.LIGHTGREY);

        Image icon = new Image("ikonka.png");
        stage.getIcons().add(icon);        
        stage.setTitle("Secret Message here too2");

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

            if(response == JFileChooser.APPROVE_OPTION){
                File file = new File(imageChooser.getSelectedFile().getAbsolutePath());
                System.out.println(file);;
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

        root.getChildren().addAll(text, line, imageview, button, returnB);
        return scene;
    }
}
