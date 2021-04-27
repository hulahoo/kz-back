package kz.uco.tsadv.service.portal;

import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.UserSessionSource;
import kz.uco.base.entity.core.notification.SendingNotification;
import kz.uco.tsadv.modules.performance.model.AssignedPerformancePlan;
import kz.uco.tsadv.pojo.BellNotificationResponsePojo;
import kz.uco.uactivity.entity.Activity;
import kz.uco.uactivity.entity.ActivityType;
import kz.uco.uactivity.entity.WindowProperty;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service(NotificationService.NAME)
public class NotificationServiceBean implements NotificationService {

    @Inject
    private UserSessionSource userSessionSource;

    @Inject
    private NotificationDao notificationDao;

    @Inject
    protected Metadata metadata;

    //todo need to add fistResult and maxResult
    @Override
    public List<BellNotificationResponsePojo> notifications() {
        final int limit = 5;
        UUID userId = userSessionSource.getUserSession().getUser().getId();

        return notificationDao.loadNotifications(userId, 0, limit)
                .stream()
                .map(this::parseNotificationRowToResponse)
                .collect(Collectors.toList());
    }

    //todo need to add fistResult and maxResult
    @Override
    public List<BellNotificationResponsePojo> tasks() {
        final int limit = 5;
        UUID userId = userSessionSource.getUserSession().getUser().getId();

        return notificationDao.loadTasks(userId, 0, limit)
                .stream()
                .map(this::parseTaskRowToResponse)
                .collect(Collectors.toList());
    }

    protected BellNotificationResponsePojo parseNotificationRowToResponse(SendingNotification row) {
        return BellNotificationResponsePojo.BellNotificationResponsePojoBuilder.aBellNotificationResponsePojo()
                .id(row.getId())
                .name(row.getSendingMessage().getCaption())
                .createTs(row.getCreateTs())
//                .code(row.getType().getCode())
//                .entityId(String.valueOf(row.getReferenceId()))
//                .link(getLinkByCode(row.getType()))
                .build();
    }

    protected BellNotificationResponsePojo parseTaskRowToResponse(Activity row) {
        return BellNotificationResponsePojo.BellNotificationResponsePojoBuilder.aBellNotificationResponsePojo()
                .id(row.getId())
                .name(row.getName())
                .createTs(row.getCreateTs())
                .code(row.getType().getCode())
                .entityId(String.valueOf(row.getReferenceId()))
                .link(getLinkByCode(row.getType()))
                .build();
    }

    /**
     * @return entityName without prefix
     * <p>
     * example: entityName = uactivity$ActivityType return value = activityType
     * </p>
     */
    public String getLinkByCode(ActivityType activityType) {
        if (NotificationDao.NOTIFICATION_CODE.equals(activityType.getCode())) return null;
        WindowProperty windowProperty = activityType.getWindowProperty();
        if (windowProperty == null || windowProperty.getEntityName() == null) return null;
        MetaClass aClass = metadata.getClass(windowProperty.getEntityName());
        if (aClass != null && AssignedPerformancePlan.class.isAssignableFrom(aClass.getJavaClass())) return "kpi";
        String entityName = windowProperty.getEntityName();
        StringBuilder builder = new StringBuilder(entityName.substring(Math.max(entityName.indexOf("_"), entityName.indexOf("$")) + 1));
        builder.setCharAt(0, Character.toLowerCase(builder.charAt(0)));
        return builder.toString();
    }
}