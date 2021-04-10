package kz.uco.tsadv.listeners;

import com.haulmont.cuba.core.TransactionalDataManager;
import com.haulmont.cuba.core.app.events.EntityChangedEvent;
import com.haulmont.cuba.core.entity.contracts.Id;
import com.haulmont.cuba.core.global.DataManager;
import kz.uco.tsadv.modules.learning.enums.EnrollmentStatus;
import kz.uco.tsadv.modules.learning.model.Course;
import kz.uco.tsadv.modules.learning.model.Enrollment;
import kz.uco.tsadv.modules.learning.model.Homework;
import kz.uco.tsadv.modules.learning.model.feedback.CourseFeedbackPersonAnswer;
import kz.uco.tsadv.service.LearningService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

@Component("tsadv_CourseFeedbackPersonAnswerChangedListener")
public class CourseFeedbackPersonAnswerChangedListener {

    @Inject
    protected DataManager dataManager;
    @Inject
    protected LearningService learningService;
    @Inject
    protected TransactionalDataManager transactionalDataManager;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void beforeCommit(EntityChangedEvent<CourseFeedbackPersonAnswer, UUID> event) {
        Id<CourseFeedbackPersonAnswer, UUID> entityId = event.getEntityId();
        CourseFeedbackPersonAnswer courseFeedbackPersonAnswer;
        if (event.getType().equals(EntityChangedEvent.Type.CREATED)) {
            courseFeedbackPersonAnswer = transactionalDataManager.load(entityId).view("courseFeedbackPersonAnswer.edit").one();
            if (learningService.allCourseSectionPassed(courseFeedbackPersonAnswer.getCourse() != null
                    ? courseFeedbackPersonAnswer.getCourse().getSections()
                    : null)
                    && learningService.allHomeworkPassed(getHomeworkForCourse(courseFeedbackPersonAnswer.getCourse()),
                    courseFeedbackPersonAnswer.getPersonGroup())
                    && learningService.haveAFeedbackQuestion(courseFeedbackPersonAnswer.getCourse(),
                    courseFeedbackPersonAnswer.getPersonGroup())) {
                Enrollment enrollment = transactionalDataManager.load(Enrollment.class)
                        .query("select e from tsadv$Enrollment e " +
                                " where e.personGroup = :personGroup " +
                                " and e.course = :course")
                        .parameter("personGroup", courseFeedbackPersonAnswer.getPersonGroup())
                        .parameter("course", courseFeedbackPersonAnswer.getCourse())
                        .view("enrollment-view")
                        .list().stream().findFirst().orElse(null);
                if (enrollment != null) {
                    enrollment.setStatus(EnrollmentStatus.COMPLETED);
                    transactionalDataManager.save(enrollment);
                }
            }
        } else if (event.getType().equals(EntityChangedEvent.Type.UPDATED)) {
            courseFeedbackPersonAnswer = dataManager.load(entityId).view("courseFeedbackPersonAnswer.edit").one();
            if (learningService.allCourseSectionPassed(courseFeedbackPersonAnswer.getCourse() != null
                    ? courseFeedbackPersonAnswer.getCourse().getSections()
                    : null)
                    && learningService.allHomeworkPassed(getHomeworkForCourse(courseFeedbackPersonAnswer.getCourse()),
                    courseFeedbackPersonAnswer.getPersonGroup())
                    && learningService.haveAFeedbackQuestion(courseFeedbackPersonAnswer.getCourse(),
                    courseFeedbackPersonAnswer.getPersonGroup())) {
                Enrollment enrollment = transactionalDataManager.load(Enrollment.class)
                        .query("select e from tsadv$Enrollment e " +
                                " where e.personGroup = :personGroup " +
                                " and e.course = :course")
                        .parameter("personGroup", courseFeedbackPersonAnswer.getPersonGroup())
                        .parameter("course", courseFeedbackPersonAnswer.getCourse())
                        .view("enrollment-view")
                        .list().stream().findFirst().orElse(null);
                if (enrollment != null) {
                    enrollment.setStatus(EnrollmentStatus.COMPLETED);
                    transactionalDataManager.save(enrollment);
                }
            }
        }
    }

    protected List<Homework> getHomeworkForCourse(Course course) {
        return dataManager.load(Homework.class)
                .query("select e from tsadv_Homework e " +
                        " where e.course = :course")
                .parameter("course", course)
                .view("homework.edit")
                .list();
    }
}