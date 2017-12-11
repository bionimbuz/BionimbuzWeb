package controllers.security;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.transaction.SystemException;

import controllers.Secure;
import controllers.adm.HomeController;
import models.MenuModel;
import models.UserModel;
import play.Logger;

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
            Logger.error(e.getMessage(), e);
            return false;  
        }        
    }

    @SuppressWarnings("unused")
    private static boolean check(String path) throws Throwable {        
        if (isConnected()) {
            final UserModel user = UserModel.findByEmail(connected());        
            return MenuModel.containsMenuProfile(path, user.getRole().getId());
        }
        return false;
    }    

    public static boolean isConnected() {
        return session.contains("username");
    }
    
    public static String connected() {
        return session.get("username");
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

