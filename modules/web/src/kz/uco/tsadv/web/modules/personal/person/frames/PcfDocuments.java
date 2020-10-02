package kz.uco.tsadv.web.modules.personal.person.frames;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.components.ButtonsPanel;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import kz.uco.tsadv.modules.personal.model.PersonDocument;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("all")
public class PcfDocuments extends EditableFrame {

    private CollectionDatasource<PersonDocument, UUID> personDocumentsDs;

    @Inject
    private ButtonsPanel docButtonsPanel;

    @Named("personDocumentsTable.create")
    private CreateAction personDocumentsTableCreate;

    @Override
    public void editable(boolean editable) {
        docButtonsPanel.setVisible(editable);
    }

    @Override
    public void initDatasource() {
        personDocumentsDs = (CollectionDatasource<PersonDocument, UUID>) getDsContext().get("personDocumentsDs");
    }

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        personDocumentsTableCreate.setInitialValuesSupplier(() ->
                getDsContext().get("personGroupDs") != null
                        ? ParamsMap.of("personGroup", getDsContext().get("personGroupDs").getItem())
                        : null);
    }
}