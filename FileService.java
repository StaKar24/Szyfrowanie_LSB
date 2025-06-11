import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class FileService {

    private static void validateFile(File file, String... allowedExtensions) throws IOException {
        if (file == null || !file.exists() || !file.isFile()) {
            throw new FileNotFoundException("Plik nie istnieje lub nie jest plikiem: " + file);
        }

        String name = file.getName().toLowerCase();
        boolean validExtension = false;

        for (String ext : allowedExtensions) {
            if (name.endsWith(ext.toLowerCase())) {
                validExtension = true;
                break;
            }
        }

        if (!validExtension) {
            throw new IOException("Nieprawidłowy format pliku. Dozwolone rozszerzenia: " + String.join(", ", allowedExtensions));
        }
    }

    // Wczytuje obraz z pliku (PNG, BMP)
    public static BufferedImage loadImage(File file) throws IOException {
        validateFile(file, ".png", ".bmp");
        return ImageIO.read(file);
    }

    // Zapisuje obraz do pliku
    public static void saveImage(BufferedImage image, File outputFile, String formatName) throws IOException {
        if (image == null) throw new IllegalArgumentException("Obraz nie może być null.");
        if (outputFile == null) throw new IllegalArgumentException("Plik wyjściowy nie może być null.");

        // Format powinien być "png" lub "bmp"
        if (!formatName.equalsIgnoreCase("png") && !formatName.equalsIgnoreCase("bmp")) {
            throw new IOException("Nieobsługiwany format obrazu: " + formatName);
        }

        ImageIO.write(image, formatName.toLowerCase(), outputFile);
    }

    // Wczytuje tekst z pliku
    public static String loadText(File file) throws IOException {
        validateFile(file, ".txt");

        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        }
        return sb.toString().trim(); // Usunięcie ostatniego entera
    }

    // Zapisuje tekst do pliku
    static public void saveText(String text, File file) throws IOException {
        if (file == null) throw new IllegalArgumentException("Plik nie może być null.");
        if (text == null) throw new IllegalArgumentException("Tekst nie może być null.");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            writer.write(text);
        }
    }


}

