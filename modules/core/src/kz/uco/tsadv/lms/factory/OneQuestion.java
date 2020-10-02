package kz.uco.tsadv.lms.factory;

import kz.uco.tsadv.modules.learning.model.Answer;
import kz.uco.tsadv.modules.learning.model.PersonAnswer;
import kz.uco.tsadv.modules.learning.model.Question;
import kz.uco.tsadv.modules.learning.model.TestSection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OneQuestion implements QuestionFactory {

    @Override
    public void checkQuestion(Question question, PersonAnswer answer, TestSection testSection, List<Answer> answers) {
        Answer correctAnswer = answers.stream().filter(Answer::getCorrect).findAny().orElse(null);
        if (correctAnswer == null) {
            throw new NullPointerException("Can not find correct answer");
        }

        List<String> personAnswerList = parseStringToList(answer.getAnswer());
        answer.setScore(0);
        if (personAnswerList.size() == 1) {
            if (correctAnswer.getId().toString().equalsIgnoreCase(personAnswerList.get(0))) {
                answer.setScore(testSection.getDynamicLoad() ? testSection.getPointsPerQuestion() != null
                        ? testSection.getPointsPerQuestion() : question.getScore() : question.getScore());
                answer.setCorrect(true);
            } else {
                answer.setCorrect(false);
            }
        }
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
}
