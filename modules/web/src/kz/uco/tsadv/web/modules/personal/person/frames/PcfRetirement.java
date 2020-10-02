package kz.uco.tsadv.web.modules.personal.person.frames;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import kz.uco.tsadv.modules.personal.model.Retirement;

import javax.inject.Named;
import java.util.Map;
import java.util.UUID;

public class PcfRetirement extends EditableFrame {

    CollectionDatasource<Retirement, UUID> retirementDs;

    @Named("retirementTable.create")
    protected CreateAction retirementTableCreate;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        retirementTableCreate.setInitialValuesSupplier(() ->
                getDsContext().get("personGroupDs") != null
                        ? ParamsMap.of("personGroupExt", getDsContext().get("personGroupDs").getItem())
                        : null);
    }

    @Override
    public void editable(boolean editable) {

    }

    @Override
    public void initDatasource() {
        retirementDs = (CollectionDatasource<Retirement, UUID>) getDsContext().get("retirementDs");
    }
}