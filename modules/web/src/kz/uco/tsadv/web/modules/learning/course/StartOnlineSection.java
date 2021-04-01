package kz.uco.tsadv.web.modules.learning.course;

import com.haulmont.bali.util.ParamsMap;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.gui.WindowParam;
import com.haulmont.cuba.gui.components.Timer;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.BaseAction;
import com.haulmont.cuba.gui.components.validators.DoubleValidator;
import com.haulmont.cuba.gui.executors.BackgroundWorker;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.beans.TestHelper;
import kz.uco.tsadv.modules.learning.enums.TestSectionOrder;
import kz.uco.tsadv.modules.learning.model.*;
import kz.uco.tsadv.service.CourseService;
import org.apache.commons.lang3.BooleanUtils;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class StartOnlineSection extends AbstractWindow {


    @WindowParam
    protected CourseSection courseSection;

    @WindowParam
    protected Enrollment enrollment;

    @WindowParam
    protected CourseSectionAttempt lastAttempt;

    @Inject
    protected CommonService commonService;
    @Inject
    protected ComponentsFactory componentsFactory;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected Metadata metadata;
    @Inject
    protected BackgroundWorker backgroundWorker;
    @Inject
    protected CourseService courseService;
    @Inject
    protected Label minute;
    @Inject
    protected Button nextBtn;
    @Inject
    protected Button previousBtn;
    @Inject
    protected Label second;
    @Inject
    protected Button finishContent;
    @Inject
    protected Button finishTest;
    @Inject
    protected Button closeResultWindow;
    @Inject
    protected HBoxLayout wrapper;
    @Inject
    protected VBoxLayout testData;
    @Inject
    protected HBoxLayout timerBlock;

    protected String minuteTemplate;
    protected String secondTemplate;


    protected Map<Integer, Component> pageQuestions;
    protected PageRouter pageRouter;
    protected int questionCounter = 1;

    protected Test test;
    protected List<PersonAnswer> personAnswers;
    protected boolean calculateFinished = false;
    private LocalDateTime endDateTime;

    @Inject
    protected TestHelper testHelper;

    @Override
    public void init(Map<String, Object> params) {
        super.init(params);

        if (courseSection != null) {
            courseSection = dataManager.reload(courseSection, "course.section.for.start.online.section");
            minuteTemplate = getMessage("test.timer.minute");
            secondTemplate = getMessage("test.timer.second");
            createCourseSection();

        }
//        this.addCloseListener(actionId -> {
//            forceFinishTest();
//        });
    }

    protected void createCourseSection() {
        CourseSectionObject object = courseSection.getSectionObject();
        if (object.getObjectType().getCode().equalsIgnoreCase("test")) {
            setCaption(getMessage("start.section.test"));

            Test test = object.getTest();
            this.test = test;

            if (lastAttempt == null) {
                lastAttempt = addAttempt(courseSection, false, false);
            }

            wrapper.removeAll();
            testData.removeAll();

            wrapper.add(createLabel(test.getName(), "course-section-name"));

            personAnswers = new ArrayList<>();
            pageQuestions = new HashMap<>();
            pageRouter = new PageRouter();
            pageRouter.setPage(1);
            try {
                if (test.getSectionOrder().equals(TestSectionOrder.RANDOM)) {
                    Collections.shuffle(test.getSections());
                }

                if (BooleanUtils.isTrue(test.getShowSectionNewPage())) {
                    int page = 1;
                    int totalQuestionsCount = 0;
                    int num = 1;
                    for (TestSection section : test.getSections()) {
                        totalQuestionsCount += section.getGenerateCount();
                    }
                    for (TestSection testSection : test.getSections()) {
                        if (testSection.getDeleteTs() == null) {
                            List<Question> orderedQuestions;

                            if (testSection.getDynamicLoad() && testSection.getGenerateCount() != null && testSection.getPointsPerQuestion() != null) {
                                orderedQuestions = filterDynamicQuestion(testSection);
                            } else {
                                orderedQuestions = filterQuestion(testSection.getQuestions(), testSection.getQuestionOrder());
                            }
                            int allCount = orderedQuestions.size();
                            List<Question> questions = new ArrayList<>();

                            for (Question question : orderedQuestions) {
                                questions.add(question);
                                if (questions.size() == testSection.getQuestionPerPage() || allCount < testSection.getQuestionPerPage()) {
                                    generateQuestionBlock(testSection, new ArrayList<>(questions), page, lastAttempt, num, totalQuestionsCount);
                                    num += questions.size();
                                    questions = new ArrayList<>();
                                    page++;
                                }
                            }

                        }
                    }
                } else {
                    List<TestSectionQuestion> testSectionQuestions = new ArrayList<>();

                    TestSectionQuestion testSectionQuestion;
                    for (TestSection testSection : test.getSections()) {
                        if (testSection.getDeleteTs() == null) {
                            List<Question> orderedQuestions;

                            if (testSection.getDynamicLoad() && testSection.getGenerateCount() != null && testSection.getPointsPerQuestion() != null) {
                                orderedQuestions = filterDynamicQuestion(testSection);
                            } else {
                                orderedQuestions = filterQuestion(testSection.getQuestions(), testSection.getQuestionOrder());
                            }
                            testSectionQuestion = new TestSectionQuestion();
                            testSectionQuestion.setTestSection(testSection);
                            testSectionQuestion.setQuestions(orderedQuestions);
                            testSectionQuestions.add(testSectionQuestion);
                        }
                    }

                    generateQuestion(testSectionQuestions, lastAttempt, test.getQuestionPerPage());
                }

                displayQuestions();

                runTimer(test, lastAttempt);

                nextBtn.setAction(new BaseAction("next") {
                    @Override
                    public void actionPerform(Component component) {
                        if (isValidated()) {
                            if (pageRouter != null) {
                                pageRouter.setPage(pageRouter.getPage() + 1);
                                displayQuestions();
                            } else {
                                showNotification("Page Router is null");
                            }
                        }
                    }
                });

                previousBtn.setAction(new BaseAction("previous") {
                    @Override
                    public void actionPerform(Component component) {
                        if (isValidated()) {
                            if (pageRouter != null) {
                                if (pageRouter.getPage() != 1) {
                                    pageRouter.setPage(pageRouter.getPage() - 1);
                                    displayQuestions();
                                }
                            } else {
                                showNotification("Page Router is null");
                            }
                        }
                    }
                });

                checkNextPrevious();

                footerButtonsVisible(true, true);
            } catch (Exception ex) {
                testData.removeAll();
                testData.add(createLabel(ex.getMessage()));
            }
        } else {
            setCaption(getMessage("start.section.content"));

            testData.removeAll();

            LearningObject learningObject = object.getContent();

            Label content = createLabel(learningObject.getDescription(), "course-section-description");
            content.setHtmlEnabled(true);
            testData.add(content);

            Link link = componentsFactory.createComponent(Link.class);
            link.setCaption(learningObject.getObjectName());
            link.setUrl(learningObject.getUrl());
            link.setTarget("_blank");
            testData.add(link);

            footerButtonsVisible(true, false);
            addAttempt(courseSection, true, true);
        }
    }

    protected void checkNextPrevious() {
        int currentPage = pageRouter.getPage();
        previousBtn.setEnabled(pageQuestions.containsKey(currentPage - 1));
        nextBtn.setEnabled(pageQuestions.containsKey(currentPage + 1));
    }

    protected void generateQuestion(List<TestSectionQuestion> testSectionQuestions, CourseSectionAttempt courseSectionAttempt, int questionPerPage) {
        int page = 1;
        int wantCount = 0;
        int count = 0;

        for (TestSectionQuestion testSectionQuestion : testSectionQuestions) {

            int questionsCount = testSectionQuestion.getQuestions().size();

            if (questionsCount <= questionPerPage && wantCount == 0) {
                addQuestionsBlock(testSectionQuestion.getTestSection(), new ArrayList<>(testSectionQuestion.getQuestions()), page, courseSectionAttempt);

                if (questionsCount < questionPerPage) {
                    wantCount = questionPerPage - questionsCount;
                }
            } else {
                List<Question> list = new ArrayList<>();

                for (Question question : testSectionQuestion.getQuestions()) {
                    list.add(question);
                    count++;

                    if (wantCount == 0) {
                        if (count == questionPerPage) {
                            addQuestionsBlock(testSectionQuestion.getTestSection(), new ArrayList<>(list), page, courseSectionAttempt);
                            list = new ArrayList<>();
                            count = 0;
                            page++;
                        }
                    } else {
                        if (count == wantCount) {
                            addQuestionsBlock(testSectionQuestion.getTestSection(), new ArrayList<>(list), page, courseSectionAttempt);
                            list = new ArrayList<>();
                            count = 0;
                            wantCount = 0;
                            page++;
                        }
                    }
                }

                if (!list.isEmpty()) {
                    addQuestionsBlock(testSectionQuestion.getTestSection(), new ArrayList<>(list), page, courseSectionAttempt);
                }
            }
        }
    }

    protected void generateQuestionBlock(
            TestSection testSection,
            List<Question> questions,
            int page,
            CourseSectionAttempt lastAttempt,
            int num,
            int allCount
    ) {
        VBoxLayout vBoxLayout = componentsFactory.createComponent(VBoxLayout.class);
        String questionTxt = " (" + num + "/" + allCount + ")";
        Label sectionLabel = createLabel(testSection.getSectionName() + questionTxt, "course-section-test-name");
        sectionLabel.setId("sectionLabel");
        vBoxLayout.add(sectionLabel);

        for (Question question : questions) {
            PersonAnswer personAnswer = metadata.create(PersonAnswer.class);
            personAnswer.setId(UUID.randomUUID());
            personAnswer.setAttempt(lastAttempt);
            personAnswer.setTestSection(testSection);
            personAnswer.setQuestion(question);
            personAnswer.setScore(0);
            personAnswer.setAnswered(false);
            vBoxLayout.add(createQuestion(question, personAnswer, num, testSection.getAnswerOrder(), testSection));
            personAnswers.add(personAnswer);
            num++;
        }
        pageQuestions.put(page, vBoxLayout);
    }

    protected void addQuestionsBlock(TestSection testSection, List<Question> questions, int page, CourseSectionAttempt lastAttempt) {
        VBoxLayout vBoxLayout = (VBoxLayout) pageQuestions.get(page);

        if (vBoxLayout == null) {
            vBoxLayout = componentsFactory.createComponent(VBoxLayout.class);
            pageQuestions.put(page, vBoxLayout);
        }

        vBoxLayout.add(createLabel(testSection.getSectionName(), "course-section-test-name"));
        for (Question question : questions) {
            PersonAnswer personAnswer = metadata.create(PersonAnswer.class);
            personAnswer.setId(UUID.randomUUID());
            personAnswer.setAttempt(lastAttempt);
            personAnswer.setTestSection(testSection);
            personAnswer.setQuestion(question);
            personAnswer.setScore(0);
            vBoxLayout.add(createQuestion(question, personAnswer, questionCounter, testSection.getAnswerOrder(), testSection));
            personAnswers.add(personAnswer);
            questionCounter++;
        }
    }

    protected void displayQuestions() {
        testData.removeAll();
        Component component = pageQuestions.get(pageRouter.getPage());
        if (component != null) {
            testData.add(pageQuestions.get(pageRouter.getPage()));
        } else {
            testData.add(createLabel("Page not found!"));
        }

        checkNextPrevious();
    }

    protected List<Question> filterQuestion(List<QuestionInSection> questionInSections, TestSectionOrder questionOrder) {
        List<Question> list = new ArrayList<>();
        for (QuestionInSection questionInSection : questionInSections) {
            if (questionInSection != null && questionInSection.getDeleteTs() == null) {
                Question question = questionInSection.getQuestion();
                if (question != null && question.getDeleteTs() == null) {
                    list.add(question);
                }
            }
        }

        if (questionOrder.equals(TestSectionOrder.RANDOM)) {
            Collections.shuffle(list);
        }

        return list;
    }

    protected List<Question> filterDynamicQuestion(TestSection testSection) {

        List<Question> questions = commonService.getEntities(
                Question.class, "select e from tsadv$Question e where e.bank.id = :bankId " +
                        " and e.type <> 1 ",
                ParamsMap.of("bankId", testSection.getQuestionBank().getId()), View.MINIMAL);
        Collections.shuffle(questions);
        int i = 1;
        StringBuilder likeStr = new StringBuilder();
        for (Question question : questions) {
            likeStr.append(question.getId().toString());
            likeStr.append("*");
            if (++i > testSection.getGenerateCount()) {
                break;
            }
        }

        List<Question> list = commonService.getEntities(
                Question.class, "select e from tsadv$Question e where cast(:ids text) like " +
                        "concat('%',concat(e.id,'%'))",
                ParamsMap.of("ids", likeStr.toString()), "question.for.test.online");
        return list;
    }

    protected void runTimer(Test test, CourseSectionAttempt attempt) {
        attempt.setTimeSpent(0L);
        endDateTime = LocalDateTime.now().plus(Duration.ofMinutes(test.getTimer().longValue()));
        minute.setValue(String.format(minuteTemplate, test.getTimer().longValue()));
        second.setValue(String.format(secondTemplate, 0L));
        Timer timer = getTimer("timer");
        if (timer != null) {
            timer.start();
        }

    }

    public void onTimer(Timer source) {
        long until = LocalDateTime.now().until(endDateTime, ChronoUnit.SECONDS);
        if (until > 0) {
            lastAttempt.setTimeSpent(test.getTimer().longValue() * 60 - until);
            minute.setValue(String.format(minuteTemplate, until / 60));
            second.setValue(String.format(secondTemplate, until % 60));
        } else {
            Timer timer = getTimer("timer");
            if (timer != null) {
                timer.stop();
            }
            if (!calculateFinished) {
                int totalScore = 0;
                if (test != null) {
                    if (personAnswers != null) {
                        courseService.insertPersonAnswers(personAnswers);
                        for (PersonAnswer personAnswer : personAnswers) {
                            if (BooleanUtils.isTrue(personAnswer.getCorrect())) {
                                totalScore += personAnswer.getScore();
                            }
                        }
                    }
                }
                lastAttempt.setTest(test);
                lastAttempt.setTestResult(BigDecimal.valueOf(totalScore));
                lastAttempt.setTestResultPercent(testHelper.calculateTestResultPercent(lastAttempt));
                lastAttempt.setTimeSpent(lastAttempt.getTimeSpent() + 1);
                lastAttempt.setActiveAttempt(true);
                lastAttempt.setSuccess(test.getTargetScore() == null || totalScore >= test.getTargetScore());
                courseService.updateCourseSectionAttempt(lastAttempt);
                calculateFinished = true;
                wrapper.removeAll();
                wrapper.add(createLabel(test.getName(), "course-section-name"));

                testData.removeAll();

                VBoxLayout vBoxLayout = componentsFactory.createComponent(VBoxLayout.class);
                vBoxLayout.setStyleName("course-start-result-block");
                vBoxLayout.add(createLabel(getMessage("test.timeout.exception"), "course-start-error"));
                testData.add(vBoxLayout);
                String successMessage = String.format(getMessage("test.finished"), enrollment.getPersonGroup().getPerson().getFullName());
                Label successLabel = createLabel(successMessage);
                successLabel.setHtmlEnabled(true);

                vBoxLayout.add(successLabel);

                if (BooleanUtils.isTrue(test.getShowResults())) {
                    StringBuilder message = new StringBuilder();
                    message.append(getMessage(lastAttempt.getSuccess() ? "test.success" : "test.not.success")).append(" ");
                    message.append(String.format(getMessage("test.correct.answer.score"),
                            Integer.toString(totalScore))).append(", ");
                    message.append(String.format(getMessage("test.correct.answer.scorePercent"),
                            lastAttempt.getTestResultPercent().toPlainString()));
                    Label resultLabel = createLabel(message.toString());
                    resultLabel.setHtmlEnabled(true);
                    vBoxLayout.add(resultLabel);
                }
                timerBlock.setVisible(false);
                footerButtonsVisible(false, true);
                closeResultWindow.setVisible(true);
            }
        }
    }

    @Override
    protected boolean preClose(String actionId) {
        if (this.test != null) {
            if (!calculateFinished && !closeResultWindow.isVisible()) {
                forceFinishTest();
                return false;
            }
        }
        return true;
    }

    protected void forceFinishTest() {
        showOptionDialog(
                getMessage("msg.warning.title"),
                getMessage("test.close.not.save"),
                MessageType.CONFIRMATION, new Action[]{
                        new DialogAction(DialogAction.Type.YES) {
                            @Override
                            public void actionPerform(Component component) {
                                finishTest(true);
                            }
                        },
                        new DialogAction(DialogAction.Type.NO)
                });
    }

    public void finishTest() {
        finishTest(false);
    }

    protected void finishTest(boolean force) {
        boolean success = true;
        calculateFinished = true;

        for (Component component : testData.getComponents()) {
            if (component instanceof TextField) {
                try {
                    ((TextField) component).validate();
                } catch (ValidationException e) {
                    success = false;
                    break;
                }
            }
        }

        if (success) {
            boolean hasError = false;
            String errorMessage = getMessage("test.person.answers.null");
            Timer timer = getTimer("timer");
            if (timer != null) {
                timer.stop();
            }
            if (personAnswers != null) {
                try {
                    courseService.insertPersonAnswers(personAnswers);

                    int totalScore = 0;
                    if (test != null) {
                        for (PersonAnswer personAnswer : personAnswers) {
                            if (BooleanUtils.isTrue(personAnswer.getCorrect())) {
                                totalScore += personAnswer.getScore();
                            }
                        }
                    }

                    if (test != null) {
                        lastAttempt.setTest(test);
                        lastAttempt.setTestResult(BigDecimal.valueOf(totalScore));
                        lastAttempt.setTestResultPercent(testHelper.calculateTestResultPercent(lastAttempt));
                        lastAttempt.setActiveAttempt(true);
                        lastAttempt.setSuccess(test.getTargetScore() == null || totalScore >= test.getTargetScore());
                        courseService.updateCourseSectionAttempt(lastAttempt);
                        wrapper.removeAll();
                        wrapper.add(createLabel(test.getName(), "course-section-name"));
                        testData.removeAll();
                        VBoxLayout vBoxLayout = componentsFactory.createComponent(VBoxLayout.class);
                        vBoxLayout.setStyleName("course-start-result-block");
                        testData.add(vBoxLayout);
                        String successMessage = String.format(getMessage("test.finished"), enrollment.getPersonGroup().getPerson().getFullName());
                        Label successLabel = createLabel(successMessage);
                        successLabel.setHtmlEnabled(true);
                        vBoxLayout.add(successLabel);
                        if (BooleanUtils.isTrue(test.getShowResults())) {
                            StringBuilder message = new StringBuilder();
                            message.append(getMessage(lastAttempt.getSuccess() ? "test.success" : "test.not.success")).append(" ");
                            message.append(String.format(getMessage("test.correct.answer.score"),
                                    Integer.toString(totalScore))).append(", ");
                            message.append(String.format(getMessage("test.correct.answer.scorePercent"),
                                    lastAttempt.getTestResultPercent().toPlainString()));
                            Label resultLabel = createLabel(message.toString());
                            resultLabel.setHtmlEnabled(true);
                            vBoxLayout.add(resultLabel);
                        }
                    }
                } catch (Exception e) {
                    showRenderMessage(e.getMessage());
                    footerButtonsVisible(false, true);
                    closeResultWindow.setVisible(true);
                    hasError = true;
                    errorMessage = e.getMessage();
                }
            } else {
                hasError = true;
            }

            if (hasError) {
                showRenderMessage(errorMessage);
            }
            closeResultWindow.setVisible(true);
            footerButtonsVisible(false, true);
            if (force && !hasError) {
                close("", true);
            }
        }
    }

    protected boolean isValidated() {
        for (Component component : testData.getComponents()) {
            if (component instanceof TextField) {
                try {
                    ((TextField) component).validate();
                } catch (ValidationException e) {
                    return false;
                }
            }
        }
        return true;
    }

    protected void showRenderMessage(String message) {
        wrapper.removeAll();
        wrapper.add(createLabel(test.getName(), "course-section-name"));

        testData.removeAll();

        VBoxLayout vBoxLayout = componentsFactory.createComponent(VBoxLayout.class);
        vBoxLayout.setStyleName("course-start-result-block");
        vBoxLayout.add(createLabel(message, "course-start-error"));
        testData.add(vBoxLayout);
    }

    protected List<String> parseStringToList(String answer) {
        List<String> answers = new ArrayList<>();
        if (answer != null && answer.length() > 2) {
            answer = answer.substring(1, answer.length() - 1);
            for (String an : answer.split(",")) {
                answers.add(an.trim());
            }
        }
        return answers;
    }

    public void finishContent() {
        footerButtonsVisible(false, false);
        close("finish");
    }

    public void closeResultWindow() {
        finishContent();
    }

    protected Answer getCorrectAnswer(List<Answer> answers) {
        for (Answer answer : answers) {
//            if (BooleanUtils.isTrue(answer.getCorrect()) && !answer.isDeleted()) return answer;   // по идее это правильно. потому что нахрен нужны удалённые. но они появились у меня в КЯУ. Но ради Дулата это я сделаю в КЯУ
            if (BooleanUtils.isTrue(answer.getCorrect())) return answer;
        }
        return null;
    }

    protected List<Answer> getCorrectAnswers(List<Answer> answers) {

        return answers.stream().filter(new Predicate<Answer>() {
            @Override
            public boolean test(Answer answer) {
                return BooleanUtils.isTrue(answer.getCorrect());
            }
        }).collect(Collectors.toList());
    }

    protected void footerButtonsVisible(boolean isSectionStarted, boolean isTest) {
        finishTest.setVisible(isSectionStarted && isTest);
        finishContent.setVisible(isSectionStarted && !isTest);
        timerBlock.setVisible(isSectionStarted && isTest);

        nextBtn.setVisible(isSectionStarted && isTest);
        previousBtn.setVisible(isSectionStarted && isTest);
    }

    protected CourseSectionAttempt addAttempt(CourseSection courseSection, boolean isActiveAttempt, boolean isSuccess) {
        CourseSectionAttempt attempt = metadata.create(CourseSectionAttempt.class);
        attempt.setCourseSection(courseSection);
        attempt.setAttemptDate(new Date());
        attempt.setActiveAttempt(isActiveAttempt);
        attempt.setSuccess(isSuccess);
        attempt.setEnrollment(this.enrollment);
        attempt.setTest(this.test);
        attempt = dataManager.commit(attempt);

        /**
         *
         * */
        courseService.updateEnrollmentStatus(attempt);
        return attempt;
    }

    protected GroupBoxLayout createQuestion(Question question, PersonAnswer personAnswer, int num, TestSectionOrder answerOrder, TestSection testSection) {
        GroupBoxLayout block = componentsFactory.createComponent(GroupBoxLayout.class);
        block.addStyleName("start-course-test-question");

//        block.setCaption(getMessage("start.course.question") + num + ". " + question.getText());
        block.setSpacing(true);
        Label questionLabel = componentsFactory.createComponent(Label.class);
        questionLabel.setWidth("100%");
        questionLabel.setStyleName("start-course-question-name");
        questionLabel.setValue(getMessage("start.course.question") + num + ". " + question.getText());
        block.add(questionLabel);


        switch (question.getType()) {
            case TEXT: {
                createQuestionBlockWithTextAnswer(question, personAnswer, testSection, block);
                break;
            }
            case ONE: {
                OptionsGroup optionsGroup = componentsFactory.createComponent(OptionsGroup.class);
                optionsGroup.addValueChangeListener(e -> {
                    personAnswer.setAnswer(Arrays.toString(optionsGroup.getLookupSelectedItems().toArray()));
                    personAnswer.setAnswered(true);
                    Answer answer = getCorrectAnswer(question.getAnswers());
                    if (answer != null) {
                        List<String> personAnswerList = parseStringToList(personAnswer.getAnswer());
                        if (personAnswerList.size() == 1) {
                            if (answer.getId().toString().equalsIgnoreCase(personAnswerList.get(0))) {
                                personAnswer.setScore(testSection.getDynamicLoad() ? testSection.getPointsPerQuestion() != null
                                        ? testSection.getPointsPerQuestion() : question.getScore() : question.getScore());
                                personAnswer.setCorrect(true);
                            } else {
                                personAnswer.setCorrect(false);
                            }
                        }
                    }
                });
                optionsGroup.setOrientation(OptionsGroup.Orientation.VERTICAL);
                optionsGroup.setOptionsMap(parseToMap(question.getAnswers(), answerOrder));
                optionsGroup.setWidthFull();
                block.add(optionsGroup);
                break;
            }
            case MANY: {
                OptionsGroup optionsGroup = componentsFactory.createComponent(OptionsGroup.class);
                optionsGroup.addValueChangeListener(e -> {
                    personAnswer.setAnswer(Arrays.toString(optionsGroup.getLookupSelectedItems().toArray()));
                    personAnswer.setAnswered(true);
                    List<Answer> answers = getCorrectAnswers(question.getAnswers());
                    if (!answers.isEmpty()) {
                        List<String> personAnswerList = parseStringToList(personAnswer.getAnswer());
                        if (!personAnswerList.isEmpty() && personAnswerList.size() == answers.size()) {
                            int matchCount = 0;
                            for (Answer answer : answers) {
                                for (String pAnswer : personAnswerList) {
                                    if (answer.getId().toString().equals(pAnswer)) {
                                        matchCount++;
                                        break;
                                    }
                                }
                            }

                            if (matchCount == answers.size()) {
                                personAnswer.setScore(testSection.getDynamicLoad() ? testSection.getPointsPerQuestion() != null
                                        ? testSection.getPointsPerQuestion() : question.getScore() : question.getScore());
                                personAnswer.setCorrect(true);
                            }
                        }
                    }
                });
                optionsGroup.setMultiSelect(true);
                optionsGroup.setOrientation(OptionsGroup.Orientation.VERTICAL);
                optionsGroup.setOptionsMap(parseToMap(question.getAnswers(), answerOrder));
                optionsGroup.setWidthFull();
                block.add(optionsGroup);
                break;
            }
            case NUM: {
                TextField<String> textField = componentsFactory.createComponent(TextField.class);
                textField.addValidator(new DoubleValidator());

                textField.addValueChangeListener(e -> {
                    personAnswer.setAnswer(textField.getValue());
                    personAnswer.setAnswered(true);
                    Answer answer = getCorrectAnswer(question.getAnswers());
                    if (answer != null) {
                        if (personAnswer.getAnswer() != null) {

                            try {
                                textField.validate();

                                String transValue = personAnswer.getAnswer().replace(",", ".");
                                if (Double.parseDouble(answer.getAnswer()) == Double.parseDouble(transValue)) {
                                    personAnswer.setScore(testSection.getDynamicLoad() ? testSection.getPointsPerQuestion() != null
                                            ? testSection.getPointsPerQuestion() : question.getScore() : question.getScore());
                                    personAnswer.setCorrect(true);
                                }
                            } catch (Exception ex) {
                                showNotification(ex.getMessage());
                            }


                        }
                    }
                });
                block.add(textField);
                break;
            }
        }

        return block;
    }

    protected void createQuestionBlockWithTextAnswer(Question question, PersonAnswer personAnswer, TestSection testSection, GroupBoxLayout block) {
        TextArea textArea = componentsFactory.createComponent(TextArea.class);
        textArea.setWidthFull();
        textArea.addValueChangeListener(e -> {
            processTextAnswer(question, personAnswer, testSection, textArea);
        });
        block.add(textArea);
    }

    protected void processTextAnswer(Question question, PersonAnswer personAnswer, TestSection testSection, TextArea<String> textArea) {
        personAnswer.setAnswer(textArea.getValue());
        personAnswer.setAnswered(true);

        processTextAnswerCheckCorrectness(question, personAnswer, testSection, textArea);
    }

    protected void processTextAnswerCheckCorrectness(Question question, PersonAnswer personAnswer, TestSection testSection, TextArea textArea) {
        // По просьбе Дулата данный код перенесён в КЯУ
/*        Answer correctAnswer = getCorrectAnswer(question.getAnswers());
        if (correctAnswer != null) {
            if (personAnswer.getAnswer() != null) {
                try {
                    textArea.validate();

                    String personAnswerText = personAnswer.getAnswer();
                    if (personAnswerText.equalsIgnoreCase(correctAnswer.getAnswer())) {
                        personAnswer.setScore(
                                testSection.getDynamicLoad() ?
                                        testSection.getPointsPerQuestion() != null
                                                ? testSection.getPointsPerQuestion() :
                                                question.getScore() :
                                        question.getScore());
                        personAnswer.setCorrect(true);
                    }
                } catch (Exception ex) {
                    showNotification(ex.getMessage());
                }
            }
        }*/
    }

    protected Map<String, String> parseToMap(List<Answer> list, TestSectionOrder answerOrder) {
        if (answerOrder.equals(TestSectionOrder.RANDOM)) {
            Collections.shuffle(list);
        }

        Map<String, String> map = new HashMap<>();
        for (Answer answer : list) {
            map.put(answer.getAnswer(), String.valueOf(answer.getId()));
        }
        return map;
    }

    protected Label createLabel(String value) {
        Label label = componentsFactory.createComponent(Label.class);
        label.setValue(value);
        return label;
    }

    @SuppressWarnings("all")
    protected Label createLabel(String value, String cssClass) {
        Label label = createLabel(value);
        label.addStyleName(cssClass);
        return label;
    }

    protected class PageRouter {
        protected int page;

        protected boolean first;

        protected boolean last;

        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
        }

        public boolean isFirst() {
            return first;
        }

        public void setFirst(boolean first) {
            this.first = first;
        }

        public boolean isLast() {
            return last;
        }

        public void setLast(boolean last) {
            this.last = last;
        }
    }

}