import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Scanner;

/**
 * Klasa Steganography sluzy do ukrywania oraz odczytywania zaszyfrowanych wiadomosci w obrazach przy uzyciu metody LSB.
 */
public class Steganography {

    public static CryptoManager cryptoManager = new CryptoManager();

    /**
     * Ukrywa zaszyfrowana wiadomosc z pliku tekstowego w pliku graficznym przy uzyciu metody LSB.
     *
     * @param imageFile obraz wejsciowy (PNG lub BMP)
     * @param textFilePath sciezka do pliku tekstowego z wiadomoscia
     * @param outputPath sciezka do pliku wynikowego z ukryta wiadomoscia
     * @throws IOException w przypadku problemow z odczytem lub zapisem plikow
     */
    public static void hideMessage(File imageFile, String textFilePath, String outputPath) throws IOException {

        if (!canTextFitInImage(imageFile.getName(), textFilePath)) {
            System.exit(-1);
        }

        BufferedImage image = FileService.loadImage(imageFile);
        String message = FileService.loadText(new File(textFilePath));
        try {
            message = cryptoManager.encrypt(message, cryptoManager.generateKey(outputPath), cryptoManager.generateIV(outputPath));
        } catch (Exception e) {
            // Pomijamy blad szyfrowania
        }
        message += '\0'; // dodajemy znak konca wiadomosci

        byte[] msgBytes = message.getBytes(StandardCharsets.UTF_8);
        int msgIndex = 0;
        int bitIndex = 0;

        outer:
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int rgb = image.getRGB(x, y);

                int red = (rgb >> 16) & 0xFF;
                int green = (rgb >> 8) & 0xFF;
                int blue = rgb & 0xFF;

                if (msgIndex < msgBytes.length) {
                    // Ukrywamy bit w najmniej znaczacym bicie koloru niebieskiego
                    int bit = (msgBytes[msgIndex] >> (7 - bitIndex)) & 1;
                    blue = (blue & 0xFE) | bit;

                    bitIndex++;
                    if (bitIndex == 8) {
                        bitIndex = 0;
                        msgIndex++;
                    }
                } else {
                    break outer;
                }

                int newRGB = (red << 16) | (green << 8) | blue;
                image.setRGB(x, y, newRGB);
            }
        }

        FileService.saveImage(image, new File(outputPath), "png");
    }

    /**
     * Odczytuje i odszyfrowuje wiadomosc ukryta w obrazie przy uzyciu metody LSB.
     *
     * @param imageFile plik graficzny z ukryta wiadomoscia
     * @return odszyfrowana wiadomosc jako String
     * @throws IOException w przypadku bledu odczytu obrazu
     */
    public static String extractMessage(File imageFile) throws IOException {
        BufferedImage image = FileService.loadImage(imageFile);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int currentByte = 0;
        int bitIndex = 0;

        outer:
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int rgb = image.getRGB(x, y);
                int blue = rgb & 0xFF;

                int bit = blue & 1;
                currentByte = (currentByte << 1) | bit;
                bitIndex++;

                if (bitIndex == 8) {
                    if (currentByte == 0) break outer; // znak konca wiadomosci
                    out.write(currentByte);
                    currentByte = 0;
                    bitIndex = 0;
                }
            }
        }

        String crypto_message = out.toString(StandardCharsets.UTF_8);
        try {
            System.out.println(crypto_message);
            Scanner scanner = new Scanner(imageFile.getName()).useDelimiter("\\.");
            String keyFilename = scanner.next();
            crypto_message = cryptoManager.decrypt(
                    crypto_message,
                    CryptoManager.loadKey(keyFilename + "secret.key"),
                    CryptoManager.loadIV(keyFilename + "secret.iv")
            );
        } catch (Exception e) {
            // Pomijamy blad deszyfrowania
        }

        return crypto_message;
    }

    /**
     * Sprawdza, czy tekst moze zmiescic sie w obrazie przy uzyciu metody LSB.
     *
     * @param imagePath sciezka do pliku obrazu
     * @param textFilePath sciezka do pliku tekstowego
     * @return true jesli tekst zmiesci sie w obrazie, w przeciwnym razie false
     */
    public static boolean canTextFitInImage(String imagePath, String textFilePath) {
        try {
            BufferedImage image = ImageIO.read(new File(imagePath));
            int width = image.getWidth();
            int height = image.getHeight();
            int totalPixels = width * height;

            File textFile = new File(textFilePath);
            long textSizeInBytes = Files.size(textFile.toPath());

            long requiredBits = textSizeInBytes * 8;

            return requiredBits <= totalPixels;
        } catch (IOException e) {
            System.err.println("Blad podczas odczytu plikow: " + e.getMessage());
            return false;
        }
    }
}
