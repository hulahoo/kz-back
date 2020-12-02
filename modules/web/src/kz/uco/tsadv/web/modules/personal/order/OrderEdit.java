package kz.uco.tsadv.web.modules.personal.order;

//import com.haulmont.bpm.gui.procactions.ProcActionsFrame;
import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.RemoveAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.tsadv.modules.personal.dictionary.DicOrderStatus;
import kz.uco.tsadv.modules.personal.dictionary.DicOrderType;
import kz.uco.tsadv.modules.personal.model.*;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.web.modules.personal.common.Utils;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;
import java.util.stream.Collectors;

public class OrderEdit extends AbstractEditor<Order> {
    private static final String PROCESS_CODE = "orderApproval";

    @Inject
    private Datasource<Order> orderDs;

    @Inject
    private CollectionDatasource<OrdAssignment, UUID> ordAssignmentDs;

    @Inject
    private CollectionDatasource<AssignmentExt, UUID> assignmentsDs;

    @Inject
    private Metadata metadata;

    @Inject
    private Table<OrdAssignment> ordAssignmentTable;

    @Inject
    private ComponentsFactory componentsFactory;

    @Inject
    private DataManager dataManager;

    /*@Inject
    private ProcActionsFrame procActionsFrame;*/

    @Inject
    private FieldGroup fieldGroup;

    @Inject
    @Named("fieldGroup.orderType")
    private PickerField<Entity> orderTypeField;

    @Inject
    @Named("fieldGroup.orderDate")
    private DateField orderDateField;

    @Inject
    @Named("fieldGroup.approverPersonGroup")
    private PickerField approverPersonGroupField;

    @Named("tabSheet.details")
    private OrderedContainer details;

    @Inject
    private Table<Absence> absenceTable;

    @Inject
    private LookupPickerField assignmentLookup;

    @Inject
    private TokenList assignmentMultiLookup;
    @Inject
    private VBoxLayout businessTripBox;

    @Inject
    private SplitPanel splitter;

    @Inject
    private VBoxLayout ordAssignmentBox;

    @Inject
    private CollectionDatasource<Salary, UUID> ordSalaryDs;

    @Inject
    private CollectionDatasource<AssignmentExt, UUID> selectedAssignmentsDs;

    @Inject
    private Table<Salary> ordSalaryTable;

    @Inject
    private Table<Dismissal> dismissalTable;

    @Named("dismissalTable.remove")
    private RemoveAction dismissalTableRemove;

    @Inject
    private CollectionDatasource<Dismissal, UUID> dismissalDs;

    @Inject
    @Named("fieldGroup.orderReason")
    private PickerField orderReasonField;

    @Inject
    private CommonService commonService;

    @Override
    protected void initNewItem(Order item) {
        super.initNewItem(item);

        DicOrderStatus status = null;
        LoadContext<DicOrderStatus> loadContext = LoadContext.create(DicOrderStatus.class)
                .setQuery(
                        LoadContext.createQuery("select e from tsadv$DicOrderStatus e where e.code = :code")
                                .setParameter("code", "DRAFT")
                );
        status = dataManager.load(loadContext);
        item.setOrderStatus(status);
    }

    @Override
    public void postInit() {
        initProcActionsFrame();
    }

    private void initProcActionsFrame() {
        /*procActionsFrame.initializer()
                .setBeforeStartProcessPredicate(this::commit)
                .setAfterStartProcessListener(() -> {
                    showNotification(getMessage("orderApproval.processStarted"), NotificationType.HUMANIZED);

                    close(COMMIT_ACTION_ID);

                })
                .setBeforeCompleteTaskPredicate(this::commit)
                .setAfterCompleteTaskListener(() -> {
                    showNotification(getMessage("orderApproval.taskCompleted"), NotificationType.HUMANIZED);
                    close(COMMIT_ACTION_ID);
                })
                .init(PROCESS_CODE, getItem());*/
    }

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        details.setVisible(false);

        Utils.customizeLookup(orderTypeField, null, WindowManager.OpenType.DIALOG, null);
        Utils.customizeLookup(assignmentLookup, "base$Assignment.browse", WindowManager.OpenType.DIALOG, null);
        Utils.customizeLookup(approverPersonGroupField, null, WindowManager.OpenType.DIALOG, null);
        Utils.customizeLookup(orderReasonField, null, WindowManager.OpenType.DIALOG, null);

        orderDateField.addValueChangeListener((e) -> {
            customizeAssignmentLookup();
            customizeAssignmentMultiLookup();
        });

