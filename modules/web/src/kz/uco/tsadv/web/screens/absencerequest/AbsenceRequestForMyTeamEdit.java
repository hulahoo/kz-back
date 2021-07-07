package kz.uco.tsadv.web.screens.absencerequest;

import com.haulmont.addon.bproc.web.processform.Outcome;
import com.haulmont.addon.bproc.web.processform.ProcessForm;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.gui.components.CheckBox;
import com.haulmont.cuba.gui.components.DateField;
import com.haulmont.cuba.gui.components.TextField;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.entity.bproc.AbstractBprocRequest;
import kz.uco.tsadv.modules.administration.TsadvUser;
import kz.uco.tsadv.modules.personal.model.AbsenceRequest;
import kz.uco.tsadv.web.abstraction.bproc.AbstractBprocEditor;

import javax.inject.Inject;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;

@UiController("tsadv$AbsenceRequestForMyTeam.edit")
@UiDescriptor("absence-request-for-my-team-edit.xml")
@EditedEntityContainer("absenceRequestDc")
@LoadDataBeforeShow
@ProcessForm(
        outcomes = {
                @Outcome(id = AbstractBprocRequest.OUTCOME_REVISION),
                @Outcome(id = AbstractBprocRequest.OUTCOME_APPROVE),
                @Outcome(id = AbstractBprocRequest.OUTCOME_REJECT),
                @Outcome(id = AbstractBprocRequest.OUTCOME_CANCEL)
        }
)
public class AbsenceRequestForMyTeamEdit extends AbstractBprocEditor<AbsenceRequest> {
    @Inject
    protected DateField<Date> timeOfStartingField;
    @Inject
    protected InstanceContainer<AbsenceRequest> absenceRequestDc;
    @Inject
    protected DateField<Date> timeOfFinishingField;
    protected boolean isBlocked = false;
    @Inject
    protected CheckBox vacationDayField;
    @Inject
    protected TextField<String> purposeTextField;
    @Inject
    protected MessageBundle messageBundle;

    @Subscribe(id = "absenceRequestDc", target = Target.DATA_CONTAINER)
    protected void onAbsenceRequestDcItemPropertyChange(InstanceContainer.ItemPropertyChangeEvent<AbsenceRequest> event) {
        if (!isBlocked) {
            isBlocked = true;
            if (event.getProperty().equals("compencation")) {
                absenceRequestDc.getItem().setVacationDay(!event.getItem().getCompensation());
            } else if (event.getProperty().equals("vacationDay")) {
                absenceRequestDc.getItem().setCompensation(!event.getItem().getVacationDay());
            }
            isBlocked = false;
        }
        if (event.getProperty().equals("type")) {
            if (event.getItem().getType() != null) {
                String code = event.getItem().getType().getCode();
                if (code != null) {
                    setTimesNull();
                    if (code.equals("WORK_ON_WEEKEND") || code.equals("SUPERTIME_WORK")) {
                        changeHint(false);
                    } else if (code.equals("TEMPORARY_TRANSFER")) {
                        changeHint(true);
                    } else {
                        changeHint(false);
                    }
                }
            }
        } else if (event.getProperty().equals("purpose")) {
            purposeTextField.setVisible(event.getItem().getPurpose() != null
                    && event.getItem().getPurpose().getCode() != null
                    && event.getItem().getPurpose().getCode().equals("OTHER"));
        } else if (event.getProperty().equals("timeOfStarting") || event.getProperty().equals("timeOfFinishing")) {
            if (event.getItem().getTimeOfFinishing() != null && event.getItem().getTimeOfStarting() != null) {
                Duration between = Duration.between(
                        convertToLocalDateTimeViaSqlTimestamp(event.getItem().getTimeOfStarting()),
                        convertToLocalDateTimeViaSqlTimestamp(event.getItem().getTimeOfFinishing()));
                absenceRequestDc.getItem().setTotalHours(Integer.valueOf(Long.valueOf(between.toHours()).toString()));
            } else {
                absenceRequestDc.getItem().setTotalHours(null);
            }

        }


    }

    protected LocalDateTime convertToLocalDateTimeViaSqlTimestamp(Date dateToConvert) {
        return new Timestamp(dateToConvert.getTime()).toLocalDateTime();
    }

    private void changeHint(boolean hint) {
        if (hint) {
            timeOfFinishingField.setDescription(messageBundle.getMessage("temporary_transfer_end"));
            timeOfStartingField.setDescription(messageBundle.getMessage("temporary_transfer_start"));
            timeOfFinishingField.setContextHelpText(messageBundle.getMessage("temporary_transfer_end"));
            timeOfStartingField.setContextHelpText(messageBundle.getMessage("temporary_transfer_start"));
        } else {
            timeOfFinishingField.setDescription(null);
            timeOfStartingField.setDescription(null);
            timeOfFinishingField.setContextHelpText(null);
            timeOfStartingField.setContextHelpText(null);
        }
    }

    protected void setTimesNull() {
        absenceRequestDc.getItem().setTimeOfFinishing(null);
        absenceRequestDc.getItem().setTimeOfStarting(null);
    }


    @Override
    protected TsadvUser getEmployee() {
        return dataManager.load(TsadvUser.class)
                .query("select e from tsadv$UserExt e where e.personGroup.id = :personGroupId")
                .setParameters(ParamsMap.of("personGroupId",
                        absenceRequestDc.getItem().getPersonGroup().getId()))
                .view(View.MINIMAL).list().stream().findFirst().orElse(null);
    }
}