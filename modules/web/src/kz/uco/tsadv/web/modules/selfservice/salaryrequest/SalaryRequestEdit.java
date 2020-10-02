package kz.uco.tsadv.web.modules.selfservice.salaryrequest;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.web.gui.components.WebGridLayout;
import kz.uco.base.entity.dictionary.DicCurrency;
import kz.uco.tsadv.entity.MyTeamNew;
import kz.uco.tsadv.exceptions.ItemNotFoundException;
import kz.uco.tsadv.global.common.CommonUtils;
import kz.uco.tsadv.modules.personal.enums.GrossNet;
import kz.uco.tsadv.modules.personal.enums.SalaryType;
import kz.uco.tsadv.modules.personal.model.AssignmentExt;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import kz.uco.tsadv.modules.personal.model.Salary;
import kz.uco.tsadv.modules.personal.model.SalaryRequest;
import kz.uco.tsadv.service.AssignmentSalaryService;
import kz.uco.tsadv.service.EmployeeService;
import kz.uco.tsadv.web.bpm.editor.AbstractBpmEditor;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.util.CollectionUtils;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

public class SalaryRequestEdit<T extends SalaryRequest> extends AbstractBpmEditor<T> {

    public static final String PROCESS_NAME = "salaryRequestApproval";
    protected boolean isAmountChange = false;
    protected boolean readOnly = true;

    @Inject
    protected EmployeeService employeeService;
    @Inject
    protected AssignmentSalaryService assignmentSalaryService;
    @Inject
    protected FieldGroup fieldGroup;
    @Inject
    protected Button buttonOkId;
    @Inject
    protected TextField<Double> newAmount, newPercent, oldAmount;
    @Inject
    protected PickerField picker;
    @Inject
    protected LookupField<Object> newCurrency, newNetGross, newSalaryType, oldCurrency, oldNetGross, oldSalaryType, reason;
    @Inject
    protected DateField<Date> oldDateFrom, newDateFrom;
    @Inject
    protected ResizableTextArea note;
    @Named("fieldGroup2.attachment")
    protected FileUploadField attachmentField;
    @Named("fieldGroup.assignmentGroup")
    protected PickerField assignmentGroup;

    @Override
    protected void initIconListeners() {
        WebGridLayout grid = ((WebGridLayout) getComponentNN("grid"));

        newAmount.addValueChangeListener(e -> ((Label) grid.getComponentNN("labelAmountIcon"))
                .setIcon(groupsComponent.getChangedIcon(!Objects.equals(e.getValue(), oldAmount.getValue()))));
        oldAmount.addValueChangeListener(e -> ((Label) grid.getComponentNN("labelAmountIcon"))
                .setIcon(groupsComponent.getChangedIcon(!Objects.equals(e.getValue(), newAmount.getValue()))));

        newSalaryType.addValueChangeListener(e -> ((Label) grid.getComponentNN("labelSalaryTypeIcon"))
                .setIcon(groupsComponent.getChangedIcon(!Objects.equals(e.getValue(), oldSalaryType.getValue()))));
        oldSalaryType.addValueChangeListener(e -> ((Label) grid.getComponentNN("labelSalaryTypeIcon"))
                .setIcon(groupsComponent.getChangedIcon(!Objects.equals(e.getValue(), newSalaryType.getValue()))));

        newNetGross.addValueChangeListener(e -> ((Label) grid.getComponentNN("labelNetGrossIcon"))
                .setIcon(groupsComponent.getChangedIcon(!Objects.equals(e.getValue(), oldNetGross.getValue()))));
        oldNetGross.addValueChangeListener(e -> ((Label) grid.getComponentNN("labelNetGrossIcon"))
                .setIcon(groupsComponent.getChangedIcon(!Objects.equals(e.getValue(), newNetGross.getValue()))));

        newCurrency.addValueChangeListener(e -> ((Label) grid.getComponentNN("labelCurrencyIcon"))
                .setIcon(groupsComponent.getChangedIcon(!Objects.equals(e.getValue(), oldCurrency.getValue()))));
        oldCurrency.addValueChangeListener(e -> ((Label) grid.getComponentNN("labelCurrencyIcon"))
                .setIcon(groupsComponent.getChangedIcon(!Objects.equals(e.getValue(), newCurrency.getValue()))));
    }

