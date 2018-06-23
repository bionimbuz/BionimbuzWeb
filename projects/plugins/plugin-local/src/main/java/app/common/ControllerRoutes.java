package app.common;

public class ControllerRoutes {
    public static final String UPLOAD = "/upload";
    public static final String UPLOAD_SPACE = "/upload/{space}";

    public static final String DOWNLOAD = "/upload";
    public static final String DOWNLOAD_SPACE_FILE = "/download/{space}/{file:.+}";
}
