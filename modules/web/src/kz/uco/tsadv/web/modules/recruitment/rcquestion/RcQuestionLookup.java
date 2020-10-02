package kz.uco.tsadv.web.modules.recruitment.rcquestion;

import com.haulmont.cuba.gui.WindowParams;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.GroupTable;
import com.haulmont.cuba.gui.data.GroupDatasource;
import kz.uco.tsadv.modules.recruitment.model.RcQuestion;

import javax.inject.Inject;
import java.util.Map;
import java.util.UUID;

public class RcQuestionLookup extends AbstractLookup {

    @Inject
    private GroupTable<RcQuestion> rcQuestionsTable;
    @Inject
    private GroupDatasource<RcQuestion, UUID> rcQuestionsDs;


    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        if (params.get("questionType") != null) {
            rcQuestionsDs.setQuery("select e from tsadv$RcQuestion e " +
                    " where e.id not in :param$excludeRcQuestionIds " +
                    " AND e.questionType = :param$questionType");
        }

        if (WindowParams.MULTI_SELECT.getBool(getContext())) {
            rcQuestionsTable.setMultiSelect(true);
        }
    }

}