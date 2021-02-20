package kz.uco.tsadv.web.screens.absencerequest;

import com.haulmont.addon.bproc.web.processform.Outcome;
import com.haulmont.addon.bproc.web.processform.ProcessForm;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.base.common.StaticVariable;
import kz.uco.tsadv.entity.bproc.AbstractBprocRequest;
import kz.uco.tsadv.modules.personal.dictionary.DicAbsenceType;
import kz.uco.tsadv.modules.personal.model.AbsenceRequest;
import kz.uco.tsadv.service.AbsenceService;
import kz.uco.tsadv.service.AssignmentService;
import kz.uco.tsadv.service.EmployeeNumberService;
import kz.uco.tsadv.web.abstraction.bproc.AbstractBprocEditor;

import javax.inject.Inject;
import java.util.Date;

@UiController("tsadv$AbsenceRequestNew.edit")
@UiDescriptor("absence-request-new-edit.xml")
@EditedEntityContainer("absenceRequestDc")
@LoadDataBeforeShow
@ProcessForm(
        outcomes = {
                @Outcome(id = AbstractBprocRequest.OUTCOME_REVISION),
                @Outcome(id = AbstractBprocRequest.OUTCOME_APPROVE),
                @Outcome(id = AbstractBprocRequest.OUTCOME_REJECT)
        }
)
public class AbsenceRequestNewEdit extends AbstractBprocEditor<AbsenceRequest> {

    @Inject
    protected InstanceContainer<AbsenceRequest> absenceRequestDc;

    @Inject
    protected DataManager dataManager;
    @Inject
    protected AssignmentService assignmentService;
    @Inject
    protected EmployeeNumberService employeeNumberService;
    @Inject
    protected AbsenceService absenceService;

    @Inject
    protected LookupPickerField<DicAbsenceType> typeField;
    @Inject
    protected HBoxLayout distanceWorkingConfirmBox;
    @Inject
    protected TextField<String> vacationDurationTypeField;
    @Inject
    protected CheckBox distanceWorkingConfirm;
    @Inject
    protected Form form;
    @Inject
    protected DateField<Date> dateFromField;

    @Subscribe
    public void onInit(InitEvent event) {
        typeField.addValueChangeListener(e -> {
            boolean visible = hasStatus("APPROVING")
                    && e.getValue() != null
                    && "RJ".equals(e.getValue().getCode());
            distanceWorkingConfirmBox.setVisible(visible);

            if (e.getValue() != null)
                vacationDurationTypeField.setValue(
                        absenceService.getVacationDurationType(
                                userSession.getAttribute(StaticVariable.USER_PERSON_GROUP_ID),
                                e.getValue().getId(),
                                getEditedEntity().getDateFrom())
                                .getId()
                );
        });
    }

    @Subscribe
    public void onInitEntity(InitEntityEvent<AbsenceRequest> event) {
        AbsenceRequest absenceRequest = event.getEntity();
        absenceRequest.setAssignmentGroup(assignmentService.getAssignmentGroup(userSession.getUser().getLogin()));
    }

    @Subscribe
    public void onBeforeShow(AfterShowEvent event) {
        if (!hasStatus("DRAFT")) {
            form.setEditable(false);
        }

        if (hasStatus("DRAFT")) {
            absenceRequestDc.getItem().setRequestDate(new Date());
        }
        if (absenceRequestDc.getItem().getRequestNumber() == null) {
            absenceRequestDc.getItem().setRequestNumber(employeeNumberService.generateNextRequestNumber());
            getScreenData().getDataContext().commit();
        }

        absenceRequestDc.addItemPropertyChangeListener(e -> {
            if ("dateTo".equals(e.getProperty()))
                dateFromField.setRangeEnd(getEditedEntity().getDateTo());
            if ("dateFrom".equals(e.getProperty()) || "dateTo".equals(e.getProperty()) || "type".equals(e.getProperty())) {
                Date dateFrom;
                Date dateTo;
                DicAbsenceType absenceType;
                if (((dateFrom = getEditedEntity().getDateFrom()) != null) &&
                        ((dateTo = getEditedEntity().getDateTo()) != null) &&
                        ((absenceType = getEditedEntity().getType()) != null)) {
                    getEditedEntity().setAbsenceDays(
                            absenceService.countDays(
                                    dateFrom,
                                    dateTo,
                                    absenceType.getId(),
                                    userSession.getAttribute(StaticVariable.USER_PERSON_GROUP_ID)));
                }
            }
        });
    }
}