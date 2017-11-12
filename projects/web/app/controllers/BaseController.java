package controllers;

public class BaseController extends CRUD {

    // Listeners priorities - @Before | @After | @Finally
    // -------
    public static final short MAX_PRIORITY = 0;
    public static final short HIGH_PRIORITY = 10;
    public static final short NORM_PRIORITY = 20;
    public static final short MIN_PRIORITY = 40;
    // HTTP methods
    // ~~~~~~~
    public static final String POST_METHOD = "POST";
    // Generic constants
    // -------
    public static final String CONNECTED_USER = "connectedUser";
}
