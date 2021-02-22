package kz.uco.tsadv.web.screens.leavingvacationrequest;

import com.haulmont.addon.bproc.web.processform.Outcome;
import com.haulmont.addon.bproc.web.processform.ProcessForm;
import com.haulmont.cuba.core.global.TimeSource;
import com.haulmont.cuba.gui.components.DateField;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.entity.bproc.AbstractBprocRequest;
import kz.uco.tsadv.modules.personal.model.LeavingVacationRequest;
import kz.uco.tsadv.web.abstraction.bproc.AbstractBprocEditor;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@UiController("tsadv$LeavingVacationRequest.edit")
@UiDescriptor("leaving-vacation-request-edit.xml")
@EditedEntityContainer("leavingVacationRequestDc")
@LoadDataBeforeShow
@ProcessForm(
        outcomes = {
                @Outcome(id = AbstractBprocRequest.OUTCOME_REVISION),
                @Outcome(id = AbstractBprocRequest.OUTCOME_APPROVE),
                @Outcome(id = AbstractBprocRequest.OUTCOME_REJECT)
        }
)
public class LeavingVacationRequestEdit extends AbstractBprocEditor<LeavingVacationRequest> {

    @Inject
    private DateField<Date> requestDateField;
    @Inject
    protected TimeSource timeSource;
    @Inject
    private DateField<Date> plannedStartDateField;
    @Inject
    private InstanceContainer<LeavingVacationRequest> leavingVacationRequestDc;


    @Subscribe
    protected void onAfterInit(AfterInitEvent event) {
        LocalDateTime ldt = LocalDateTime.ofInstant(timeSource.currentTimestamp().toInstant(), ZoneId.systemDefault());
        Date outRequestDate = Date.from(ldt.minusDays(1).atZone(ZoneId.systemDefault()).toInstant());

        requestDateField.setRangeStart(outRequestDate);

    }

    @Subscribe
    public void onAfterShow(AfterShowEvent event) {

        LocalDateTime ldt1 = LocalDateTime.ofInstant(requestDateField.getValue().toInstant(), ZoneId.systemDefault());
        Date outPlannedStart = Date.from(ldt1.minusDays(-30).atZone(ZoneId.systemDefault()).toInstant());

        plannedStartDateField.setRangeStart(outPlannedStart);

    }


    @Subscribe
    public void onBeforeShow(BeforeShowEvent event) {
        plannedStartDateField.setRangeEnd(leavingVacationRequestDc.getItem().getEndData());
    }
}