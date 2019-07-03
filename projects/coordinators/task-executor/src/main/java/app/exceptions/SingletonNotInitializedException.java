package app.exceptions;

public class SingletonNotInitializedException extends Exception {
	private static final long serialVersionUID = 1L;

    public SingletonNotInitializedException(Class<?> clazz) {
        super("Singleton ["+clazz.getSimpleName()+"] not initialized.");
    }

}
