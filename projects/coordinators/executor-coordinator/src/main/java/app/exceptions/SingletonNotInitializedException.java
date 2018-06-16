package app.exceptions;

public class SingletonNotInitializedException extends Exception {

    public SingletonNotInitializedException(Class<?> clazz) {
        super("Singleton ["+clazz.getSimpleName()+"] not initialized.");
    }

}
