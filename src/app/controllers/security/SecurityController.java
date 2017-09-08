package controllers.security;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.transaction.SystemException;

import controllers.HomeController;
import controllers.Secure;
import models.UserModel;

public class SecurityController extends Secure.Security {
    
    private static final String SHA512 = "SHA-512";
    private static final String UTF8 = "UTF-8";
    
    public static boolean authenticate(final String username, final String password) throws SystemException {        
        try {
            final UserModel user = UserModel.findByEmail(username);        
            if (user == null) 
                return false;
            
            final String storedPass = user.getPass();
            final String inputPass = getSHA512(password);
    
            if(!storedPass.equals(inputPass))
                return false;

            return true;  
            
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;  
        }        
    }
    
    public static void onAuthenticated() {
        HomeController.index();
    }
    
    public static String getSHA512(final String password) 
            throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance(SHA512);
        byte[] bytes = md.digest(password.getBytes(UTF8));
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }
}

