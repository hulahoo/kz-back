package kz.uco.tsadv.service.portal;

import com.haulmont.cuba.core.global.UserSessionSource;
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

    //todo need to add fistResult and maxResult
    @Override
    public List<BellNotificationResponsePojo> notifications() {
        final int limit = 10;
        UUID userId = userSessionSource.getUserSession().getUser().getId();

        return notificationDao.loadNotifications(userId, 0, limit)
                .stream()
                .map(this::parseRowToResponse)
                .collect(Collectors.toList());
    }

    //todo need to add fistResult and maxResult
    @Override
    public List<BellNotificationResponsePojo> tasks() {
        final int limit = 10;
        UUID userId = userSessionSource.getUserSession().getUser().getId();

        return notificationDao.loadTasks(userId, 0, limit)
                .stream()
                .map(this::parseRowToResponse)
                .collect(Collectors.toList());
    }

    protected BellNotificationResponsePojo parseRowToResponse(Activity row) {
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
    protected String getLinkByCode(ActivityType activityType) {
        if (NotificationDao.NOTIFICATION_CODE.equals(activityType.getCode())) return null;
        WindowProperty windowProperty = activityType.getWindowProperty();
        if (windowProperty == null || windowProperty.getEntityName() == null) return null;
        String entityName = windowProperty.getEntityName();
        StringBuilder builder = new StringBuilder(entityName.substring(Math.max(entityName.indexOf("_"), entityName.indexOf("$")) + 1));
        builder.setCharAt(0, Character.toLowerCase(builder.charAt(0)));
        return builder.toString();
    }
}