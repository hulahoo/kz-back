package kz.uco.tsadv.web.assignmentRequset;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.web.gui.components.WebGridLayout;
import kz.uco.tsadv.entity.AssignmentRequest;
import kz.uco.tsadv.entity.MyTeamNew;
import kz.uco.tsadv.entity.dbview.OrganizationSsView;
import kz.uco.tsadv.exceptions.ItemNotFoundException;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.group.PositionGroupExt;
import kz.uco.tsadv.modules.personal.model.AssignmentExt;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import kz.uco.tsadv.modules.personal.model.PositionExt;
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

public class AssignmentRequestEdit<T extends AssignmentRequest> extends AbstractBpmEditor<T> {
    public static final String PROCESS_NAME = "assignmentRequest";

    @Inject
    protected AssignmentSalaryService assignmentSalaryService;
    @Inject
    protected SelfService selfService;
    @Inject
    protected EmployeeService employeeService;
    @Inject
    protected Datasource<AssignmentExt> assignmentExtDs;
    @Named("fieldGroup1.personGroup")
    protected PickerField personGroupField;
    @Named("fieldGroup2.attachment")
    protected FileUploadField attachmentField;
    @Inject
    protected DateField<Date> oldDateFrom, newDateFrom;
    @Inject
    protected PickerField<Entity> newPosition, organizationSs, newGrade, picker, pickerSubstitutedEmployee;
    @Inject
    protected ResizableTextArea note;
    @Inject
    protected Button buttonOkId;
    /*    @Inject
        protected PickerField newJob;*/

    @Override
    protected void initIconListeners() {
        WebGridLayout grid = ((WebGridLayout) getComponentNN("grid"));

        pickerSubstitutedEmployee.addValueChangeListener(e -> ((Label) grid.getComponentNN("labelSubstitutedEmployeeIcon"))
                .setIcon(groupsComponent.getChangedIcon(!Objects.equals(e.getValue(), assignmentExtDs.getItem() != null ?
                        assignmentExtDs.getItem().getSubstituteEmployee() : null))));
        ((PickerField<Entity>) grid.getComponentNN("oldSubstitutedEmployee")).addValueChangeListener(e -> ((Label) grid.getComponentNN("labelSubstitutedEmployeeIcon"))
                .setIcon(groupsComponent.getChangedIcon(!Objects.equals(e.getValue(), pickerSubstitutedEmployee.getValue()))));

        newPosition.addValueChangeListener(e -> ((Label) grid.getComponentNN("labelPositionIcon"))
                .setIcon(groupsComponent.getChangedIcon(!Objects.equals(e.getValue(), assignmentExtDs.getItem() != null ? assignmentExtDs.getItem().getPositionGroup() : null))));
        ((PickerField<Entity>) grid.getComponentNN("oldPosition")).addValueChangeListener(e -> ((Label) grid.getComponentNN("labelPositionIcon"))
                .setIcon(groupsComponent.getChangedIcon(!Objects.equals(e.getValue(), newPosition.getValue()))));

        newGrade.addValueChangeListener(e -> ((Label) grid.getComponentNN("labelGradeIcon"))
                .setIcon(groupsComponent.getChangedIcon(!Objects.equals(e.getValue(), assignmentExtDs.getItem() != null ? assignmentExtDs.getItem().getGradeGroup() : null))));
        ((PickerField<Entity>) grid.getComponentNN("oldGrade")).addValueChangeListener(e -> ((Label) grid.getComponentNN("labelGradeIcon"))
                .setIcon(groupsComponent.getChangedIcon(!Objects.equals(e.getValue(), newGrade.getValue()))));

        /*newJob.addValueChangeListener(e -> ((Label) grid.getComponentNN("labelJobIcon"))
                .setIcon(groupsComponent.getChangedIcon(!Objects.equals(e.getValue(), assignmentExtDs.getItem() != null ? assignmentExtDs.getItem().getJobGroup() : null))));
        ((PickerField) grid.getComponentNN("oldJob")).addValueChangeListener(e -> ((Label) grid.getComponentNN("labelJobIcon"))
                .setIcon(groupsComponent.getChangedIcon(!Objects.equals(e.getValue(), newJob.getValue()))));*/

        organizationSs.addValueChangeListener(e -> ((Label) grid.getComponentNN("labelOrganizationIcon"))
                .setIcon(groupsComponent.getChangedIcon(!Objects.equals(e.getValue() != null ? ((OrganizationSsView) e.getValue()).getOrganizationGroup() : null,
                        assignmentExtDs.getItem() != null ? assignmentExtDs.getItem().getOrganizationGroup() : null))));
        ((PickerField<Entity>) grid.getComponentNN("oldDepartment")).addValueChangeListener(e -> ((Label) grid.getComponentNN("labelOrganizationIcon"))
                .setIcon(groupsComponent.getChangedIcon(!Objects.equals(e.getValue(), organizationSs.getValue() != null ? ((OrganizationSsView) organizationSs.getValue()).getOrganizationGroup() : null))));
    }

