package kz.uco.tsadv.lms.factory;

import kz.uco.tsadv.modules.learning.model.Answer;
import kz.uco.tsadv.modules.learning.model.PersonAnswer;
import kz.uco.tsadv.modules.learning.model.Question;
import kz.uco.tsadv.modules.learning.model.TestSection;
import org.apache.commons.lang3.BooleanUtils;

import java.util.List;
import java.util.stream.Collectors;

public class ManyQuestion implements QuestionFactory {

    @Override
    public void checkQuestion(Question question, PersonAnswer answer, TestSection testSection, List<Answer> answers) {
        String rightAnswers = answers.stream().filter(a -> BooleanUtils.isTrue(a.getCorrect())).map(answer1 ->
                "\"" + answer1.getId().toString() + "\"").sorted(String::compareTo)
                .collect(Collectors.joining(";"));
        if (rightAnswers.equals(answer.getAnswer())) {
            answer.setScore(testSection.getDynamicLoad() ? testSection.getPointsPerQuestion() != null
                    ? testSection.getPointsPerQuestion() : question.getScore() : question.getScore());
            answer.setCorrect(true);
        }
    }
}
