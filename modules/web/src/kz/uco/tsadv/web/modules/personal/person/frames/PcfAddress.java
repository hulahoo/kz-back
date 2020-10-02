package kz.uco.tsadv.web.modules.personal.person.frames;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.components.ButtonsPanel;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.components.actions.RemoveAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.DsContext;
import kz.uco.tsadv.modules.personal.model.Address;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;
import java.util.UUID;

/**
 * @author veronika.buksha
 */
public class PcfAddress extends EditableFrame {
    @Inject
    private ButtonsPanel buttonsPanel;

    @Named("addressesTable.remove")
    public RemoveAction addressesTableRemove;

    @Named("addressesTable.create")
    public CreateAction addressesTableCreate;

    @Named("addressesTable.edit")
    public EditAction addressesTableEdit;

    public CollectionDatasource<Address, UUID> addressesDs;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        addressesTableCreate.setInitialValuesSupplier(() ->
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
        addressesDs = (CollectionDatasource<Address, UUID>) getDsContext().get("addressesDs");
    }
}