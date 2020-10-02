package kz.uco.tsadv.web.modules.personal.agreement;

import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.data.GroupDatasource;
import kz.uco.tsadv.modules.personal.model.Agreement;
import kz.uco.base.web.components.CustomFilter;

import javax.inject.Inject;
import java.util.Map;
import java.util.UUID;

public class AgreementLookup extends AbstractLookup {

    @Inject
    private GroupDatasource<Agreement, UUID> agreementsDs;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        if (params.containsKey("personGroupId")) {
            agreementsDs.setQuery(CustomFilter.getFilteredQuery(agreementsDs.getQuery(), " e.personGroup.id = :param$personGroupId "));
        }


    }
}