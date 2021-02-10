package kz.uco.tsadv.service.portal;

import com.google.gson.Gson;
import com.haulmont.cuba.core.global.UserSessionSource;
import kz.uco.tsadv.pojo.BellNotificationResponsePojo;
import kz.uco.uactivity.entity.StatusEnum;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service(NotificationService.NAME)
public class NotificationServiceBean implements NotificationService {

    @Inject
    private UserSessionSource userSessionSource;

    @Inject
    private NotificationDao notificationDao;

    @Override
    public List<BellNotificationResponsePojo> notifications() {
        final int limit = 10;
        UUID userId = userSessionSource.getUserSession().getUser().getId();
        String language = userSessionSource.getUserSession().getLocale().getLanguage();

        return notificationDao.loadNotifications(userId, StatusEnum.active, language, limit).stream()
                .map(this::parseRowToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<BellNotificationResponsePojo> tasks() {
        final int limit = 10;
        UUID userId = userSessionSource.getUserSession().getUser().getId();
        String language = userSessionSource.getUserSession().getLocale().getLanguage();

        return notificationDao.loadTasks(userId, StatusEnum.active, language, limit).stream()
                .map(this::parseRowToResponse)
                .collect(Collectors.toList());
    }

    protected BellNotificationResponsePojo parseRowToResponse(Object[] row) {
        return BellNotificationResponsePojo.BellNotificationResponsePojoBuilder.aBellNotificationResponsePojo()
                .id((UUID) row[0])
                .name((String) row[1])
                .createTs((Date) row[2])
                .code((String) row[3])
                .entityId(row[4] != null ? row[4].toString() : null)
                .link(getLinkByCode((String) row[3]))
                .build();
    }

    //todo
    protected String getLinkByCode(String code) {
        switch (code) {
            case "ABSENCE_REQUEST_APPROVE":
                return "";
            case "CERTIFICATE_REQUEST_APPROVE":
                return "/certificateRequestManagement";
            default:
                return null;
        }
    }
}