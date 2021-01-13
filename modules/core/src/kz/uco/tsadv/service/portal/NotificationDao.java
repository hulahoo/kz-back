package kz.uco.tsadv.service.portal;

import com.haulmont.cuba.core.Persistence;
import kz.uco.uactivity.entity.Activity;
import kz.uco.uactivity.entity.StatusEnum;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

@Component
public class NotificationDao {

    @Inject
    private Persistence persistence;

    List<Object[]> loadTasksAndNotifications(UUID userId, Integer activityStatus, String locale, int limit) {
        final String notificationCode = "NOTIFICATION";
        final String taskCode = "KPI_APPROVE";

        return persistence.callInTransaction(em -> em.createNativeQuery(String.format("" +
                        "WITH temp_tasks AS (SELECT a.id, " +
                        "                           a.name_%s AS name, " +
                        "                           a.create_ts, " +
                        "                           at.code " +
                        "                    FROM uactivity_activity a " +
                        "                             INNER JOIN uactivity_activity_type at ON a.type_id = at.id " +
                        "                        AND a.delete_ts IS NULL " +
                        "                        AND at.delete_ts IS NULL " +
                        "                        AND a.assigned_user_id = ?1 " +
                        "                        AND a.status = ?2 " +
                        "                        AND at.code = ?3 " +
                        "                    ORDER BY a.create_ts desc " +
                        "                    LIMIT %d), " +
                        "     temp_notifications AS (SELECT a.id, " +
                        "                                   a.name_%s AS name, " +
                        "                                   a.create_ts, " +
                        "                                   at.code " +
                        "                            FROM uactivity_activity a " +
                        "                                     INNER JOIN uactivity_activity_type at ON a.type_id = at.id " +
                        "                                AND a.delete_ts IS NULL " +
                        "                                AND at.delete_ts IS NULL " +
                        "                                AND a.assigned_user_id = ?1 " +
                        "                                AND a.status = ?2 " +
                        "                                AND at.code = ?4 " +
                        "                            ORDER BY a.create_ts desc " +
                        "                            LIMIT %d) " +
                        "SELECT * " +
                        "FROM temp_tasks " +
                        "UNION ALL " +
                        "SELECT * " +
                        "FROM temp_notifications;",
                locale,
                limit,
                locale,
                limit))
                .setParameter(1, userId)
                .setParameter(2, activityStatus)
                .setParameter(3, notificationCode)
                .setParameter(4, taskCode)
                .getResultList());
    }
}