    @Override
    protected void initNewItem(T item) {
        super.initNewItem(item);
        item.setCurrency(commonService.getEntity(DicCurrency.class, ("KZT")));
        item.setNetGross(GrossNet.GROSS);
        item.setStartDate(CommonUtils.getSystemDate());
        item.setEndDate(CommonUtils.getEndOfTime());
        item.setType(SalaryType.monthlyRate);
    }

    @Override
    protected void postInit() {
        readOnly = !isDraft();
        super.postInit();

        setPickerSettings();
        listener();
    }

    @Override
    protected void initEditableFields() {
        super.initEditableFields();
        note.setEditable(!readOnly);
        newDateFrom.setEditable(!readOnly);
        attachmentField.setEditable(!readOnly);
        newSalaryType.setEditable(!readOnly);
        newAmount.setEditable(!readOnly);
        newCurrency.setEditable(!readOnly);
        newNetGross.setEditable(!readOnly);
        newPercent.setEditable(!readOnly);
        picker.setEditable(!readOnly);
        reason.setEditable(!readOnly);
    }

    @Override
    protected void initVisibleFields() {
        super.initVisibleFields();
        buttonOkId.setVisible(!readOnly);
    }

    protected void setPickerSettings() {
        picker.setMetaClass(metadata.getClass(MyTeamNew.class));
        picker.setCaptionMode(CaptionMode.PROPERTY);
        picker.setCaptionProperty("fullName");
        addLookupAction(picker.addLookupAction());
    }

    protected void addLookupAction(PickerField.LookupAction lookupAction) {
        lookupAction.setLookupScreen("tsadv$AssignmentMyTeam.browse");
        lookupAction.setAfterLookupSelectionHandler(items -> {
            if (!CollectionUtils.isEmpty(items)) {
                newAssignmentSalaryListener();
            }
        });
    }

    protected void newAssignmentSalaryListener() {
        if (newDateFrom.getValue() != null && picker.getValue() != null) {
            UUID id = ((MyTeamNew) picker.getValue()).getPersonGroupId();
            if (id == null) {
                picker.setValue(null);
                throw new ItemNotFoundException(getMessage("noPersonGroup"));
            }
            AssignmentExt assignmentExt = employeeService.getAssignmentExt(id,
                    newDateFrom.getValue(), "assignmentExt.bpm.view");
            if (assignmentExt != null) {
                assignmentGroup.setValue(assignmentExt.getGroup());
                Salary oldSalary = employeeService.getSalary(assignmentExt.getGroup(), newDateFrom.getValue(), "salary.view");
                getItem().setOldSalary(oldSalary);
                if (oldSalary != null) {
                    oldDateFrom.setValue(oldSalary.getStartDate());
                    oldAmount.setValue(oldSalary.getAmount());
                    oldSalaryType.setValue(oldSalary.getType());
                    oldNetGross.setValue(oldSalary.getNetGross());
                    oldCurrency.setValue(oldSalary.getCurrency());

                    if (oldSalary.getAmount() != null && newAmount.getValue() != null) {
                        newPercent.setValue(100.0 * (double) newAmount.getValue() / ((double) oldAmount.getValue()) - 100);
                    }
                }
            } else {
                showNotification(getMessage("noAssignment"));
            }
            buttonOkId.setEnabled(assignmentExt != null);
            procActionButtonHBox.setEnabled(assignmentExt != null);
            listener();
        }
    }

