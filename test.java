import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.control.Button;

public class test extends Application{
    public void start(Stage myStage){

        Group root = new Group();
        Scene scene = new Scene(root, 400, 400, Color.LIGHTGREY);

        Image icon = new Image("ikonka.png");
        myStage.getIcons().add(icon);        
        myStage.setTitle("Secret Message here ;)");
        myStage.setWidth(1000);
        myStage.setHeight(1000);
        myStage.setResizable(false);
        //myStage.setX(50);
        //myStage.setY(50);
        //myStage.setFullScreen(true);

        Text text = new Text();
        text.setText("Secret TextFileCoder!");
        text.setX(50);
        text.setY(100);
        text.setFont(Font.font("Verdana", 50));
        text.setFill(Color.BLACK);

        Line line = new Line();
        line.setStartX(55);
        line.setStartY(110);
        line.setEndX(600);
        line.setEndY(110);
        line.setStrokeWidth(5);

        ImageView imageview = new ImageView(icon);
        imageview.setX(50);
        imageview.setY(400);
        imageview.setScaleX(0.5);
        imageview.setScaleY(0.5);

        Button button = new Button("Cypher message");
        button.setLayoutX(700); // pozycja X
        button.setLayoutY(300); // pozycja Y
        button.setPrefWidth(200);
        button.setPrefHeight(60);
        button.setFont(new Font("Arial", 20));

        button.setOnAction(e -> {
        CypherScene cs = new CypherScene();
        Scene newScene = cs.getScene(myStage, scene); // przekazujemy Stage i scenę główną
        myStage.setScene(newScene);
        });

        Button button2 = new Button("Decypher message");
        button2.setLayoutX(700); // pozycja X
        button2.setLayoutY(400); // pozycja Y
        button2.setPrefWidth(200);
        button2.setPrefHeight(60);
        button2.setFont(new Font("Arial", 20));

        button2.setOnAction(e -> {
        DecypherScene cs = new DecypherScene();
        Scene newScene = cs.getScene(myStage, scene); // przekazujemy Stage i scenę główną
        myStage.setScene(newScene);
        });

        root.getChildren().add(text);
        root.getChildren().add(line);
        root.getChildren().add(imageview);
        root.getChildren().add(button);
        root.getChildren().add(button2);

        myStage.setScene(scene);
        myStage.show();
    }
    public static void main(String[] args){
        launch(args);
    }
}