package kz.uco.tsadv.listeners;

import com.haulmont.cuba.core.app.events.EntityChangedEvent;
import com.haulmont.cuba.core.global.CommitContext;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.View;
import kz.uco.tsadv.config.ExtAppPropertiesConfig;
import kz.uco.tsadv.modules.learning.enums.feedback.LearningFeedbackUsageType;
import kz.uco.tsadv.modules.learning.model.feedback.LearningFeedbackQuestion;
import kz.uco.tsadv.modules.learning.model.feedback.LearningFeedbackTemplate;
import kz.uco.tsadv.modules.learning.model.feedback.LearningFeedbackTemplateQuestion;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.UUID;

@Component("tsadv_LearningFeedbackTemplateChangedListener")
public class LearningFeedbackTemplateChangedListener {
    @Inject
    protected ExtAppPropertiesConfig extAppPropertiesConfig;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected Metadata metadata;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void afterCommit(EntityChangedEvent<LearningFeedbackTemplate, UUID> event) {
        UUID defaultQuestionForFeedback = extAppPropertiesConfig.getDefaultQuestionForFeedback();
        if (defaultQuestionForFeedback == null || defaultQuestionForFeedback.toString().isEmpty()) {
            return;
        }
        LearningFeedbackQuestion learningFeedbackQuestion = dataManager.load(LearningFeedbackQuestion.class)
                .query("select e from tsadv$LearningFeedbackQuestion e where e.id = :id")
                .parameter("id", defaultQuestionForFeedback).view(View.MINIMAL).list()
                .stream().findFirst().orElse(null);
        if (learningFeedbackQuestion == null) {
            return;
        }
        if (EntityChangedEvent.Type.CREATED.equals(event.getType())) {
            CommitContext commitContext = new CommitContext();
            LearningFeedbackTemplate learningFeedbackTemplate = dataManager
                    .load(LearningFeedbackTemplate.class)
                    .query("select e from tsadv$LearningFeedbackTemplate e where e.id = :id")
                    .parameter("id", event.getEntityId().getValue()).view("learningFeedbackTemplate.edit")
                    .list().stream().findFirst().orElse(null);
            if (learningFeedbackTemplate != null) {
                if (learningFeedbackTemplate.getTemplateQuestions() != null) {
                    if (learningFeedbackTemplate.getTemplateQuestions().stream().anyMatch(learningFeedbackTemplateQuestion ->
                            learningFeedbackTemplateQuestion.getFeedbackQuestion() != null
                                    && learningFeedbackTemplateQuestion.getFeedbackQuestion().getId()
                                    .equals(learningFeedbackQuestion.getId())) ||
                            !LearningFeedbackUsageType.COURSE.equals(learningFeedbackTemplate.getUsageType())) {
                        return;
                    }
                }
                LearningFeedbackTemplateQuestion learningFeedbackTemplateQuestion = metadata.create(LearningFeedbackTemplateQuestion.class);
                learningFeedbackTemplateQuestion.setFeedbackTemplate(learningFeedbackTemplate);
                learningFeedbackTemplateQuestion.setFeedbackQuestion(learningFeedbackQuestion);
                if (learningFeedbackTemplate.getTemplateQuestions() == null) {
                    learningFeedbackTemplate.setTemplateQuestions(new ArrayList<>());
                }
                commitContext.addInstanceToCommit(learningFeedbackTemplate);
                Integer maxOrder = learningFeedbackTemplate.getTemplateQuestions().stream().filter(learningFeedbackTemplateQuestion1 ->
                        learningFeedbackTemplateQuestion1.getOrder() != null).map(learningFeedbackTemplateQuestion1 ->
                        learningFeedbackTemplateQuestion1.getOrder()).max(Integer::compareTo).orElse(0);
                learningFeedbackTemplateQuestion.setOrder(maxOrder + 1);
                learningFeedbackTemplate.getTemplateQuestions().add(learningFeedbackTemplateQuestion);
                commitContext.addInstanceToCommit(learningFeedbackTemplateQuestion);
                dataManager.commit(commitContext);
            }
        }
    }
}