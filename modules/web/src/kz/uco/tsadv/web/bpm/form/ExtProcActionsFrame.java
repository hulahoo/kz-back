package kz.uco.tsadv.web.bpm.form;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.bpm.entity.ProcActor;
import com.haulmont.bpm.entity.ProcInstance;
import com.haulmont.bpm.entity.ProcTask;
import com.haulmont.bpm.form.ProcFormDefinition;
import com.haulmont.bpm.gui.action.CompleteProcTaskAction;
import com.haulmont.bpm.gui.procactions.ProcActionsFrame;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.cuba.security.global.UserSession;
import kz.uco.base.service.common.CommonService;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

public class ExtProcActionsFrame extends ProcActionsFrame {

    @Inject
    private UserSession userSession;

    @Override
    protected void init(String procCode, Entity entity) {
        super.init(procCode, entity);

        if (procTask == null && completeTaskEnabled) {
            procTask = findCurrentProcTask();
            if (isVisibleReassign()) {
                initCompleteTaskReassignUI();
            }
        }

    }

    protected boolean isVisibleReassign() {
        User user = userSession.getUser();
        return procTask != null && procTask.getProcActor() != null
                && (procTask.getProcActor().getUser().getId().equals(user.getId())
                || !procInstance.getCreatedBy().equals(user.getLogin())
                && isNotApprover(procInstance, user));
    }

    protected boolean isNotApprover(ProcInstance procInstance, User user) {
        return AppBeans.get(CommonService.class).getEntities(ProcActor.class,
                "select e from bpm$ProcActor e " +
                        " where e.procInstance.id = :procInstanceId " +
                        "       and e.user.id = :userId ",
                ParamsMap.of("procInstanceId", procInstance.getId(),
                        "userId", user.getId()), View.MINIMAL).isEmpty();
    }

    protected void initCompleteTaskReassignUI() {
        if (taskInfoEnabled)
            initTaskInfoGrid();
        noActionsAvailableLbl.setVisible(false);
        Map<String, ProcFormDefinition> outcomesWithForms = processFormService.getOutcomesWithForms(procTask);
        String key = "reassign";
        if (outcomesWithForms.containsKey(key)) {
            Button actionBtn = componentsFactory.createComponent(Button.class);
            actionBtn.setWidth(buttonWidth);
            CompleteProcTaskAction action = new CompleteProcTaskAction(procTask, key, outcomesWithForms.get(key));
            action.addBeforeActionPredicate(beforeCompleteTaskPredicate);
            action.addAfterActionListener(afterCompleteTaskListener);
            actionBtn.setAction(action);
            actionsBox.add(actionBtn);
            completeProcTaskActions.add(action);
        }

    }

    protected ProcTask findCurrentProcTask() {
        LoadContext<ProcTask> ctx = new LoadContext<>(ProcTask.class);
        ctx.setQueryString("select pt from bpm$ProcTask pt " +
                "where pt.procInstance.id = :procInstance " +
                "and pt.endDate is null")
                .setParameter("procInstance", procInstance.getId());
        ctx.setView("procTask-complete");
        List<ProcTask> result = dataManager.loadList(ctx);
        return result.isEmpty() ? null : result.get(0);
    }
}