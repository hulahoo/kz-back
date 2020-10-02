package kz.uco.tsadv.web.microtraum;

import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.data.GroupDatasource;
import kz.uco.tsadv.entity.tb.Microtraum;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;
import java.util.UUID;

public class MicrotraumBrowse extends AbstractLookup {

    @Inject
    private GroupDatasource<Microtraum, UUID> microtraumsDs;
    @Named("microtraumsTable.create")
    private CreateAction createAction;
    @Named("microtraumsTable.edit")
    private EditAction editAction;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        createAction.setAfterCommitHandler(entity -> microtraumsDs.refresh());
        editAction.setAfterCommitHandler(entity -> microtraumsDs.refresh());
    }
}