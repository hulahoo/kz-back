package kz.uco.tsadv.web.modules.learning.enrollment;

import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.tsadv.modules.learning.model.Answer;
import kz.uco.tsadv.modules.learning.model.CourseSectionAttempt;
import kz.uco.tsadv.modules.learning.model.PersonAnswer;
import kz.uco.tsadv.modules.learning.model.Question;
import kz.uco.tsadv.service.CourseService;

import javax.inject.Inject;
import java.util.*;

public class AssignedTestAttemptAnswers extends AbstractWindow {

    public static final String ATTEMPT = "ATAA_ATTEMPT";

    @Inject
    protected ScrollBoxLayout scrollBox;
    @Inject
    protected ComponentsFactory componentsFactory;
    @Inject
    protected CourseService courseService;

    @Inject
    protected DataManager dataManager;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        if (params.containsKey(ATTEMPT)) {
            showAnswers((CourseSectionAttempt) params.get(ATTEMPT));
        }
    }

    protected void showAnswers(CourseSectionAttempt courseSectionAttempt) {
        List<PersonAnswer> personAnswers = loadPersonAnswers(courseSectionAttempt.getId());
        int num = 1;
        for (PersonAnswer personAnswer : personAnswers) {
            scrollBox.add(makeQuestionAnswer(personAnswer, num));
            num++;
        }
    }

    protected GroupBoxLayout makeQuestionAnswer(PersonAnswer personAnswer, int num) {
        GroupBoxLayout block = componentsFactory.createComponent(GroupBoxLayout.class);
        block.addStyleName("start-course-test-question");
        block.setCaption(getMessage("start.course.question") + num + ". " + personAnswer.getQuestion().getText());
        block.setSpacing(true);
        Question question = personAnswer.getQuestion();
        switch (question.getType()) {
            case TEXT: {
                TextArea textArea = componentsFactory.createComponent(TextArea.class);
                textArea.setWidthFull();
                textArea.setEditable(false);
                if (personAnswer.getAnswered()) {
                    textArea.setValue(personAnswer.getAnswer());
                }
                block.add(textArea);
                break;
            }
            case ONE: {
                OptionsGroup optionsGroup = componentsFactory.createComponent(OptionsGroup.class);
                optionsGroup.setOrientation(OptionsGroup.Orientation.VERTICAL);
                optionsGroup.setOptionsMap(parseToMap(question.getAnswers()));
                if (personAnswer.getAnswered()) {
                    String answer = personAnswer.getAnswer();
                    if (answer != null && answer.startsWith("[")) {
                        answer = answer.substring(1, answer.length() - 1);
                        optionsGroup.setValue(answer);
                    }
                }
                optionsGroup.setEditable(false);
                block.add(optionsGroup);
                break;
            }
            case MANY: {
                OptionsGroup optionsGroup = componentsFactory.createComponent(OptionsGroup.class);
                optionsGroup.setMultiSelect(true);
                optionsGroup.setOrientation(OptionsGroup.Orientation.VERTICAL);
                optionsGroup.setOptionsMap(parseToMap(question.getAnswers()));
                if (personAnswer.getAnswered()) {
                    String answer = personAnswer.getAnswer();
                    if (answer != null && answer.startsWith("[")) {
                        List<String> answers = new ArrayList<>();
                        for (String answerId : answer.substring(1, answer.length() - 1).split(",")) {
                            answers.add(answerId.trim());
                        }
                        optionsGroup.setValue(answers);
                    }
                }
                optionsGroup.setEditable(false);
                block.add(optionsGroup);
                break;
            }
            case NUM: {
                TextField textField = componentsFactory.createComponent(TextField.class);
                if (personAnswer.getAnswered()) {
                    textField.setValue(personAnswer.getAnswer());
                }
                textField.setEditable(false);
                block.add(textField);
                break;
            }
        }
        return block;
    }

    protected List<PersonAnswer> loadPersonAnswers(UUID attemptId) {
        LoadContext<PersonAnswer> loadContext = LoadContext.create(PersonAnswer.class);
        LoadContext.Query query = LoadContext.createQuery(
                "select e from tsadv$PersonAnswer e where e.attempt.id = :attemptId");
        query.setParameter("attemptId", attemptId);
        loadContext.setQuery(query);
        loadContext.setView("personAnswer-view");
        List<PersonAnswer> resultList = dataManager.loadList(loadContext);
        if (resultList == null) resultList = new ArrayList<>();
        return resultList;
    }

    protected Map<String, String> parseToMap(List<Answer> list) {
        Map<String, String> map = new HashMap<>();
        for (Answer answer : list) {
            map.put(answer.getAnswer(), String.valueOf(answer.getId()));
        }
        return map;
    }
}