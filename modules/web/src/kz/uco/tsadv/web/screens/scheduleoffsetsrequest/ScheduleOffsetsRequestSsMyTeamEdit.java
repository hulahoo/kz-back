package kz.uco.tsadv.web.screens.scheduleoffsetsrequest;

import com.haulmont.cuba.gui.components.TextField;
import com.haulmont.cuba.gui.model.InstanceContainer;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.personal.model.ScheduleOffsetsRequest;

import javax.inject.Inject;

@UiController("tsadv_ScheduleOffsetsRequestSsMyTeam.edit")
@UiDescriptor("schedule-offsets-request-ss-my-team-edit.xml")
@EditedEntityContainer("scheduleOffsetsRequestDc")
@LoadDataBeforeShow
public class ScheduleOffsetsRequestSsMyTeamEdit extends StandardEditor<ScheduleOffsetsRequest> {
    @Inject
    protected TextField<String> purposeTextField;

    @Subscribe(id = "scheduleOffsetsRequestDc", target = Target.DATA_CONTAINER)
    public void onScheduleOffsetsRequestDcItemPropertyChange(
            InstanceContainer.ItemPropertyChangeEvent<ScheduleOffsetsRequest> event) {
        if (event.getProperty().equals("purpose")) {
            purposeTextField.setVisible(event.getItem().getPurpose() != null
                    && event.getItem().getPurpose().getCode() != null
                    && event.getItem().getPurpose().getCode().equals("OTHER"));
        }
    }

}