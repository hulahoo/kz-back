package kz.uco.tsadv.web.bpmusersubstitution;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.security.global.UserSession;
import kz.uco.base.common.StaticVariable;
import kz.uco.tsadv.exceptions.ItemNotFoundException;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;

public class BpmUserSubstitutionSsBrowse extends AbstractLookup {

    @Named("bpmUserSubstitutionsTable.create")
    protected CreateAction bpmUserSubstitutionsTableCreate;
    @Named("bpmUserSubstitutionsTable.edit")
    protected EditAction bpmUserSubstitutionsTableEdit;
    @Inject
    protected UserSession userSession;

    @Override
    public void init(Map<String, Object> params) {
        if (userSession.getAttribute(StaticVariable.USER_PERSON_GROUP_ID) == null) {
            throw new ItemNotFoundException("noPerson");
        }

        bpmUserSubstitutionsTableCreate.setInitialValuesSupplier(() -> ParamsMap.of("user", userSession.getUser()));
        bpmUserSubstitutionsTableCreate.setWindowParamsSupplier(() -> ParamsMap.of("isUserEditable", false));
        bpmUserSubstitutionsTableEdit.setWindowParamsSupplier(() -> ParamsMap.of("isUserEditable", false));
        super.init(params);
    }
}