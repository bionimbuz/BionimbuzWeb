package app.models.security;

import app.models.Body;
import org.openstack4j.model.identity.v3.Token;

public class TokenModel extends Body<TokenModel> {
    
    private String token;
    private String identity;
    private Token os4jToken;

    public TokenModel(String token, String identity) {
        super();
        this.token = token;
        this.identity = identity;
    }
    
    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
    public String getIdentity() {
        return identity;
    }
    public void setIdentity(String identity) {
        this.identity = identity;
    }
    public Token getOs4jToken() { return os4jToken; }
    public void setOs4jToken(Token os4jToken) { this.os4jToken = os4jToken; }
}
