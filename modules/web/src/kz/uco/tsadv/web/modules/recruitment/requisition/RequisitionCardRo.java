package kz.uco.tsadv.web.modules.recruitment.requisition;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.security.global.UserSession;
import kz.uco.base.common.StaticVariable;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.recruitment.enums.JobRequestStatus;
import kz.uco.tsadv.modules.recruitment.model.JobRequest;
import kz.uco.tsadv.modules.recruitment.model.Requisition;

import javax.inject.Inject;
import java.util.Date;
import java.util.Map;

@SuppressWarnings("all")
public class RequisitionCardRo extends AbstractEditor<Requisition> {

    @Inject
    private FieldGroup fieldGroup1;

    @Inject
    private FieldGroup fieldGroup2;

    @Inject
    private Metadata metadata;

    @Inject
    private DataManager dataManager;

    @Inject
    private UserSession userSession;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        disableFields(fieldGroup1);
        disableFields(fieldGroup2);
    }

    public void respond() {
        PersonGroupExt currentPersonGroup = userSession.getAttribute(StaticVariable.USER_PERSON_GROUP);

        try {
            if (hasJobRequest(getItem(), currentPersonGroup)) {
                showNotification(
                        getMessage("msg.warning.title"),
                        getMessage("widget.respond.has.record"),
                        NotificationType.TRAY);
            } else {
                showOptionDialog(
                        getMessage("msg.warning.title"),
                        getMessage("widget.respond.confirm.text"),
                        MessageType.CONFIRMATION,
                        new Action[]{
                                new DialogAction(DialogAction.Type.YES) {
                                    @Override
                                    public void actionPerform(Component component) {
                                        addJobRequest(getItem(), currentPersonGroup);

                                        showNotification(
                                                getMessage("msg.success.title"),
                                                getMessage("widget.respond.success.text"),
                                                NotificationType.TRAY);
                                    }
                                },
                                new DialogAction(DialogAction.Type.CANCEL)
                        });
            }
        } catch (Exception ex) {
            showNotification(
                    getMessage("msg.error.title"),
                    ex.getMessage(),
                    NotificationType.TRAY);
        }
    }

    private boolean hasJobRequest(Requisition requisition, PersonGroupExt personGroup) {
        LoadContext<JobRequest> loadContext = LoadContext.create(JobRequest.class);
        loadContext.setQuery(LoadContext.createQuery(
                "select e from tsadv$JobRequest e " +
                        "where e.requisition.id = :rId " +
                        "and e.candidatePersonGroup.id = :pgId")
                .setParameter("rId", requisition.getId())
                .setParameter("pgId", personGroup.getId()));
        return dataManager.getCount(loadContext) > 0;
    }

    private void addJobRequest(Requisition requisition, PersonGroupExt personGroup) {
        JobRequest jobRequest = metadata.create(JobRequest.class);
        jobRequest.setCandidatePersonGroup(personGroup);
        jobRequest.setRequestStatus(JobRequestStatus.ON_APPROVAL);
        jobRequest.setRequestDate(new Date());
        jobRequest.setRequisition(requisition);
        dataManager.commit(jobRequest);
    }

    private void disableFields(FieldGroup fieldGroup) {
        for (FieldGroup.FieldConfig fieldConfig : fieldGroup.getFields()) {
            fieldConfig.setEditable(false);
            fieldConfig.setWidth("300px");
        }
    }
}