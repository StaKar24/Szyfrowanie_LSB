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
import java.util.Scanner;

/**
 * Klasa CryptoManager obsluguje szyfrowanie i deszyfrowanie tekstu
 * przy uzyciu algorytmu AES w trybie CBC z wypelnieniem PKCS5.
 */
public class CryptoManager {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";

    /**
     * Generuje losowy 128-bitowy klucz AES i zapisuje go do pliku w formacie Base64.
     *
     * @param fileName nazwa pliku, na podstawie ktorej zostanie utworzona nazwa pliku z kluczem (np. plik + secret.key)
     * @return wygenerowany klucz AES
     * @throws Exception w przypadku bledu generowania lub zapisu klucza
     */
    public SecretKey generateKey(String fileName) throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
        keyGen.init(128);
        SecretKey key = keyGen.generateKey();
        String base64Key = Base64.getEncoder().encodeToString(key.getEncoded());

        Scanner scanner = new Scanner(fileName).useDelimiter("\\.");
        try (FileOutputStream fos = new FileOutputStream(scanner.next() + "secret.key")) {
            fos.write(base64Key.getBytes());
        }
        return key;
    }

    /**
     * Generuje losowy 16-bajtowy wektor inicjalizujacy (IV) i zapisuje go do pliku.
     *
     * @param fileName nazwa pliku, na podstawie ktorej zostanie utworzona nazwa pliku z IV (np. plik + secret.iv)
     * @return wygenerowany wektor inicjalizujacy
     * @throws Exception w przypadku bledu generowania lub zapisu IV
     */
    public IvParameterSpec generateIV(String fileName) throws Exception {
        byte[] iv = new byte[16]; // AES block size
        new SecureRandom().nextBytes(iv);

        Scanner scanner = new Scanner(fileName).useDelimiter("\\.");
        String base64IV = Base64.getEncoder().encodeToString(iv);
        try (FileOutputStream fos = new FileOutputStream(scanner.next() + "secret.iv")) {
            fos.write(base64IV.getBytes());
        }

        return new IvParameterSpec(iv);
    }

    /**
     * Szyfruje podany tekst przy uzyciu podanego klucza i IV.
     *
     * @param plainText tekst jawny do zaszyfrowania
     * @param key klucz AES
     * @param iv wektor inicjalizujacy
     * @return tekst zaszyfrowany zakodowany w Base64
     * @throws Exception w przypadku bledu szyfrowania
     */
    public String encrypt(String plainText, SecretKey key, IvParameterSpec iv) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encrypted);
    }

    /**
     * Deszyfruje tekst zakodowany w Base64 przy uzyciu podanego klucza i IV.
     *
     * @param encryptedBase64 zaszyfrowany tekst w formacie Base64
     * @param key klucz AES
     * @param iv wektor inicjalizujacy
     * @return odszyfrowany tekst jawny
     * @throws Exception w przypadku bledu deszyfrowania
     */
    public String decrypt(String encryptedBase64, SecretKey key, IvParameterSpec iv) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        byte[] decoded = Base64.getDecoder().decode(encryptedBase64);
        byte[] decrypted = cipher.doFinal(decoded);
        return new String(decrypted, StandardCharsets.UTF_8);
    }

    /**
     * Wczytuje klucz AES z pliku zakodowanego w Base64.
     *
     * @param filename sciezka do pliku z kluczem
     * @return wczytany klucz AES
     * @throws Exception w przypadku bledu odczytu lub dekodowania
     */
    public static SecretKey loadKey(String filename) throws Exception {
        byte[] encoded = Files.readAllBytes(Paths.get(filename));
        byte[] decodedKey = Base64.getDecoder().decode(encoded);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    }

    /**
     * Wczytuje wektor inicjalizujacy z pliku zakodowanego w Base64.
     *
     * @param filename sciezka do pliku z IV
     * @return wczytany wektor inicjalizujacy
     * @throws Exception w przypadku bledu odczytu lub dekodowania
     */
    public static IvParameterSpec loadIV(String filename) throws Exception {
        byte[] encoded = Files.readAllBytes(Paths.get(filename));
        byte[] iv = Base64.getDecoder().decode(encoded);
        return new IvParameterSpec(iv);
    }
}
