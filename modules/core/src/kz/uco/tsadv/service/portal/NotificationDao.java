package kz.uco.tsadv.service.portal;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.DataManager;
import kz.uco.uactivity.entity.Activity;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

@Component
public class NotificationDao {

    @Inject
    protected DataManager dataManager;

    public static final String NOTIFICATION_CODE = "NOTIFICATION";

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
                        "       and e.status = @enum(uco.uactivity.entity.StatusEnum.active) " +
                        "       and e.type.code %s :code", onlyNotification ? '=' : "<>"))
                .setParameters(ParamsMap.of("assignedUserId", userId, "code", NOTIFICATION_CODE))
                .view("portal-activity")
                .firstResult(firstResult)
                .maxResults(limit)
                .list();
    }
}
