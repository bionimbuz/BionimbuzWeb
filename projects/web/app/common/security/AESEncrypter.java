package common.security;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class AESEncrypter {

    private static int ITERATIONS = 65536;
    private static int KEY_SIZE = 256;

    private static byte[] INIT_VECTOR = new byte[]{
            60, 68, -93, -30, -103, 10, 87, 50, 35, 
            -5, -111, -107, -31, -108, 52, 56};
    private static byte[] SALT = new byte[]{
            -17, -65, -67, -17, -65, -67, -17, -65, -67, 
            64, 80, 13, -17, -65, -67, -17, -65, -67, 
            114, -17, -65, -67, 104, 9, 29, 70, 6, 90, 
            21, 117, 72 };    
    private static String KEY_TYPE = "AES";
    private static String ENCRYPT_TYPE = "AES/CBC/PKCS5Padding";
    private static String KEY_FACTORY_ALGORITHM = "PBKDF2WithHmacSHA1";    
    private byte[] buf = new byte[1024];
    private Cipher ecipher;
    private Cipher dcipher;
    
    public AESEncrypter(final String keyStr) throws Exception {      
        SecretKeyFactory skf = SecretKeyFactory.getInstance(KEY_FACTORY_ALGORITHM);
        PBEKeySpec spec = new PBEKeySpec(keyStr.toCharArray(), SALT, ITERATIONS, KEY_SIZE);
        SecretKey secretKey = skf.generateSecret(spec);
        SecretKeySpec key = new SecretKeySpec(secretKey.getEncoded(), KEY_TYPE);

        AlgorithmParameterSpec paramSpec = new IvParameterSpec(INIT_VECTOR);
        ecipher = Cipher.getInstance(ENCRYPT_TYPE);
        dcipher = Cipher.getInstance(ENCRYPT_TYPE);
        ecipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
        dcipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
    }
    
    public void encrypt(final InputStream in, final OutputStream out) throws Exception {
        try(OutputStream cipherOut = new CipherOutputStream(out, ecipher)) {
            int numRead = 0;
            while ((numRead = in.read(buf)) >= 0) {
                cipherOut.write(buf, 0, numRead);
            }
        }
    }

    public void decrypt(final InputStream in, final OutputStream out) throws Exception {
        try(InputStream cipherIn = new CipherInputStream(in, dcipher)){
            int numRead = 0;
            while ((numRead = cipherIn.read(buf)) >= 0) {
                out.write(buf, 0, numRead);
            }
        }
    }    
    
    public byte[] encryptBytes(byte [] plainText) throws Exception{
        if(plainText == null) { 
            return null;
        }
        byte[] encryptedText = ecipher.doFinal(plainText);
        return encryptedText;
    }

    public byte[] decryptBytes(byte[] encryptText) throws Exception {
        byte[] decyrptTextBytes = null;
        if(encryptText == null) { 
            return null;
        }
        try {
            decyrptTextBytes = dcipher.doFinal(encryptText);
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return decyrptTextBytes;
    }
    
}