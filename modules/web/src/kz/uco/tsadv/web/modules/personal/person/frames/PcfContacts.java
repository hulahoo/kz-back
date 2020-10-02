package kz.uco.tsadv.web.modules.personal.person.frames;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.components.ButtonsPanel;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import kz.uco.tsadv.modules.personal.model.PersonContact;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("all")
public class PcfContacts extends EditableFrame {

    @Inject
    protected ButtonsPanel buttonsPanel;

    protected CollectionDatasource<PersonContact, UUID> personContactsDs;

    @Named("personContactsTable.create")
    protected CreateAction personContactsTableCreate;

    @Named("personContactsTable.edit")
    private EditAction personContactsTableEdit;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        personContactsTableCreate.setInitialValuesSupplier(() ->
                getDsContext().get("personGroupDs") != null
                        ? ParamsMap.of("personGroup", getDsContext().get("personGroupDs").getItem())
                        : null);

        personContactsTableEdit.setEditorCloseListener(actionId -> personContactsDs.refresh());
    }

    @Override
    public void editable(boolean editable) {
        buttonsPanel.setVisible(editable);
    }

    @Override
    public void initDatasource() {
        personContactsDs = (CollectionDatasource<PersonContact, UUID>) getDsContext().get("personContactsDs");
    }
}