package kz.uco.tsadv.web.modules.personal.person.frames;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.components.ButtonsPanel;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.Agreement;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("all")
public class PcfAgreement extends EditableFrame {

    @Inject
    protected Button editButton;
    @Inject
    protected Table agreementsTable;
    @Inject
    protected ButtonsPanel buttonsPanel;
    @Named("agreementsTable.create")
    protected CreateAction agreementsTableCreate;

    public CollectionDatasource<Agreement, UUID> agreementsDs;

    public Datasource<PersonGroupExt> personGroupDs;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        agreementsTableCreate.setInitialValuesSupplier(() ->
                getDsContext().get("personGroupDs") != null
                        ? ParamsMap.of("personGroup", getDsContext().get("personGroupDs").getItem())
                        : null);
        ((EditAction) agreementsTable.getAction("edit")).setWindowParams(ParamsMap.of("allValue", agreementsDs.getItems()));
        ((CreateAction) agreementsTable.getAction("create")).setWindowParams(ParamsMap.of("allValue", agreementsDs.getItems()));
        agreementsDs.addCollectionChangeListener(e -> {
            ((EditAction) agreementsTable.getAction("edit")).setWindowParams(ParamsMap.of("allValue", agreementsDs.getItems()));
            ((CreateAction) agreementsTable.getAction("create")).setWindowParams(ParamsMap.of("allValue", agreementsDs.getItems()));
        });
    }

    @Override
    public void editable(boolean editable) {
        buttonsPanel.setVisible(editable);
    }

    @Override
    public void initDatasource() {
        agreementsDs = (CollectionDatasource<Agreement, UUID>) getDsContext().get("agreementsDs");
        personGroupDs = getDsContext().get("personGroupDs");
    }
}