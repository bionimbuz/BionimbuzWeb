package common.security;

import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;

public class DesEncrypterTest {
    
    @Test
    public void testDecription() throws Exception {
        
        DesEncrypter encrypter = new DesEncrypter("W9ybgH+PV2s=");
        
        String test = "My test string";
        
        byte [] bytesEncrypted = 
                encrypter.encryptBytes(test.getBytes());
        
        byte [] bytesDecrypted = 
                encrypter.decryptBytes(bytesEncrypted);
        
        assertArrayEquals(
            test.getBytes(), 
            bytesDecrypted);        
    }
}