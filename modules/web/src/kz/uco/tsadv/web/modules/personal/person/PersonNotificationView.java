package kz.uco.tsadv.web.modules.personal.person;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.data.Datasource;
import kz.uco.base.entity.core.notification.SendingNotification;

import javax.inject.Inject;
import java.util.Map;

public class PersonNotificationView extends AbstractWindow {

    @WindowParam
    private SendingNotification notification;

    @Inject
    private Datasource<SendingNotification> notificationDs;

    @Inject
    private DataManager dataManager;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        if (notification != null) {
            notificationDs.setItem(notification);
        }
    }

    public void ok() {
        SendingNotification notification = notificationDs.getItem();
        if (notification != null) {
            notification.setReaded(true);
            dataManager.commit(notification);
        }

        close("ok", true);
    }

    public void close() {
        close("cancel", true);
    }
}