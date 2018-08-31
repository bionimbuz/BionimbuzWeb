package app.models;

public class SecureCoordinatorAccess {

    private String token;    
    private String refreshTokenUrl;
    
    public SecureCoordinatorAccess(String token, String refreshTokenURl) {
        this.token = token;
        this.refreshTokenUrl = refreshTokenURl;
    }
    
    public final String getToken() {
        return token;
    }
    public final void setToken(String token) {
        this.token = token;
    }
    public final String getRefreshTokenUrl() {
        return refreshTokenUrl;
    }
    public final void setRefreshTokenUrl(String refreshTokenURl) {
        this.refreshTokenUrl = refreshTokenURl;
    }    
}
