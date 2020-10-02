package kz.uco.tsadv.web.harmfullfactors;

import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.data.GroupDatasource;
import kz.uco.tsadv.entity.tb.HarmfullFactors;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;
import java.util.UUID;

public class HarmfullFactorsBrowse extends AbstractLookup {

    @Named("harmfullFactorsesTable.create")
    private CreateAction createAction;
    @Named("harmfullFactorsesTable.edit")
    private EditAction editAction;
    @Inject
    private GroupDatasource<HarmfullFactors, UUID> harmfullFactorsesDs;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        createAction.setAfterCommitHandler(entity -> harmfullFactorsesDs.refresh());
        editAction.setAfterCommitHandler(entity -> harmfullFactorsesDs.refresh());
    }
}