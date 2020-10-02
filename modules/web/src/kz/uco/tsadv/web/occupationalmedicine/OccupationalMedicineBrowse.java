package kz.uco.tsadv.web.occupationalmedicine;

import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.data.GroupDatasource;
import kz.uco.tsadv.entity.tb.OccupationalMedicine;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;
import java.util.UUID;

public class OccupationalMedicineBrowse extends AbstractLookup {

    @Named("occupationalMedicinesTable.create")
    private CreateAction createAction;
    @Named("occupationalMedicinesTable.edit")
    private EditAction editAction;
    @Inject
    private GroupDatasource<OccupationalMedicine, UUID> occupationalMedicinesDs;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        createAction.setAfterCommitHandler(entity -> occupationalMedicinesDs.refresh());
        editAction.setAfterCommitHandler(entity -> occupationalMedicinesDs.refresh());
    }
}