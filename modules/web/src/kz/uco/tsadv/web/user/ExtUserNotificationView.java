package kz.uco.tsadv.web.user;

import com.haulmont.cuba.core.entity.SendingMessage;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.VBoxLayout;
import kz.uco.base.entity.core.notification.SendingNotification;
import kz.uco.base.web.user.UserNotificationView;
import kz.uco.uactivity.entity.Activity;
import kz.uco.uactivity.entity.StatusEnum;
import kz.uco.uactivity.service.ActivityService;

import javax.inject.Inject;
import java.util.Map;

public class ExtUserNotificationView extends UserNotificationView {
    @WindowParam
    protected Activity activity;

    @Inject
    private ActivityService activityService;
    @Inject
    private Metadata metadata;

    @Inject
    private VBoxLayout contentVBox;
    boolean isRealSendingNotification = true;

    @Override
    public void init(Map<String, Object> params) {
        contentVBox.remove(notificationBodyLabel);
        if (notification != null) {
            notificationDs.setItem(notification);
        } else if (notificationMessagePojo != null) {
            SendingNotification sendingNotification = metadata.create(SendingNotification.class);
            SendingMessage sendingMessage = metadata.create(SendingMessage.class);
            sendingMessage.setCaption(notificationMessagePojo.getNotificationHeader());
            sendingMessage.setContentText(notificationMessagePojo.getNotificationBody());
            sendingNotification.setSendingMessage(sendingMessage);
            notificationDs.setItem(sendingNotification);
            isRealSendingNotification = false;
        }
    }

    @Override
    public void ok() {
        SendingNotification notification = notificationDs.getItem();
        if (isRealSendingNotification) {
            notification.setReaded(true);
            dataManager.commit(notification);
        }
        activityService.setActivityStatus(activity.getId(), StatusEnum.done);

        close("ok", true);
    }
}