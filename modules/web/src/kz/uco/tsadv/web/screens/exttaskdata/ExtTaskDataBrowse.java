package kz.uco.tsadv.web.screens.exttaskdata;

import com.haulmont.addon.bproc.entity.ProcessInstanceData;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.gui.UiComponents;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.Label;
import com.haulmont.cuba.gui.components.PopupView;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.components.data.table.ContainerTableItems;
import com.haulmont.cuba.gui.model.CollectionContainer;
import com.haulmont.cuba.gui.model.DataComponents;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.entity.bproc.ExtTaskData;
import kz.uco.tsadv.entity.dbview.ProcInstanceV;
import kz.uco.tsadv.modules.administration.TsadvUser;
import kz.uco.tsadv.service.BprocService;
import org.springframework.util.CollectionUtils;

import javax.inject.Inject;
import java.util.List;


/**
 * @author Alibek Berdaulet
 */
@UiController("tsadv_ExtTaskData.browse")
@UiDescriptor("ext-task-data-browse.xml")
@LookupComponent("procTasksTable")
public class ExtTaskDataBrowse extends StandardLookup<ExtTaskData> {

    protected ProcInstanceV procInstanceV;
    @Inject
    protected UiComponents uiComponents;
    @Inject
    protected Messages messages;
    @Inject
    protected DataComponents dataComponents;

    public ProcInstanceV getProcInstanceV() {
        return procInstanceV;
    }

    public void setProcInstanceV(ProcInstanceV procInstanceV) {
        this.procInstanceV = procInstanceV;
    }

    @Inject
    protected CollectionContainer<ExtTaskData> extTaskDatasDc;
    @Inject
    protected BprocService bprocService;

    @Subscribe
    protected void onBeforeShow(BeforeShowEvent event) {
        ProcessInstanceData processInstanceData = bprocService.getProcessInstanceData(procInstanceV.getProcessInstanceBusinessKey(), procInstanceV.getProcessDefinitionKey());
        List<ExtTaskData> processTasks = bprocService.getProcessTasks(processInstanceData);
        extTaskDatasDc.setItems(processTasks);
    }

    @SuppressWarnings({"unchecked", "UnstableApiUsage"})
    public Component generateAssignee(ExtTaskData taskData) {
        List<TsadvUser> assigneeOrCandidates = taskData.getAssigneeOrCandidates();
        if (!CollectionUtils.isEmpty(assigneeOrCandidates)) {
            if (assigneeOrCandidates.size() == 1) {
                Label<String> lbl = uiComponents.create(Label.TYPE_STRING);
                lbl.setValue(assigneeOrCandidates.get(0).getFullNameWithLogin());
                return lbl;
            } else {
                PopupView popupView = uiComponents.create(PopupView.class);
                popupView.setMinimizedValue(assigneeOrCandidates.get(0).getFullNameWithLogin() + " +" + (assigneeOrCandidates.size() - 1));

                CollectionContainer<TsadvUser> container = dataComponents.createCollectionContainer(TsadvUser.class);

                container.setItems(assigneeOrCandidates);

                Table<TsadvUser> table = uiComponents.create(Table.class);
                table.addGeneratedColumn("fullNameWithLogin", entity -> {
                    Label<String> lbl = uiComponents.create(Label.TYPE_STRING);
                    lbl.setValue(entity.getFullNameWithLogin());
                    return lbl;
                });
                table.setItems(new ContainerTableItems<>(container));
                table.setColumnHeaderVisible(false);
                table.setShowSelection(false);
                table.setWidthAuto();

                popupView.setPopupContent(table);
                return popupView;
            }
        }
        return null;
    }

    @SuppressWarnings("UnstableApiUsage")
    public Component generateOutcome(ExtTaskData taskData) {
        Label<String> label = uiComponents.create(Label.TYPE_STRING);
        String outcome = taskData.getOutcome();
        if (outcome != null)
            label.setValue(messages.getMainMessage("OUTCOME_" + outcome));
        return label;
    }

}