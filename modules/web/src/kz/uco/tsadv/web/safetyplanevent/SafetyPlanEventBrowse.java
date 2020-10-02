package kz.uco.tsadv.web.safetyplanevent;

import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.data.GroupDatasource;
import kz.uco.tsadv.entity.tb.SafetyPlanEvent;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;
import java.util.UUID;

public class SafetyPlanEventBrowse extends AbstractLookup {

    @Named("safetyPlanEventsTable.create")
    private CreateAction createAction;
    @Named("safetyPlanEventsTable.edit")
    private EditAction editAction;
    @Inject
    private GroupDatasource<SafetyPlanEvent, UUID> safetyPlanEventsDs;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        createAction.setAfterCommitHandler(entity -> safetyPlanEventsDs.refresh());
        editAction.setAfterCommitHandler(entity -> safetyPlanEventsDs.refresh());
    }
}