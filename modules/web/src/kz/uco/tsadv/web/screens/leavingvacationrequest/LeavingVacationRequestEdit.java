package kz.uco.tsadv.web.screens.leavingvacationrequest;

import com.haulmont.cuba.core.global.TimeSource;
import com.haulmont.cuba.gui.components.DateField;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.model.LeavingVacationRequest;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@UiController("tsadv$LeavingVacationRequest.edit")
@UiDescriptor("leaving-vacation-request-edit.xml")
@EditedEntityContainer("leavingVacationRequestDc")
@LoadDataBeforeShow
public class LeavingVacationRequestEdit extends StandardEditor<LeavingVacationRequest> {

    @Inject
    private DateField<Date> startDateField;
    @Inject
    private DateField<Date> requestDateField;
    @Inject
    protected TimeSource timeSource;

    @Subscribe
    protected void onAfterInit(AfterInitEvent event) {
        LocalDateTime ldt = LocalDateTime.ofInstant(timeSource.currentTimestamp().toInstant(), ZoneId.systemDefault());
        Date out = Date.from(ldt.minusDays(1).atZone(ZoneId.systemDefault()).toInstant());

        requestDateField.setRangeStart(out);
    }
}