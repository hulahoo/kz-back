package kz.uco.tsadv.web.assignmentsalaryrequest;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.web.gui.components.WebGridLayout;
import com.haulmont.cuba.web.gui.components.WebResizableTextArea;
import kz.uco.tsadv.entity.AssignmentSalaryRequest;
import kz.uco.tsadv.entity.MyTeamNew;
import kz.uco.tsadv.entity.dbview.OrganizationSsView;
import kz.uco.tsadv.exceptions.ItemNotFoundException;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;
import kz.uco.tsadv.modules.personal.model.AssignmentExt;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import kz.uco.tsadv.modules.personal.model.PositionExt;
import kz.uco.tsadv.modules.personal.model.Salary;
import kz.uco.tsadv.service.AssignmentSalaryService;
import kz.uco.tsadv.service.EmployeeService;
import kz.uco.tsadv.service.SelfService;
import kz.uco.tsadv.web.bpm.editor.AbstractBpmEditor;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.util.CollectionUtils;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;
import java.util.function.Supplier;

public class AssignmentSalaryRequestEdit<T extends AssignmentSalaryRequest> extends AbstractBpmEditor<T> {

    public static final String PROCESS_NAME = "assignmentSalaryRequest";
    protected boolean isAmountChange = false;
    protected boolean readOnly = true;
    protected PositionGroupExt positionGroupExt;

    @Inject
    protected AssignmentSalaryService assignmentSalaryService;
    @Inject
    protected EmployeeService employeeService;
    @Inject
    protected SelfService selfService;
    @Inject
    protected Datasource<AssignmentExt> assignmentExtDs;
    @Inject
    protected Datasource<Salary> salaryDs;
    @Named("fieldGroup1.personGroup")
    protected PickerField<PersonGroupExt> personGroupField;
    @Inject
    protected PickerField<Entity> newGrade, picker, organizationSs, positionSs, pickerSubstitutedEmployee;
    @Inject
    protected DateField<Date> oldDateFrom, newDateFrom;
    @Inject
    protected TextField<Object> newAmount, newPercent, oldAmount;
    @Inject
    protected LookupField<Object> /*newCurrency,*/ newNetGross, newSalaryType;
    /*    @Inject
        protected PickerField newJob;*/

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        salaryDs.addItemChangeListener(e -> setOldDate());
        assignmentExtDs.addItemChangeListener(e -> setOldDate());

