package kz.uco.tsadv.web.modules.personal.persondocument;

//import com.haulmont.bpm.gui.procactions.ProcActionsFrame;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.PickerField;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.security.global.UserSession;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.administration.UserExt;
import kz.uco.tsadv.modules.personal.dictionary.DicApprovalStatus;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.PersonDocument;
import kz.uco.tsadv.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;
import java.util.Optional;

public class PersonDocumentEdit extends AbstractEditor<PersonDocument> {
    protected static final Logger log = LoggerFactory.getLogger(PersonDocumentEdit.class);

    protected static final String PROCESS_CODE = "personEntityApproval";
    @Inject
    protected CommonService commonService;
    @Inject
    protected Metadata metadata;
    @Inject
    protected DataManager dataManager;

    @Inject
    protected EmployeeService employeeService;

    @Inject
    protected UserSession userSession;

//    @Inject
//    protected ProcActionsFrame procActionsFrame;
    @Inject
    protected Datasource<PersonDocument> personDocumentDs;
    protected boolean isFromSelfService;
    @Named("fieldGroup.personGroup")
    protected PickerField personGroupField;
    protected PersonGroupExt personGroupExt;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        setShowSaveNotification(false);

        isFromSelfService = params.containsKey("fromSelfService");
        if (params.containsKey("personGroup")) {
            personGroupExt = (PersonGroupExt) params.get("personGroup");
        }
    }

    @Override
    protected void initNewItem(PersonDocument item) {
        super.initNewItem(item);
        item.setStatus(commonService.getEntity(DicApprovalStatus.class, "PROJECT"));
        item.setExpiredDate(CommonUtils.getEndOfTime());
        item.setIssueDate(CommonUtils.getSystemDate());
        if (personGroupExt != null) {
            item.setPersonGroup(personGroupExt);
        }

    }

    @Override
    protected boolean preCommit() {
        final PersonDocument item = super.getItem();
        if (item.getExpiredDate() == null) item.setExpiredDate(CommonUtils.getEndOfTime());
        if (item.getIssueDate().compareTo(item.getExpiredDate()) > 0) {
            super.showNotification(this.messages.getMessage("kz.uco.tsadv.web", "dateError"), NotificationType.ERROR);
            return false;
        }
        if (isFromSelfService) {
            Optional.ofNullable(this.userSession.getAttribute("user"))
                    .map(obj -> (UserExt) obj)
                    .map(userExt -> this.employeeService.getPersonGroupByUserId(userExt.getId()))
                    .ifPresent(item::setPersonGroup);
        }
        return true;
    }

    @Override
    public void postInit() {
        initProcActionsFrame();
    }

    private void initProcActionsFrame() {
        /*procActionsFrame.initializer()
                .setBeforeStartProcessPredicate(this::commit)
                .setAfterStartProcessListener(() -> {
                    showNotification(getMessage("PersonDocumentApproval.processStarted"), NotificationType.HUMANIZED);
                    close(COMMIT_ACTION_ID);
                })
                .setAfterClaimTaskListener(() -> {
                    this.commit();
                    initProcActionsFrame();
                })
                .setBeforeCompleteTaskPredicate(this::commit)
                .setAfterCompleteTaskListener(() -> {
                    showNotification(getMessage("PersonDocumentApproval.taskCompleted"), NotificationType.HUMANIZED);
                    close(COMMIT_ACTION_ID);

                })
                .init(PROCESS_CODE, getItem());*/
    }

    @Override
    protected boolean postCommit(boolean committed, boolean close) {
        showNotification(getMessage("person.card.commit.title"), getMessage("person.card.commit.msg"), NotificationType.TRAY);
        return super.postCommit(committed, close);
    }


}