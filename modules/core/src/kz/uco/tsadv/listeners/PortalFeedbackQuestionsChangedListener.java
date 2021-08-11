package kz.uco.tsadv.listeners;

import com.haulmont.cuba.core.app.events.EntityChangedEvent;
import com.haulmont.cuba.core.entity.contracts.Id;
import com.haulmont.cuba.core.global.DataManager;
import kz.uco.base.service.NotificationService;
import kz.uco.tsadv.modules.learning.model.PortalFeedbackQuestions;
import kz.uco.tsadv.modules.personal.dto.PersonProfileDto;
import kz.uco.tsadv.service.EmployeeService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component("tsadv_PortalFeedbackQuestionsChangedListener")
public class PortalFeedbackQuestionsChangedListener {

    @Inject
    protected DataManager dataManager;
    @Inject
    protected EmployeeService employeeService;
    @Inject
    protected NotificationService notificationService;

    protected SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void afterCommit(EntityChangedEvent<PortalFeedbackQuestions, UUID> event) {
        if (event.getType().equals(EntityChangedEvent.Type.CREATED)) {
            Id<PortalFeedbackQuestions, UUID> entityId = event.getEntityId();
            PortalFeedbackQuestions portalFeedbackQuestions = dataManager.load(entityId).view("portalFeedbackQuestions.edit").one();

            PersonProfileDto dto = employeeService.personProfile(portalFeedbackQuestions.getUser().getPersonGroup().getId());

            Map<String, Object> map = new HashMap<>();
            map.put("mail", dto.getEmail());
            map.put("phone", dto.getPhone());
            map.put("createTs", format.format(portalFeedbackQuestions.getCreateTs()));
            map.put("fullName", portalFeedbackQuestions.getUser().getPersonGroup().getFullName());
            map.put("userName", portalFeedbackQuestions.getUser().getLogin());
            map.put("company", portalFeedbackQuestions.getPortalFeedback() != null
                    && portalFeedbackQuestions.getPortalFeedback().getCompany() != null
                    ? portalFeedbackQuestions.getPortalFeedback().getCompany().getLangValue()
                    : "");
            map.put("category", portalFeedbackQuestions.getPortalFeedback() != null
                    && portalFeedbackQuestions.getPortalFeedback().getCategory() != null
                    ? portalFeedbackQuestions.getPortalFeedback().getCategory().getLangValue()
                    : "");
            map.put("topic", portalFeedbackQuestions.getTopic());
            map.put("text", portalFeedbackQuestions.getText());

            notificationService.sendNotification("portal.admin.feedback",
                    portalFeedbackQuestions.getPortalFeedback().getEmail(), map, "");
        }
    }
}