package kz.uco.tsadv.listener;

import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.PersistenceTools;
import com.haulmont.cuba.core.listener.AfterInsertEntityListener;
import com.haulmont.cuba.core.listener.AfterUpdateEntityListener;
import kz.uco.base.notification.NotificationSenderAPI;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.administration.TsadvUser;
import kz.uco.tsadv.modules.learning.model.Budget;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.Case;
import kz.uco.tsadv.service.EmployeeService;
import kz.uco.tsadv.service.TemplateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

@Component("tsadv_BudgetListener")
public class BudgetListener implements AfterInsertEntityListener<Budget>, AfterUpdateEntityListener<Budget> {
    private static final Logger log = LoggerFactory.getLogger(BudgetListener.class);
    @Inject
    private Persistence persistence;

    @Inject
    private NotificationSenderAPI notificationSender;

    @Inject
    private CommonService commonService;
    @Inject
    private TemplateService templateService;
    @Inject
    private EmployeeService employeeService;

    @Override
    public void onAfterInsert(Budget entity, Connection connection) {
        if ("APPROVED".equals(entity.getStatus().getCode()))
            sendNotification(entity);
    }

    @Override
    public void onAfterUpdate(Budget entity, Connection connection) {
        PersistenceTools tools = persistence.getTools();
        if (tools.getDirtyFields(entity).contains("status") && "APPROVED".equals(entity.getStatus().getCode()))
            sendNotification(entity);
    }

    private void sendNotification(Budget budget) {
        String notificationCode = "budget.manager.notification";

        Map<String, Object> params = new HashMap<>();
        params.put("budget", budget);

        Map<String, Object> userParams = new HashMap<>();
        userParams.put("sysdate", CommonUtils.getSystemDate());


        for (TsadvUser user : commonService.getEntities(TsadvUser.class,
                "   select e from tsadv$UserExt e, tsadv$PositionStructure ps, base$AssignmentExt a " +
                        "              where a.positionGroup.id = ps.positionGroup.id " +
                        "              and e.personGroup.id = a.personGroup.id " +
                        "              and :sysdate between ps.startDate and ps.endDate " +
                        "              and :sysdate between a.startDate and a.endDate " +
                        "              and ps.managerFlag = TRUE ",
                userParams,
                "user.browse")) {
            PersonGroupExt personGroup = employeeService.getPersonGroupByUserId(user.getId());
            if (personGroup != null) {
                Case personNameEn = templateService.getCasePersonName(employeeService.getPersonGroupByUserId(user.getId()).getId(), "en", "Nominative");
                Case personNameKz = templateService.getCasePersonName(employeeService.getPersonGroupByUserId(user.getId()).getId(), "kz", "Атау септік");
                Case personNameRu = templateService.getCasePersonName(employeeService.getPersonGroupByUserId(user.getId()).getId(), "ru", "Именительный");

                params.put("personFullNameEn", templateService.getPersonFullName(personNameEn, employeeService.getPersonGroupByUserId(user.getId())));
                params.put("personFullNameKz", templateService.getPersonFullName(personNameKz, employeeService.getPersonGroupByUserId(user.getId())));
                params.put("personFullNameRu", templateService.getPersonFullName(personNameRu, employeeService.getPersonGroupByUserId(user.getId())));

                notificationSender.sendParametrizedNotification(notificationCode, user, params);
            }
        }
    }

}