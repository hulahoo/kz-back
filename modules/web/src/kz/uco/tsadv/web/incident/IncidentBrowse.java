package kz.uco.tsadv.web.incident;

import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.data.GroupDatasource;
import kz.uco.tsadv.entity.tb.Incident;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;
import java.util.UUID;

public class IncidentBrowse extends AbstractLookup {

    @Inject
    private GroupDatasource<Incident, UUID> incidentsDs;
    @Named("incidentsTable.create")
    private CreateAction createAction;
    @Named("incidentsTable.edit")
    private EditAction editAction;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        createAction.setAfterCommitHandler(entity -> incidentsDs.refresh());
        editAction.setAfterCommitHandler(entity -> incidentsDs.refresh());
    }
}