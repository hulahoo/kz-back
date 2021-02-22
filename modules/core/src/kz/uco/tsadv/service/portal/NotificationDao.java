package kz.uco.tsadv.service.portal;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.security.app.Authenticated;
import kz.uco.uactivity.entity.Activity;
import kz.uco.uactivity.entity.StatusEnum;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Component
public class NotificationDao {

    @Inject
    protected DataManager dataManager;

    public static final String NOTIFICATION_CODE = "NOTIFICATION";

    public static final String TASK_CODE = "TASK";

    List<Activity> loadNotifications(UUID userId, int firstResult, int limit) {
        return getActiveActivityList(userId, firstResult, limit, true);
    }

    public List<Activity> loadTasks(UUID userId, int firstResult, int limit) {
        return getActiveActivityList(userId, firstResult, limit, false);
    }

    protected List<Activity> getActiveActivityList(UUID userId, int firstResult, int limit, boolean onlyNotification) {
        return dataManager.load(Activity.class)
                .query(String.format("select e from uactivity$Activity e " +
                        "   where e.assignedUser.id = :assignedUserId " +
                        "       and e.status = :status " +
                        "       and e.type.code %s :code " +
                        "   order by e.createTs desc ", onlyNotification ? '=' : "<>"))
                .setParameters(ParamsMap.of("assignedUserId", userId,
                        "code", NOTIFICATION_CODE, "status",
                        StatusEnum.active.getId()))
                .view("portal-activity")
                .firstResult(firstResult)
                .maxResults(limit)
                .list();
    }

    @Authenticated
    public Long notificationsCount(UUID userId) {
        return dataManager.getCount(LoadContext.create(Activity.class)
                .setQuery(LoadContext.createQuery("select e from uactivity$Activity e " +
                        "   where e.assignedUser.id = :assignedUserId " +
                        "       and e.status = :status " +
                        "       and e.type.code IN :codes ")
                        .setParameter("assignedUserId", userId)
                        .setParameter("codes", Arrays.asList(NOTIFICATION_CODE, TASK_CODE))
                        .setParameter("status", StatusEnum.active.getId())
                ));
    }
}
