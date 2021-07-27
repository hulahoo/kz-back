package kz.uco.tsadv.web.modules.personal.person.frames;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.components.ButtonsPanel;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import kz.uco.tsadv.modules.personal.model.ListOfDocument;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("all")
public class PcfListOfDocument extends EditableFrame {

    @Inject
    protected ButtonsPanel buttonsPanel;

    public CollectionDatasource<ListOfDocument, UUID> listOfDocumentDs;

    @Named("listOfDocumentTable.create")
    protected CreateAction listOfDocumentTableCreate;


    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        listOfDocumentTableCreate.setInitialValuesSupplier(() ->
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
        listOfDocumentDs = (CollectionDatasource<ListOfDocument, UUID>) getDsContext().get("listOfDocumentDs");
    }
}