package app.common;

public class ControllerRoutes {
    public static final String UPLOAD = "/upload";
    public static final String SPACES_NAME_FILE_UPLOAD = "/spaces/{name:.+}/file/{file_name:.+}/upload";

    public static final String DOWNLOAD = "/upload";
    public static final String SPACES_NAME_FILE_DOWNLOAD = "/spaces/{name:.+}/file/{file_name:.+}/download";
}
