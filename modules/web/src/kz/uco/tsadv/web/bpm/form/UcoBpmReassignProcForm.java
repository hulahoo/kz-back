package kz.uco.tsadv.web.bpm.form;

import com.haulmont.bali.util.ParamsMap;
//import com.haulmont.bpm.entity.ProcActor;
//import com.haulmont.bpm.gui.form.standard.StandardProcForm;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.gui.components.PickerField;
import com.haulmont.cuba.gui.components.ValidationException;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import kz.uco.tsadv.modules.administration.UserExt;
import kz.uco.tsadv.service.BpmService;

import javax.inject.Inject;
import java.util.Map;
import java.util.UUID;

public class UcoBpmReassignProcForm /*extends StandardProcForm*/ {
    /*@Inject
    private PickerField reassignUser;
    @Inject
    private CollectionDatasource<ProcActor, UUID> procActorsDs;
    @Inject
    private DataManager dataManager;
    @Inject
    private BpmService bpmService;

    @Override
    public void init(Map<String, Object> params) {
        reassignUser.getLookupAction().setLookupScreenParamsSupplier(() -> ParamsMap.of("EMPLOYEE", true));
        super.init(params);
        procActorsVisible = false;
        procActorsLabel.setVisible(false);
        procActorsFrame.setVisible(false);
        procActorsDs.refresh(ParamsMap.of("procInstance", procTask.getProcInstance().getId()));
    }

    @Override
    public boolean validateAll() {
        try {
            reassignUser.validate();
        } catch (ValidationException e) {
            showNotification(e.getDetailsMessage(), NotificationType.TRAY);
            return false;
        }
        return super.validateAll();
    }

    @Override
    public void onWindowCommit() {
        if (!validateAll()) {
            return;
        }
        addingReassignUserToProcActor();
        if (procAttachmentsVisible) {
//            procAttachmentsFrame.commit();
        }
        close(COMMIT_ACTION_ID);
    }

    protected void addingReassignUserToProcActor() {
        ProcActor currentProcActor = dataManager.reload(procTask.getProcActor(), "procActor-procTaskCreation");
        bpmService.commitReassignProcActor(procTask, currentProcActor, procActorsDs.getItems(), ((UserExt) reassignUser.getValue()));
    }*/
}