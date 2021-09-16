package kz.uco.tsadv.web.modules.learning.testsection.v72;

import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.ScreenBuilders;
import com.haulmont.cuba.gui.actions.list.AddAction;
import com.haulmont.cuba.gui.components.Action;
import com.haulmont.cuba.gui.components.HasValue;
import com.haulmont.cuba.gui.components.TextField;
import com.haulmont.cuba.gui.model.CollectionPropertyContainer;
import com.haulmont.cuba.gui.screen.*;
import kz.uco.tsadv.modules.learning.enums.TestSectionOrder;
import kz.uco.tsadv.modules.learning.model.Question;
import kz.uco.tsadv.modules.learning.model.QuestionBank;
import kz.uco.tsadv.modules.learning.model.QuestionInSection;
import kz.uco.tsadv.modules.learning.model.TestSection;
import kz.uco.tsadv.web.modules.learning.question.v72.QuestionBrowse;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.stream.Collectors;

@UiController("tsadv$TestSection.edit")
@UiDescriptor("test-section-edit.xml")
@EditedEntityContainer("testSectionDc")
@LoadDataBeforeShow
public class TestSectionEdit extends StandardEditor<TestSection> {
    @Inject
    protected CollectionPropertyContainer<QuestionInSection> questionsDc;
    @Inject
    protected TextField<Integer> generateCountField;
    @Inject
    protected TextField<Integer> pointsPerQuestionField;
    @Named("questionsTable.add")
    protected AddAction<QuestionInSection> questionsTableAdd;
    @Inject
    protected Metadata metadata;
    @Inject
    protected ScreenBuilders screenBuilders;

    @Subscribe
    protected void onInitEntity(InitEntityEvent<TestSection> event) {
        TestSection item = event.getEntity();

        item.setQuestionOrder(TestSectionOrder.RANDOM);
        if (item.getQuestionPerPage() == null) {
            item.setQuestionPerPage(1);
        }
        item.setAnswerOrder(TestSectionOrder.RANDOM);
    }

    @Subscribe("dynamicLoadField")
    protected void onDynamicLoadFieldValueChange(HasValue.ValueChangeEvent<Boolean> e) {
        boolean value = Boolean.TRUE.equals(e.getValue());
        generateCountField.setRequired(value);
        pointsPerQuestionField.setRequired(value);
        questionsTableAdd.setEnabled(questionsTableAddEnabledRule());
    }

    @Install(to = "questionsTable.add", subject = "enabledRule")
    protected boolean questionsTableAddEnabledRule() {
        return Boolean.FALSE.equals(getEditedEntity().getDynamicLoad())
                && getEditedEntity().getQuestionBank() != null;
    }

    protected QuestionInSection parseToQuestionInSection(Question question) {
        QuestionInSection section = metadata.create(QuestionInSection.class);
        section.setQuestion(question);
        section.setTestSection(getEditedEntity());
        return section;
    }

    @Subscribe("questionsTable.add")
    protected void onQuestionsTableAdd(Action.ActionPerformedEvent event) {
        QuestionBrowse browse = screenBuilders.lookup(Question.class, this)
                .withScreenClass(QuestionBrowse.class)
                .withSelectHandler(questions -> {
                    List<Question> questionList = questionsDc.getItems().stream().map(QuestionInSection::getQuestion).collect(Collectors.toList());

                    questions.stream()
                            .filter(question -> !questionList.contains(question))
                            .map(this::parseToQuestionInSection)
                            .forEach(questionsDc.getMutableItems()::add);
                })
                .build();

        browse.setBank(getEditedEntity().getQuestionBank());
        browse.show();
    }

    @Subscribe("questionBankField")
    protected void onQuestionBankFieldValueChange(HasValue.ValueChangeEvent<QuestionBank> event) {
        questionsTableAdd.setEnabled(questionsTableAddEnabledRule());
    }
}