package kz.uco.tsadv.web.temporarytranslationrequest;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.web.gui.components.WebGridLayout;
import kz.uco.tsadv.entity.MyTeamNew;
import kz.uco.tsadv.entity.dbview.OrganizationSsView;
import kz.uco.tsadv.entity.tb.TemporaryTranslationRequest;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Supplier;

public class TemporaryTranslationRequestEdit<T extends TemporaryTranslationRequest> extends AbstractBpmEditor<T> {
    public static final String PROCESS_NAME = "temporaryTranslationRequest";

    /*@Inject
    protected SelfService selfService;
    @Inject
    protected EmployeeService employeeService;
    @Inject
    protected AssignmentSalaryService assignmentSalaryService;
    @Inject
    protected Datasource<AssignmentExt> assignmentExtDs;
    @Inject
    protected Datasource<TemporaryTranslationRequest> temporaryTranslationRequestDs;
    @Inject
    protected PickerField<Entity> picker, organizationSs, newGrade, newPosition, pickerSubstitutedEmployee;
    @Named("fieldGroup1.personGroup")
    protected PickerField personGroupField;
    @Inject
    protected DateField<Date> oldDateFrom, newDateFrom, newDateEnd;
    @Inject
    protected ResizableTextArea reason;
    *//*@Inject
    protected PickerField newJob;*//*

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

        *//*newJob.addValueChangeListener(e -> ((Label) grid.getComponentNN("labelJobIcon"))
                .setIcon(groupsComponent.getChangedIcon(!Objects.equals(e.getValue(), assignmentExtDs.getItem() != null ? assignmentExtDs.getItem().getJobGroup() : null))));
        ((PickerField) grid.getComponentNN("oldJob")).addValueChangeListener(e -> ((Label) grid.getComponentNN("labelJobIcon"))
                .setIcon(groupsComponent.getChangedIcon(!Objects.equals(e.getValue(), newJob.getValue()))));*//*

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
                getItem().getGradeGroup(), getItem().getStartDate());
    }

    protected void initLookupActions() {
        groupsComponent.addLookupActionGradeGroup(newGrade, this, "startDate")
                .setLookupScreenParamsSupplier(() -> ParamsMap.of("date", getItem().getStartDate()));

        *//*groupsComponent.addLookupActionJobGroup(newJob, this, "startDate")
                .setLookupScreenParamsSupplier(getParams("job"));*//*

        groupsComponent.addLookupActionPositionGroup(newPosition, this, "startDate")
                .setLookupScreenParamsSupplier(getParams("pos"));

        PickerField.LookupAction lookupActionOrganization = organizationSs.addLookupAction();
        lookupActionOrganization.setLookupScreen("tsadv$OrganizationSsView.browse");
        lookupActionOrganization.setAfterLookupSelectionHandler(items -> {
            getItem().setOrganizationGroup(CollectionUtils.isEmpty(items) ? null : ((OrganizationSsView) items.iterator().next()).getOrganizationGroup());
            listener();
        });

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
            if (organizationSs.getValue() != null && !type.equalsIgnoreCase("org") && ((OrganizationSsView) organizationSs.getValue()).getOrganizationGroup() != null) {
                params.put("orgGroupId", ((OrganizationSsView) organizationSs.getValue()).getOrganizationGroup().getId());
            }
            *//*if (newJob.getValue() != null && !type.equalsIgnoreCase("job")) {
                params.put("jobGroupId", ((JobGroup) newJob.getValue()).getId());
            }*//*
            if (newPosition.getValue() != null && !type.equals("pos") && getItem().getStartDate() != null) {
                PositionGroupExt positionGroupExt = (PositionGroupExt) newPosition.getValue();
                PositionExt position = positionGroupExt.getPositionInDate(getItem().getStartDate());
                if (position != null) {
                    params.put("orgGroupId", position.getOrganizationGroupExt() != null ? position.getOrganizationGroupExt().getId() : null);
                    params.put("jobGroupId", position.getJobGroup() != null ? position.getJobGroup().getId() : null);
                }
            }
            params.put("date", getItem().getStartDate());
            params.put("posOrgJob", params.containsKey("orgGroupId") || params.containsKey("jobGroupId"));
            return params;
        };
    }

    @Override
    protected void initEditableFields() {
        super.initEditableFields();
        if (!isDraft()) {
            newDateFrom.setEditable(false);
            personGroupField.setEditable(false);
            newPosition.setEditable(false);
            organizationSs.setEditable(false);
//            newJob.setEditable(false);
            newGrade.setEditable(false);
            pickerSubstitutedEmployee.setEditable(false);
            picker.setEditable(false);
            newDateEnd.setEditable(false);
            ((ResizableTextArea) getComponentNN("reason")).setEditable(false);

            ((FieldGroup) getComponentNN("fieldGroup2")).getFieldNN("attachment").setEditable(false);
            ((FieldGroup) getComponentNN("fieldGroup2")).getFieldNN("note").setEditable(false);
        }
    }

    @Override
    protected void initVisibleFields() {
        super.initVisibleFields();
        getComponentNN("buttonOkId").setVisible(!isDraft());

        WebGridLayout grid = ((WebGridLayout) getComponentNN("grid"));
        boolean isActualPositionVisible = getItem().getActualPositionGroup() != null
                && getItem().getStatus() != null
                && getItem().getStatus().getCode().matches("APPROVED|REJECTED");

        grid.getComponentNN("actualPositionGroup").setVisible(isActualPositionVisible);
        grid.getComponentNN("actualPositionGroupLabel").setVisible(isActualPositionVisible);
        grid.getComponentNN("emptyLbl").setVisible(isActualPositionVisible);
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
                newDateEnd.setRangeStart(oldDateFrom.getValue());
            }
        });

        newPosition.addValueChangeListener(e -> {
            PositionGroupExt positionGroupExt = (PositionGroupExt) e.getValue();
            PositionExt position = positionGroupExt != null && getItem().getStartDate() != null ? positionGroupExt.getPositionInDate(getItem().getStartDate()) : null;
            groupsComponent.setInDateGroups(position, getItem().getStartDate());
//            newJob.setValue(position != null ? position.getJobGroup() : null);
            setOrganization(position != null ? position.getOrganizationGroupExt() : null);
            newGrade.setValue(position != null ? position.getGradeGroup() : null);
            getItem().setPositionGroup(positionGroupExt);

            listener();
        });

        pickerSubstitutedEmployee.addValueChangeListener(e -> listener());
        organizationSs.addValueChangeListener(e -> listener());
        newGrade.addValueChangeListener(e -> listener());
//        newJob.addValueChangeListener(e -> listener());
    }

    protected void setOrganization(OrganizationGroupExt organizationGroupExt) {
        if (getItem().getStartDate() != null) {
            Optional.ofNullable(organizationGroupExt).ifPresent(o -> o.getOrganizationInDate(getItem().getStartDate()));
        }
        organizationSs.setValue(organizationGroupExt != null && getItem().getStartDate() != null ?
                selfService.getOrganizationSsView(organizationGroupExt, getItem().getStartDate()) : null);
        getItem().setOrganizationGroup(organizationGroupExt);
    }

    protected void newAssignmentListener() {
        if (personGroupField.getValue() != null && newDateFrom.getValue() != null) {
            AssignmentExt assignmentExt = employeeService.getAssignmentExt(getItem().getPersonGroup().getId(), newDateFrom.getValue(), "assignmentExt.bpm.view");
            groupsComponent.setInDateGroups(assignmentExt, newDateFrom.getValue());
            assignmentExtDs.setItem(assignmentExt);
            listener();
        }
    }

    protected void listener() {
        procActionButtonHBox.setEnabled(isValueChanged());
    }

    protected boolean isValueChanged() {
        AssignmentExt assignmentExt = assignmentExtDs.getItem();
        return (assignmentExt == null) ||
                !Objects.equals(getItem().getPositionGroup(), assignmentExt.getPositionGroup()) ||
                !Objects.equals(getItem().getOrganizationGroup(), assignmentExt.getOrganizationGroup()) ||
                !Objects.equals(getItem().getJobGroup(), assignmentExt.getJobGroup()) ||
                !Objects.equals(getItem().getGradeGroup(), assignmentExt.getGradeGroup()) ||
                !Objects.equals(assignmentExt.getSubstituteEmployee(), getItem().getSubstitutedEmployee());
    }

    @Override
    protected boolean alreadyHasRequest(UUID statusId) {
        Map<Integer, Object> params = new HashMap<>();
        params.put(2, statusId);
        params.put(1, getItem().getPersonGroup());
        params.put(3, getItem().getStartDate());
        if (assignmentSalaryService.isHaveRequest(getEntityNameRequestList(), params)) {
            throw new ItemNotFoundException(getMessage("haveRequest"));
        }
        return false;
    }

    protected List<String> getEntityNameRequestList() {
        List<String> list = new ArrayList<>();
        list.add("tsadv_assignment_request");
        list.add("tsadv_temporary_translation_request");
        list.add("tsadv_assignment_salary_request");
        return list;
    }

    @Override
    public void ready() {
        super.ready();

        if (getItem().getStartDate() != null) {
            if (getItem().getPersonGroup() != null) {
                MyTeamNew myTeam = metadata.create(MyTeamNew.class);
                myTeam.setFullName(getItem().getPersonGroup().getFioWithEmployeeNumber());
                myTeam.setPersonGroupId(getItem().getPersonGroup().getId());
                picker.setValue(myTeam);
                Date date = DateUtils.addDays(getItem().getStartDate(), -1);
                AssignmentExt assignmentExt = employeeService.getAssignmentExt(getItem().getPersonGroup().getId(),
                        date,
                        "assignmentExt.bpm.view");
                groupsComponent.setInDateGroups(assignmentExt, date);
                assignmentExtDs.setItem(assignmentExt);
            }

            if (getItem().getOrganizationGroup() != null) {
                organizationSs.setValue(selfService.getOrganizationSsView(getItem().getOrganizationGroup(), getItem().getStartDate()));
            }

            Optional.ofNullable(getItem().getPositionGroup()).ifPresent(p -> p.getPositionInDate(getItem().getStartDate()));
        }

        newPosition.setValue(getItem().getPositionGroup());

        if (isCurrentUserInitiator()) {
            ((Table) getComponentNN("procTasksTable")).removeColumn(((Table) getComponentNN("procTasksTable")).getColumn("comment"));
        }

        addListeners();
    }

    @Override
    protected void overrideProcActionComponents(Component component) {
        if (component instanceof VBoxLayout) {
            Collection<Component> vBoxComp = ((VBoxLayout) component).getComponents();
            changeSettingsProcActionBtn(vBoxComp);
            procActionsFrame.remove(component);
            vBoxComp.forEach(component1 -> {
                component1.setWidthAuto();
                component1.setParent(null);
                if (component1 instanceof Button && "completeTask_approve".equals(((Button) component1).getAction().getId())
                        && getItem().getPositionGroup().getPositionInDate(getItem().getStartDate()) != null
                        && (Optional.ofNullable(getItem().getPositionGroup().getPositionInDate(getItem().getStartDate()).getFte()).orElse(0.0) <= 0
                        || !Optional.ofNullable(getItem().getPositionGroup().getPositionInDate(getItem().getStartDate())
                        .getPositionStatus().getCode()).orElse("ACTIVE").equalsIgnoreCase("ACTIVE"))
                        && bpmUtils.getActiveProcTask(procActionsFrame.getProcInstance().getId(), "procTask-frame-extended").getProcActor().getProcRole().getCode().equals("HR_SPECIALIST")) {
                    Button button = (Button) component1;
                    Action action = getNewApproveAction(button, button.getAction());
                    button.setAction(action);
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
        if (getItem().getPersonGroup() != null && getItem().getStartDate() != null &&
                !assignmentSalaryService.isLastAssignment(getItem().getPersonGroup().getId(),
                        oldDate != null && oldDate.before(getItem().getStartDate()) ? oldDate : DateUtils.addDays(getItem().getStartDate(), -1))) {
            errors.add(getMessage("lastAssignmentError"));
        }

        if (newDateEnd.getValue() != null && newDateFrom.getValue() != null) {
            try {
                SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                Date endDate = format.parse(format.format(newDateEnd.getValue()));
                Date startDate = format.parse(format.format(newDateFrom.getValue()));
                if (endDate.before(startDate))
                    errors.add(getMessage("dateError"));
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    protected String getProcessName() {
        return PROCESS_NAME;
    }*/
}