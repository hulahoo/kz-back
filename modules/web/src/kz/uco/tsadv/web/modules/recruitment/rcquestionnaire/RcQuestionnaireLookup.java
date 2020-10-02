package kz.uco.tsadv.web.modules.recruitment.rcquestionnaire;

import com.haulmont.cuba.gui.WindowParams;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.GroupTable;
import com.haulmont.cuba.gui.data.GroupDatasource;
import kz.uco.tsadv.modules.recruitment.model.RcQuestionnaire;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RcQuestionnaireLookup extends AbstractLookup {

    @Inject
    private GroupTable<RcQuestionnaire> rcQuestionnairesTable;

    @Inject
    private GroupDatasource<RcQuestionnaire, UUID> rcQuestionnairesDs;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        if (WindowParams.MULTI_SELECT.getBool(getContext())) {
            rcQuestionnairesTable.setMultiSelect(true);
        }
        if (params.containsKey("validatePrescreening")) {
            setLookupValidator(() -> {
                for (RcQuestionnaire rcQuestionnaire : rcQuestionnairesTable.getSelected()) {
                    if ("PRE_SCREEN_TEST".equals(rcQuestionnaire.getCategory().getCode())) {
                        if (params.containsKey("alreadyExistRcQuestionnaires")) {
                            List<RcQuestionnaire> list = ((List<RcQuestionnaire>) params.get("alreadyExistRcQuestionnaires"));
                            if (list != null) {
                                for (RcQuestionnaire rcQuestionnaire1 : list) {
                                    if ("PRE_SCREEN_TEST".equals(rcQuestionnaire1.getCategory().getCode()))
                                        showNotification(getMessage("error"), NotificationType.ERROR);
                                    return false;
                                }
                            }
                        }
                    }
                }
                return true;
            });
            rcQuestionnairesDs.setQuery("select e from tsadv$RcQuestionnaire e" +
                    " where e.id not in :param$alreadyExistRcQuestionnaires\n ");
        }
    }
}