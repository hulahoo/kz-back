package kz.uco.tsadv.listener;

import com.haulmont.cuba.core.global.UserSessionSource;
import kz.uco.base.events.NotificationRefreshEvent;
import kz.uco.tsadv.beans.NotificationWebSocket;
import kz.uco.tsadv.service.portal.NotificationDao;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.UUID;

@Component(ActivityNotificationEventListener.NAME)
public class ActivityNotificationEventListener {
    public static final String NAME = "tsadv_ActivityNotificationEventListener";

    @Inject
    private UserSessionSource userSessionSource;

    @Inject
    private NotificationDao notificationDao;

    @Inject
    private NotificationWebSocket notificationWebSocket;

    @EventListener
    public void handleNotificationChange(NotificationRefreshEvent event) {
        UUID currentUserId = event.getUserId();
        notificationWebSocket.sendNotificationsCount(notificationDao.notificationsCount(currentUserId), currentUserId);
    }
}