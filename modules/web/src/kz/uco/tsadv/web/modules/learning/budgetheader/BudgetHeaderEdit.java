package kz.uco.tsadv.web.modules.learning.budgetheader;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.core.global.UserSessionSource;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.components.FieldGroup;
import com.haulmont.cuba.gui.components.PickerField;
import com.haulmont.cuba.gui.data.Datasource;
import kz.uco.base.service.NotificationService;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.global.entity.OrganizationTree;
import kz.uco.tsadv.modules.administration.UserExt;
import kz.uco.tsadv.modules.learning.model.Budget;
import kz.uco.tsadv.modules.learning.model.BudgetHeader;
import kz.uco.tsadv.modules.learning.model.BudgetHeaderHistory;
import kz.uco.tsadv.modules.performance.enums.BudgetHeaderStatus;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.service.EmployeeService;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

//entity has listener
public class BudgetHeaderEdit extends AbstractEditor<BudgetHeader> {


    public static final String ACTIVE_STATUS = "ACTIVE";
    @Inject
    protected Datasource<BudgetHeader> budgetHeaderDs;
    @Inject
    protected UserSessionSource userSessionSource;
    @Inject
    protected EmployeeService employeeService;
    protected UUID userId;
    protected PersonGroupExt currentPersonGroup;
    @Inject
    protected Metadata metadata;
    @Inject
    protected FieldGroup fieldGroup;
    @Inject
    protected Button approve;
    @Inject
    protected Button sendToApprove;
    @Inject
    protected Button rejectToDraft;
    @Inject
    protected Button moveToObsolete;
    @Inject
    protected Button windowClose;
    @Inject
    protected Button windowCommit;
    @Inject
    protected CommonService commonService;
    @Inject
    private NotificationService notificationService;
    boolean allIsWell = true;

    @Override
    protected void initNewItem(BudgetHeader item) {
        super.initNewItem(item);
        item.setStatus(BudgetHeaderStatus.DRAFT);
        approve.setVisible(false);
        sendToApprove.setVisible(false);
        rejectToDraft.setVisible(false);
        List<Budget> activeBudgetList = getActiveBudgetList();
        if (activeBudgetList.size() == 1) {
            String organizationName = item.getOrganizationGroup() != null ? item.getOrganizationGroup().getOrganizationName() : "";
            item.setHeaderName(activeBudgetList.get(0).getName() + (!organizationName.isEmpty() ? "_" + organizationName : ""));

            item.setBudget(activeBudgetList.get(0));
        }

        item.setResponsiblePerson(currentPersonGroup);
    }

    protected List<Budget> getActiveBudgetList() {
        return commonService.getEntities(Budget.class, "select e from tsadv$Budget e where e.status.code=:code",
                ParamsMap.of("code", ACTIVE_STATUS), View.LOCAL);
    }

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        setShowSaveNotification(false);

        userId = userSessionSource.getUserSession().getUser().getId();
        currentPersonGroup = employeeService.getPersonGroupByUserId(userId);

        PickerField pickerField = (PickerField) fieldGroup.getFieldNN("organizationGroup").getComponent();
        pickerField.removeAction(PickerField.LookupAction.NAME);
        pickerField.removeAction(PickerField.ClearAction.NAME);
        PickerField.LookupAction lookupAction = new PickerField.LookupAction(pickerField) {
            public OrganizationGroupExt transformValueFromLookupWindow(Entity valueFromLookupWindow) {
                return ((OrganizationTree) valueFromLookupWindow).getOrganizationGroupExt();
            }
        };
        pickerField.addAction(lookupAction);
        pickerField.addClearAction();
        lookupAction.setLookupScreen("organization-tree");
        lookupAction.setLookupScreenOpenType(WindowManager.OpenType.DIALOG);
    }

    @Override
    public void ready() {
        super.ready();
        if (!getItem().getStatus().equals(BudgetHeaderStatus.DRAFT)) {
            fieldGroup.setEditable(false);
        } else {
            fieldGroup.setEditable(true);
            approve.setVisible(false);
            moveToObsolete.setVisible(false);
            rejectToDraft.setVisible(false);
        }
        if (getItem().getStatus().equals(BudgetHeaderStatus.ON_AGREEMENT)) {
            sendToApprove.setVisible(false);
            windowCommit.setVisible(false);
            moveToObsolete.setVisible(false);
            fieldGroup.setEditable(false);
        }
        if (getItem().getStatus().equals(BudgetHeaderStatus.APPROVED)) {
            sendToApprove.setVisible(false);
            windowCommit.setVisible(false);
            moveToObsolete.setVisible(true);
            approve.setVisible(false);
            fieldGroup.setEditable(false);
        }
        if (getItem().getStatus().equals(BudgetHeaderStatus.OBSOLETE)) {
            sendToApprove.setVisible(false);
            windowCommit.setVisible(false);
            rejectToDraft.setVisible(false);
            moveToObsolete.setVisible(false);
            approve.setVisible(false);
            fieldGroup.setEditable(false);
        }
    }

    public void sendToApprove() {
        createHistoryAndClose(BudgetHeaderStatus.ON_AGREEMENT);
    }

    public void approve() {
        createHistoryAndClose(BudgetHeaderStatus.APPROVED);
    }

    public void rejectToDraft() {
        createHistoryAndClose(BudgetHeaderStatus.DRAFT);
    }

    public void moveToObsolete() {
        createHistoryAndClose(BudgetHeaderStatus.OBSOLETE);
    }

    protected void createHistoryAndClose(BudgetHeaderStatus status) {
        getItem().setStatus(status);
        BudgetHeaderHistory history = metadata.create(BudgetHeaderHistory.class);
        history.setBudgetHeader(getItem());
        history.setChangePerson(currentPersonGroup);
        history.setBudgetHeaderStatus(getItem().getStatus());
        openEditor("tsadv$BudgetHeaderHistory.edit", history, WindowManager.OpenType.DIALOG);
        commitAndClose();
    }

    @Override
    protected boolean preCommit() {
        if (currentPersonGroup == null) {
            showNotification(getMessage("userNotPersonGroup"), NotificationType.TRAY);
            allIsWell = false;
        }
        return allIsWell;
    }

    @Override
    public void commitAndClose() {
        if (PersistenceHelper.isNew(getItem())) {
            if (commit()) {
                showNotification(getMessage("saved"), NotificationType.TRAY);
                budgetHeaderDs.refresh();
                approve.setVisible(true);
                sendToApprove.setVisible(true);
                rejectToDraft.setVisible(true);
            }

        } else {
            budgetHeaderDs.addItemChangeListener(e -> {
                if (getItem().getResponsiblePerson() != null) {
                    Map<String, Object> param = new HashMap<>();
                    param.put("budgetHeaderName", getItem().getHeaderName());
                    UserExt userExt = employeeService.getUserExtByPersonGroupId(e.getItem().getResponsiblePerson().getId());
                    notificationService.sendParametrizedNotification("budgetHeaderChanges", userExt, param);
                }
            });
            super.commitAndClose();
        }
    }

}