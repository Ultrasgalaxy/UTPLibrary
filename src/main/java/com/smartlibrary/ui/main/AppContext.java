package com.smartlibrary.ui.main;

import com.smartlibrary.auth.AuthService;
import com.smartlibrary.facade.LibraryFacade;
import com.smartlibrary.model.User;
import javafx.stage.Stage;

public class AppContext {

    private static final LibraryFacade facade     = new LibraryFacade();
    private static final AuthService   authService = new AuthService();
    private static User currentUser;

    private static double  stageX = Double.NaN, stageY = Double.NaN;
    private static double  stageWidth = 1280, stageHeight = 800;
    private static boolean maximized = false;

    public static LibraryFacade getFacade()          { return facade; }
    public static AuthService   getAuthService()     { return authService; }
    public static User          getCurrentUser()     { return currentUser; }
    public static void          setCurrentUser(User u){ currentUser = u; }
    public static void          logout()             { currentUser = null; }

    public static void saveStageState(Stage stage) {
        maximized = stage.isMaximized();
        if (!maximized) {
            stageX = stage.getX(); stageY = stage.getY();
            stageWidth = stage.getWidth(); stageHeight = stage.getHeight();
        }
    }

    public static void restoreStageState(Stage stage) {
        if (maximized) { stage.setMaximized(true); }
        else {
            if (!Double.isNaN(stageX)) { stage.setX(stageX); stage.setY(stageY); }
            stage.setWidth(stageWidth); stage.setHeight(stageHeight);
        }
    }
}
