package kz.uco.tsadv.listeners;

import com.haulmont.cuba.core.TransactionalDataManager;
import com.haulmont.cuba.core.app.events.EntityChangedEvent;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.View;
import kz.uco.tsadv.config.ExtAppPropertiesConfig;
import kz.uco.tsadv.modules.learning.model.feedback.LearningFeedbackQuestion;
import kz.uco.tsadv.modules.learning.model.feedback.LearningFeedbackTemplateQuestion;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

@Component("tsadv_LearningFeedbackTemplateQuestionChangedListener")
public class LearningFeedbackTemplateQuestionChangedListener {

    @Inject
    protected ExtAppPropertiesConfig extAppPropertiesConfig;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected TransactionalDataManager transactionalDataManager;
    @Inject
    protected Metadata metadata;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void beforeCommit(EntityChangedEvent<LearningFeedbackTemplateQuestion, UUID> event) {
        UUID defaultQuestionForFeedback = extAppPropertiesConfig.getDefaultQuestionForFeedback();
        LearningFeedbackQuestion defaultLearningFeedbackQuestion = null;
        if (defaultQuestionForFeedback != null) {
            defaultLearningFeedbackQuestion = transactionalDataManager.load(LearningFeedbackQuestion.class)
                    .query("select e from tsadv$LearningFeedbackQuestion e where e.id = :id")
                    .parameter("id", defaultQuestionForFeedback).view(View.MINIMAL)
                    .list().stream().findFirst().orElse(null);
            if (defaultLearningFeedbackQuestion == null) {
                return;
            }
        } else {
            return;
        }
        LearningFeedbackTemplateQuestion learningFeedbackTemplateQuestion = transactionalDataManager
                .load(event.getEntityId()).view("learningFeedbackTemplateQuestion.edit").optional().orElse(null);
        if (learningFeedbackTemplateQuestion != null) {
            if (learningFeedbackTemplateQuestion.getFeedbackQuestion() != null
                    && learningFeedbackTemplateQuestion.getFeedbackQuestion().getId().equals(defaultQuestionForFeedback)) {
                return;
            }
            LearningFeedbackTemplateQuestion defaultLearningFeedbackTemplateQuestion = transactionalDataManager
                    .load(LearningFeedbackTemplateQuestion.class)
                    .query("select e from tsadv$LearningFeedbackTemplateQuestion e " +
                            " where e.feedbackTemplate.id = :feedbackTemplateId " +
                            " and e.feedbackQuestion.id = :defaultQuestionId")
                    .parameter("feedbackTemplateId", learningFeedbackTemplateQuestion.getFeedbackTemplate().getId())
                    .parameter("defaultQuestionId", defaultQuestionForFeedback)
                    .view("learningFeedbackTemplateQuestion.edit").list().stream().findFirst().orElse(null);
            List<LearningFeedbackTemplateQuestion> orderedLFTQ = transactionalDataManager.load(LearningFeedbackTemplateQuestion.class)
                    .query("select e from tsadv$LearningFeedbackTemplateQuestion e " +
                            " where e.feedbackTemplate.id = :feedbackTemplateId " +
                            " and e.order is not null  " +
                            " and e.feedbackQuestion.id <> :defaultQuestionId")
                    .parameter("feedbackTemplateId", learningFeedbackTemplateQuestion.getFeedbackTemplate().getId())
                    .parameter("defaultQuestionId", defaultQuestionForFeedback)
                    .view("learningFeedbackTemplateQuestion.edit").list();
            LearningFeedbackTemplateQuestion maxOrderLFTQ = orderedLFTQ.stream().filter(learningFeedbackTemplateQuestion1 -> learningFeedbackTemplateQuestion1.getOrder() != null)
                    .max((o1, o2) -> o1.getOrder() > o2.getOrder() ? 1 : -1).orElse(null);
            Integer maxOrder = 0;
            if (maxOrderLFTQ != null) {
                maxOrder = (maxOrderLFTQ.getOrder() > (learningFeedbackTemplateQuestion.getOrder() != null
                        ? learningFeedbackTemplateQuestion.getOrder() : 0)
                        ? maxOrderLFTQ.getOrder() : learningFeedbackTemplateQuestion.getOrder());
            }
            if (defaultLearningFeedbackTemplateQuestion != null) {
                defaultLearningFeedbackTemplateQuestion.setOrder(maxOrder + 1);
            } else {
                defaultLearningFeedbackTemplateQuestion = metadata.create(LearningFeedbackTemplateQuestion.class);
                defaultLearningFeedbackTemplateQuestion.setOrder(maxOrder + 1);
                defaultLearningFeedbackTemplateQuestion.setFeedbackTemplate(learningFeedbackTemplateQuestion.getFeedbackTemplate());
                defaultLearningFeedbackTemplateQuestion.setFeedbackQuestion(defaultLearningFeedbackQuestion);
            }
            transactionalDataManager.save(defaultLearningFeedbackTemplateQuestion);
        } else {
            learningFeedbackTemplateQuestion = transactionalDataManager
                    .load(event.getEntityId()).view("learningFeedbackTemplateQuestion.edit").softDeletion(false)
                    .optional().orElse(null);
            if (learningFeedbackTemplateQuestion != null) {
                if (learningFeedbackTemplateQuestion.getFeedbackQuestion() != null
                        && learningFeedbackTemplateQuestion.getFeedbackQuestion().getId().equals(defaultQuestionForFeedback)) {
                    return;
                }
                LearningFeedbackTemplateQuestion defaultLearningFeedbackTemplateQuestion = transactionalDataManager
                        .load(LearningFeedbackTemplateQuestion.class)
                        .query("select e from tsadv$LearningFeedbackTemplateQuestion e " +
                                " where e.feedbackTemplate.id = :feedbackTemplateId " +
                                " and e.feedbackQuestion.id = :defaultQuestionId")
                        .parameter("feedbackTemplateId", learningFeedbackTemplateQuestion.getFeedbackTemplate().getId())
                        .parameter("defaultQuestionId", defaultQuestionForFeedback)
                        .view("learningFeedbackTemplateQuestion.edit").list().stream().findFirst().orElse(null);
                List<LearningFeedbackTemplateQuestion> orderedLFTQ = transactionalDataManager.load(LearningFeedbackTemplateQuestion.class)
                        .query("select e from tsadv$LearningFeedbackTemplateQuestion e " +
                                " where e.feedbackTemplate.id = :feedbackTemplateId " +
                                " and e.order is not null  " +
                                " and e.feedbackQuestion.id <> :defaultQuestionId")
                        .parameter("feedbackTemplateId", learningFeedbackTemplateQuestion.getFeedbackTemplate().getId())
                        .parameter("defaultQuestionId", defaultQuestionForFeedback)
                        .view("learningFeedbackTemplateQuestion.edit").list();
                LearningFeedbackTemplateQuestion maxOrderLFTQ = orderedLFTQ.stream().filter(learningFeedbackTemplateQuestion1 -> learningFeedbackTemplateQuestion1.getOrder() != null)
                        .max((o1, o2) -> o1.getOrder() > o2.getOrder() ? 1 : -1).orElse(null);
                Integer maxOrder = 0;
                if (maxOrderLFTQ != null) {
                    maxOrder = maxOrderLFTQ.getOrder() != null ? maxOrderLFTQ.getOrder() : 0;
                }
                if (defaultLearningFeedbackTemplateQuestion != null) {
                    defaultLearningFeedbackTemplateQuestion.setOrder(maxOrder + 1);
                } else {
                    defaultLearningFeedbackTemplateQuestion = metadata.create(LearningFeedbackTemplateQuestion.class);
                    defaultLearningFeedbackTemplateQuestion.setOrder(maxOrder + 1);
                    defaultLearningFeedbackTemplateQuestion.setFeedbackTemplate(learningFeedbackTemplateQuestion.getFeedbackTemplate());
                    defaultLearningFeedbackTemplateQuestion.setFeedbackQuestion(defaultLearningFeedbackQuestion);
                }
                transactionalDataManager.save(defaultLearningFeedbackTemplateQuestion);
            }
        }
    }
}