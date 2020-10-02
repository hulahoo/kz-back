package kz.uco.tsadv.web.modules.personal.person.frames;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.actions.CreateAction;

import javax.inject.Named;
import java.util.Map;

public class PcfAwardsBrowse extends EditableFrame {

    @Named("awardsesTable.create")
    private CreateAction awardsesTableCreate;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        awardsesTableCreate.setInitialValuesSupplier(() ->
                getDsContext().get("assignmentGroupDs") != null
                        ? ParamsMap.of("assignmentGroup", getDsContext().get("assignmentGroupDs").getItem())
                        : null);
    }

    @Override
    public void editable(boolean editable) {

    }

    @Override
    public void initDatasource() {

    }
}