        orderTypeField.addValueChangeListener((e) -> {
            if (e.getValue() != null) {
                DicOrderType orderType = (DicOrderType) e.getValue();
                orderTypeField.setEditable(false);
                details.setVisible(true);

                if ("VACATION".equals(orderType.getCode())) {
                    absenceTable.setVisible(true);
                } else {
                    absenceTable.setVisible(false);
                }

                if ("BUSINESS_TRIP".equals(orderType.getCode())) {
                    businessTripBox.setVisible(true);
                } else {
                    businessTripBox.setVisible(false);
                }

                if ("SALARY".equals(orderType.getCode())) {
                    splitter.setVisible(false);
                    details.getComponent("salaryBox").setHeightFull();
                }

                if ("DISMISSAL".equals(orderType.getCode())) {
                    dismissalTable.setVisible(true);

                    dismissalDs.addItemChangeListener((ev) -> {
                        dismissalTableRemove.setEnabled(dismissalDs.getItem() != null && "PROJECT".equals(dismissalDs.getItem().getStatus().getCode()));
                    });
                } else {
                    dismissalTable.setVisible(false);
                }
            }

        });

        assignmentMultiLookup.setAfterLookupSelectionHandler(e -> {
            addOrdAssignmentSalary();
        });

        /*EditAction.BulkEditorIntegration bulkEditorIntegration = ordSalaryTableEdit.getBulkEditorIntegration();
        bulkEditorIntegration.setEnabled(true);
        bulkEditorIntegration.setOpenType(WindowManager.OpenType.DIALOG);
        bulkEditorIntegration.setExcludePropertiesRegex("ordAssignment|assignmentGroup|contract|order");*/
    }


    @Override
    public void ready() {
        super.ready();
        exclude(false);
        customizeAssignmentMultiLookup();
        customizeAssignmentLookup();
    }

    private void exclude(boolean refresh) {
        if (refresh) {
            assignmentsDs.clear();
            assignmentsDs.refresh();
        }
        Set<AssignmentExt> excluded = new HashSet<>();

        for (AssignmentExt assignment : assignmentsDs.getItems()) {
            for (OrdAssignment ordAssignment : ordAssignmentDs.getItems()) {
                if (assignment.getGroup().getId().toString().equals(ordAssignment.getAssignmentGroup().getId().toString())) {
                    excluded.add(assignment);
                }
            }
        }

        for (AssignmentExt assignment : excluded) {
            assignmentsDs.excludeItem(assignment);
        }


    }

    private void customizeAssignmentMultiLookup() {
        Map<String, Object> assignmentMultiLookupParams = new HashMap<>();
        Collection<UUID> excludeAssignments = new ArrayList<UUID>();
        excludeAssignments.addAll(ordAssignmentDs.getItems().stream().map((ordAssignment) -> ordAssignment.getAssignmentGroup().getId()).collect(Collectors.toCollection(ArrayList::new)));
        assignmentMultiLookupParams.put("excludeAssignmentGroupIds", excludeAssignments);
        assignmentMultiLookupParams.put("filterDate", getItem().getOrderDate());
        assignmentMultiLookup.setLookupScreenParams(assignmentMultiLookupParams);
    }

    private void customizeAssignmentLookup() {
        Map<String, Object> assignmentLookupParams = new HashMap<>();
        Collection<UUID> excludeAssignments = new ArrayList<UUID>();
        excludeAssignments.addAll(ordAssignmentDs.getItems().stream().map((ordAssignment) -> ordAssignment.getAssignmentGroup().getId()).collect(Collectors.toCollection(ArrayList::new)));
        assignmentLookupParams.put("excludeAssignmentGroupIds", excludeAssignments);
        assignmentLookupParams.put("filterDate", getItem().getOrderDate());
        assignmentLookup.getLookupAction().setLookupScreenParams(assignmentLookupParams);
    }

    public void deleteOrdAssignment() {
        for (OrdAssignment ordAssignment : ordAssignmentTable.getSelected()) {
            ordAssignmentDs.removeItem(ordAssignment);
            ordAssignmentDs.refresh();
        }

        exclude(true);
        customizeAssignmentLookup();
    }


    public void addOrdAssigment() {
        if (assignmentsDs.getItem() != null) {
            OrdAssignment ordAssignment = metadata.create(OrdAssignment.class);
            AssignmentExt a = assignmentsDs.getItem();
            ordAssignment.setOrder(getItem());
            ordAssignment.setAssignmentGroup(a.getGroup());
            ordAssignmentDs.addItem(ordAssignment);
            ordAssignmentDs.refresh();
            assignmentsDs.excludeItem(a);
        }
        customizeAssignmentLookup();
    }

    public void addOrdAssignmentSalary() {
        for (AssignmentExt a : selectedAssignmentsDs.getItems()) {
            OrdAssignment ordAssignment = metadata.create(OrdAssignment.class);
            ordAssignment.setOrder(getItem());
            ordAssignment.setAssignmentGroup(a.getGroup());
            ordAssignmentDs.addItem(ordAssignment);
            ordAssignmentDs.refresh();

            Salary s = metadata.create(Salary.class);
            s.setOrdAssignment(ordAssignment);
            s.setAssignmentGroup(a.getGroup());

            ordSalaryDs.addItem(s);
        }
        customizeAssignmentMultiLookup();
        selectedAssignmentsDs.clear();
    }

    public void deleteOrdAssignmentSalary() {
        for (Salary s : ordSalaryTable.getSelected()) {
            OrdAssignment oa = s.getOrdAssignment();
            ordAssignmentDs.removeItem(oa);
            ordSalaryDs.removeItem(s);

            customizeAssignmentMultiLookup();
        }
    }

    public void salaryEdit() {
        Salary s = metadata.create(Salary.class);
        AbstractEditor salaryEditor = openEditor("tsadv$Salary.multiEdit", s, WindowManager.OpenType.DIALOG);

        salaryEditor.addCloseListener(e -> {
            for (Salary ss : ordSalaryTable.getSelected()) {
                if (s.getAmount() != null)
                    ss.setAmount(s.getAmount());
                if (s.getCurrency() != null)
                    ss.setCurrency(s.getCurrency());
                if (s.getNetGross() != null)
                    ss.setNetGross(s.getNetGross());
                if (s.getReason() != null)
                    ss.setReason(s.getReason());
                if (s.getStartDate() != null)
                    ss.setStartDate(s.getStartDate());
                if (s.getEndDate() != null)
                    ss.setEndDate(s.getEndDate());
            }
        });
    }

    @Override
    protected void postValidate(ValidationErrors errors) {
        if (getItem().getOrderType() != null && "SALARY".equals(getItem().getOrderType().getCode())) {
            Set<String> errorSet = new HashSet<>();
            MetaClass salaryMetaClass = metadata.getClass("tsadv$Salary");

            for (Salary s : ordSalaryDs.getItems()) {
                if (s.getStartDate() == null)
                    errorSet.add(messages.formatMainMessage("validation.required.defaultMsg",
                            messages.getTools().getPropertyCaption(salaryMetaClass, "startDate")));
                if (s.getEndDate() == null)
                    errorSet.add(messages.formatMainMessage("validation.required.defaultMsg",
                            messages.getTools().getPropertyCaption(salaryMetaClass, "endDate")));
                if (s.getAmount() == null)
                    errorSet.add(messages.formatMainMessage("validation.required.defaultMsg",
                            messages.getTools().getPropertyCaption(salaryMetaClass, "amount")));
                if (s.getCurrency() == null)
                    errorSet.add(messages.formatMainMessage("validation.required.defaultMsg",
                            messages.getTools().getPropertyCaption(salaryMetaClass, "currency")));
                if (s.getNetGross() == null)
                    errorSet.add(messages.formatMainMessage("validation.required.defaultMsg",
                            messages.getTools().getPropertyCaption(salaryMetaClass, "netGross")));

                if (s.getStartDate() != null && s.getEndDate() != null) {
                    if (s.getEndDate().before(s.getStartDate()))
                        errorSet.add(getMessage("AbstractHrEditor.endDate.validatorMsg"));
                    else if (salaryExists(s))
                        errorSet.add(getMessage("SalaryEdit.startEndDate.validatorMsg"));
                }
            }

            // WebTable<Salary> t = ordSalaryTable.unwrap(WebTable.class);

            ordSalaryTable.setStyleProvider((entity, property) -> {
                        if (property == null) {
                            return null;
                        } else {
                            switch (property) {
                                case "startDate":
                                    if (entity.getStartDate() == null
                                            || entity.getStartDate() != null
                                            && entity.getEndDate() != null
                                            && entity.getEndDate().before(entity.getStartDate())
                                            || entity.getStartDate() != null
                                            && entity.getEndDate() != null
                                            && salaryExists(entity)
                                            ) {
                                        return "red-table-cell";
                                    }
                                    break;
                                case "endDate":
                                    if (entity.getEndDate() == null
                                            || entity.getStartDate() != null
                                            && entity.getEndDate() != null
                                            && entity.getEndDate().before(entity.getStartDate())
                                            || entity.getStartDate() != null
                                            && entity.getEndDate() != null
                                            && salaryExists(entity)
                                            ) {
                                        return "red-table-cell";
                                    }
                                    break;
                                case "amount":
                                    if (entity.getAmount() == null) {
                                        return "red-table-cell";
                                    }
                                    break;
                                case "currency":
                                    if (entity.getCurrency() == null) {
                                        return "red-table-cell";
                                    }
                                    break;
                                case "netGross":
                                    if (entity.getNetGross() == null) {
                                        return "red-table-cell";
                                    }
                                    break;
                            }
                        }
                        return null;
                    }
            );

            for (String err : errorSet)
                errors.add(err);
        }

        super.postValidate(errors);
    }

    private boolean salaryExists(Salary salary) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", salary.getId());
        params.put("assignmentGroupId", salary.getAssignmentGroup().getId());
        params.put("startDate", salary.getStartDate());
        params.put("endDate", salary.getEndDate());

        List<Salary> salaries = commonService.getEntities(Salary.class, "select e " +
                        " from tsadv$Salary e " +
                        " where e.assignmentGroup.id = :assignmentGroupId " +
                        " and e.id <> :id " +
                        " and (" +
                        " (:startDate between e.startDate and e.endDate) or " +
                        " (:endDate between e.startDate and e.endDate) or " +
                        " (e.startDate >= :startDate and e.endDate <= :endDate) " +
                        " )",
                params,
                View.LOCAL);
        if (salaries != null && !salaries.isEmpty()) return true;

        return false;
    }
}