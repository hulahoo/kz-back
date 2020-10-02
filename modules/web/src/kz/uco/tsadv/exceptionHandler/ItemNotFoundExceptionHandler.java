package kz.uco.tsadv.exceptionHandler;

import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.Frame;
import com.haulmont.cuba.gui.exception.AbstractGenericExceptionHandler;
import kz.uco.tsadv.exceptions.ItemNotFoundException;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.inject.Inject;

@Component("tsadv_itemNotFoundException")
public class ItemNotFoundExceptionHandler extends AbstractGenericExceptionHandler {

    @Inject
    private Messages messages;

    public ItemNotFoundExceptionHandler() {
        super(ItemNotFoundException.class.getName());
    }

    @Override
    protected void doHandle(String className, String message, @Nullable Throwable throwable, WindowManager windowManager) {
        windowManager.showNotification(translateMessage(message), Frame.NotificationType.ERROR);
    }

    private String translateMessage(String message) {
        String[] messageArr = message.split("/");
        StringBuilder builder = new StringBuilder();
        for (String mess : messageArr) {
            builder.append(messages.getMessage(this.getClass(), mess)).append(' ');
        }
        return builder.toString();
    }
}
