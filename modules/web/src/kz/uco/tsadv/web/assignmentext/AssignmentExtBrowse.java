package kz.uco.tsadv.web.assignmentext;

import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.tsadv.modules.personal.model.AssignmentExt;
import kz.uco.tsadv.service.AssignmentService;
import kz.uco.tsadv.web.modules.personal.assignment.AssignmentHistoryEdit;

import javax.inject.Inject;
import java.util.*;

public class AssignmentExtBrowse extends AbstractLookup {

    @Inject
    private CollectionDatasource<AssignmentExt, UUID> assignmentsDs;
    @Inject
    private ComponentsFactory componentsFactory;
    @Inject
    private AssignmentService assignmentService;

    public void editHistory() {
        AssignmentExt item = assignmentsDs.getItem();

        List<AssignmentExt> items = new ArrayList<>(assignmentsDs.getItems());
        items.sort((i1, i2) -> i1.getStartDate().before(i2.getStartDate()) ? -1 : 1);

        if (item != null) {
            Map<String, Object> paramsMap = new HashMap<>();
            paramsMap.put("editHistory", Boolean.TRUE);
            paramsMap.put("firstRow", items.indexOf(item) == 0);
            paramsMap.put("lastRow", items.indexOf(item) == items.size() - 1);
            paramsMap.put("fromHistory", null);

            openAssignmentEditor(item, paramsMap);
        }
    }

    private void openAssignmentEditor(AssignmentExt assignment, Map<String, Object> params) {
        AssignmentHistoryEdit assignmentHistoryEdit = (AssignmentHistoryEdit) openEditor("base$Assignment.historyEdit", assignment, WindowManager.OpenType.THIS_TAB, params);
        assignmentHistoryEdit.addCloseListener(actionId -> assignmentsDs.refresh());
        assignmentHistoryEdit.addCloseWithCommitListener(() -> assignmentsDs.refresh());
    }

    public void removeHistory() {
        AssignmentExt item = assignmentsDs.getItem();
        List<AssignmentExt> items = new ArrayList<>(assignmentsDs.getItems());
        items.sort((i1, i2) -> i1.getStartDate().before(i2.getStartDate()) ? -1 : 1);

        int index = items.indexOf(item);
        if (index == 0) {
            showNotification(getMessage("removeHistory.deny"));
        } else {
            showOptionDialog(getMessage("removeDialog.confirm.title"), getMessage("removeDialog.confirm.text"),
                    MessageType.CONFIRMATION,
                    new Action[]{
                            new DialogAction(DialogAction.Type.YES) {
                                @Override
                                public void actionPerform(Component component) {
                                    if (index == items.size() - 1) {
                                        items.get(index - 1).setEndDate(item.getEndDate());
                                        assignmentsDs.modifyItem(items.get(index - 1));
                                    } else {
                                        items.get(index + 1).setStartDate(item.getStartDate());
                                        assignmentsDs.modifyItem(items.get(index + 1));
                                    }

                                    assignmentsDs.removeItem(item);
                                    assignmentsDs.commit();
                                    assignmentsDs.refresh();
                                }
                            },
                            new DialogAction(DialogAction.Type.NO)
                    });

        }
    }

    public void removeHistories() {
        AssignmentExt item = assignmentsDs.getItem();
        List<AssignmentExt> items = new ArrayList<>(assignmentsDs.getItems());
        items.sort((i1, i2) -> i1.getStartDate().before(i2.getStartDate()) ? -1 : 1);

        int index = items.indexOf(item);
        if (index == 0) {
            showNotification(getMessage("removeHistory.deny"));
        } else {
            showOptionDialog(getMessage("removeDialog.confirm.title"), getMessage("removeDialog.confirm.text"),
                    MessageType.CONFIRMATION,
                    new Action[]{
                            new DialogAction(DialogAction.Type.YES) {
                                @Override
                                public void actionPerform(Component component) {
                                    items.get(index - 1).setEndDate(items.get(items.size() - 1).getEndDate());
                                    assignmentsDs.modifyItem(items.get(index - 1));

                                    for (int i = index; i < items.size(); i++)
                                        assignmentsDs.removeItem(items.get(i));
                                        assignmentsDs.commit();
                                        assignmentsDs.refresh();
                                }
                            },
                            new DialogAction(DialogAction.Type.NO)
                    });

        }
    }

    public Component generateReHireColumn(AssignmentExt assignment) {
        CheckBox checkBox = (CheckBox)componentsFactory.createComponent(CheckBox.NAME);
        checkBox.setValue(assignmentService.isReHire(assignment));
        checkBox.setEditable(false);
        return checkBox;
    }

}