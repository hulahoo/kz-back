package kz.uco.tsadv.web.modules.personal.successionplanning;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.components.actions.RemoveAction;
import com.haulmont.cuba.gui.data.GroupDatasource;
import kz.uco.tsadv.modules.personal.model.SuccessionPlanning;
import kz.uco.tsadv.modules.personal.model.Successor;
import kz.uco.tsadv.service.EmployeeService;
import kz.uco.tsadv.web.modules.personal.common.Utils;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@SuppressWarnings("all")
public class SuccessionPlanningBrowse extends AbstractLookup {
    private static final String IMAGE_CELL_HEIGHT = "50px";

    @Inject
    protected GroupDatasource<Successor, UUID> successorsDs;
    @Inject
    protected EmployeeService employeeService;
    @Named("successionPlanningsTable.edit")
    protected EditAction successionPlanningsTableEdit;
    @Named("successionPlanningsTable.create")
    protected CreateAction successionPlanningsTableCreate;
    @Named("successionPlanningsTable.remove")
    protected RemoveAction successionPlanningsTableRemove;
    @Inject
    protected DataManager dataManager;

    public Component generatePersonImageCell(Successor entity) {
        return Utils.getPersonImageEmbedded(entity.getPersonGroup().getPerson(), IMAGE_CELL_HEIGHT, null);
    }

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        successionPlanningsTableEdit.setAfterCommitHandler((a) -> {
            successorsDs.refresh();

            try {
                for (Successor successor : successorsDs.getItems()) {
                    employeeService.changePersonType(successor.getPersonGroup(), "RESERVE");
                }
            } catch (Exception ex) {
                showException(ex);
            }
        });

        successionPlanningsTableCreate.setAfterCommitHandler((a) -> {
            successorsDs.refresh();

            try {
                for (Successor successor : successorsDs.getItems()) {
                    employeeService.changePersonType(successor.getPersonGroup(), "RESERVE");
                }
            } catch (Exception ex) {
                showException(ex);
            }
        });

        successionPlanningsTableRemove.setAfterRemoveHandler(new RemoveAction.AfterRemoveHandler() {
            @Override
            public void handle(Set removedItems) {
                try {
                    for (Object object : removedItems) {
                        SuccessionPlanning planning = (SuccessionPlanning) object;
                        List<Successor> successors = planning.getSuccessors();
                        if (successors != null && !successors.isEmpty()) {
                            for (Successor successor : successors) {
                                employeeService.changePersonType(successor.getPersonGroup(), "EMPLOYEE");
                            }
                        }
                    }
                } catch (Exception ex) {
                    showException(ex);
                }
            }
        });
    }

    protected void showException(Exception ex) {
        showNotification(
                getMessage("msg.error.title"),
                ex.getMessage(),
                NotificationType.TRAY);
    }

}