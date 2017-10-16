package app.common;

public class Response {

    public enum Type {
        SUCCESS, ERROR
    }

    /*
     * Class members
     */
    private Type type;
    private String message;

    /*
     * Constructors
     */
    public Response(Type type, String message) {
        this.type = type;
        this.message = message;
    }

    /*
     * Getters and Setters
     */
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public Type getType() {
        return type;
    }
    public void setType(Type type) {
        this.type = type;
    }
}