    protected void setListener() {
        picker.addValueChangeListener(e -> newAssignmentSalaryListener());
        newDateFrom.addValueChangeListener(e -> newAssignmentSalaryListener());

        oldDateFrom.addValueChangeListener(e -> {
            if (oldDateFrom.getValue() != null) {
                if (newDateFrom.getValue() != null && newDateFrom.getValue().before(oldDateFrom.getValue())) {
                    newDateFrom.setValue(oldDateFrom.getValue());
                }
                newDateFrom.setRangeStart(oldDateFrom.getValue());
            }
        });

        oldAmount.addValueChangeListener(e -> {
            if (e.getValue() == null) {
                newPercent.setValue(null);
            }
            newPercent.setEditable(e.getValue() != null);
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

        newCurrency.addValueChangeListener(e -> listener());
        newNetGross.addValueChangeListener(e -> listener());
        newSalaryType.addValueChangeListener(e -> listener());
    }

    protected void listener() {
        procActionButtonHBox.setEnabled(isChanged());
    }

    protected boolean isChanged() {
        Salary salary = getItem().getOldSalary();
        return (salary == null) ||
                Objects.equals(salary.getCurrency(), getItem().getCurrency()) ||
                Objects.equals(salary.getNetGross(), getItem().getNetGross()) ||
                Objects.equals(salary.getAmount(), getItem().getAmount()) ||
                Objects.equals(salary.getType(), getItem().getType());
    }

    @Override
    public void ready() {
        super.ready();

        if (getItem().getAssignmentGroup() != null && getItem().getStartDate() != null) {
            Date oldDate = DateUtils.addDays(getItem().getStartDate(), -1);
            PersonExt personExt = commonService.getEntities(PersonExt.class,
                    "select p from base$PersonExt p " +
                            "   join base$AssignmentExt s " +
                            "       on p.group.id = s.personGroup.id " +
                            "   where s.group.id = :id " +
                            "       and :date between p.startDate and p.endDate ",
                    ParamsMap.of("id", getItem().getAssignmentGroup().getId(), "date", oldDate),
                    "person-view").get(0);

            MyTeamNew myTeamNew = metadata.create(MyTeamNew.class);
            myTeamNew.setFullName(personExt.getFioWithEmployeeNumber());
            myTeamNew.setPersonGroupId(personExt.getGroup().getId());

            picker.setValue(myTeamNew);

            Salary salary = employeeService.getSalary(getItem().getAssignmentGroup(), oldDate, "salary.view");
            getItem().setOldSalary(salary);
            if (salary != null) {
                oldDateFrom.setValue(salary.getStartDate());
                oldCurrency.setValue(salary.getCurrency());
                oldAmount.setValue(salary.getAmount());
                oldSalaryType.setValue(salary.getType());
                oldNetGross.setValue(salary.getNetGross());

                if (salary.getAmount() != null && newAmount.getValue() != null) {
                    newPercent.setValue(100.0 * (double) newAmount.getValue() / ((double) oldAmount.getValue()) - 100);
                }

                oldDateFrom.setValue(salary.getStartDate());
            }
        }

        if (oldAmount.getValue() != null && newAmount.getValue() != null) {
            newPercent.setValue(100.0 * (double) newAmount.getValue() / ((double) oldAmount.getValue()) - 100);
        }

        newPercent.setEditable(!readOnly && oldAmount.getValue() != null);


        if (isCurrentUserInitiator()) {
            ((Table) getComponentNN("procTasksTable")).removeColumn(((Table) getComponentNN("procTasksTable")).getColumn("comment"));
        }

        listener();
        setListener();
    }

    @Override
    protected boolean alreadyHasRequest(UUID statusId) {
        Map<Integer, Object> params = new HashMap<>();
        params.put(1,
                employeeService.getPersonGroupByAssignmentGroupId(getItem().getAssignmentGroup().getId()));
        params.put(2, statusId);
        params.put(3, getItem().getStartDate());
        if (assignmentSalaryService.isHaveRequest(getEntityNameRequestList(), params)) {
            throw new ItemNotFoundException(getMessage("haveRequest"));
        }
        return false;
    }

    protected List<String> getEntityNameRequestList() {
        List<String> list = new ArrayList<>();
        list.add("tsadv_assignment_salary_request");
        list.add("tsadv_salary_request");
        return list;
    }

    @Override
    protected void postValidate(ValidationErrors errors) {
        super.postValidate(errors);
        if (picker.getValue() == null) {
            errors.add(getMessage("errorPersonGroup"));
        }
        Date oldDate = oldDateFrom.getValue();
        if (getItem().getAssignmentGroup() != null && getItem().getStartDate() != null &&
                !assignmentSalaryService.isLastSalary(getItem().getAssignmentGroup().getId(), oldDate != null ? oldDate : getItem().getStartDate())) {
            errors.add(getMessage("lastSalaryError"));
        }
    }

    @Override
    protected String getProcessName() {
        return PROCESS_NAME;
    }
}