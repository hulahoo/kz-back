package kz.uco.tsadv.service.portal;

import com.google.gson.Gson;
import com.haulmont.cuba.core.global.UserSessionSource;
import kz.uco.tsadv.pojo.BellNotificationResponsePojo;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service(NotificationService.NAME)
public class NotificationServiceBean implements NotificationService {

    private final Gson gson = new Gson();

    @Inject
    private UserSessionSource userSessionSource;

    @Inject
    private NotificationDao notificationDao;

    @Override
    public List<BellNotificationResponsePojo> notifications() {
        final int limit = 3;
        UUID userId = userSessionSource.getUserSession().getUser().getId();
        String language = userSessionSource.getUserSession().getLocale().getLanguage();

        return notificationDao.loadTasksAndNotifications(userId, 10, language, limit).stream()
                .map(this.parseRowToResponse())
                .collect(Collectors.toList());
    }

    protected Function<Object[], BellNotificationResponsePojo> parseRowToResponse() {
        return row -> BellNotificationResponsePojo.BellNotificationResponsePojoBuilder.aBellNotificationResponsePojo()
                .id((UUID) row[0])
                .name((String) row[1])
                .createTs((Date) row[2])
                .code((String) row[3])
                .build();
    }
}