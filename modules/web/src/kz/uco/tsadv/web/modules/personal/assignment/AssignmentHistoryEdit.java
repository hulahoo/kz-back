package kz.uco.tsadv.web.modules.personal.assignment;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.PersistenceHelper;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.config.EmployeeConfig;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.gui.components.AbstractHrEditor;
import kz.uco.tsadv.modules.personal.dictionary.DicPersonType;
import kz.uco.tsadv.modules.personal.group.AssignmentGroupExt;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;
import kz.uco.tsadv.modules.personal.model.AssignmentExt;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import kz.uco.tsadv.service.EmployeeNumberService;
import kz.uco.tsadv.service.OrderNumberService;
import kz.uco.tsadv.service.OrganizationService;
import kz.uco.tsadv.web.modules.personal.common.Utils;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Calendar;
import java.util.*;

public class AssignmentHistoryEdit extends AbstractHrEditor<AssignmentExt> {

    @Inject
    protected FieldGroup fieldGroup;

    @Inject
    protected FieldGroup fieldGroup3;

    @Inject
    @Named("fieldGroup.personGroup")
    protected PickerField personGroup;

    @Inject
    @Named("fieldGroup.organizationGroup")
    protected PickerField organizationGroup;

    @Inject
    @Named("fieldGroup.jobGroup")
    protected PickerField jobGroup;

    @Inject
    @Named("fieldGroup.gradeGroup")
    protected PickerField gradeGroup;

    @Inject
    @Named("fieldGroup.location")
    protected PickerField location;

    @Named("fieldGroup.primaryFlag")
    protected CheckBox primaryFlag;

    @Named("fieldGroup.assignmentNumber")
    protected TextField assignmentNumberField;

    @Inject
    protected Datasource<AssignmentExt> assignmentDs;
    @Inject
    protected ComponentsFactory componentsFactory;

    @Inject
    protected DataManager dataManager;

    @Inject
    protected CommonService commonService;
    protected boolean transfer;
    protected boolean combination;
    protected String assignmentNumber;
    @Inject
    protected CollectionDatasource<AssignmentExt, UUID> assignmentsDs;
    @Inject
    protected Datasource<AssignmentGroupExt> assignmentGroupDs;
    @Inject
    protected OrganizationService organizationService;
    //    @Named("fieldGroup3.substituteEmployee")
//    private PickerField substituteEmployeeField;
    @Inject
    private EmployeeNumberService employeeNumberService;
    @Named("fieldGroup3.orderNumber")
    private TextField orderNumberField;
    @Inject
    private EmployeeConfig employeeConfig;
    @Inject
    protected OrderNumberService orderNumberService;
    @Inject
    private Frame windowActions;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        if (params.containsKey("fromHistory")) {
            if (windowActions.getAction("windowCommitHistory") != null) {
                windowActions.getAction("windowCommitHistory").setVisible(false);
            }
//          getComponent("windowCommitHistory").setVisible(false);
        }
        personGroup.removeAction(PickerField.OpenAction.NAME);
        personGroup.removeAction(PickerField.ClearAction.NAME);
        organizationGroup.removeAction(PickerField.OpenAction.NAME);
        jobGroup.removeAction(PickerField.OpenAction.NAME);
        gradeGroup.removeAction(PickerField.OpenAction.NAME);
        location.removeAction(PickerField.OpenAction.NAME);

//        FieldGroup.FieldConfig positionGroupConfig = fieldGroup.getField("positionGroup");
//        PickerField positionGroup = componentsFactory.createComponent(PickerField.class);
//        positionGroup.setDatasource(assignmentDs, positionGroupConfig.getProperty());
//        if (!editHistory)
//            positionGroup.addAction(new AbstractAction("customLookup") {
//                @Override
//                public void actionPerform(Component component) {
//                    openPositionStructureBrowse();
//                }
//
//                @Override
//                public String getIcon() {
//                    return "font-icon:ELLIPSIS_H";
//                }
//            });
//        else
//            positionGroup.setEditable(false);
//        positionGroup.setWidth("100%");
//        positionGroup.setCaptionMode(CaptionMode.PROPERTY);
//        positionGroup.setCaptionProperty("position");
//        positionGroupConfig.setComponent(positionGroup);
        if (params.containsKey("transfer")) {
            transfer = true;
        }
        if (params.containsKey("combination")) {
            combination = true;
        }
        if (params.containsKey("assignmentNumber")) {
            assignmentNumber = (String) params.get("assignmentNumber");
        }

