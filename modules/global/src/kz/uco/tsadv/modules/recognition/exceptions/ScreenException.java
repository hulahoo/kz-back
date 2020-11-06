package kz.uco.tsadv.modules.recognition.exceptions;

public class ScreenException extends RuntimeException {
    private ScreenException(String message) {
        super(message);
    }

    public static ScreenException createNoScreenException(String menuItemId) {
        return new ScreenException("No screen provided in menu item " + menuItemId + ".");
    }

    public static ScreenException createSeveralScreensException() {
        return new ScreenException("Menu item must have one screen provided, but has several screens.");
    }
}
