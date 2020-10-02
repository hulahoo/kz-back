package kz.uco.tsadv.web.modules.recruitment.requisitionhiringstep;

import com.haulmont.cuba.gui.components.AbstractWindow;
import com.haulmont.cuba.gui.data.Datasource;
import kz.uco.tsadv.modules.performance.model.Questionnaire;
import kz.uco.tsadv.modules.recruitment.model.RcQuestionnaire;

import javax.inject.Inject;
import java.util.Map;

public class QuestionnaireDetail extends AbstractWindow {

    @Inject
    private Datasource<RcQuestionnaire> rcQuestionnaireDs;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        rcQuestionnaireDs.setItem(((RcQuestionnaire) params.get("questionnaire")));
    }
}