package kz.uco.tsadv.lms.pojo;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public class AnsweredFeedback implements Serializable {
    protected UUID courseId;
    protected UUID templateId;
    protected List<AttemptQuestionPojo> questionsAndAnswers;

    public UUID getCourseId() {
        return courseId;
    }

    public void setCourseId(UUID courseId) {
        this.courseId = courseId;
    }

    public UUID getTemplateId() {
        return templateId;
    }

    public void setTemplateId(UUID templateId) {
        this.templateId = templateId;
    }

    public List<AttemptQuestionPojo> getQuestionsAndAnswers() {
        return questionsAndAnswers;
    }

    public void setQuestionsAndAnswers(List<AttemptQuestionPojo> questionsAndAnswers) {
        this.questionsAndAnswers = questionsAndAnswers;
    }
}
