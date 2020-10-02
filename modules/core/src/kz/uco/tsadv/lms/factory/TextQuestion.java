package kz.uco.tsadv.lms.factory;

import kz.uco.tsadv.modules.learning.model.Answer;
import kz.uco.tsadv.modules.learning.model.PersonAnswer;
import kz.uco.tsadv.modules.learning.model.Question;
import kz.uco.tsadv.modules.learning.model.TestSection;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TextQuestion implements QuestionFactory {

    @Override
    public void checkQuestion(Question question, PersonAnswer personAnswer, TestSection testSection, List<Answer> answers) {
        Optional<Answer> correctAnswerOptional = answers.stream().filter(a -> BooleanUtils.isTrue(a.getCorrect())).findFirst();
        if (!correctAnswerOptional.isPresent()) {
            throw new NullPointerException("Can not find correct \"text\" answer");
        }
        Answer answer = correctAnswerOptional.get();

        personAnswer.setScore(0);
        String personAnswerString = personAnswer.getAnswer();
        if (answer.getAnswer().trim().equalsIgnoreCase(personAnswerString.trim())) {
            personAnswer.setScore(testSection.getDynamicLoad() ? testSection.getPointsPerQuestion() != null
                    ? testSection.getPointsPerQuestion() : question.getScore() : question.getScore());
            personAnswer.setCorrect(true);
        }
    }
}
