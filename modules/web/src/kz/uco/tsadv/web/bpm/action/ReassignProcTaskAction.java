package kz.uco.tsadv.web.bpm.action;

import com.google.common.base.Strings;
//import com.haulmont.bpm.entity.ProcTask;
//import com.haulmont.bpm.form.ProcFormDefinition;
//import com.haulmont.bpm.gui.action.ProcAction;
//import com.haulmont.bpm.service.ProcessMessagesService;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.Window;
import com.haulmont.cuba.gui.components.actions.BaseAction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReassignProcTaskAction extends BaseAction {

    public ReassignProcTaskAction(String id) {
        super(id);
    }

    //    protected List<ProcAction.AfterActionListener> afterListeners = new ArrayList<>();
//
//    protected ProcTask procTask;
//    protected String outcome;
//    protected ProcFormDefinition formDefinition;
//    protected Component.BelongToFrame target;
//    protected final ProcessMessagesService processMessagesService;
//
//    public ReassignProcTaskAction(ProcTask procTask, String outcome, ProcFormDefinition formDefinition, Component.BelongToFrame target) {
//        super("completeTask_" + outcome);
//        DataManager dataManager = AppBeans.get(DataManager.class);
//        this.procTask = dataManager.reload(procTask, "procTask-complete");
//        this.outcome = outcome;
//        this.formDefinition = formDefinition;
//        this.target = target;
//        processMessagesService = AppBeans.get(ProcessMessagesService.class);
//    }
//
    @Override
    public void actionPerform(Component component) {
//        if (formDefinition != null && !Strings.isNullOrEmpty(formDefinition.getName())) {
//            Map<String, Object> formParams = new HashMap<>();
//            formParams.put("formDefinition", formDefinition);
//            formParams.put("procTask", procTask);
//            formParams.put("procInstance", procTask.getProcInstance());
//            formParams.put("caption", ReassignProcTaskAction.this.getCaption());
//            formParams.put("comment", true);
//            formParams.put("outcome", outcome);
//
//            final Window window = target.getFrame().openWindow("UcoBpmStartProcForm", WindowManager.OpenType.DIALOG, formParams);
//            window.addCloseListener(actionId -> {
//                if (Window.COMMIT_ACTION_ID.equals(actionId)) {
//                    fireAfterActionListeners();
//                }
//            });
//        }
    }
//
//    public void addAfterActionListener(ProcAction.AfterActionListener listener) {
//        if (listener == null) return;
//        afterListeners.add(listener);
//    }
//
//    protected void fireAfterActionListeners() {
//        for (ProcAction.AfterActionListener listener : afterListeners) {
//            listener.actionCompleted();
//        }
//    }
//
//    @Override
//    public String getCaption() {
//        Messages messages = AppBeans.get(Messages.class);
//        return messages.getMessage(this.getClass(), "reassign");
//    }
}
