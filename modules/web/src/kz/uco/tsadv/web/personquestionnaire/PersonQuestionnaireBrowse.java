package kz.uco.tsadv.web.personquestionnaire;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.QueryParser;
import com.haulmont.cuba.core.global.filter.QueryFilter;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.GroupTable;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.haulmont.cuba.gui.components.actions.EditAction;
import com.haulmont.cuba.gui.data.GroupDatasource;
import com.haulmont.cuba.security.global.UserSession;
import kz.uco.base.common.StaticVariable;
import kz.uco.tsadv.modules.performance.model.Questionnaire;
import kz.uco.tsadv.modules.performance.model.QuestionnaireQuestion;
import kz.uco.tsadv.modules.personal.model.PersonQuestionnaire;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;
import java.util.UUID;

public class PersonQuestionnaireBrowse extends AbstractLookup {

    @Inject
    protected GroupDatasource<PersonQuestionnaire, UUID> personQuestionnairesDs;

    @Named("personQuestionnairesTable.create")
    protected CreateAction personQuestionnairesTableCreate;

    @Named("personQuestionnairesTable.edit")
    protected EditAction personQuestionnairesTableEdit;
    @Inject
    protected GroupTable<PersonQuestionnaire> personQuestionnairesTable;
    @Inject
    protected UserSession userSession;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        if (params.containsKey("forManager")) {
            personQuestionnairesTable.removeColumn(personQuestionnairesTable.getColumn("appraiser"));
            personQuestionnairesDs.setQuery("select e from tsadv$PersonQuestionnaire e where e.appraiser.id = :session$"+StaticVariable.USER_PERSON_GROUP_ID);
            personQuestionnairesTableCreate.setInitialValues(ParamsMap.of("appraiser", userSession.getAttribute(StaticVariable.USER_PERSON_GROUP)));
            personQuestionnairesTableCreate.setWindowParams(ParamsMap.of("forManager", true));
            personQuestionnairesTableEdit.setWindowParams(ParamsMap.of("forManager", true));
        } else if (params.containsKey("questionnaire")) {
            personQuestionnairesTable.removeColumn(personQuestionnairesTable.getColumn("questionnaire"));
            Questionnaire questionnaire = (Questionnaire) params.get("questionnaire");
            personQuestionnairesDs.setQuery("select e from tsadv$PersonQuestionnaire e where e.questionnaire.id = :param$questionnaire");
            personQuestionnairesTableCreate.setInitialValues(ParamsMap.of("questionnaire", questionnaire));
            personQuestionnairesTableCreate.setWindowParams(ParamsMap.of("questionnaire", true));
            personQuestionnairesTableEdit.setWindowParams(ParamsMap.of("questionnaire", true));
        }
    }

    public void estimate() {
        AbstractEditor abstractEditor = openEditor("tsadv$PersonQuestionnaireEstimate.edit",
                personQuestionnairesDs.getItem(), WindowManager.OpenType.THIS_TAB);
        abstractEditor.addCloseWithCommitListener(() -> personQuestionnairesDs.refresh());
    }
}