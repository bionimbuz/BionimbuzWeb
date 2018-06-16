package app.exceptions;

public class SingletonAlreadyInitializedException extends Exception {

    public SingletonAlreadyInitializedException(Class<?> clazz) {
        super("Singleton ["+clazz.getSimpleName()+"] already initialized.");
    }

}
