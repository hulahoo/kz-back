package kz.uco.tsadv.strategy.aggregation;

import com.haulmont.cuba.gui.data.aggregation.AggregationStrategy;
import kz.uco.tsadv.modules.recruitment.enums.RcAnswerType;
import kz.uco.tsadv.modules.recruitment.model.InterviewAnswer;
import kz.uco.tsadv.modules.recruitment.model.InterviewQuestion;

import java.text.DecimalFormat;
import java.util.Collection;

/**
 * @author veronika.buksha
 */
public class InterviewQuestionnaireScoreAggregation implements AggregationStrategy<InterviewQuestion, String> {
    private static final DecimalFormat df = new DecimalFormat("###.##");

    @Override
    public String aggregate(Collection<InterviewQuestion> propertyValues) {
        Double maxScore = 0d;
        Double score = 0d;

        for (InterviewQuestion interviewQuestion : propertyValues) {
            if (interviewQuestion.getQuestion().getAnswerType() == RcAnswerType.MULTI) {
                maxScore += interviewQuestion.getAnswers().stream().mapToDouble(InterviewAnswer::getWeight).filter(w -> w >= 0).sum();
                score += interviewQuestion.getAnswers().stream().filter(answer -> answer != null && answer.getBooleanAnswer() != null && answer.getBooleanAnswer()).mapToDouble(InterviewAnswer::getWeight).sum();
            }
            if (interviewQuestion.getQuestion().getAnswerType() == RcAnswerType.SINGLE) {
                maxScore += interviewQuestion.getAnswers().stream().mapToDouble(InterviewAnswer::getWeight).max().orElse(0d);
                score += interviewQuestion.getAnswers().stream().filter(answer -> answer != null && answer.getBooleanAnswer() != null && answer.getBooleanAnswer()).mapToDouble(InterviewAnswer::getWeight).sum();
            }
        }

        StringBuilder result = new StringBuilder();
        if (maxScore != 0) {
            result.append(score);
            result.append(" (");
            result.append(df.format(score / maxScore * 100));
            result.append("%)");
        }
        return result.toString();
    }

    @Override
    public Class<String> getResultClass() {
        return String.class;
    }
}
