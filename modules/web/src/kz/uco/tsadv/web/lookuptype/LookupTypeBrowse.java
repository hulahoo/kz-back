package kz.uco.tsadv.web.lookuptype;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.Button;
import com.haulmont.cuba.gui.components.GroupTable;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.data.Datasource;
import com.haulmont.cuba.gui.data.GroupDatasource;
import kz.uco.tsadv.modules.administration.LookupType;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collections;
import java.util.UUID;

public class LookupTypeBrowse extends AbstractLookup {

    @Inject
    protected GroupDatasource<LookupType, UUID> lookupTypesDs;

    @Inject
    protected GroupTable<LookupType> lookupTypesTable;

    @Named("lookupValuesTable.create")
    private CreateAction lookupValuesTableCreate;

    @Override
    public void ready() {
        super.ready();
        lookupTypesDs.addItemChangeListener(e -> {
            lookupTypesDsItemChangeListener(e);
            lookupValuesTableCreate.setInitialValues(Collections.singletonMap("lookupType", e.getItem()));
        });
    }

    protected void lookupTypesDsItemChangeListener(Datasource.ItemChangeEvent<LookupType> e) {
        if (lookupTypesTable.getSingleSelected() != null) {
            lookupValuesTableCreate.setEnabled(true);
        } else {
            lookupValuesTableCreate.setEnabled(false);
        }
    }
}