        oldDateFrom.setContextHelpText(getMessage("helpOldDate"));
    }

    @Override
    protected void initIconListeners() {
        WebGridLayout grid = ((WebGridLayout) getComponentNN("grid"));

        pickerSubstitutedEmployee.addValueChangeListener(e -> ((Label) grid.getComponentNN("labelSubstitutedEmployeeIcon"))
                .setIcon(groupsComponent.getChangedIcon(!Objects.equals(e.getValue(), assignmentExtDs.getItem() != null ?
                        assignmentExtDs.getItem().getSubstituteEmployee() : null))));
        ((PickerField<Entity>) grid.getComponentNN("oldSubstitutedEmployee")).addValueChangeListener(e -> ((Label) grid.getComponentNN("labelSubstitutedEmployeeIcon"))
                .setIcon(groupsComponent.getChangedIcon(!Objects.equals(e.getValue(), pickerSubstitutedEmployee.getValue()))));

        newGrade.addValueChangeListener(e -> ((Label) grid.getComponentNN("labelGradeIcon"))
                .setIcon(groupsComponent.getChangedIcon(!Objects.equals(e.getValue(), assignmentExtDs.getItem() != null ? assignmentExtDs.getItem().getGradeGroup() : null))));
        ((PickerField<Entity>) grid.getComponentNN("oldGrade")).addValueChangeListener(e -> ((Label) grid.getComponentNN("labelGradeIcon"))
                .setIcon(groupsComponent.getChangedIcon(!Objects.equals(e.getValue(), newGrade.getValue()))));

        positionSs.addValueChangeListener(e -> ((Label) grid.getComponentNN("labelPositionIcon"))
                .setIcon(groupsComponent.getChangedIcon(!Objects.equals(e.getValue(), assignmentExtDs.getItem() != null ? assignmentExtDs.getItem().getPositionGroup() : null))));
        ((PickerField<Entity>) grid.getComponentNN("oldPosition")).addValueChangeListener(e -> ((Label) grid.getComponentNN("labelPositionIcon"))
                .setIcon(groupsComponent.getChangedIcon(!Objects.equals(e.getValue(), positionSs.getValue()))));

        organizationSs.addValueChangeListener(e -> ((Label) grid.getComponentNN("labelOrganizationIcon"))
                .setIcon(groupsComponent.getChangedIcon(!Objects.equals(e.getValue() != null ? ((OrganizationSsView) e.getValue()).getOrganizationGroup() : null,
                        assignmentExtDs.getItem() != null ? assignmentExtDs.getItem().getOrganizationGroup() : null))));
        ((PickerField<Entity>) grid.getComponentNN("oldDepartment")).addValueChangeListener(e -> ((Label) grid.getComponentNN("labelOrganizationIcon"))
                .setIcon(groupsComponent.getChangedIcon(!Objects.equals(e.getValue(), organizationSs.getValue() != null ? ((OrganizationSsView) organizationSs.getValue()).getOrganizationGroup() : null))));

        /*newJob.addValueChangeListener(e -> ((Label) grid.getComponentNN("labelJobIcon"))
                .setIcon(groupsComponent.getChangedIcon(!Objects.equals(e.getValue(), assignmentExtDs.getItem() != null ? assignmentExtDs.getItem().getJobGroup() : null))));
        ((PickerField<Entity>) grid.getComponentNN("oldJob")).addValueChangeListener(e -> ((Label) grid.getComponentNN("labelJobIcon"))
                .setIcon(groupsComponent.getChangedIcon(!Objects.equals(e.getValue(), newJob.getValue()))));*/

        newSalaryType.addValueChangeListener(e -> ((Label) grid.getComponentNN("labelSalaryTypeIcon"))
                .setIcon(groupsComponent.getChangedIcon(!Objects.equals(e.getValue(), salaryDs.getItem() != null ? salaryDs.getItem().getType() : null))));
        ((LookupField<Object>) grid.getComponentNN("oldSalaryType")).addValueChangeListener(e -> ((Label) grid.getComponentNN("labelSalaryTypeIcon"))
                .setIcon(groupsComponent.getChangedIcon(!Objects.equals(e.getValue(), newSalaryType.getValue()))));

        newAmount.addValueChangeListener(e -> ((Label) grid.getComponentNN("labelAmountIcon"))
                .setIcon(groupsComponent.getChangedIcon(!Objects.equals(e.getValue(), salaryDs.getItem() != null ? salaryDs.getItem().getAmount() : null))));
        ((TextField<Object>) grid.getComponentNN("oldAmount")).addValueChangeListener(e -> ((Label) grid.getComponentNN("labelAmountIcon"))
                .setIcon(groupsComponent.getChangedIcon(!Objects.equals(e.getValue(), newAmount.getValue()))));


        newNetGross.addValueChangeListener(e -> ((Label) grid.getComponentNN("labelNetGrossIcon"))
                .setIcon(groupsComponent.getChangedIcon(!Objects.equals(e.getValue(), salaryDs.getItem() != null ? salaryDs.getItem().getNetGross() : null))));
        ((LookupField<Object>) grid.getComponentNN("oldNetGross")).addValueChangeListener(e -> ((Label) grid.getComponentNN("labelNetGrossIcon"))
                .setIcon(groupsComponent.getChangedIcon(!Objects.equals(e.getValue(), newNetGross.getValue()))));


        /*newCurrency.addValueChangeListener(e -> ((Label) grid.getComponentNN("labelCurrencyIcon"))
                .setIcon(groupsComponent.getChangedIcon(!Objects.equals(e.getValue(), salaryDs.getItem() != null ? salaryDs.getItem().getCurrency() : null))));
        ((LookupField) grid.getComponentNN("oldCurrency")).addValueChangeListener(e -> ((Label) grid.getComponentNN("labelCurrencyIcon"))
                .setIcon(groupsComponent.getChangedIcon(!Objects.equals(e.getValue(), newCurrency.getValue()))));*/

    }

    @Override
    protected void initLookupActions() {

        groupsComponent.addLookupActionGradeGroup(newGrade, this, "dateFrom")
                .setLookupScreenParamsSupplier(() -> ParamsMap.of("date", getItem().getDateFrom()));

        groupsComponent.addLookupActionPositionGroup(positionSs, this, "dateFrom")
                .setLookupScreenParamsSupplier(getParams("pos"));

        /*groupsComponent.addLookupActionJobGroup(newJob, this, "dateFrom")
                .setLookupScreenParamsSupplier(getParams("job"));*/

        PickerField.LookupAction lookupAction = picker.addLookupAction();
        lookupAction.setLookupScreen("tsadv$AssignmentMyTeam.browse");
        lookupAction.setAfterLookupSelectionHandler(items -> {
            if (!CollectionUtils.isEmpty(items)) {
                UUID id = ((MyTeamNew) items.iterator().next()).getPersonGroupId();
                if (id == null) {
                    picker.setValue(null);
                    throw new ItemNotFoundException(getMessage("noPersonGroup"));
                }
                PersonExt personExt = employeeService.getPersonByPersonGroup(id, newDateFrom.getValue(), "person-view");
                PersonGroupExt groupExt = metadata.create(PersonGroupExt.class);
                List<PersonExt> list = new ArrayList<>();
                list.add(personExt);
                groupExt.setList(list);
                groupExt.setId(personExt.getGroup().getId());
                personGroupField.setValue(groupExt);
            }
        });

        PickerField.LookupAction lookupActionOrganization = organizationSs.addLookupAction();
        lookupActionOrganization.setLookupScreen("tsadv$OrganizationSsView.browse");
        lookupActionOrganization.setAfterLookupSelectionHandler(items -> {
            getItem().setOrganizationGroup(CollectionUtils.isEmpty(items) ? null : ((OrganizationSsView) items.iterator().next()).getOrganizationGroup());
            listener();
        });
        lookupActionOrganization.setLookupScreenParamsSupplier(getParams("org"));

        pickerSubstitutedEmployee.getLookupAction().setLookupScreen("base$SubstitutedEmployee.browse");
        pickerSubstitutedEmployee.getLookupAction().setLookupScreenParamsSupplier(() ->
                ParamsMap.of("onlyEmployee", true,
                        "personGroupId", Optional.ofNullable(getItem().getPersonGroup()).map(BaseUuidEntity::getId).orElse(null),
                        "date", newDateFrom.getValue()));
    }

    @Override
    protected void postInit() {
        readOnly = !isDraft();

        super.postInit();

        listener();
        oldDateFrom.setDateFormat(newDateFrom.getDateFormat());

        groupsComponent.setInDateGroups(getItem().getPositionGroup(),
                getItem().getOrganizationGroup(),
                getItem().getJobGroup(),
                getItem().getGradeGroup(), getItem().getDateFrom());
        Optional.ofNullable(getItem().getPositionGroup()).ifPresent(p -> p.getPositionInDate(getItem().getDateFrom()));
    }

    protected void addListeners() {

        personGroupField.addValueChangeListener(e -> {
            newAssignmentSalaryListener();
            listener();
        });
        newDateFrom.addValueChangeListener(e -> {
            newAssignmentSalaryListener();
            positionSs.setValue(null);
            listener();
        });

        oldDateFrom.addValueChangeListener(e -> {
            if (oldDateFrom.getValue() != null) {
                if (newDateFrom.getValue() != null && newDateFrom.getValue().before(oldDateFrom.getValue())) {
                    newDateFrom.setValue(oldDateFrom.getValue());
                }
                newDateFrom.setRangeStart(oldDateFrom.getValue());
            }
        });

        positionSs.addValueChangeListener(e -> {
            if (!Objects.equals(e.getValue(), e.getPrevValue())) {
                refreshNewAssigment();
                listener();
            }
        });

        oldAmount.addValueChangeListener(e -> {
            boolean isValueNull = e.getValue() == null;
            if (isValueNull) {
                newPercent.setValue(null);
            }
            newPercent.setEditable(!isValueNull);
        });

        newAmount.addValueChangeListener(e -> {
            if (e.getValue() != null && oldAmount.getValue() != null) {
                double newA = (double) e.getValue();
                if (newA < 0) {
                    showNotification(getMessage("negativeSum"));
                    newAmount.setValue(0.0);
                } else {
                    double d = 100.0 * newA / ((double) oldAmount.getValue()) - 100;
                    if (newPercent.getValue() == null || Math.abs((double) newPercent.getValue() - d) > 1e-7) {
                        isAmountChange = true;
                        newPercent.setValue(d);
                    }
                }
            }
            listener();
        });

        newPercent.addValueChangeListener(e -> {
            if (e.getValue() != null && !isAmountChange) {
                double newP = (double) e.getValue();
                if (newP < -100) {
                    showNotification(getMessage("negativeSum"));
                    newPercent.setValue(-100.0);
                } else {
                    Double newA = (1 + newP / 100) * (double) oldAmount.getValue();
                    if (newAmount.getValue() == null || Math.abs((double) newAmount.getValue() - newA) > 1e-7) {
                        newAmount.setValue(newA);
                    }
                }
            }
            isAmountChange = false;
        });

        organizationSs.addValueChangeListener(e -> listener());
        newGrade.addValueChangeListener(e -> listener());
//        newJob.addValueChangeListener(e -> listener());

//        newCurrency.addValueChangeListener(e -> listener());
        newNetGross.addValueChangeListener(e -> listener());
        newSalaryType.addValueChangeListener(e -> listener());
    }

    protected void refreshNewAssigment() {
        PositionGroupExt positionGroupExt = (PositionGroupExt) positionSs.getValue();
        PositionExt position = positionGroupExt != null && getItem().getDateFrom() != null ? positionGroupExt.getPositionInDate(getItem().getDateFrom()) : null;
        groupsComponent.setInDateGroups(position, getItem().getDateFrom());
//        newJob.setValue(position != null ? position.getJobGroup() : null);
        newGrade.setValue(position != null ? position.getGradeGroup() : null);
        setOrganization(position != null ? position.getOrganizationGroupExt() : null);
        getItem().setPositionGroup(positionGroupExt);
    }

    protected void setOrganization(OrganizationGroupExt organizationGroupExt) {
        Optional.ofNullable(organizationGroupExt).ifPresent(o -> o.getOrganizationInDate(getItem().getDateFrom()));
        organizationSs.setValue(organizationGroupExt != null && getItem().getDateFrom() != null ?
                selfService.getOrganizationSsView(organizationGroupExt, getItem().getDateFrom()) : null);
        getItem().setOrganizationGroup(organizationGroupExt);
    }

    protected void setOldDate() {
        AssignmentExt assignmentExt = assignmentExtDs.getItem();
        Salary salary = salaryDs.getItem();

        Date date = assignmentExt != null ?
                assignmentExt.getStartDate() : null;

        if (salary != null && (date == null || salary.getStartDate() != null && salary.getStartDate().after(date))) {
            date = salary.getStartDate();
        }

        oldDateFrom.setValue(date);
    }

    protected void newAssignmentSalaryListener() {
        if (personGroupField.getValue() != null && newDateFrom.getValue() != null) {

            AssignmentExt assignmentExt = employeeService.getAssignmentExt(getItem().getPersonGroup().getId(), newDateFrom.getValue(), "assignmentExt.bpm.view");
            Salary salary = assignmentExt != null ? employeeService.getSalary(assignmentExt.getGroup(), newDateFrom.getValue(), "salary.view") : null;
            groupsComponent.setInDateGroups(assignmentExt, newDateFrom.getValue());
            assignmentExtDs.setItem(assignmentExt);
            salaryDs.setItem(salary);

            boolean hasAmount = salary != null && salary.getAmount() != null;

            newPercent.setValue((hasAmount && newAmount.getValue() != null) ? (100.0 * (double) newAmount.getValue() / salary.getAmount() - 100) : null);
            newPercent.setEditable(hasAmount);
        }
    }

    protected void listener() {
        procActionButtonHBox.setEnabled(isChanged());
    }

    protected boolean isChanged() {
        return isAssignmentChanged() && isSalaryChanged();
    }

    protected boolean isAssignmentChanged() {
        AssignmentExt assignmentExt = assignmentExtDs.getItem();
        return (assignmentExt == null) ||
                !Objects.equals(getItem().getOrganizationGroup(), assignmentExt.getOrganizationGroup()) ||
                !Objects.equals(getItem().getPositionGroup(), assignmentExt.getPositionGroup()) ||
                !Objects.equals(getItem().getJobGroup(), assignmentExt.getJobGroup()) ||
                !Objects.equals(getItem().getGradeGroup(), assignmentExt.getGradeGroup());
    }

    protected boolean isSalaryChanged() {
        Salary salary = salaryDs.getItem();
        return (salary == null) ||
                !Objects.equals(salary.getCurrency(), getItem().getCurrency()) ||
                !Objects.equals(salary.getNetGross(), getItem().getNetGross()) ||
                !Objects.equals(salary.getAmount(), getItem().getAmount()) ||
                !Objects.equals(salary.getAmount(), getItem().getAmount());
    }

    protected Supplier<Map<String, Object>> getParams(String type) {
        return () -> {
            Map<String, Object> params = new HashMap<>();
            if (organizationSs.getValue() != null && !type.equals("org") && ((OrganizationSsView) organizationSs.getValue()).getOrganizationGroup() != null) {
                params.put("orgGroupId", ((OrganizationSsView) organizationSs.getValue()).getOrganizationGroup().getId());
            }
            /*if (newJob.getValue() != null && !type.equals("job")) {
                params.put("jobGroupId", ((JobGroup) newJob.getValue()).getId());
            }*/
            if (positionSs.getValue() != null && !type.equals("pos")) {
                PositionGroupExt positionGroupExt = (PositionGroupExt) positionSs.getValue();
                PositionExt position = positionGroupExt.getPositionInDate(getItem().getDateFrom());
                if (position != null) {
                    params.put("orgGroupId", position.getOrganizationGroupExt() != null ? position.getOrganizationGroupExt().getId() : null);
                    params.put("jobGroupId", position.getJobGroup() != null ? position.getJobGroup().getId() : null);
                }
            }
            params.put("date", getItem().getDateFrom());
            params.put("posOrgJob", params.containsKey("orgGroupId") || params.containsKey("jobGroupId"));
            return params;
        };
    }

    @Override
    protected void initVisibleFields() {
        super.initVisibleFields();
        WebGridLayout grid = ((WebGridLayout) getComponentNN("grid"));
        boolean isActualPositionVisible = getItem().getActualPositionGroup() != null
                && getItem().getStatus() != null
                && getItem().getStatus().getCode().matches("APPROVED|REJECTED");

        grid.getComponentNN("actualPositionGroup").setVisible(isActualPositionVisible);
        grid.getComponentNN("actualPositionGroupLabel").setVisible(isActualPositionVisible);
        grid.getComponentNN("emptyLbl").setVisible(isActualPositionVisible);

        getComponentNN("buttonOkId").setVisible(!readOnly);
    }

    @Override
    protected void initEditableFields() {
        super.initEditableFields();
        personGroupField.setEditable(!readOnly);
        picker.setEditable(!readOnly);

        organizationSs.setEditable(!readOnly);
//        newJob.setEditable(!readOnly);
        positionSs.setEditable(!readOnly);
        newGrade.setEditable(newGrade.isEditable() && !readOnly);
        newDateFrom.setEditable(!readOnly);

        newPercent.setEditable(!readOnly);
        newSalaryType.setEditable(!readOnly);
        newNetGross.setEditable(!readOnly);
//        newCurrency.setEditable(!readOnly);
        newAmount.setEditable(!readOnly);
        pickerSubstitutedEmployee.setEditable(!readOnly);

        ((LookupField) getComponentNN("reason")).setEditable(!readOnly);
        ((WebResizableTextArea) getComponentNN("note")).setEditable(!readOnly);
        ((FieldGroup) getComponentNN("fieldGroup2")).getFieldNN("attachment").setEditable(!readOnly);
    }

    @Override
    protected boolean alreadyHasRequest(UUID statusId) {
        Map<Integer, Object> params = new HashMap<>();
        params.put(1, getItem().getPersonGroup());
        params.put(2, statusId);
        params.put(3, getItem().getDateFrom());
        if (assignmentSalaryService.isHaveRequest(getEntityNameRequestList(), params)) {
            throw new ItemNotFoundException(getMessage("haveRequest"));
        }
        return false;
    }

    protected List<String> getEntityNameRequestList() {
        List<String> list = new ArrayList<>();
        list.add("tsadv_assignment_request");
        list.add("tsadv_assignment_salary_request");
        list.add("tsadv_salary_request");
        list.add("tsadv_temporary_translation_request");
        return list;
    }

    @Override
    public void ready() {
        super.ready();

        if (getItem().getPersonGroup() != null) {
            MyTeamNew myTeam = metadata.create(MyTeamNew.class);
            myTeam.setPersonGroupId(getItem().getPersonGroup().getId());
            myTeam.setFullName(getItem().getPersonGroup().getFioWithEmployeeNumber());
            picker.setValue(myTeam);

            Date newDate = DateUtils.addDays(newDateFrom.getValue(), -1);

            AssignmentExt assignmentExt = employeeService.getAssignmentExt(getItem().getPersonGroup().getId(), newDate, "assignmentExt.bpm.view");
            groupsComponent.setInDateGroups(assignmentExt, newDate);
            assignmentExtDs.setItem(assignmentExt);
            salaryDs.setItem(employeeService.getSalary(assignmentExt.getGroup(), newDate, "salary.view"));
        }

        if (getItem().getDateFrom() != null) {
            setOrganization(getItem().getOrganizationGroup());
            Optional.ofNullable(getItem().getPositionGroup()).ifPresent(p -> p.getPositionInDate(getItem().getDateFrom()));
        }
        positionSs.setValue(getItem().getPositionGroup());

        if (oldAmount.getValue() != null && newAmount.getValue() != null) {
            newPercent.setValue(100.0 * (double) newAmount.getValue() / ((double) oldAmount.getValue()) - 100);
        }

        if (isCurrentUserInitiator()) {
            ((Table) getComponentNN("procTasksTable")).removeColumn(((Table) getComponentNN("procTasksTable")).getColumn("comment"));
        }

        newPercent.setEditable(oldAmount.getValue() != null && !readOnly);
        listener();
        addListeners();
    }

    @Override
    protected void overrideProcActionComponents(Component component) {
        if (component instanceof VBoxLayout) {
            Collection<Component> vBoxComp = ((VBoxLayout) component).getComponents();
            procActionsFrame.remove(component);
            vBoxComp.forEach(component1 -> {
                component1.setParent(null);
                changeSettingsProcActionBtn(vBoxComp);
                component1.setWidthAuto();
                if (component1 instanceof Button && "completeTask_approve".equals(((Button) component1).getAction().getId())
                        && getItem().getPositionGroup().getPositionInDate(getItem().getDateFrom()) != null
                        && (!Optional.ofNullable(getItem().getPositionGroup().getPositionInDate(getItem().getDateFrom())
                        .getPositionStatus().getCode()).orElse("ACTIVE").equalsIgnoreCase("ACTIVE")
                        || Optional.ofNullable(getItem().getPositionGroup().getPositionInDate(getItem().getDateFrom()).getFte()).orElse(0.0) <= 0)
                        && bpmUtils.getActiveProcTask(procActionsFrame.getProcInstance().getId(), "procTask-frame-extended").getProcActor().getProcRole().getCode().equals("HR_SPECIALIST")) {
                    Button button = (Button) component1;
                    button.setAction(getNewApproveAction(button, button.getAction()));
                }
                procActionButtonHBox.add(component1);
            });
        }
    }

    protected Action getNewApproveAction(Button button, Action oldAction) {
        return new BaseAction("openWarningWindow") {
            @Override
            public void actionPerform(Component component) {
                AbstractEditor abstractEditor = openEditor("base$PositionGroupExt.edit",
                        metadata.create(PositionGroupExt.class),
                        WindowManager.OpenType.DIALOG,
                        ParamsMap.empty());

                abstractEditor.addCloseListener(actionId -> {
                    if (abstractEditor.getItem() != null) {
                        getItem().setActualPositionGroup((PositionGroupExt) abstractEditor.getItem());
                        getDsContext().commit();
                        oldAction.actionPerform(button);
                    }
                });
            }
        };
    }

    @Override
    protected void postValidate(ValidationErrors errors) {
        super.postValidate(errors);

        if (picker.getValue() == null) {
            errors.add(getMessage("errorPersonGroup"));
        }

        Date oldDate = oldDateFrom.getValue();
        if (getItem().getPersonGroup() != null && getItem().getDateFrom() != null) {
            if (!assignmentSalaryService.isLastAssignment(getItem().getPersonGroup().getId(),
                    oldDate != null && oldDate.before(getItem().getDateFrom()) ? oldDate : DateUtils.addDays(getItem().getDateFrom(), -1))) {
                errors.add(getMessage("lastAssignmentError"));
            }
            if (assignmentExtDs.getItem() != null &&
                    !assignmentSalaryService.isLastSalary(assignmentExtDs.getItem().getGroup().getId(), oldDate != null ? oldDate : DateUtils.addDays(getItem().getDateFrom(), -1))) {
                errors.add(getMessage("lastSalaryError"));
            }
        }
    }

    @Override
    protected String getProcessName() {
        return PROCESS_NAME;
    }
}