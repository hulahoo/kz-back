package kz.uco.tsadv.web.modules.recognition.entity.coindistributionrule;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.actions.CreateAction;

import javax.inject.Named;
import java.util.Map;

public class CoinDistributionRuleBrowse extends AbstractLookup {
    @Named("coinDistributionRulesTable.create")
    private CreateAction createAction;
    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        createAction.setWindowParams(ParamsMap.of("createAction", true));
    }
}