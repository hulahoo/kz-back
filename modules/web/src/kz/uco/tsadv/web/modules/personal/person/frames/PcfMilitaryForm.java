package kz.uco.tsadv.web.modules.personal.person.frames;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.components.Filter;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.GroupDatasource;
import kz.uco.tsadv.modules.personal.model.MilitaryForm;
import kz.uco.tsadv.web.modules.personal.person.frames.EditableFrame;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;
import java.util.UUID;

public class PcfMilitaryForm extends EditableFrame {
    @Inject
    protected CollectionDatasource<MilitaryForm, UUID> militaryRankDs;
    @Inject
    protected Filter filter;
    @Named("militaryFormsTable.create")
    protected CreateAction militaryFormsTableCreate;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        militaryFormsTableCreate.setInitialValuesSupplier(() ->
                getDsContext().get("personGroupDs") != null
                        ? ParamsMap.of("personGroup", getDsContext().get("personGroupDs").getItem())
                        : null);
    }

    @Override
    public void editable(boolean editable) {

    }

    @Override
    public void initDatasource() {

    }
}