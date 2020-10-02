package kz.uco.tsadv.web.modules.learning.questionnairequestion;

import com.haulmont.cuba.gui.components.AbstractEditor;
import kz.uco.tsadv.modules.learning.enums.QuestionType;
import kz.uco.tsadv.modules.performance.model.QuestionnaireQuestion;

public class QuestionnaireQuestionEdit extends AbstractEditor<QuestionnaireQuestion> {
    @Override
    protected void initNewItem(QuestionnaireQuestion item) {
        super.initNewItem(item);
        item.setQuestionType(QuestionType.ONE);
    }
}