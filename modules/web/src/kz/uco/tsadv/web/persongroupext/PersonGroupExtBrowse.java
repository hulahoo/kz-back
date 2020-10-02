package kz.uco.tsadv.web.persongroupext;

import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.data.GroupDatasource;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.inject.Inject;
import java.util.Map;
import java.util.UUID;

public class PersonGroupExtBrowse extends AbstractLookup {
    @Inject
    protected GroupDatasource<PersonGroupExt, UUID> personGroupExtsDs;

    protected Map<String, Object> param;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        param = params;
    }
}