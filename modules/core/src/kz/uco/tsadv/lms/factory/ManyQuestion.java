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
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ManyQuestion implements QuestionFactory {

    @Override
    public void checkQuestion(Question question, PersonAnswer answer, TestSection testSection, List<Answer> answers) {
        List<Answer> correctAnswers = answers.stream().filter(a -> BooleanUtils.isTrue(a.getCorrect())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(correctAnswers)) {
            throw new NullPointerException("Can not find correct answers");
        }

        answer.setScore(0);
        List<String> personAnswerList = parseStringToList(answer.getAnswer());
        Optional<Integer> matchCountOptional = correctAnswers.stream().map(correctAnswer -> {
            Optional<String> optional = personAnswerList.stream().filter(personAnswer -> personAnswer.equals(correctAnswer.getId().toString())).findFirst();
            if (optional.isPresent()) {
                return 1;
            } else {
                return 0;
            }
        }).reduce(Integer::sum);
        int matchCount = matchCountOptional.orElse(0);

        if (matchCount == correctAnswers.size()) {
            answer.setScore(testSection.getDynamicLoad() ? testSection.getPointsPerQuestion() != null
                    ? testSection.getPointsPerQuestion() : question.getScore() : question.getScore());
            answer.setCorrect(true);
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
