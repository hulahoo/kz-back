package kz.uco.tsadv.web.personquestionnaire;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.GroupDatasource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.tsadv.modules.performance.model.QuestionAnswer;
import kz.uco.tsadv.modules.performance.model.QuestionnaireQuestion;
import kz.uco.tsadv.modules.personal.model.PersonQuestionnaire;
import kz.uco.tsadv.modules.personal.model.PersonQuestionnaireAnswer;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.UUID;
import java.util.function.Predicate;

public class PersonQuestionnaireEstimate extends AbstractEditor<PersonQuestionnaire> {
    @Inject
    private ComponentsFactory componentsFactory;

    @Inject
    private CollectionDatasource<QuestionnaireQuestion, UUID> questionDs;

    @Inject
    private CollectionDatasource<QuestionAnswer, UUID> answerDs;

    @Inject
    private GroupDatasource<PersonQuestionnaireAnswer, UUID> personQuestionnaireAnswerDs;

    @Inject
    private Metadata metadata;

    @Inject
    private GroupTable<PersonQuestionnaireAnswer> personQuestionnaireAnswerTable;

    @Override
    protected void postInit() {
        super.postInit();
        for (QuestionnaireQuestion questionnaireQuestion : questionDs.getItems()) {
            if (personQuestionnaireAnswerDs.getItems().stream().noneMatch(personQuestionnaireAnswer -> {
                if (questionnaireQuestion.equals(personQuestionnaireAnswer.getQuestion())) {
                    return true;
                }
                return false;
            })) {
                PersonQuestionnaireAnswer personQuestionnaireAnswer = metadata.create(PersonQuestionnaireAnswer.class);
                personQuestionnaireAnswer.setQuestion(questionnaireQuestion);
                personQuestionnaireAnswer.setPersonQuestionnaire(getItem());
                personQuestionnaireAnswerDs.addItem(personQuestionnaireAnswer);
            }
        }
    }

    @Override
    protected void postValidate(ValidationErrors errors) {
        super.postValidate(errors);
        boolean isAllAnswerChecked = true;
        for (PersonQuestionnaireAnswer personQuestionnaireAnswer : personQuestionnaireAnswerDs.getItems()) {
            if (personQuestionnaireAnswer.getAnswer() == null) {
                personQuestionnaireAnswerTable.setStyleProvider((entity, property) -> {
                    if (entity.getAnswer() == null) return "red-day";
                    else return "";
                });
                isAllAnswerChecked = false;
            }
        }
        if (!isAllAnswerChecked) errors.add(getMessage("error.answer.required"));
    }

    public Component generateAnswer(Entity entity) {
        questionDs.setItem(((PersonQuestionnaireAnswer) entity).getQuestion());
        OptionsGroup<Object, Object> optionsGroup = componentsFactory.createComponent(OptionsGroup.class);
        optionsGroup.setOrientation(OptionsGroup.Orientation.HORIZONTAL);
        optionsGroup.setOptionsList(new ArrayList(answerDs.getItems()));
        optionsGroup.addValueChangeListener(e -> {
            ((PersonQuestionnaireAnswer) entity).setAnswer((QuestionAnswer) e.getValue());
            Integer sum = 0;
            for (PersonQuestionnaireAnswer personQuestionnaireAnswer : personQuestionnaireAnswerDs.getItems()) {
                sum += personQuestionnaireAnswer.getAnswer() != null ? personQuestionnaireAnswer.getAnswer().getScore() : 0;
            }
            getItem().setOverallScore(sum);
            getItem().setAverageScore(sum.doubleValue() / questionDs.size());
        });
        optionsGroup.setValue(((PersonQuestionnaireAnswer) entity).getAnswer());
        return optionsGroup;
    }
}