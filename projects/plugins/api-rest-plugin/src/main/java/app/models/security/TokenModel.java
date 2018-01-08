package app.models.security;

import app.models.Body;

public class TokenModel extends Body {
    private String token;
    private String identity;
    
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
}
