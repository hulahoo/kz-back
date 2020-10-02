package kz.uco.tsadv.web.partycontactperson;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.actions.CreateAction;

import javax.inject.Named;
import java.util.Map;

public class PartyContactPersonBrowse extends AbstractLookup {
    @Named("partyContactPersonsTable.create")
    protected CreateAction partyContactPersonsTableCreate;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        partyContactPersonsTableCreate.setInitialValues(ParamsMap.of("partyExt",params.get("partyExt")));
    }
}