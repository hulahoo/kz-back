package kz.uco.tsadv.web.bpm.form;

import com.haulmont.bpm.gui.form.standard.StandardProcForm;
import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.gui.components.Label;
import com.haulmont.cuba.gui.components.ValidationErrors;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.exceptions.ItemNotFoundException;
import kz.uco.tsadv.modules.personal.model.AbsenceRequest;
import kz.uco.tsadv.modules.personal.model.PositionChangeRequest;
import kz.uco.tsadv.service.AbsenceService;
import kz.uco.tsadv.service.BpmService;
import kz.uco.tsadv.service.DatesService;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UcoBpmStartProcForm extends StandardProcForm {

    protected boolean reassignVisible;
    protected boolean commentVisible;

    @Inject
    protected Label commentLabel;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected CommonService commonService;
    @Inject
    protected AbsenceService absenceService;
    @Inject
    protected Metadata metadata;
    @Inject
    protected BpmService bpmService;

    @Override
    public void init(Map<String, Object> params) {
        procInstance = dataManager.reload(procInstance, "procInstance-start");
        UUID entityId = (UUID) procInstance.getObjectEntityId();
        if (procInstance.getEntityName().contains("AbsenceRequest")) {
            AbsenceRequest absenceRequest = commonService.getEntity(AbsenceRequest.class, entityId);
            boolean isException = !absenceService.bpmRequiredForAbsence(entityId);
            params.put("HR_BUSINESS_PARTNER", isException);

            if (!isException)
                isException = AppBeans.get(DatesService.class)
                        .getFullDaysCount(absenceRequest.getDateFrom(), absenceRequest.getDateTo()) <= 30
                        || !bpmService.isAbsenceTypeLong(entityId);

            params.put("HR_VP", isException);
        }
        MetaClass aClass = metadata.getClass(procInstance.getEntityName());
        if (aClass != null
                && !aClass.equals(metadata.getClass(PositionChangeRequest.class))
                && aClass.getProperty("organizationGroup") != null) {
            Entity entity = commonService.getEntity(aClass.getJavaClass(), entityId,
                    new View(aClass.getJavaClass()).addProperty("organizationGroup"));
            Entity org = entity != null ? entity.getValue("organizationGroup") : null;
            if (org != null) {
                params.put("ORGANIZATION_BUSINESS_PARTNER", org.getId());
            }
        }
        super.init(params);

        commentVisible = formDefinition.getParam(COMMENT_REQUIRED_PARAM) != null || params.containsKey("comment");
        reassignVisible = !procActorsVisible && params.containsKey("outcome") && params.get("outcome").equals("reassign");

        commentLabel.setVisible(commentVisible);
        comment.setVisible(commentVisible);
    }

    @Override
    public void onWindowCommit() {
        if (procActorsVisible) {
            checkActors();
        }

        super.onWindowCommit();
    }

    @Override
    public boolean validateAll() {
        if (procInstance.getStartedBy() == null && isProcessStarted()) {
            close(CLOSE_ACTION_ID, true);
            throw new ItemNotFoundException(messages.getMessage(this.getClass(), "processStarted"));
        }
        return super.validateAll();
    }

    protected boolean isProcessStarted() {
        procInstance = dataManager.reload(procInstance, "procInstance-start");
        return procInstance.getStartDate() != null;
    }

    protected void checkActors() {
        if (procActorsFrame instanceof UcoBpmProcActorsFrame) {
            HashMap<String, Boolean> params = new HashMap<>();
            if (procInstance.getEntityName().contains("AbsenceRequest")) {
                params.put("HR_BUSINESS_PARTNER", !absenceService.bpmRequiredForAbsence(procInstance.getEntity().getEntityId()));
            }
            ((UcoBpmProcActorsFrame) procActorsFrame).checkUsers(params);
        }
    }

    @Override
    public boolean close(String actionId) {
        return close(actionId, true);
    }

    @Override
    protected void postValidate(ValidationErrors errors) {
        super.postValidate(errors);
        if (procActorsFrame instanceof UcoBpmProcActorsFrame) {
            ((UcoBpmProcActorsFrame) procActorsFrame).postValidate(errors);
        }
    }
}
