package app.common;

//TODO: replace by ResponseEntity
public class Response <T> {

    public enum Type {
        SUCCESS, ERROR
    }

    /*
     * Class members
     */
    private Type type;
    private String message;
    private T content;

    /*
     * Constructors
     */
    private Response() {
    }    
    private Response(Type type, String message) {
        this.type = type;
        this.message = message;
    }
    private Response(Type type, String message, T content) {
    	this(type, message);    	
        this.content = content;
    }    
    
    public static <T> Response<T> error(final String message) {
        return new Response<T>(Type.ERROR, message);        
    }
    
    public static <T> Response<T> success(final String message, T content) {
        return new Response<T>(Type.SUCCESS, message, content);        
    }    
    
    public static <T> Response<T> success(final String message) {
        return success(message, null);        
    }
    
    public static <T> Response<T> success(T content) {
        return success("", content);        
    }
    
    public static <T> Response<T> success() {
        return success("", null);        
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
    public T getContent() {
        return content;
    }
    public void setContent(T content) {
        this.content = content;
    }
}