package kz.uco.tsadv.web.healthdeterioration;

import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.data.GroupDatasource;
import kz.uco.tsadv.entity.tb.HealthDeterioration;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;
import java.util.UUID;

public class HealthDeteriorationBrowse extends AbstractLookup {

    @Named("healthDeteriorationsTable.create")
    private CreateAction createAction;
    @Named("healthDeteriorationsTable.edit")
    private EditAction editAction;
    @Inject
    private GroupDatasource<HealthDeterioration, UUID> healthDeteriorationsDs;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        createAction.setAfterCommitHandler(entity -> healthDeteriorationsDs.refresh());
        editAction.setAfterCommitHandler(entity -> healthDeteriorationsDs.refresh());
    }
}