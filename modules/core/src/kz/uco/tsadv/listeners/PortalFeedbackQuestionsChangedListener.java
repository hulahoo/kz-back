package kz.uco.tsadv.listeners;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.app.FileStorageAPI;
import com.haulmont.cuba.core.app.events.EntityChangedEvent;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.entity.contracts.Id;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.EmailAttachment;
import com.haulmont.cuba.core.global.FileStorageException;
import com.haulmont.cuba.core.global.View;
import com.haulmont.reports.app.service.ReportService;
import kz.uco.base.service.NotificationSenderAPIService;
import kz.uco.base.service.NotificationService;
import kz.uco.tsadv.config.FrontConfig;
import kz.uco.tsadv.modules.learning.model.PortalFeedbackQuestions;
import kz.uco.uactivity.entity.ActivityType;
import kz.uco.uactivity.entity.StatusEnum;
import kz.uco.uactivity.entity.WindowProperty;
import kz.uco.uactivity.service.ActivityService;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.*;

@Component("tsadv_PortalFeedbackQuestionsChangedListener")
public class PortalFeedbackQuestionsChangedListener {

    @Inject
    protected DataManager dataManager;
    @Inject
    protected NotificationService notificationService;
    protected SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
    @Inject
    private FrontConfig frontConfig;
    @Inject
    private FileStorageAPI fileStorageAPI;
    @Inject
    private ReportService reportService;
    @Inject
    private NotificationSenderAPIService notificationSenderAPIService;
    @Inject
    private ActivityService activityService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void afterCommit(EntityChangedEvent<PortalFeedbackQuestions, UUID> event) {
        if (event.getType().equals(EntityChangedEvent.Type.CREATED)) {
            Id<PortalFeedbackQuestions, UUID> entityId = event.getEntityId();
            PortalFeedbackQuestions portalFeedbackQuestions = dataManager.load(entityId).view("portalFeedbackQuestions.edit").one();

            Map<String, Object> map = new HashMap<>();
            map.put("createTs", format.format(portalFeedbackQuestions.getCreateTs()));
            map.put("fullName", portalFeedbackQuestions.getUser().getPersonGroup().getFullName());
            map.put("userName", portalFeedbackQuestions.getUser().getLogin());
            map.put("company", portalFeedbackQuestions.getUser().getPersonGroup().getCompany().getCode());
            map.put("category", portalFeedbackQuestions.getPortalFeedback() != null
                    && portalFeedbackQuestions.getPortalFeedback().getCategory() != null
                    ? portalFeedbackQuestions.getPortalFeedback().getCategory().getLangValue()
                    : "");
            map.put("topic", portalFeedbackQuestions.getTopic());
            map.put("text", portalFeedbackQuestions.getText());
            map.put("req_type", portalFeedbackQuestions.getType().getLangValue());
            String full=portalFeedbackQuestions.getUser().getPersonGroup().getAssignments().get(0).getPositionGroup().getPosition().getPositionFullNameLang1();
            map.put("position",full);
            EmailAttachment[] emailAttachments = new EmailAttachment[portalFeedbackQuestions.getFiles().size()];
            if(portalFeedbackQuestions.getFiles()!=null) {
                    emailAttachments = getEmailAttachments(portalFeedbackQuestions.getFiles(), emailAttachments);
            }
            notificationSenderAPIService.sendParametrizedNotification("portal.admin.feedback",
                    portalFeedbackQuestions.getPortalFeedback().getEmail(),map, emailAttachments);

        }
    }

    protected EmailAttachment[] getEmailAttachments(List<FileDescriptor> fileDescriptor, EmailAttachment[] emailAttachments) {
        try {
            for(int i=0;i<fileDescriptor.size();i++) {
                EmailAttachment emailAttachment = new EmailAttachment(fileStorageAPI.loadFile(fileDescriptor.get(i)), fileDescriptor.get(i).getName());
                emailAttachments[i]=emailAttachment;
            }
        } catch (FileStorageException e) {
            e.printStackTrace();
        }
        return emailAttachments;
    }
}