package kz.uco.tsadv.listener;

import com.haulmont.cuba.security.entity.User;
import kz.uco.base.listener.abstraction.SendingNotificationListener;

/**
 * @author Alibek Berdaulet
 */
public class ExtSendingNotificationListener extends SendingNotificationListener {
    @Override
    protected void publishNotificationRefreshEvent(User user) {

    }
}
