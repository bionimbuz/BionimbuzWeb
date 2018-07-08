package app.common.exceptions;

public class VersionException extends Exception{

    private static final long serialVersionUID = 1L;

    public VersionException(String message) {
        super(message);
    }
}
