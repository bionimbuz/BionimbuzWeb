package app.models;

public class SecureCoordinatorAccess {

    private String token;
    private String refreshTokenUrl;

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    // Constructors.
    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public SecureCoordinatorAccess() {
        super();
    }

    public SecureCoordinatorAccess(final String token, final String refreshTokenURl) {
        super();
        this.token = token;
        this.refreshTokenUrl = refreshTokenURl;
    }

    public final String getToken() {
        return this.token;
    }

    public final void setToken(final String token) {
        this.token = token;
    }

    public final String getRefreshTokenUrl() {
        return this.refreshTokenUrl;
    }

    public final void setRefreshTokenUrl(final String refreshTokenURl) {
        this.refreshTokenUrl = refreshTokenURl;
    }
}
