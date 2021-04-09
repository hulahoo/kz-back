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
import kz.uco.tsadv.modules.learning.model.Course;
import kz.uco.tsadv.modules.learning.model.CourseSectionAttempt;
import kz.uco.tsadv.modules.learning.model.Enrollment;
import kz.uco.tsadv.modules.learning.model.Homework;
import kz.uco.tsadv.service.LearningService;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.sql.Connection;
import java.util.List;

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
        if (courseSectionAttempt.getCourseSection() != null && courseSectionAttempt.getCourseSection().getCourse() != null
                && courseSectionAttempt.getCourseSection().getCourse().getSections() != null) {
            if (learningService.allCourseSectionPassed(courseSectionAttempt.getCourseSection().getCourse().getSections())
                    && learningService.allHomeworkPassed(getHomeworkForCourse(courseSectionAttempt.getCourseSection().getCourse()),
                    courseSectionAttempt.getEnrollment() != null
                            ? courseSectionAttempt.getEnrollment().getPersonGroup()
                            : null)) {
                Enrollment enrollment = courseSectionAttempt.getEnrollment();
                enrollment.setStatus(EnrollmentStatus.COMPLETED);
                transactionalDataManager.save(enrollment);
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
        ) {
            courseSectionAttempt.setTestResultPercent(testHelper.calculateTestResultPercent(courseSectionAttempt));
        }
        if (courseSectionAttempt.getCourseSection() != null && courseSectionAttempt.getCourseSection().getCourse() != null
                && courseSectionAttempt.getCourseSection().getCourse().getSections() != null) {
            if (learningService.allCourseSectionPassed(courseSectionAttempt.getCourseSection().getCourse().getSections())
                    && learningService.allHomeworkPassed(getHomeworkForCourse(courseSectionAttempt.getCourseSection().getCourse()),
                    courseSectionAttempt.getEnrollment() != null
                            ? courseSectionAttempt.getEnrollment().getPersonGroup()
                            : null)) {
                Enrollment enrollment = courseSectionAttempt.getEnrollment();
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