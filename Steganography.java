import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;
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
    // Wczytujemy surowy tekst
    String rawMessage = FileService.loadText(new File(textFilePath));
    String encryptedMessage = null;

    try {
        // Szyfrujemy wiadomość
        encryptedMessage = cryptoManager.encrypt(
            rawMessage,
            cryptoManager.generateKey(outputPath),
            cryptoManager.generateIV(outputPath)
        );
    } catch (Exception e) {
        System.err.println("Błąd szyfrowania: " + e.getMessage());
        System.exit(-1);
    }

    // Teraz sprawdzamy, czy zaszyfrowana i zakodowana wiadomość zmieści się w obrazie
    if (!canTextFitInImage(imageFile.getName(), encryptedMessage)) {
        System.err.println("Zaszyfrowana wiadomość jest za długa, aby zmieścić się w obrazie.");
        System.exit(-1);
    }

    BufferedImage image = FileService.loadImage(imageFile);

    // Konwertujemy zaszyfrowaną wiadomość do bajtów
    byte[] messageBytes = encryptedMessage.getBytes(StandardCharsets.UTF_8);
    int messageLength = messageBytes.length;

    // Dodajemy 4-bajtowy nagłówek z długością wiadomości
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    baos.write(ByteBuffer.allocate(4).putInt(messageLength).array());
    baos.write(messageBytes);
    byte[] payload = baos.toByteArray();

    int msgIndex = 0;
    int bitIndex = 0;

    outer:
    for (int y = 0; y < image.getHeight(); y++) {
        for (int x = 0; x < image.getWidth(); x++) {
            int rgb = image.getRGB(x, y);

            int red   = (rgb >> 16) & 0xFF;
            int green = (rgb >> 8) & 0xFF;
            int blue  = rgb & 0xFF;

            int[] channels = { red, green, blue };

            for (int c = 0; c < 3; c++) {
                if (msgIndex >= payload.length) break outer;

                int bit = (payload[msgIndex] >> (7 - bitIndex)) & 1;
                channels[c] = (channels[c] & 0xFE) | bit;

                bitIndex++;
                if (bitIndex == 8) {
                    bitIndex = 0;
                    msgIndex++;
                }
            }

            int newRGB = (0xFF << 24) | (channels[0] << 16) | (channels[1] << 8) | channels[2];
            image.setRGB(x, y, newRGB);
        }
    }

    FileService.saveImage(image, new File(outputPath), "png");
    System.out.println("Wiadomość ukryta w obrazie: " + outputPath);
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

    int msgLength = -1;
    int msgBytesRead = 0;

    int currentByte = 0;
    int bitIndex = 0;
    int headerBytesRead = 0;
    byte[] header = new byte[4];

    outer:
    for (int y = 0; y < image.getHeight(); y++) {
        for (int x = 0; x < image.getWidth(); x++) {
            int rgb = image.getRGB(x, y);
            int red = (rgb >> 16) & 0xFF;
            int green = (rgb >> 8) & 0xFF;
            int blue = rgb & 0xFF;

            int[] channels = {red, green, blue};
            for (int c = 0; c < 3; c++) {
                int bit = channels[c] & 1;
                currentByte = (currentByte << 1) | bit;
                bitIndex++;

                if (bitIndex == 8) {
                    if (msgLength == -1) {
                        header[headerBytesRead++] = (byte) currentByte;
                        if (headerBytesRead == 4) {
                            msgLength = ByteBuffer.wrap(header).getInt();
                            System.out.println("Długość wiadomości do odczytu: " + msgLength);
                            if (msgLength <= 0) {
                                throw new IOException("Niepoprawna długość wiadomości: " + msgLength);
                            }
                        }
                    } else {
                        out.write(currentByte);
                        msgBytesRead++;
                        if (msgBytesRead == msgLength) {
                            break outer;
                        }
                    }
                    currentByte = 0;
                    bitIndex = 0;
                }
            }
        }
    }

    String base64EncryptedMessage = out.toString(StandardCharsets.UTF_8);
    System.out.println("Odczytany tekst Base64 (zaszyfrowany): " + base64EncryptedMessage);

    try {
        Scanner scanner = new Scanner(imageFile.getName()).useDelimiter("\\.");
        String baseName = scanner.next();

        SecretKey key = CryptoManager.loadKey(baseName + "secret.key");
        IvParameterSpec iv = CryptoManager.loadIV(baseName + "secret.iv");

        CryptoManager cryptoManager = new CryptoManager();
        return cryptoManager.decrypt(base64EncryptedMessage, key, iv);
    } catch (Exception e) {
        System.err.println("Błąd deszyfrowania: " + e.getMessage());
        return base64EncryptedMessage;
    }
}




    /**
 * Metoda zmodyfikowana, aby przyjmować zaszyfrowany Base64 string zamiast ścieżki pliku tekstowego,
 * aby prawidłowo policzyć rozmiar ukrywanej wiadomości.
 */
public static boolean canTextFitInImage(String imagePath, String base64Message) {
    final int BITS_PER_PIXEL = 3;       // 1 bit na każdy z kanałów: R, G, B
    final int HEADER_SIZE_BITS = 32;    // 32-bitowy nagłówek np. na długość danych

    try {
        BufferedImage image = ImageIO.read(new File(imagePath));
        int width = image.getWidth();
        int height = image.getHeight();
        long totalPixels = (long) width * height;

        long messageSizeInBytes = base64Message.getBytes(StandardCharsets.UTF_8).length;
        long requiredBits = (messageSizeInBytes * 8) + HEADER_SIZE_BITS;

        long availableBits = totalPixels * BITS_PER_PIXEL;

        System.out.println("Dostępne bity w obrazie: " + availableBits);
        System.out.println("Wymagane bity na wiadomość: " + requiredBits);

        return requiredBits <= availableBits;
    } catch (IOException e) {
        System.err.println("Błąd podczas odczytu obrazu: " + e.getMessage());
        return false;
    }
}

}