package kz.uco.tsadv.web.modules.learning.learningfeedbacktemplatequestion;

import com.haulmont.cuba.gui.components.AbstractEditor;
import kz.uco.tsadv.modules.learning.model.feedback.LearningFeedbackTemplateQuestion;

import java.util.ArrayList;
import java.util.Map;

public class LearningFeedbackTemplateQuestionEdit extends AbstractEditor<LearningFeedbackTemplateQuestion> {
    protected Map<String, Object> param;
    protected ArrayList<LearningFeedbackTemplateQuestion> questionList;
    @Override
    public void init(Map<String, Object> params) {
        super.init(params);
        param=params;
        if (params.containsKey("questionList")){
            questionList = (ArrayList<LearningFeedbackTemplateQuestion>)params.get("questionList");
        }
    }

    @Override
    protected boolean preCommit() {
        if (questionList!=null){
            for (LearningFeedbackTemplateQuestion question:questionList){
                if (question.getOrder().equals(getItem().getOrder())){
                    showNotification(getMessage("OrderAlreadyIncluded"));
                    return false;
                }
                if (question.getFeedbackQuestion().getId().equals(getItem().getFeedbackQuestion().getId())){
                    showNotification(getMessage("QuestionAlreadyIncluded"));
                    return false;
                }
            }
        }
        return super.preCommit();
    }
}