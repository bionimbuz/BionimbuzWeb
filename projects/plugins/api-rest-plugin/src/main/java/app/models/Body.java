package app.models;

public class Body <T> {

    public static final String OK = "OK";
    
    private String message = "";
    private T content = null;
    
    public Body() {   
    }    
    public Body(final String message) {   
        this.message = message;
    }
    public Body(final T content) {
        this.content = content;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public T getContent() {
        return content;
    }
    public void setContent(T content) {
        this.content = content;
    }    

    public static <T2> Body <T2> create(final String message, final T2 content) {
        Body<T2> body = new Body<T2>(content);
        body.setMessage(message);        
        return body;        
    }
    
    public static <T2> Body <T2> create(final T2 content) {
        return create(OK, content);
    }   
}
