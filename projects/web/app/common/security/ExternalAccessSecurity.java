package common.security;


import java.sql.Date;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import play.Play;
import play.libs.Time;

public class ExternalAccessSecurity {

    private static String EXPIRATION_TIME_CONF = Play.configuration.getProperty("application.token.maxAge");
    private static String SECRET = Play.secretKey;
    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String HEADER_STRING = "Authorization";
    
    private IIdentityChecker checker;
    
    public interface IIdentityChecker {
        boolean check(final String identity);
    }
    
    public ExternalAccessSecurity() {
        this(null);
    }
    
    public ExternalAccessSecurity(final IIdentityChecker checker) {
        this.checker = checker;
    }
        
    public String getToken(final String identity) {
        long EXPIRATION_TIME = (Time.parseDuration(EXPIRATION_TIME_CONF) * 1000l);        
        String JWT = Jwts.builder()
                .setSubject(identity)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, SECRET)
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
        
        return Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
                .getBody()
                .getSubject();
    }
    
    public String refreshToken(final String token) throws 
        UnsupportedJwtException, 
        MalformedJwtException, 
        SignatureException, 
        IllegalArgumentException{
        
        if (token == null) {
            return null;
        }
        
        String identity = "";
        try {
            identity = checkToken(token);            
        } catch (ExpiredJwtException e) {
            identity = e.getClaims().getSubject();
        }
        
        if(!checkIdentity(identity))
            return null;        
        return getToken(identity);
    }
    
    private boolean checkIdentity(final String identity) {
        if(checker == null)
            return true;
        return checker.check(identity);
    }
    
}
