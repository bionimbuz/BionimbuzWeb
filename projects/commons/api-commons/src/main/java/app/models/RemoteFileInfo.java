package app.models;

import java.util.Map;

public class RemoteFileInfo {

    private String url;
    private String method;
    private String name;
    private Map<String, String> headers;

    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getMethod() {
        return method;
    }
    public void setMethod(String method) {
        this.method = method;
    }
    public Map<String, String> getHeaders() {
        return headers;
    }
    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }
    public final String getName() {
        return name;
    }
    public final void setName(String name) {
        this.name = name;
    }
}