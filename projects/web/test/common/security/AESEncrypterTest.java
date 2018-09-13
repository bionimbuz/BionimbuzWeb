package common.security;

import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;

public class AESEncrypterTest {
    
    @Test
    public void testDecription() throws Exception {
        
        AESEncrypter encrypter = 
                new AESEncrypter("Ha2uwrn1jKwmVp50BJ8K0W1LmEiuXzNnlrQuugR4ia6veGCVMMrptuMB1dljXmbU");
        
        String test = "My test string";
        
        byte [] bytesEncrypted = 
                encrypter.encryptBytes(test.getBytes());
        
        AESEncrypter encrypter2 = 
                new AESEncrypter("Ha2uwrn1jKwmVp50BJ8K0W1LmEiuXzNnlrQuugR4ia6veGCVMMrptuMB1dljXmbU");
        
        byte [] bytesDecrypted = 
                encrypter2.decryptBytes(bytesEncrypted);
                
        assertArrayEquals(
            test.getBytes(), 
            bytesDecrypted);        
    }
}