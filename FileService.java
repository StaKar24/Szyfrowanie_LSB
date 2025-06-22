import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Klasa FileService zawiera metody do obslugi plikow tekstowych i graficznych.
 * Umozliwia wczytywanie i zapisywanie obrazow (PNG, BMP) oraz tekstu (TXT).
 */
public class FileService {

    /**
     * Sprawdza poprawnosc pliku: czy istnieje, czy jest plikiem oraz czy ma dozwolone rozszerzenie.
     *
     * @param file plik do sprawdzenia
     * @param allowedExtensions dozwolone rozszerzenia (np. ".png", ".txt")
     * @throws IOException gdy plik nie istnieje, nie jest plikiem lub ma niedozwolone rozszerzenie
     */
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
            throw new IOException("Nieprawidlowy format pliku. Dozwolone rozszerzenia: " + String.join(", ", allowedExtensions));
        }
    }

    /**
     * Wczytuje obraz z pliku (dozwolone formaty: PNG, BMP).
     *
     * @param file plik z obrazem
     * @return wczytany obraz jako BufferedImage
     * @throws IOException gdy plik jest niepoprawny lub wystapi blad odczytu
     */
    public static BufferedImage loadImage(File file) throws IOException {
        validateFile(file, ".png", ".bmp");
        return ImageIO.read(file);
    }

    /**
     * Zapisuje obraz do pliku w zadanym formacie (PNG lub BMP).
     *
     * @param image obraz do zapisania
     * @param outputFile plik wyjsciowy
     * @param formatName nazwa formatu ("png" lub "bmp")
     * @throws IOException gdy zapis sie nie powiedzie lub format jest nieobslugiwany
     */
    public static void saveImage(BufferedImage image, File outputFile, String formatName) throws IOException {
        if (image == null) throw new IllegalArgumentException("Obraz nie moze byc null.");
        if (outputFile == null) throw new IllegalArgumentException("Plik wyjsciowy nie moze byc null.");

        if (!formatName.equalsIgnoreCase("png") && !formatName.equalsIgnoreCase("bmp")) {
            throw new IOException("Nieobslugiwany format obrazu: " + formatName);
        }

        ImageIO.write(image, formatName.toLowerCase(), outputFile);
    }

    /**
     * Wczytuje tekst z pliku tekstowego (TXT).
     *
     * @param file plik tekstowy
     * @return zawartosc pliku jako String
     * @throws IOException gdy wystapi blad odczytu lub plik ma nieprawidlowe rozszerzenie
     */
    public static String loadText(File file) throws IOException {
        validateFile(file, ".txt");

        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        }
        return sb.toString().trim(); // Usuniecie ostatniego entera
    }

    /**
     * Zapisuje tekst do pliku tekstowego.
     *
     * @param text tekst do zapisania
     * @param file plik wyjsciowy
     * @throws IOException gdy zapis sie nie powiedzie
     */
    public static void saveText(String text, File file) throws IOException {
        if (file == null) throw new IllegalArgumentException("Plik nie moze byc null.");
        if (text == null) throw new IllegalArgumentException("Tekst nie moze byc null.");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            writer.write(text);
        }
    }
}
