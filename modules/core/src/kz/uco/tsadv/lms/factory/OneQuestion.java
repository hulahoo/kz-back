package kz.uco.tsadv.lms.factory;

import kz.uco.tsadv.modules.learning.model.Answer;
import kz.uco.tsadv.modules.learning.model.PersonAnswer;
import kz.uco.tsadv.modules.learning.model.Question;
import kz.uco.tsadv.modules.learning.model.TestSection;

import java.util.List;

public class OneQuestion implements QuestionFactory {

    @Override
    public void checkQuestion(Question question, PersonAnswer answer, TestSection testSection, List<Answer> answers) {
        Answer correctAnswer = answers.stream().filter(Answer::getCorrect).findFirst().orElse(null);
        if (correctAnswer == null) {
            throw new NullPointerException("Can not find correct answer");
        }
        answer.setScore(0);
        if (("\"" + correctAnswer.getId().toString() + "\"").equalsIgnoreCase(answer.getAnswer())) {
            answer.setScore(testSection.getDynamicLoad() ? testSection.getPointsPerQuestion() != null
                    ? testSection.getPointsPerQuestion() : question.getScore() : question.getScore());
            answer.setCorrect(true);
        } else {
            answer.setCorrect(false);
        }
    }
}
