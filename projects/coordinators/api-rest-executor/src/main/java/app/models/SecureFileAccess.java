package app.models;

public class SecureFileAccess {

    private String serverBaseUrl;
    private String refreshTokenPath;
    private String token;    
    private String downloadUrl;
    private String uploadUrl;    
    
    public final String getServerBaseUrl() {
        return serverBaseUrl;
    }
    public final void setServerBaseUrl(String serverBaseUrl) {
        this.serverBaseUrl = serverBaseUrl;
    }
    public final String getToken() {
        return token;
    }
    public final void setToken(String token) {
        this.token = token;
    }
    public final String getDownloadUrl() {
        return downloadUrl;
    }
    public final void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }
    public final String getUploadUrl() {
        return uploadUrl;
    }
    public final void setUploadUrl(String uploadUrl) {
        this.uploadUrl = uploadUrl;
    }
    public final String getRefreshTokenPath() {
        return refreshTokenPath;
    }
    public final void setRefreshTokenPath(String refreshTokenPath) {
        this.refreshTokenPath = refreshTokenPath;
    }
}
