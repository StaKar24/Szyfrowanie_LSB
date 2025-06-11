// CypherScene.java
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
import javax.swing.JFileChooser;
import java.io.File;
public class CypherScene {
    public Scene getScene(Stage stage, Scene mainScene) {
        Group root = new Group();
        Scene scene = new Scene(root, 1000, 1000, Color.LIGHTGREY);

        Image icon = new Image("ikonka.png");
        stage.getIcons().add(icon);        
        stage.setTitle("Secret Message here too");

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
                File file = new File(fileChooser.getSelectedFile().getAbsolutePath());
                System.out.println(file);;
            }
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

        root.getChildren().addAll(text, line, imageview, button, button2, returnB, imageview2);
        return scene;
    }
}
