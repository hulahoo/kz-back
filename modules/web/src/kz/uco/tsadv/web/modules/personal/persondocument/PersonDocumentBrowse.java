package kz.uco.tsadv.web.modules.personal.persondocument;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.data.GroupDatasource;
import com.haulmont.cuba.security.global.UserSession;
import kz.uco.base.common.StaticVariable;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.personal.model.PersonDocument;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;
import java.util.UUID;

public class PersonDocumentBrowse extends AbstractLookup {

    @Named("personDocumentsTable.create")
    protected CreateAction personDocumentsTableCreate;

    @Inject
    protected UserSession userSession;

    @Inject
    private GroupDatasource<PersonDocument, UUID> personDocumentsDs;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        personDocumentsTableCreate.setInitialValuesSupplier(() ->
                ParamsMap.of("personGroup", userSession.getAttribute(StaticVariable.USER_PERSON_GROUP))
        );
    }
}