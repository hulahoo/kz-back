package kz.uco.tsadv.web.modules.personal.person.frames;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.components.ButtonsPanel;
import com.haulmont.cuba.gui.components.DataGrid;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import kz.uco.tsadv.modules.personal.model.PersonLearningHistory;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;

public class PcfPersonLearningContract extends EditableFrame {

    @Named("personLearningContractTable.create")
    protected CreateAction personLearningContractTableCreate;
    @Inject
    protected DataGrid personLearningHistoryTable;
    @Inject
    protected ButtonsPanel buttonsPanel;

    @Override
    public void editable(boolean editable) {
        buttonsPanel.setVisible(editable);
    }

    @Override
    public void initDatasource() {
    }

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        personLearningContractTableCreate.setInitialValuesSupplier(() ->
                getDsContext().get("personGroupDs") != null
                        ? ParamsMap.of("personGroup", getDsContext().get("personGroupDs").getItem())
                        : null);
    }
}