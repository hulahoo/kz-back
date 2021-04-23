package kz.uco.tsadv.listeners;

import com.haulmont.cuba.core.TransactionalDataManager;
import com.haulmont.cuba.core.app.events.AttributeChanges;
import com.haulmont.cuba.core.app.events.EntityChangedEvent;
import com.haulmont.cuba.core.entity.contracts.Id;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.GlobalConfig;
import kz.uco.base.common.BaseCommonUtils;
import kz.uco.base.service.NotificationSenderAPIService;
import kz.uco.tsadv.modules.administration.TsadvUser;
import kz.uco.tsadv.modules.learning.enums.EnrollmentStatus;
import kz.uco.tsadv.modules.learning.model.Course;
import kz.uco.tsadv.modules.learning.model.Enrollment;
import kz.uco.tsadv.modules.learning.model.Homework;
import kz.uco.tsadv.modules.learning.model.StudentHomework;
import kz.uco.tsadv.modules.learning.model.feedback.CourseFeedbackTemplate;
import kz.uco.tsadv.modules.performance.model.CourseTrainer;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.service.LearningService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import javax.inject.Inject;
import java.util.*;

@Component("tsadv_StudentHomeworkChangedListener")
public class StudentHomeworkChangedListener {

    @Inject
    protected DataManager dataManager;

