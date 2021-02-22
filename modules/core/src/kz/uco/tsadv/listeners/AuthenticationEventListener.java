package kz.uco.tsadv.listeners;

import com.haulmont.cuba.security.auth.events.UserLoggedOutEvent;
import kz.uco.tsadv.beans.NotificationWebSocket;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component("tsadv_AuthenticationEventListener")
public class AuthenticationEventListener {

    @Inject
    private NotificationWebSocket notificationWebSocket;

    @EventListener
    public void userLoggedOut(UserLoggedOutEvent event) {
        notificationWebSocket.closeConnection(event.getUserSession().getCurrentOrSubstitutedUser().getId());
    }
}