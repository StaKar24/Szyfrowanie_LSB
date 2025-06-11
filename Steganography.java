import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class Steganography {
    public static CryptoManager cryptoManager = new CryptoManager();
    public static void hideMessage(BufferedImage image, String message, String outputPath) throws IOException {

        try{
        message = cryptoManager.encrypt(message, cryptoManager.generateKey(), cryptoManager.generateIV());
        }catch(Exception e){}
        message += '\0'; // dodajemy znak końca wiadomości

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
                    // Ukrywamy bit w nieznaczącym bicie koloru niebieskiego
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

    public static String extractMessage(BufferedImage image) {
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
                    if (currentByte == 0) break outer; // znak końca
                    out.write(currentByte);
                    currentByte = 0;
                    bitIndex = 0;
                }
            }
        }

        String crypto_message=  out.toString(StandardCharsets.UTF_8);
        try {
            crypto_message = cryptoManager.decrypt(crypto_message, CryptoManager.loadKey("secret.key"), CryptoManager.loadIV("secret.iv"));
        }catch(Exception e){}
        return crypto_message;
    }
}
