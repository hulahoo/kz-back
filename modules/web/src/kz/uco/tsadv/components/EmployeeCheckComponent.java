package kz.uco.tsadv.components;

import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.gui.Notifications;
import com.haulmont.cuba.web.AppUI;
import kz.uco.tsadv.service.UserService;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component("tsadv_EmployeeCheckComponent")
public class EmployeeCheckComponent {
    @Inject
    private UserSessionSource userSessionSource;
    @Inject
    private Messages messages;
    @Inject
    private UserService userService;

    public boolean checkIfEmployeeAndSendNotification() {
        if (userService.isEmployee()) {
            return true;
        }
        AppUI ui = AppUI.getCurrent();
        if (ui != null) {
            ui.getNotifications().create(Notifications.NotificationType.ERROR).withCaption(messages.getMessage("kz.uco.tsadv.web.personaldatarequest", "noEmployee")).show();
        }
        return false;
    }
}
