package kz.uco.tsadv.web.modules.personal.person.frames;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.components.ButtonsPanel;
import com.haulmont.cuba.gui.components.actions.CreateAction;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;

public class PcfTradeUnion extends EditableFrame {

    @Inject
    private ButtonsPanel buttonsPanel;

    @Named("tradeUnionsTable.create")
    private CreateAction tradeUnionsTableCreate;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        tradeUnionsTableCreate.setInitialValuesSupplier(() ->
                getDsContext().get("personGroupDs") != null
                        ? ParamsMap.of("personGroup", getDsContext().get("personGroupDs").getItem())
                        : null);
    }

    @Override
    public void editable(boolean editable) {
        buttonsPanel.setVisible(editable);
    }

    @Override
    public void initDatasource() {

    }
}