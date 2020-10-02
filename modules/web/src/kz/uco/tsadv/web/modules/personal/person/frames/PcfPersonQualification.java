package kz.uco.tsadv.web.modules.personal.person.frames;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import kz.uco.tsadv.entity.tb.PersonQualification;

import javax.inject.Named;
import java.util.Map;
import java.util.UUID;

public class PcfPersonQualification extends EditableFrame {

    protected CollectionDatasource<PersonQualification, UUID> personQualificationsDs;

    @Named("personQualificationsTable.create")
    private CreateAction personQualificationsTableCreate;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        personQualificationsTableCreate.setInitialValuesSupplier(() ->
                getDsContext().get("personGroupDs") != null
                        ? ParamsMap.of("personGroup", getDsContext().get("personGroupDs").getItem())
                        : null);
    }

    @Override
    public void editable(boolean editable) {

    }

    @Override
    public void initDatasource() {
        personQualificationsDs = (CollectionDatasource<PersonQualification, UUID>) getDsContext().get("personQualificationsDs");
    }
}