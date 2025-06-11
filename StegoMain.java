import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class StegoMain {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.print("Podaj ścieżkę do obrazu PNG lub BMP: ");
            String imagePath = scanner.nextLine().trim();
            File imageFile = new File(imagePath);
            if (!imageFile.exists()) {
                System.err.println("Plik obrazu nie istnieje: " + imageFile.getAbsolutePath());
                return;
            }

            BufferedImage image = ImageIO.read(imageFile);
            if (image == null) {
                System.err.println("Nie można wczytać obrazu. Czy to poprawny PNG/BMP?");
                return;
            }

            System.out.print("1. Ukryj wiadomość\n2. Odczytaj wiadomość\nWybierz (1/2): ");
            String choice = scanner.nextLine().trim();

            if (choice.equals("1")) {
                System.out.print("Podaj ścieżkę do pliku tekstowego do ukrycia: ");
                String textFilePath = scanner.nextLine().trim();
                String message = new String(Files.readAllBytes(Paths.get(textFilePath)), "UTF-8");

                System.out.print("Podaj nazwę pliku wynikowego (np. ukryty.png): ");
                String outputPath = scanner.nextLine().trim();

                Steganography.hideMessage(image, message, outputPath);
                System.out.println("Wiadomość została ukryta w obrazie: " + outputPath);

            } else if (choice.equals("2")) {
                String extracted = Steganography.extractMessage(image);
                System.out.println("Odczytana wiadomość:\n" + extracted);

                System.out.print("Zapisz wynik do pliku? (t/n): ");
                String save = scanner.nextLine().trim();
                if (save.equalsIgnoreCase("t")) {
                    System.out.print("Podaj nazwę pliku wyjściowego: ");
                    String outputTextPath = scanner.nextLine().trim();
                    Files.write(Paths.get(outputTextPath), extracted.getBytes("UTF-8"));
                    System.out.println("Zapisano do pliku: " + outputTextPath);
                }

            } else {
                System.out.println("Niepoprawny wybór.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
