package common.security;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class DesEncrypter {
    
    private static String ENCRYPT_TYPE = "DES/CBC/PKCS5Padding";
    private static String KEY_TYPE = "DES";
    private static byte[] INIT_VECTOR = new byte[]{
            (byte) 0x8E, 0x12, 0x39, 
            (byte) 0x9C, 0x07, 0x72, 0x6F, 0x5A};
    private byte[] buf = new byte[1024];
    private Cipher ecipher;
    private Cipher dcipher;
    
    public DesEncrypter(final String keyStr) throws Exception {        
        byte [] decodedKey = Base64.getDecoder().decode(keyStr); 
        SecretKey key = new SecretKeySpec(decodedKey, KEY_TYPE);      
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

    public byte[] encryptBytes(byte [] bytes) {
        if(bytes == null)
            return null;
        try(ByteArrayOutputStream output = new ByteArrayOutputStream();    
            ByteArrayInputStream input = new ByteArrayInputStream(bytes);){
            encrypt(input, output);                          
            return output.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }        
    }
    
    public byte[] decryptBytes(byte [] bytes) {
        if(bytes == null)
            return null;
        try(ByteArrayOutputStream output = new ByteArrayOutputStream();
            ByteArrayInputStream input = new ByteArrayInputStream(bytes);){                
            decrypt(input, output);                          
            return output.toByteArray();            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }   
}