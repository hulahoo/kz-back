package kz.uco.tsadv.listener;

import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.TransactionalDataManager;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.listener.*;
import kz.uco.base.service.common.CommonService;
import kz.uco.tsadv.beans.TestHelper;
import kz.uco.tsadv.modules.learning.enums.EnrollmentStatus;
import kz.uco.tsadv.modules.learning.model.*;
import kz.uco.tsadv.modules.learning.model.feedback.CourseFeedbackTemplate;
import kz.uco.tsadv.service.LearningService;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.sql.Connection;
import java.util.List;
import java.util.UUID;

@Component("tsadv_CourseSectionAttemptListener")
public class CourseSectionAttemptListener implements BeforeDeleteEntityListener<CourseSectionAttempt>, BeforeDetachEntityListener<CourseSectionAttempt>, BeforeInsertEntityListener<CourseSectionAttempt>, BeforeUpdateEntityListener<CourseSectionAttempt>, AfterDeleteEntityListener<CourseSectionAttempt>, AfterInsertEntityListener<CourseSectionAttempt>, AfterUpdateEntityListener<CourseSectionAttempt>, BeforeAttachEntityListener<CourseSectionAttempt> {

    @Inject
    protected DataManager dataManager;

    @Inject
    protected Metadata metadata;

    @Inject
    protected CommonService commonService;
    @Inject
    protected LearningService learningService;
    @Inject
    protected TransactionalDataManager transactionalDataManager;

    @Override
    public void onBeforeDelete(CourseSectionAttempt courseSectionAttempt, EntityManager entityManager) {

    }


    @Override
    public void onBeforeDetach(CourseSectionAttempt courseSectionAttempt, EntityManager entityManager) {

    }


    @Override
    public void onBeforeInsert(CourseSectionAttempt courseSectionAttempt, EntityManager entityManager) {
        if (courseSectionAttempt.getTest() != null) {
            courseSectionAttempt.setTestResultPercent(testHelper.calculateTestResultPercent(courseSectionAttempt));
        }
        CourseSection courseSection = entityManager.find(CourseSection.class, courseSectionAttempt.getCourseSection().getId(), "courseSection.course.sections");
        if (courseSection != null && courseSection.getCourse() != null
                && courseSection.getCourse().getSections() != null) {
            UUID enrollmentId = courseSectionAttempt.getEnrollment().getId();
            Enrollment enrollment = entityManager.find(Enrollment.class, enrollmentId, "enrollment.person");
            if (enrollment != null && learningService.allCourseSectionPassed(courseSection.getCourse().getSections())) {
                boolean homework = true;
                boolean feedbackQuestion = true;
                List<CourseFeedbackTemplate> courseFeedbackTemplateList = courseSection.getCourse().getFeedbackTemplates();
                if (!courseFeedbackTemplateList.isEmpty()) {
                    feedbackQuestion = learningService.haveAFeedbackQuestion(courseFeedbackTemplateList, enrollment.getPersonGroup());
                }
                List<Homework> homeworkList = getHomeworkForCourse(courseSection.getCourse());
                if (!homeworkList.isEmpty()) {
                    homework = learningService.allHomeworkPassed(homeworkList, enrollment.getPersonGroup());
                }
                if (homework && feedbackQuestion) {
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


    @Override
    public void onBeforeUpdate(CourseSectionAttempt courseSectionAttempt, EntityManager entityManager) {
        if (courseSectionAttempt.getTest() != null &&
                persistence.getTools().isDirty(courseSectionAttempt, "testResult")
                && courseSectionAttempt.getTestResultPercent() == null
        ) {
            courseSectionAttempt.setTestResultPercent(testHelper.calculateTestResultPercent(courseSectionAttempt));
        }
        CourseSection courseSection = entityManager.find(CourseSection.class, courseSectionAttempt.getCourseSection().getId(), "courseSection.course.sections");
        if (courseSection != null && courseSection.getCourse() != null
                && courseSection.getCourse().getSections() != null) {
            Enrollment enrollment = entityManager.find(Enrollment.class, courseSectionAttempt.getEnrollment().getId(), "enrollment.person");
            if (learningService.allCourseSectionPassed(courseSection.getCourse().getSections())
                    && learningService.allHomeworkPassed(getHomeworkForCourse(courseSection.getCourse()),
                    enrollment != null
                            ? enrollment.getPersonGroup()
                            : null)) {
                enrollment.setStatus(EnrollmentStatus.COMPLETED);
                transactionalDataManager.save(enrollment);
            }
        }
    }


    @Override
    public void onAfterDelete(CourseSectionAttempt courseSectionAttempt, Connection connection) {

    }


    @Override
    public void onAfterInsert(CourseSectionAttempt courseSectionAttempt, Connection connection) {

    }


    @Override
    public void onAfterUpdate(CourseSectionAttempt courseSectionAttempt, Connection connection) {

    }


    @Override
    public void onBeforeAttach(CourseSectionAttempt courseSectionAttempt) {

    }

    @Inject
    protected Persistence persistence;
    @Inject
    protected TestHelper testHelper;

}