    @Override
    protected void postInit() {
        super.postInit();
        listener();

        groupsComponent.setInDateGroups(getItem().getPositionGroup(),
                getItem().getOrganizationGroup(),
                getItem().getJobGroup(),
                getItem().getGradeGroup(), getItem().getDateFrom());
    }

    @Override
    protected void initLookupActions() {
        groupsComponent.addLookupActionGradeGroup(newGrade, this, "dateFrom")
                .setLookupScreenParamsSupplier(() -> ParamsMap.of("date", getItem().getDateFrom()));

        /*groupsComponent.addLookupActionJobGroup(newJob, this, "dateFrom")
                .setLookupScreenParamsSupplier(getParams("job"));*/

        groupsComponent.addLookupActionPositionGroup(newPosition, this, "dateFrom")
                .setLookupScreenParamsSupplier(getParams("pos"));


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
                groupExt.setList(Collections.singletonList(personExt));
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

        organizationSs.getLookupAction().setLookupScreenParamsSupplier(getParams("org"));

        pickerSubstitutedEmployee.getLookupAction().setLookupScreen("base$SubstitutedEmployee.browse");
        pickerSubstitutedEmployee.getLookupAction().setLookupScreenParamsSupplier(() ->
                ParamsMap.of("onlyEmployee", true,
                        "personGroupId", Optional.ofNullable(getItem().getPersonGroup()).map(BaseUuidEntity::getId).orElse(null),
                        "date", newDateFrom.getValue()));
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
            if (newPosition.getValue() != null && !type.equals("pos")) {
                PositionGroupExt positionGroupExt = (PositionGroupExt) newPosition.getValue();
                PositionExt position = positionGroupExt != null && getItem().getDateFrom() != null ?
                        positionGroupExt.getPositionInDate(getItem().getDateFrom()) : null;
                Optional.ofNullable(position).ifPresent(positionExt -> {
                    params.put("jobGroupId", position.getJobGroup() != null ? position.getJobGroup().getId() : null);
                    params.put("orgGroupId", position.getOrganizationGroupExt() != null ? position.getOrganizationGroupExt().getId() : null);
                });
            }
            params.put("date", getItem().getDateFrom());
            params.put("posOrgJob", params.containsKey("orgGroupId") || params.containsKey("jobGroupId"));
            return params;
        };
    }

    protected void addListeners() {
        personGroupField.addValueChangeListener(e -> newAssignmentListener());
        newDateFrom.addValueChangeListener(e -> {
            newAssignmentListener();
            newPosition.setValue(null);
        });

        oldDateFrom.addValueChangeListener(e -> {
            if (oldDateFrom.getValue() != null) {
                if (newDateFrom.getValue() != null && newDateFrom.getValue().before(oldDateFrom.getValue())) {
                    newDateFrom.setValue(oldDateFrom.getValue());
                }
                newDateFrom.setRangeStart(oldDateFrom.getValue());
            }
        });

        newPosition.addValueChangeListener(e -> {
            if (!Objects.equals(e.getValue(), e.getPrevValue())) {
                PositionGroupExt positionGroupExt = (PositionGroupExt) e.getValue();
                PositionExt position = positionGroupExt != null && getItem().getDateFrom() != null ? positionGroupExt.getPositionInDate(getItem().getDateFrom()) : null;
                groupsComponent.setInDateGroups(position, getItem().getDateFrom());
                newGrade.setValue(position != null ? position.getGradeGroup() : null);
//                newJob.setValue(position != null ? position.getJobGroup() : null);
                setOrganization(position != null ? position.getOrganizationGroupExt() : null);
                listener();
            }
        });

        organizationSs.addValueChangeListener(e -> listener());
        newGrade.addValueChangeListener(e -> listener());
//        newJob.addValueChangeListener(e -> listener());
    }

    protected void setOrganization(OrganizationGroupExt organizationGroupExt) {
        if (getItem().getDateFrom() != null) {
            Optional.ofNullable(organizationGroupExt).ifPresent(o -> o.getOrganizationInDate(getItem().getDateFrom()));
        }
        organizationSs.setValue(organizationGroupExt != null ? selfService.getOrganizationSsView(organizationGroupExt, getItem().getDateFrom()) : null);
        getItem().setOrganizationGroup(organizationGroupExt);
    }

    protected void listener() {
        procActionButtonHBox.setEnabled(isValueChanged());
    }

    protected boolean isValueChanged() {
        AssignmentExt assignmentExt = assignmentExtDs.getItem();
        return (assignmentExt == null) ||
                !Objects.equals(getItem().getPositionGroup(), assignmentExt.getPositionGroup()) ||
                !Objects.equals(getItem().getJobGroup(), assignmentExt.getJobGroup()) ||
                !Objects.equals(getItem().getOrganizationGroup(), assignmentExt.getOrganizationGroup()) ||
                !Objects.equals(getItem().getGradeGroup(), assignmentExt.getGradeGroup());
    }

    protected void newAssignmentListener() {
        if (personGroupField.getValue() != null && newDateFrom.getValue() != null) {
            AssignmentExt assignmentExt = employeeService.getAssignmentExt(getItem().getPersonGroup().getId(), newDateFrom.getValue(), "assignmentExt.bpm.view");
            groupsComponent.setInDateGroups(assignmentExt, newDateFrom.getValue());
            assignmentExtDs.setItem(assignmentExt);
            listener();
        }
    }

    @Override
    protected void initEditableFields() {
        if (!isDraft()) {
            personGroupField.setEditable(false);
            newDateFrom.setEditable(false);
            newPosition.setEditable(false);
            newGrade.setEditable(false);
            organizationSs.setEditable(false);
//            newJob.setEditable(false);
            note.setEditable(false);
            attachmentField.setEditable(false);
            picker.setEditable(false);
            pickerSubstitutedEmployee.setEditable(false);
        }

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
    }

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
        list.add("tsadv_temporary_translation_request");
        return list;
    }

    @Override
    public void ready() {
        super.ready();
        if (getItem().getDateFrom() != null) {
            if (getItem().getPersonGroup() != null) {
                Date date = DateUtils.addDays(getItem().getDateFrom(), -1);
                MyTeamNew myTeam = metadata.create(MyTeamNew.class);
                myTeam.setFullName(getItem().getPersonGroup().getFioWithEmployeeNumber());
                myTeam.setPersonGroupId(getItem().getPersonGroup().getId());
                picker.setValue(myTeam);
                AssignmentExt assignmentExt = employeeService.getAssignmentExt(getItem().getPersonGroup().getId(), date, "assignmentExt.bpm.view");
                groupsComponent.setInDateGroups(assignmentExt, date);
                assignmentExtDs.setItem(assignmentExt);
            }

            if (getItem().getOrganizationGroup() != null) {
                getItem().getOrganizationGroup().getOrganizationInDate(getItem().getDateFrom());
                organizationSs.setValue(selfService.getOrganizationSsView(getItem().getOrganizationGroup(), getItem().getDateFrom()));
            }

            Optional.ofNullable(getItem().getPositionGroup()).ifPresent(p -> p.getPositionInDate(getItem().getDateFrom()));
        }

        if (isCurrentUserInitiator()) {
            ((Table) getComponentNN("procTasksTable")).removeColumn(((Table) getComponentNN("procTasksTable")).getColumn("comment"));
        }

        newPosition.setValue(getItem().getPositionGroup());
        addListeners();
    }

    @Override
    protected void overrideProcActionComponents(Component component) {
        if (component instanceof VBoxLayout) {
            Collection<Component> vBoxComp = ((VBoxLayout) component).getComponents();
            changeSettingsProcActionBtn(vBoxComp);
            procActionsFrame.remove(component);
            vBoxComp.forEach(component1 -> {
                component1.setParent(null);
                changeSettingsProcActionBtn(vBoxComp);
                component1.setWidthAuto();
                if (component1 instanceof Button && "completeTask_approve".equals(((Button) component1).getAction().getId())
                        && getItem().getPositionGroup().getPositionInDate(getItem().getDateFrom()) != null
                        && (Optional.ofNullable(getItem().getPositionGroup().getPositionInDate(getItem().getDateFrom()).getFte()).orElse(0.0) <= 0
                        || !Optional.ofNullable(getItem().getPositionGroup().getPositionInDate(getItem().getDateFrom())
                        .getPositionStatus().getCode()).orElse("ACTIVE").equalsIgnoreCase("ACTIVE"))
                        && bpmUtils.getActiveProcTask(procActionsFrame.getProcInstance().getId(), "procTask-frame-extended").getProcActor().getProcRole().getCode().equals("HR_SPECIALIST")) {
                    Action action = getNewApproveAction((Button) component1, ((Button) component1).getAction());
                    ((Button) component1).setAction(action);
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
        if (picker.getValue() == null) {
            errors.add(getMessage("errorPersonGroup"));
        }
        super.postValidate(errors);
        Date oldDate = oldDateFrom.getValue();
        if (getItem().getPersonGroup() != null && getItem().getDateFrom() != null &&
                !assignmentSalaryService.isLastAssignment(getItem().getPersonGroup().getId(),
                        oldDate != null && oldDate.before(getItem().getDateFrom()) ? oldDate : DateUtils.addDays(getItem().getDateFrom(), -1))) {
            errors.add(getMessage("lastAssignmentError"));
        }
    }

    @Override
    protected String getProcessName() {
        return PROCESS_NAME;
    }
}