package kz.uco.tsadv.listener;

import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.PersistenceTools;
import com.haulmont.cuba.core.listener.AfterInsertEntityListener;
import com.haulmont.cuba.core.listener.AfterUpdateEntityListener;
import kz.uco.base.service.NotificationSenderAPIService;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.modules.administration.TsadvUser;
import kz.uco.tsadv.modules.learning.model.BudgetRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

@Component("tsadv_BudgetRequestListener")
public class BudgetRequestListener implements AfterInsertEntityListener<BudgetRequest>, AfterUpdateEntityListener<BudgetRequest> {
    private static final Logger log = LoggerFactory.getLogger(BudgetRequestListener.class);
    @Inject
    private Persistence persistence;

    @Inject
    private NotificationSenderAPIService notificationSenderAPIService;

    @Inject
    private CommonService commonService;

    @Override
    public void onAfterInsert(BudgetRequest entity, Connection connection) {
        PersistenceTools tools = persistence.getTools();
        if (tools.getDirtyFields(entity).contains("status") && "APPROVED".equals(entity.getStatus().getCode()))
            sendNotification(entity);
    }


    @Override
    public void onAfterUpdate(BudgetRequest entity, Connection connection) {
        PersistenceTools tools = persistence.getTools();
        if (tools.getDirtyFields(entity).contains("status") && "APPROVED".equals(entity.getStatus().getCode()))
            sendNotification(entity);
    }

    private void sendNotification(BudgetRequest budgetRequest) {
        String notificationCode = "budgetRequest.manager.notification";

        Map<String, Object> params = new HashMap<>();
        params.put("budgetRequest", budgetRequest);

        Map<String, Object> userParams = new HashMap<>();
        userParams.put("initiatorPersonGroupId", budgetRequest.getInitiatorPersonGroup().getId());

        for (TsadvUser user : commonService.getEntities(TsadvUser.class,
                "select e " +
                        "    from tsadv$UserExt e " +
                        "   where e.personGroup.id = :initiatorPersonGroupId ",
                userParams,
                "user.browse")) {
            params.put("user", user);
            notificationSenderAPIService.sendParametrizedNotification(notificationCode, user, params);
        }
    }
}