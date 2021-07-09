package kz.uco.tsadv.web.screens.scheduleoffsetsrequest;

import com.haulmont.addon.bproc.web.processform.Outcome;
import com.haulmont.addon.bproc.web.processform.ProcessForm;
import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.gui.components.TextField;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.entity.bproc.AbstractBprocRequest;
import kz.uco.tsadv.modules.administration.TsadvUser;
import kz.uco.tsadv.modules.personal.model.ScheduleOffsetsRequest;
import kz.uco.tsadv.web.abstraction.bproc.AbstractBprocEditor;

import javax.inject.Inject;

@UiController("tsadv_ScheduleOffsetsRequestSsMyTeam.edit")
@UiDescriptor("schedule-offsets-request-ss-my-team-edit.xml")
@EditedEntityContainer("scheduleOffsetsRequestDc")
@LoadDataBeforeShow
@ProcessForm(
        outcomes = {
                @Outcome(id = AbstractBprocRequest.OUTCOME_REVISION),
                @Outcome(id = AbstractBprocRequest.OUTCOME_APPROVE),
                @Outcome(id = AbstractBprocRequest.OUTCOME_REJECT),
                @Outcome(id = AbstractBprocRequest.OUTCOME_CANCEL)
        }
)
public class ScheduleOffsetsRequestSsMyTeamEdit extends AbstractBprocEditor<ScheduleOffsetsRequest> {
    @Inject
    protected TextField<String> purposeTextField;
    @Inject
    protected InstanceContainer<ScheduleOffsetsRequest> scheduleOffsetsRequestDc;

    @Subscribe(id = "scheduleOffsetsRequestDc", target = Target.DATA_CONTAINER)
    public void onScheduleOffsetsRequestDcItemPropertyChange(
            InstanceContainer.ItemPropertyChangeEvent<ScheduleOffsetsRequest> event) {
        if (event.getProperty().equals("purpose")) {
            purposeTextField.setVisible(event.getItem().getPurpose() != null
                    && event.getItem().getPurpose().getCode() != null
                    && event.getItem().getPurpose().getCode().equals("OTHER"));
        }
    }

    @Override
    protected void initEditableFields() {
        super.initEditableFields();
    }

    @Override
    protected TsadvUser getEmployee() {
        TsadvUser user = dataManager.load(TsadvUser.class)
                .query("select e from tsadv$UserExt e where e.personGroup.id = :personGroupId")
                .setParameters(ParamsMap.of("personGroupId",
                        scheduleOffsetsRequestDc.getItem().getPersonGroup().getId()))
                .view(View.MINIMAL).list().stream().findFirst().orElse(null);

        return user;
    }
}