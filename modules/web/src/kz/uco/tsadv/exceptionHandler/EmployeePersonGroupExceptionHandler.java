package kz.uco.tsadv.exceptionHandler;

import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.gui.exception.AbstractUiExceptionHandler;
import kz.uco.tsadv.modules.recognition.exceptions.EmployeePersonGroupException;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.inject.Inject;

@Component("tsadv_EmployeePersonGroupExceptionHandler")
public class EmployeePersonGroupExceptionHandler extends AbstractUiExceptionHandler {

    @Inject
    private Messages messages;

    public EmployeePersonGroupExceptionHandler() {
        super(EmployeePersonGroupException.class.getName());
    }

    @Override
    protected void doHandle(String className, String message, @Nullable Throwable throwable, UiContext context) {
        context.getNotifications()
                .create(Notifications.NotificationType.ERROR)
                .withCaption(messages.getMessage("kz.uco.tsadv.web.personaldatarequest", "noEmployee"))
                .show();
    }
}