        PickerField.LookupAction substituteEmployee = ((PickerField) fieldGroup3.getFieldNN("substituteEmployee")
                .getComponentNN()).getLookupAction();
        substituteEmployee.setLookupScreen("base$SubstitutedEmployee.browse");
        substituteEmployee.setLookupScreenParamsSupplier(() ->
                ParamsMap.of("onlyEmployee", true, "personGroupId", assignmentDs.getItem().getPersonGroup().getId()));
    }

    @Override
    protected void postInit() {
        super.postInit();

        if (combination) {
            fieldGroup.getField("positionGroup").setRequired(true);
            fieldGroup3.getField("primaryFlag").setVisible(false);
            //
            assignmentDs.addItemPropertyChangeListener(e -> {
                if ("assignDate".equals(e.getProperty())) {
                    getItem().setStartDate((Date) e.getValue());
                }
            });
            assignmentDs.getItem().setEndDate(CommonUtils.getEndOfTime());
            assignmentDs.getItem().setAssignDate(CommonUtils.getSystemDate());
        }
        if (transfer) {
            getAction("windowCommit").setVisible(false);
            //getItem().setFte(1.0);
            if (fieldGroup.getField("") != null) {
                fieldGroup.getField("assignDate").setVisible(false);

                fieldGroup.getField("startDate").setEditable(true);
                Date prevEndDate = getPrevAssignment(getItem()).getEndDate();
                fieldGroup.getField("startDate").addValidator(value -> {
                    if (value != null && getItem().getEndDate() != null) {
                        Date startDate = (Date) value;
                        if (startDate.after(getItem().getEndDate()))
                            throw new ValidationException(getMessage("AbstractHrEditor.startDate.validatorMsg"));
                        if (prevEndDate != null) {
                            if (!startDate.after(prevEndDate)) {
                                throw new ValidationException(getMessage("AbstractHrEditor.startDate.validatorMsg1"));
                            }
                        }
                    }
                });

                getItem().setStartDate(CommonUtils.getSystemDate());

                getItem().setAssignDate(getItem().getStartDate());
                assignmentDs.addItemPropertyChangeListener(e -> {
                    if ("startDate".equals(e.getProperty())) {
                        getItem().setAssignDate((Date) e.getValue());
                    }
                });
            }
            if (employeeConfig.getEnableOrderNumberAutogenerationForAssignments()) {
                orderNumberField.setValue((orderNumberService.getLastAssignmentOrderNumber() + 1) + "");
            }
        }

        if (PersistenceHelper.isNew(getItem())) {
            personGroup.setEditable(true);
            Map<String, Object> map = new HashMap<>();
            map.put("pickerField", personGroup);
            map.put("personTypeFilter", "and (p.type.code in ('CANDIDATE', 'EXEMPLOYEE', 'EMPLOYEE') or p.type.code is null)");
            Utils.customizeLookup(personGroup,
                    "base$PersonGroup.browse",
                    WindowManager.OpenType.DIALOG,
                    Collections.singletonMap("personTypeFilter", "and (p.type.code in ('CANDIDATE', 'EXEMPLOYEE', 'EMPLOYEE') or p.type.code is null)"));
        }

        /*if (getItem().getPositionGroup()!=null) {
            location.setValue(getItem().getPositionGroup().getPosition().getLocation());
        }*/
    }

    @Override
    public void ready() {
        super.ready();
        if (combination) {
            assignmentNumberField.setValue(employeeNumberService.increaseAssignmentNumber(assignmentNumber));
        }
        assignmentDs.addItemPropertyChangeListener(e -> {
            if (!getItem().getPositionGroup().getPosition().getJobGroup().equals(e.getItem().getJobGroup())) {
                getItem().setSubstituteEmployee(null);
            }
            if ("positionGroup".equals(e.getProperty())) {
                if (e.getValue() != null) {
                    e.getItem().setOrganizationGroup(
                            e.getItem().getPositionGroup().getPosition().getOrganizationGroupExt());
                    getItem().setFte(1.0);
                            /*organizationService.getOrganizationGroupByPositionGroupId(
                                    ((PositionGroupExt) e.getValue()).getId(),
                                    "organizationGroup.lookup"));*/
                    getItem().setLocation(getItem().getPositionGroup().getPosition().getLocation());
                    getItem().setGradeGroup(getItem().getPositionGroup().getPosition().getGradeGroup());
                    if (getItem().getPositionGroup().getPosition().getCostCenter() != null) {
                        getItem().setCostCenter(getItem().getPositionGroup().getPosition().getCostCenter());
                    } else if (getItem().getOrganizationGroup().getOrganization().getCostCenter() != null) {
                        getItem().setCostCenter(getItem().getOrganizationGroup().getOrganization().getCostCenter());
                    } else {
                        getItem().setCostCenter(null);
                    }
                }
            }

        });

//        substituteEmployeeField.getLookupAction().setLookupScreenParams(ParamsMap.of("personGroupId", assignmentDs.getItem().getPersonGroup().getId(), "assignmentHistoryEditKey", ""));
        //orderNumberField.setValue(orderNumberService.getLastAssignmentOrderNumber()+1);
    }

    protected AssignmentExt getPrevAssignment(AssignmentExt currentAssignment) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentAssignment.getStartDate());
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("assignmentGroupId", currentAssignment.getGroup().getId());
        queryParams.put("prevAssignmentEndDate", calendar.getTime());
        AssignmentExt prevAssignmentExt = commonService.getEntity(AssignmentExt.class, "select e from base$AssignmentExt e" +
                " where e.group.id = :assignmentGroupId" +
                "   and e.endDate = :prevAssignmentEndDate", queryParams, "_local");
        return prevAssignmentExt == null ? null : prevAssignmentExt;
    }


    protected void openPositionStructureBrowse() {
        AbstractLookup hierarchyElementBrowse = openLookup("base$PositionGroup.browse", new Lookup.Handler() {
                    @Override
                    public void handleLookup(Collection items) {
                        for (Object o : items) {
                            PositionGroupExt positionGroup = ((PositionGroupExt) o);

                            getItem().setPositionGroup(positionGroup);
//                    getItem().setOrganizationGroup(ps.getOrganizationGroup());
                            getItem().setJobGroup(positionGroup.getPosition().getJobGroup());
                            getItem().setGradeGroup(positionGroup.getPosition().getGradeGroup());
                            getItem().setLocation(positionGroup.getPosition().getLocation());
                        }
                    }
                }, WindowManager.OpenType.DIALOG,
                ParamsMap.of("openedFromAssignmentHistoryEdit", ""));
    }

    @Override
    protected FieldGroup getStartEndDateFieldGroup() {
        return this.fieldGroup;
    }

    @Override
    protected boolean preCommit() {
        if (PersistenceHelper.isNew(getItem())) {
            PersonExt p = dataManager.reload(getItem().getPersonGroup().getPerson(), "person.full");
            DicPersonType personType = dataManager.reload(p.getType(), View.LOCAL);
            if (!"EMPLOYEE".equals(personType.getCode())) {
                p.setWriteHistory(true);
                p.setType(commonService.getEntity(DicPersonType.class, "EMPLOYEE"));
                dataManager.commit(p);
            }
        }
        return super.preCommit();
    }

    public void editHistory() {
        AssignmentExt item = assignmentsDs.getItem();

        List<AssignmentExt> items = new ArrayList<>(assignmentsDs.getItems());
        items.sort((i1, i2) -> i1.getStartDate().before(i2.getStartDate()) ? -1 : 1);

        if (item != null) {
            Map<String, Object> paramsMap = new HashMap<>();
            paramsMap.put("editHistory", Boolean.TRUE);
            paramsMap.put("firstRow", items.indexOf(item) == 0);
            paramsMap.put("lastRow", items.indexOf(item) == items.size() - 1);

            openAssignmentEditor(item, paramsMap);
        }
    }

    protected void openAssignmentEditor(AssignmentExt assignment, Map<String, Object> params) {
        AssignmentHistoryEdit assignmentHistoryEdit = (AssignmentHistoryEdit) openEditor("base$Assignment.historyEdit", assignment, WindowManager.OpenType.THIS_TAB, params);
        assignmentHistoryEdit.addCloseListener(actionId ->
                assignmentDs.refresh());
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
                                }
                            },
                            new DialogAction(DialogAction.Type.NO)
                    });

        }
    }
}