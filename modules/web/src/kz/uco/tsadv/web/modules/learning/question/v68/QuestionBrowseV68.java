package kz.uco.tsadv.web.modules.learning.question.v68;

import com.haulmont.cuba.gui.components.AbstractLookup;
import com.haulmont.cuba.gui.components.Table;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import kz.uco.tsadv.modules.learning.model.Question;
import kz.uco.tsadv.modules.learning.model.QuestionInSection;

import javax.inject.Inject;
import java.util.Map;
import java.util.UUID;

@Deprecated
public class QuestionBrowseV68 extends AbstractLookup {

    @Inject
    private CollectionDatasource<Question, UUID> questionsDs;

    @Inject
    private Table<Question> questionsTable;

    private CollectionDatasource<QuestionInSection, UUID> questionInSectionDs;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        if (params.containsKey("bank")) {
            questionsDs.setQuery(String.format("select e from tsadv$Question e where e.bank.id = '%s'", UUID.fromString(params.get("bank").toString())));
        }

        if (params.containsKey("questionInSectionDs")) {
            questionInSectionDs = (CollectionDatasource<QuestionInSection, UUID>) params.get("questionInSectionDs");
        }

    }

    @Override
    public void ready() {
        super.ready();

        questionsTable.removeAllActions();

        if (questionInSectionDs != null && questionInSectionDs.size() != 0) {
            for (QuestionInSection questionInSection : questionInSectionDs.getItems()) {
                questionsDs.excludeItem(questionInSection.getQuestion());
            }
        }
    }

    public void selectAll() {
        questionsTable.selectAll();
    }
}