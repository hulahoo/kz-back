package kz.uco.tsadv.web.medicalinspection;

import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.data.GroupDatasource;
import kz.uco.tsadv.entity.tb.MedicalInspection;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;
import java.util.UUID;

public class MedicalInspectionBrowse extends AbstractLookup {

    @Named("medicalInspectionsTable.create")
    private CreateAction createAction;
    @Named("medicalInspectionsTable.edit")
    private EditAction editAction;
    @Inject
    private GroupDatasource<MedicalInspection, UUID> medicalInspectionsDs;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        createAction.setAfterCommitHandler(entity -> medicalInspectionsDs.refresh());
        editAction.setAfterCommitHandler(entity -> medicalInspectionsDs.refresh());
    }
}