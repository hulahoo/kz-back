package kz.uco.tsadv.service.portal;

import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.global.DataManager;
import kz.uco.uactivity.entity.StatusEnum;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

@Component
public class NotificationDao {

    @Inject
    private Persistence persistence;

    @Inject
    protected DataManager dataManager;

    //todo
    List<Object[]> loadNotifications(UUID userId, StatusEnum activityStatus, String locale, int limit) {
        final String notificationCode = "NOTIFICATION";

        return persistence.callInTransaction(em -> em.createNativeQuery(String.format(
                "" +
                        "SELECT a.id, " +
                        "                           a.name_%s AS name, " +
                        "                           a.create_ts, " +
                        "                           at.code, " +
//                        "                           at.code " +
                        "                           a.REFERENCE_ID " +
                        "                    FROM uactivity_activity a " +
                        "                             INNER JOIN uactivity_activity_type at ON a.type_id = at.id " +
                        "                        AND a.delete_ts IS NULL " +
                        "                        AND at.delete_ts IS NULL " +
                        "                        AND a.assigned_user_id = #userId " +
                        "                        AND a.status = #status " +
                        "                        AND at.code = #code " +
                        "                    ORDER BY a.create_ts desc " +
                        "                    LIMIT %d ",
                locale,
                limit))
                .setParameter("userId", userId)
                .setParameter("status", activityStatus.getId())
                .setParameter("code", notificationCode)
                .getResultList());
    }

    public List<Object[]> loadTasks(UUID userId, StatusEnum activityStatus, String language, int limit) {

        final String notificationCode = "NOTIFICATION";

        return persistence.callInTransaction(em -> em.createNativeQuery(String.format("" +
                        "SELECT a.id, " +
                        "                           a.name_%s AS name, " +
                        "                           a.create_ts, " +
                        "                           at.code, " +
//                        "                           at.code " +
                        "                           a.REFERENCE_ID " +
                        "                    FROM uactivity_activity a " +
                        "                             INNER JOIN uactivity_activity_type at ON a.type_id = at.id " +
                        "                        AND a.delete_ts IS NULL " +
                        "                        AND at.delete_ts IS NULL " +
                        "                        AND a.assigned_user_id = #userId " +
                        "                        AND a.status = #status " +
                        "                        AND at.code <> #code " +
                        "                    ORDER BY a.create_ts desc " +
                        "                    LIMIT %d ",
                language,
                limit))
                .setParameter("userId", userId)
                .setParameter("status", activityStatus.getId())
                .setParameter("code", notificationCode)
                .getResultList());
    }
}
