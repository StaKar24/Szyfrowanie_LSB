import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.KeyGenerator;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

//Klasa do szyfrowania i odszyfrowywania tekstu za pomocą algorytmu ABS/CBC

public class CryptoManager {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";


    // Generuje losowy 128-bitowy klucz AES
    public SecretKey generateKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
        keyGen.init(128); // 128-bit AES
        SecretKey key = keyGen.generateKey();
        String base64Key = Base64.getEncoder().encodeToString(key.getEncoded());
        //zapisywanie do bliku
        try (FileOutputStream fos = new FileOutputStream("secret.key")) {
            fos.write(base64Key.getBytes());
        }
        return key;
    }

    // Generuje losowy wektor inicjalizujący (IV)
    public IvParameterSpec generateIV() throws Exception {
        byte[] iv = new byte[16]; // AES block size
        new SecureRandom().nextBytes(iv);

        //zapisywanie do pliku
        String base64IV = Base64.getEncoder().encodeToString(iv);
        try (FileOutputStream fos = new FileOutputStream("secret.iv")) {
            fos.write(base64IV.getBytes());
        }

        return new IvParameterSpec(iv);
    }

    // Szyfruje tekst
    public String encrypt(String plainText, SecretKey key, IvParameterSpec iv) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encrypted);
    }

    // Odszyfrowuje tekst
    public String decrypt(String encryptedBase64, SecretKey key, IvParameterSpec iv) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        byte[] decoded = Base64.getDecoder().decode(encryptedBase64);
        byte[] decrypted = cipher.doFinal(decoded);
        return new String(decrypted, StandardCharsets.UTF_8);
    }



    public static SecretKey loadKey(String filename) throws Exception {
        byte[] encoded = Files.readAllBytes(Paths.get(filename));
        byte[] decodedKey = Base64.getDecoder().decode(encoded);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    }

    public static IvParameterSpec loadIV(String filename) throws Exception {
        byte[] encoded = Files.readAllBytes(Paths.get(filename));
        byte[] iv = Base64.getDecoder().decode(encoded);
        return new IvParameterSpec(iv);
    }

}
