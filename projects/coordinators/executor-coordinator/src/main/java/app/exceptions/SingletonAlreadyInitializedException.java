package app.exceptions;

public class SingletonAlreadyInitializedException extends Exception {
	private static final long serialVersionUID = 1L;

	public SingletonAlreadyInitializedException(Class<?> clazz) {
        super("Singleton ["+clazz.getSimpleName()+"] already initialized.");
    }

}
