package kz.uco.tsadv.web.modules.learning.testsection;

import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.WindowManager;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.data.CollectionDatasource;
import com.haulmont.cuba.gui.data.Datasource;
import kz.uco.tsadv.modules.learning.enums.TestSectionOrder;
import kz.uco.tsadv.modules.learning.model.Question;
import kz.uco.tsadv.modules.learning.model.QuestionBank;
import kz.uco.tsadv.modules.learning.model.QuestionInSection;
import kz.uco.tsadv.modules.learning.model.TestSection;
import kz.uco.tsadv.web.modules.learning.question.QuestionBrowse;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.*;

public class TestSectionEdit extends AbstractEditor<TestSection> {
    @Named("fieldGroup.dynamicLoad")
    protected CheckBox dynamicLoadField;
    @Named("fieldGroup.pointsPerQuestion")
    protected TextField pointsPerQuestionField;
    @Named("fieldGroup.generateCount")
    protected TextField generateCountField;
    @Named("fieldGroup.questionBank")
    protected PickerField questionBankField;

    @Inject
    protected CollectionDatasource<QuestionInSection, UUID> questionInSectionDs;

    @Inject
    protected Metadata metadata;

    @Inject
    protected Datasource<TestSection> testSectionDs;

    @Inject
    protected Button addQuestion;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        dynamicLoadField.addValueChangeListener(e -> {
            enableDisableQuestionCreate();
            generateCountField.setRequired((Boolean) e.getValue());
            pointsPerQuestionField.setRequired((Boolean) e.getValue());
        });
    }

    @Override
    protected void initNewItem(TestSection item) {
        super.initNewItem(item);
        item.setQuestionOrder(TestSectionOrder.RANDOM);
        if (item.getQuestionPerPage()==null){
            item.setQuestionPerPage(1);
        }
        item.setAnswerOrder(TestSectionOrder.RANDOM);

    }

    @Override
    public void ready() {
        super.ready();

        enableDisableQuestionCreate();

        questionBankField.addValueChangeListener(e -> {
            enableDisableQuestionCreate();

            List<QuestionInSection> list = new ArrayList<>(questionInSectionDs.getItems());

            for (QuestionInSection q : list) {
                questionInSectionDs.removeItem(q);
            }
        });
    }

    protected void enableDisableQuestionCreate() {
        addQuestion.setEnabled(questionBankField.getValue() != null && !dynamicLoadField.getValue());
    }

    public void openQuestions() {
        QuestionBrowse questionBrowse = (QuestionBrowse) openLookup(Question.class, items -> {
            for (Question question : (Iterable<Question>) items) {
                QuestionInSection questionInSection = metadata.create(QuestionInSection.class);
                questionInSection.setTestSection(testSectionDs.getItem());
                questionInSection.setQuestion(question);
                questionInSectionDs.addItem(questionInSection);
            }
        }, WindowManager.OpenType.DIALOG, new HashMap<String, Object>() {{
            put("bank", ((QuestionBank) questionBankField.getValue()).getId());
            put("questionInSectionDs", questionInSectionDs);
        }});

        questionBrowse.ready();
    }
}