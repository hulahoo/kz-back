package kz.uco.tsadv.web.safetyevent;

import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.data.GroupDatasource;
import kz.uco.tsadv.entity.tb.SafetyEvent;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;
import java.util.UUID;

public class SafetyEventBrowse extends AbstractLookup {

    @Named("safetyEventsTable.create")
    private CreateAction createAction;
    @Named("safetyEventsTable.edit")
    private EditAction editAction;
    @Inject
    private GroupDatasource<SafetyEvent, UUID> safetyEventsDs;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        createAction.setAfterCommitHandler(entity -> safetyEventsDs.refresh());
        editAction.setAfterCommitHandler(entity -> safetyEventsDs.refresh());
    }
}