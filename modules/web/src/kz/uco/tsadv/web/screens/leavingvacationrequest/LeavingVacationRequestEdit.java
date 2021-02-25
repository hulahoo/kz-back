package kz.uco.tsadv.web.screens.leavingvacationrequest;

import com.haulmont.addon.bproc.web.processform.Outcome;
import com.haulmont.addon.bproc.web.processform.ProcessForm;
import com.haulmont.cuba.core.global.TimeSource;
import com.haulmont.cuba.gui.components.DateField;
import com.haulmont.cuba.gui.components.TextArea;
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
    protected TextArea<String> commentField;
    @Inject
    private DateField<Date> requestDateField;
    @Inject
    protected TimeSource timeSource;
    @Inject
    private DateField<Date> plannedStartDateField;
    @Inject
    private InstanceContainer<LeavingVacationRequest> leavingVacationRequestDc;

    @Subscribe
    public void onAfterShow(AfterShowEvent event) {

        super.onAfterShow(event);

        if (leavingVacationRequestDc.getItem().getStatus() != null
                && !leavingVacationRequestDc.getItem().getStatus().getCode().equals("DRAFT")) {
            plannedStartDateField.setEditable(false);
            commentField.setEditable(false);
        }
        LocalDateTime ldt1 = LocalDateTime.ofInstant(requestDateField.getValue().toInstant(), ZoneId.systemDefault());
        Date outPlannedStart = Date.from(ldt1.minusDays(-30).atZone(ZoneId.systemDefault()).toInstant());

        plannedStartDateField.setRangeStart(outPlannedStart);

    }
}