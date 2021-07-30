package kz.uco.tsadv.listeners;

import com.haulmont.cuba.core.app.events.EntityChangedEvent;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import kz.uco.tsadv.config.ExtAppPropertiesConfig;
import kz.uco.tsadv.modules.learning.model.CourseReview;
import kz.uco.tsadv.modules.learning.model.feedback.CourseFeedbackPersonAnswerDetail;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import javax.inject.Inject;
import java.util.UUID;

@Component("tsadv_CourseFeedbackPersonAnswerDetailChangedListener")
public class CourseFeedbackPersonAnswerDetailChangedListener {

    @Inject
    protected ExtAppPropertiesConfig extAppPropertiesConfig;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected Metadata metadata;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void afterCommit(EntityChangedEvent<CourseFeedbackPersonAnswerDetail, UUID> event) {
        UUID defaultQuestionForFeedback = extAppPropertiesConfig.getDefaultQuestionForFeedback();
        if (defaultQuestionForFeedback != null) {
            if (EntityChangedEvent.Type.CREATED.equals(event.getType())) {
                CourseFeedbackPersonAnswerDetail courseFeedbackPersonAnswerDetail = dataManager
                        .load(event.getEntityId()).view("courseFeedbackPersonAnswerDetail.edit").one();
                if (courseFeedbackPersonAnswerDetail.getQuestion() != null
                        && defaultQuestionForFeedback.equals(courseFeedbackPersonAnswerDetail.getQuestion().getId())) {
                    CourseReview courseReview = null;
                    if (courseFeedbackPersonAnswerDetail.getCourseFeedbackPersonAnswer() != null) {
                        courseReview = dataManager.load(CourseReview.class)
                                .query("select e from tsadv$CourseReview e " +
                                        " where e.fromCourseFeedbackPersonAnswer.id = :fromCourseFeedbackPersonAnswerId")
                                .parameter("fromCourseFeedbackPersonAnswerId",
                                        courseFeedbackPersonAnswerDetail.getCourseFeedbackPersonAnswer().getId())
                                .view("courseReview.browse").list().stream().findFirst().orElse(null);
                        if (courseReview == null) {
                            courseReview = metadata.create(CourseReview.class);
                            courseReview.setFromCourseFeedbackPersonAnswer(courseFeedbackPersonAnswerDetail.getCourseFeedbackPersonAnswer());
                            courseReview.setPersonGroup(courseFeedbackPersonAnswerDetail.getPersonGroup());
                            courseReview.setCourse(courseFeedbackPersonAnswerDetail.getCourse());
                        }
                        courseReview.setRate(courseFeedbackPersonAnswerDetail.getCourseFeedbackPersonAnswer().getAvgScore());
                        if (courseFeedbackPersonAnswerDetail.getTextAnswer() != null) {
                            courseReview.setText(courseFeedbackPersonAnswerDetail.getTextAnswer());
                        }
                        dataManager.commit(courseReview);
                    }
                }
            } else if (EntityChangedEvent.Type.UPDATED.equals(event.getType())) {
                CourseFeedbackPersonAnswerDetail courseFeedbackPersonAnswerDetail = dataManager
                        .load(event.getEntityId()).view("courseFeedbackPersonAnswerDetail.edit").one();
                if (courseFeedbackPersonAnswerDetail.getQuestion() != null
                        && defaultQuestionForFeedback.equals(courseFeedbackPersonAnswerDetail.getQuestion().getId())) {
                    if (event.getChanges().isChanged("textAnswer")) {
                        CourseReview courseReview = dataManager.load(CourseReview.class)
                                .query("select e from tsadv$CourseReview e " +
                                        " where e.fromCourseFeedbackPersonAnswer.id = :courseFeedbackPersonAnswerId")
                                .parameter("courseFeedbackPersonAnswerId", courseFeedbackPersonAnswerDetail
                                        .getCourseFeedbackPersonAnswer().getId()).view("courseReview.browse")
                                .list().stream().findFirst().orElse(null);
                        if (courseReview != null) {
                            courseReview.setText(courseFeedbackPersonAnswerDetail.getTextAnswer());
                            courseReview.setRate(courseFeedbackPersonAnswerDetail
                                    .getCourseFeedbackPersonAnswer().getAvgScore());
                        } else {
                            courseReview = metadata.create(CourseReview.class);
                            if (courseFeedbackPersonAnswerDetail.getCourseFeedbackPersonAnswer() != null) {
                                courseReview.setRate(courseFeedbackPersonAnswerDetail.getCourseFeedbackPersonAnswer().getAvgScore());
                                courseReview.setFromCourseFeedbackPersonAnswer(courseFeedbackPersonAnswerDetail.getCourseFeedbackPersonAnswer());
                            }
                            courseReview.setPersonGroup(courseFeedbackPersonAnswerDetail.getPersonGroup());
                            courseReview.setCourse(courseFeedbackPersonAnswerDetail.getCourse());
                            if (courseFeedbackPersonAnswerDetail.getTextAnswer() != null) {
                                courseReview.setText(courseFeedbackPersonAnswerDetail.getTextAnswer());
                            }
                        }
                        dataManager.commit(courseReview);
                    }
                }
            } else if (EntityChangedEvent.Type.DELETED.equals(event.getType())) {
                CourseFeedbackPersonAnswerDetail courseFeedbackPersonAnswerDetail = dataManager
                        .load(event.getEntityId()).softDeletion(false).view("courseFeedbackPersonAnswerDetail.edit").one();
                if (courseFeedbackPersonAnswerDetail.getQuestion() != null) {
                    if (defaultQuestionForFeedback.equals(courseFeedbackPersonAnswerDetail.getQuestion().getId())) {
                        if (courseFeedbackPersonAnswerDetail.getCourseFeedbackPersonAnswer() != null) {
                            CourseReview courseReview = dataManager.load(CourseReview.class)
                                    .query("select e from tsadv$CourseReview e " +
                                            " where e.fromCourseFeedbackPersonAnswer.id = :fromCourseFeedbackPersonAnswerId")
                                    .parameter("fromCourseFeedbackPersonAnswerId", courseFeedbackPersonAnswerDetail
                                            .getCourseFeedbackPersonAnswer().getId()).list().stream().findFirst()
                                    .orElse(null);
                            if (courseReview != null) {
                                dataManager.remove(courseReview);
                            }
                        }
                    }
                }
            }
        }
    }
}