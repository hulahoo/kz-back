package kz.uco.tsadv.listeners;

import com.haulmont.cuba.core.TransactionalDataManager;
import com.haulmont.cuba.core.app.events.EntityChangedEvent;
import com.haulmont.cuba.core.entity.contracts.Id;
import com.haulmont.cuba.core.global.DataManager;
import kz.uco.base.service.NotificationSenderAPIService;
import kz.uco.tsadv.modules.administration.TsadvUser;
import kz.uco.tsadv.modules.learning.enums.EnrollmentStatus;
import kz.uco.tsadv.modules.learning.model.Course;
import kz.uco.tsadv.modules.learning.model.Enrollment;
import kz.uco.tsadv.modules.learning.model.Homework;
import kz.uco.tsadv.modules.learning.model.feedback.CourseFeedbackPersonAnswer;
import kz.uco.tsadv.modules.performance.model.CourseTrainer;
import kz.uco.tsadv.service.LearningService;
import kz.uco.tsadv.service.OrganizationHrUserService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component("tsadv_CourseFeedbackPersonAnswerChangedListener")
public class CourseFeedbackPersonAnswerChangedListener {

    @Inject
    protected DataManager dataManager;
    @Inject
    protected LearningService learningService;
    @Inject
    protected TransactionalDataManager transactionalDataManager;
    @Inject
    private NotificationSenderAPIService notificationSenderAPIService;
    @Inject
    private OrganizationHrUserService organizationHrUserService;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void beforeCommit(EntityChangedEvent<CourseFeedbackPersonAnswer, UUID> event) {
        Id<CourseFeedbackPersonAnswer, UUID> entityId = event.getEntityId();
        CourseFeedbackPersonAnswer courseFeedbackPersonAnswer;
        if (event.getType().equals(EntityChangedEvent.Type.CREATED)) {
            courseFeedbackPersonAnswer = transactionalDataManager.load(entityId).view("courseFeedbackPersonAnswer.edit").one();
            Enrollment enrollment = transactionalDataManager.load(Enrollment.class)
                    .query("select e from tsadv$Enrollment e " +
                            " where e.personGroup = :personGroup " +
                            " and e.course = :course")
                    .parameter("personGroup", courseFeedbackPersonAnswer.getPersonGroup())
                    .parameter("course", courseFeedbackPersonAnswer.getCourse())
                    .view("enrollment-view")
                    .list().stream().findFirst().orElse(null);
            if (learningService.allCourseSectionPassed(courseFeedbackPersonAnswer.getCourse() != null
                    ? courseFeedbackPersonAnswer.getCourse().getSections()
                    : null, enrollment)
                    && learningService.haveAFeedbackQuestion(courseFeedbackPersonAnswer.getCourse().getFeedbackTemplates(),
                    courseFeedbackPersonAnswer.getPersonGroup())) {
                boolean homework = true;
                List<Homework> homeworkList = getHomeworkForCourse(courseFeedbackPersonAnswer.getCourse());
                if (!homeworkList.isEmpty()) {
                    homework = learningService.allHomeworkPassed(homeworkList, courseFeedbackPersonAnswer.getPersonGroup());
                }
                if (enrollment != null && homework) {
                    enrollment.setStatus(EnrollmentStatus.COMPLETED);
                    transactionalDataManager.save(enrollment);
                    sendNotifyToTrainers(enrollment);
                    sendNotifyForLineManager(enrollment);
                }
            }
        } else if (event.getType().equals(EntityChangedEvent.Type.UPDATED)) {
            courseFeedbackPersonAnswer = dataManager.load(entityId).view("courseFeedbackPersonAnswer.edit").one();
            Enrollment enrollment = transactionalDataManager.load(Enrollment.class)
                    .query("select e from tsadv$Enrollment e " +
                            " where e.personGroup = :personGroup " +
                            " and e.course = :course")
                    .parameter("personGroup", courseFeedbackPersonAnswer.getPersonGroup())
                    .parameter("course", courseFeedbackPersonAnswer.getCourse())
                    .view("enrollment-view")
                    .list().stream().findFirst().orElse(null);
            if (learningService.allCourseSectionPassed(courseFeedbackPersonAnswer.getCourse() != null
                    ? courseFeedbackPersonAnswer.getCourse().getSections()
                    : null, enrollment)
                    && learningService.haveAFeedbackQuestion(courseFeedbackPersonAnswer.getCourse().getFeedbackTemplates(),
                    courseFeedbackPersonAnswer.getPersonGroup())) {
                boolean homework = true;
                List<Homework> homeworkList = getHomeworkForCourse(courseFeedbackPersonAnswer.getCourse());
                if (!homeworkList.isEmpty()) {
                    homework = learningService.allHomeworkPassed(homeworkList, courseFeedbackPersonAnswer.getPersonGroup());
                }
                if (enrollment != null && homework) {
                    enrollment.setStatus(EnrollmentStatus.COMPLETED);
                    transactionalDataManager.save(enrollment);
                    sendNotifyToTrainers(enrollment);
                    sendNotifyForLineManager(enrollment);
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

    protected void sendNotifyToTrainers(Enrollment enrollment) {
        List<CourseTrainer> courseTrainerList = enrollment.getCourse() != null
                ? enrollment.getCourse().getCourseTrainers() : null;
        if (courseTrainerList != null && !courseTrainerList.isEmpty()) {
            courseTrainerList.forEach(courseTrainer -> {
                TsadvUser tsadvUserTrainer = dataManager.load(TsadvUser.class)
                        .query("select e from tsadv$UserExt e " +
                                " where e.personGroup = :personGroup ")
                        .parameter("personGroup", courseTrainer.getTrainer() != null
                                ? courseTrainer.getTrainer().getEmployee() : null)
                        .view("userExt.edit")
                        .list().stream().findFirst().orElse(null);
                if (tsadvUserTrainer != null) {
                    Map<String, Object> params = new HashMap<>();
                    params.put("trainerFioRu", courseTrainer.getTrainer().getEmployee() != null
                            ? courseTrainer.getTrainer().getEmployee().getFullName() : "");
                    params.put("trainerFioEn", courseTrainer.getTrainer().getEmployee() != null
                            ? courseTrainer.getTrainer().getEmployee().getPersonFirstLastNameLatin()
                            : "");
                    params.put("studentFioRu", enrollment.getPersonGroup() != null
                            ? enrollment.getPersonGroup().getFullName() : "");
                    params.put("studentFioEn", enrollment.getPersonGroup() != null
                            ? enrollment.getPersonGroup().getPersonFirstLastNameLatin()
                            : "");
                    params.put("course", enrollment.getCourse().getName());

                    notificationSenderAPIService.sendParametrizedNotification("tdc.student.completed.study",
                            tsadvUserTrainer, params);
                }
            });
        }
    }

    protected void sendNotifyForLineManager(Enrollment enrollment) {
        organizationHrUserService.getHrUsersForPerson(enrollment.getPersonGroup().getId(), OrganizationHrUserService.HR_ROLE_MANAGER)
                .stream()
                .map(user -> (TsadvUser) user)
                .forEach(tsadvUser -> {
                    tsadvUser = dataManager.reload(tsadvUser, "tsadvUserExt-view");
                    Map<String, Object> params = new HashMap<>();
                    params.put("personFioRu", tsadvUser.getPersonGroup() != null
                            ? tsadvUser.getPersonGroup().getFullName() : "");
                    params.put("personFioEn", tsadvUser.getPersonGroup() != null
                            ? tsadvUser.getPersonGroup().getPersonFirstLastNameLatin() : "");
                    params.put("employeeFioRu", enrollment.getPersonGroup().getFullName());
                    params.put("employeeFioEn", enrollment.getPersonGroup().getPersonFirstLastNameLatin());
                    params.put("course", enrollment.getCourse().getName());
                    notificationSenderAPIService.sendParametrizedNotification("tdc.employee.completed.study",
                            tsadvUser, params);
                });
    }
}