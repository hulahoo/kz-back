package kz.uco.tsadv.exceptionHandler;

import com.haulmont.bpm.exception.BpmException;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.Frame;
import com.haulmont.cuba.gui.exception.AbstractGenericExceptionHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.inject.Inject;

@Component("bpm_exception")
public class BpmExceptionHandler extends AbstractGenericExceptionHandler {

    @Inject
    private Messages messages;

    public BpmExceptionHandler() {
        super(BpmException.class.getName());
    }

    @Override
    protected void doHandle(String className, String message, @Nullable Throwable throwable, WindowManager windowManager) {
        windowManager.showNotification(getMessage(message), Frame.NotificationType.ERROR);
    }

    protected String getMessage(String message) {
        if (message.matches("^(procTask)(.*)(already completed)$")) {
            message = "procTaskCompletedException";
        }
        return messages.getMessage(this.getClass(), message);
    }
}
