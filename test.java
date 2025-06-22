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

/**
 * Glowna klasa aplikacji "Secret TextFileCoder".
 * Tworzy ekran startowy z przyciskami do ekranow inkrypcji i dekrypcji.
 */
public class test extends Application {

    /**
     * Metoda startowa aplikacji JavaFX.
     * Ustawia UI ekranu startowego.
     *
     * @param myStage glowna scena (stage) aplikacji
     */
    public void start(Stage myStage) {

        // Utworzenie kontenera dla elementow UI
        Group root = new Group();
        Scene scene = new Scene(root, 400, 400, Color.LIGHTGREY);

        // Ustawienie ikony i parametrow okna
        Image icon = new Image("ikonka.png");
        myStage.getIcons().add(icon);
        myStage.setTitle("Secret Message here ;)");
        myStage.setWidth(1000);
        myStage.setHeight(1000);
        myStage.setResizable(false);

        // Tytul aplikacji
        Text text = new Text();
        text.setText("Secret TextFileCoder!");
        text.setX(50);
        text.setY(100);
        text.setFont(Font.font("Verdana", 50));
        text.setFill(Color.BLACK);

        // Linia dekoracyjna pod tytulem
        Line line = new Line();
        line.setStartX(55);
        line.setStartY(110);
        line.setEndX(600);
        line.setEndY(110);
        line.setStrokeWidth(5);

        // Obrazek (ikona)
        ImageView imageview = new ImageView(icon);
        imageview.setX(50);
        imageview.setY(400);
        imageview.setScaleX(0.5);
        imageview.setScaleY(0.5);

        // Przycisk do przejscia do szyfrowania
        Button button = new Button("Cypher message");
        button.setLayoutX(700);
        button.setLayoutY(300);
        button.setPrefWidth(200);
        button.setPrefHeight(60);
        button.setFont(new Font("Arial", 20));

        button.setOnAction(e -> {
            CypherScene cs = new CypherScene();
            Scene newScene = cs.getScene(myStage, scene); // przekazujemy Stage i scene glowna
            myStage.setScene(newScene);
        });

        // Przycisk do przejscia do odszyfrowania
        Button button2 = new Button("Decypher message");
        button2.setLayoutX(700);
        button2.setLayoutY(400);
        button2.setPrefWidth(200);
        button2.setPrefHeight(60);
        button2.setFont(new Font("Arial", 20));

        button2.setOnAction(e -> {
            DecypherScene cs = new DecypherScene();
            Scene newScene = cs.getScene(myStage, scene); // przekazujemy Stage i scene glowna
            myStage.setScene(newScene);
        });

        // Dodanie elementow do kontenera
        root.getChildren().add(text);
        root.getChildren().add(line);
        root.getChildren().add(imageview);
        root.getChildren().add(button);
        root.getChildren().add(button2);

        // Ustawienie sceny i pokazanie okna
        myStage.setScene(scene);
        myStage.show();
    }

    /**
     * Metoda glowna - uruchamia aplikacje JavaFX.
     */
    public static void main(String[] args){
        launch(args);
    }
}
