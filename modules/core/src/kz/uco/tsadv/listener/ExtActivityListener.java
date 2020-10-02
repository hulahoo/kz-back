package kz.uco.tsadv.listener;

import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Events;
import com.haulmont.cuba.core.listener.AfterDeleteEntityListener;
import com.haulmont.cuba.core.listener.AfterInsertEntityListener;
import com.haulmont.cuba.core.listener.AfterUpdateEntityListener;
import kz.uco.base.events.NotificationRefreshEvent;
import kz.uco.uactivity.listener.ActivityListener;
import kz.uco.uactivity.entity.Activity;

import java.sql.Connection;

/**
 * @author Alibek Berdaulet
 */
public class ExtActivityListener extends ActivityListener implements
        AfterInsertEntityListener<Activity>,
        AfterUpdateEntityListener<Activity>,
        AfterDeleteEntityListener<Activity> {

    @Override
    public void onAfterDelete(Activity entity, Connection connection) {
        publishNotificationRefreshEvent(entity);
    }

    @Override
    public void onAfterInsert(Activity entity, Connection connection) {
        publishNotificationRefreshEvent(entity);
    }

    @Override
    public void onAfterUpdate(Activity entity, Connection connection) {
        publishNotificationRefreshEvent(entity);
    }

    protected void publishNotificationRefreshEvent(Activity activity) {
        ((Events) AppBeans.get("cuba_Events")).publish(new NotificationRefreshEvent(activity.getAssignedUser().getId()));
    }
}