    @Inject
    protected NotificationSenderAPIService notificationSenderAPIService;
    @Inject
    protected GlobalConfig globalConfig;
    @Inject
    protected LearningService learningService;
    @Inject
    protected TransactionalDataManager transactionalDataManager;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void onTsadv_StudentHomeworkBeforeCommit(EntityChangedEvent<StudentHomework, UUID> event) {
        Id<StudentHomework, UUID> entityId = event.getEntityId();
        StudentHomework studentHomework;
        if (event.getType().equals(EntityChangedEvent.Type.CREATED)) {
            studentHomework = transactionalDataManager.load(entityId).view("studentHomework.edit").one();
            Enrollment enrollment = transactionalDataManager.load(Enrollment.class)
                    .query("select e from tsadv$Enrollment e " +
                            " where e.personGroup = :personGroup " +
                            " and e.course = :course")
                    .parameter("personGroup", studentHomework.getPersonGroup())
                    .parameter("course", studentHomework.getHomework().getCourse())
                    .view("enrollment-view")
                    .list().stream().findFirst().orElse(null);
            if (learningService.allCourseSectionPassed(studentHomework.getHomework().getCourse() != null
                    ? studentHomework.getHomework().getCourse().getSections()
                    : null, enrollment)
                    && learningService.allHomeworkPassed(getHomeworkForCourse(studentHomework.getHomework().getCourse()),
                    studentHomework.getPersonGroup())) {
                boolean feedbackQuestion = true;
                List<CourseFeedbackTemplate> courseFeedbackTemplateList = studentHomework.getHomework().getCourse().getFeedbackTemplates();
                if (!courseFeedbackTemplateList.isEmpty()) {
                    feedbackQuestion = learningService.haveAFeedbackQuestion(courseFeedbackTemplateList, studentHomework.getPersonGroup());
                }
                if (enrollment != null && feedbackQuestion) {
                    enrollment.setStatus(EnrollmentStatus.COMPLETED);
                    transactionalDataManager.save(enrollment);
                }
            }
        } else if (event.getType().equals(EntityChangedEvent.Type.UPDATED)) {
            studentHomework = transactionalDataManager.load(entityId).view("studentHomework.edit").one();
            Enrollment enrollment = transactionalDataManager.load(Enrollment.class)
                    .query("select e from tsadv$Enrollment e " +
                            " where e.personGroup = :personGroup " +
                            " and e.course = :course")
                    .parameter("personGroup", studentHomework.getPersonGroup())
                    .parameter("course", studentHomework.getHomework().getCourse())
                    .view("enrollment-view")
                    .list().stream().findFirst().orElse(null);
            if (learningService.allCourseSectionPassed(studentHomework.getHomework().getCourse() != null
                    ? studentHomework.getHomework().getCourse().getSections()
                    : null, enrollment)
                    && learningService.allHomeworkPassed(getHomeworkForCourse(studentHomework.getHomework().getCourse()),
                    studentHomework.getPersonGroup())) {
                boolean feedbackQuestion = true;
                List<CourseFeedbackTemplate> courseFeedbackTemplateList = studentHomework.getHomework().getCourse().getFeedbackTemplates();
                if (!courseFeedbackTemplateList.isEmpty()) {
                    feedbackQuestion = learningService.haveAFeedbackQuestion(courseFeedbackTemplateList, studentHomework.getPersonGroup());
                }

                if (enrollment != null && feedbackQuestion) {
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


    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void afterCommit(EntityChangedEvent<StudentHomework, UUID> event) {
        AttributeChanges changes = event.getChanges();
        Id<StudentHomework, UUID> entityId = event.getEntityId();
        StudentHomework studentHomework = dataManager.load(entityId).view("studentHomework.edit").one();
        if (event.getType().equals(EntityChangedEvent.Type.CREATED)) {
            sendNotification(studentHomework, "tdc.homework.trainer.newHomework", true);
        } else if (event.getType().equals(EntityChangedEvent.Type.UPDATED)) {

            for (String attribute : changes.getAttributes()) {
                if (attribute.equals("answer") || attribute.equals("answerFile")) {
                    sendNotification(studentHomework, "tdc.homework.trainer.newHomework", true);
                } else if (attribute.equals("isDone") || attribute.equals("trainerComment")) {
                    sendNotification(studentHomework, "tdc.homework.student.trainerAnswer", false);
                }
            }
        }

    }

    protected void sendNotification(StudentHomework studentHomework, String notificationCode, Boolean isStudentAnswer) {
        if (isStudentAnswer) {
            List<CourseTrainer> courseTrainers = dataManager.load(CourseTrainer.class)
                    .query("select e from tsadv$CourseTrainer e " +
                            " where e.course = :course " +
                            " and e.trainer.employee is not null " +
                            " and current_date between coalesce(e.dateFrom, :startDate) and coalesce(e.dateTo, :endDate) "
                    )
                    .parameter("course", studentHomework.getHomework().getCourse())
                    .parameter("startDate", new Date(20000101))
                    .parameter("endDate", BaseCommonUtils.getEndOfTime())
                    .view("courseTrainer.edit")
                    .list();

            if (!courseTrainers.isEmpty()) {
                courseTrainers.forEach(courseTrainer -> {
                    TsadvUser tsadvUser = getTsadvUser(courseTrainer.getTrainer().getEmployee());
                    if (tsadvUser != null) {
                        Map<String, Object> map = new HashMap<>();
                        String requestLink = "<a href=\"" + globalConfig.getWebAppUrl() + "/open?screen=" +
                                "tsadv_StudentHomework.edit" +
                                "&item=" + "tsadv_StudentHomework" + "-" + studentHomework.getId() +
                                "\" target=\"_blank\">%s " + "</a>";
                        map.put("courseName", studentHomework.getHomework().getCourse().getName());
                        map.put("studentFullName", studentHomework.getPersonGroup().getFullName());
                        map.put("requestLink", String.format(requestLink, "ссылке"));
                        notificationSenderAPIService.sendParametrizedNotification(notificationCode,
                                tsadvUser, map);
                    }
                });
            }
        } else {
            TsadvUser tsadvUser = getTsadvUser(studentHomework.getPersonGroup());

            if (tsadvUser != null) {
                Map<String, Object> map = new HashMap<>();
                map.put("courseName", studentHomework.getHomework().getCourse().getName());
                map.put("studentFullName", studentHomework.getPersonGroup().getFullName());
                map.put("trainerFullName", studentHomework.getTrainer() != null
                        ? studentHomework.getTrainer().getFullName()
                        : "");
                map.put("isDone", studentHomework.getIsDone() ? "Сдан" : "Не сдан");
                map.put("trainerComment", studentHomework.getTrainerComment());

                notificationSenderAPIService.sendParametrizedNotification(notificationCode,
                        tsadvUser, map);
            }
        }
    }

    private TsadvUser getTsadvUser(PersonGroupExt personGroupExt) {
        return dataManager.load(TsadvUser.class)
                .query("select e from tsadv$UserExt e " +
                        " where e.personGroup = :personGroup")
                .parameter("personGroup", personGroupExt)
                .list().stream().findFirst().orElse(null);
    }
}