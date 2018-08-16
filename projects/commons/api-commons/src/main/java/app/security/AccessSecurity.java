package app.security;


import java.sql.Date;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

public class AccessSecurity {

    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    
    private String secret;
    private IIdentityChecker checker;
    
    public AccessSecurity(final String secret) {
        this(secret, null);
    }
    
    public AccessSecurity(final String secret, final IIdentityChecker checker) {
        this.secret = secret;
        this.checker = checker;
    }
        
    public String generateToken(final String identity, long expiration) { 
        String JWT = Jwts.builder()
                .setSubject(identity)
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();        
        return JWT;        
    }
    
    public String checkToken(final String token) throws 
        ExpiredJwtException, 
        UnsupportedJwtException, 
        MalformedJwtException, 
        SignatureException, 
        IllegalArgumentException{
        
        if (token == null) {
            return null;
        }

        String identity = Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
                .getBody()
                .getSubject();

        if(!checkIdentity(identity))
            return null; 
        
        return identity;
    }
    
    public String refreshToken(final String token, long expiration) throws 
        UnsupportedJwtException, 
        MalformedJwtException, 
        SignatureException, 
        IllegalArgumentException{
        
        if (token == null) {
            return null;
        }
        
        String identity = "";
        try {
            // No problems detected with token, 
            // it cannot be refreshed until reaching
            // expiration date
            identity = checkToken(token);  
            return token.replace(TOKEN_PREFIX, "");
        } catch (ExpiredJwtException e) {
            identity = e.getClaims().getSubject();            
            if(!checkIdentity(identity))
                return null;        
            return generateToken(identity, expiration);
        }
    }
    
    private boolean checkIdentity(final String identity) {
        if(checker == null)
            return true;
        return checker.check(identity);
    }
    
}
