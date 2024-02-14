package helper;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;
import java.security.spec.KeySpec;
import java.util.Base64;

/**
 * Utiliza AES (Advanced Encryption Standard) para encriptar y
 * PBKDF2 (Password-Based Key Derivation Function) para generar la clave
 * secreta.
 */
public class EncryptionHelper {

    public static String encriptar(String cadena, String claveSecreta) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        SecretKey secretKey = generarClaveSecreta(claveSecreta);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(new byte[16]));
        byte[] datosEncriptados = cipher.doFinal(cadena.getBytes());
        return Base64.getEncoder().encodeToString(datosEncriptados);
    }

    public static String decrypt(String datosEncriptados, String claveSecreta) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        SecretKey secretKey = generarClaveSecreta(claveSecreta);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(new byte[16]));
        byte[] datosDesencriptados = cipher.doFinal(Base64.getDecoder().decode(datosEncriptados));
        return new String(datosDesencriptados);
    }

    private static SecretKey generarClaveSecreta(String claveSecreta) throws GeneralSecurityException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(claveSecreta.toCharArray(), claveSecreta.getBytes(), 65536, 256);
        SecretKey tmp = factory.generateSecret(spec);
        return new SecretKeySpec(tmp.getEncoded(), "AES");
    }
}
