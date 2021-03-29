package kz.uco.tsadv.service;


import kz.uco.base.entity.core.notification.SendingNotification;
import kz.uco.tsadv.lms.pojo.*;
import kz.uco.tsadv.modules.learning.model.Course;
import kz.uco.tsadv.modules.learning.model.CourseSection;
import kz.uco.tsadv.modules.learning.model.LearningObject;
import kz.uco.tsadv.modules.learning.model.feedback.LearningFeedbackQuestion;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface LmsService {
    String NAME = "tsadv_LmsService";

    List<EnrollmentPojo> getPersonCourses();

    List<EnrollmentPojo> getPersonHistory();

    Boolean hasEnrollment(UUID courseId);

    CoursePojo loadCourseData(UUID courseId);

    void registerToCourse(UUID courseId);

    List<Course> loadCourses();

    List<Course> loadCourses(List<ConditionPojo> conditions);

    List<LearningObject> loadLearningObject(String contentType);

    TestPojo startAndLoadTest(UUID courseSectionObjectId, UUID enrollmentId);

    TestPojo startAndLoadTest(UUID testId);

    List<EnrollmentPojo> loadPersonTests();

    TestScorePojo finishTest(AnsweredTest answeredTest);

    List<ProgressCoursePojo> loadPersonProgress() throws SQLException;

    String getCertificate(String enrollmentId);

    void removeCertificate(String certificateFileId);

    Map<Date, List<CalendarMonthPojo>> personMonthEvents(Date date) throws SQLException;

    List<NotificationPojo> getPersonNotifications();

    SendingNotification getNotification(UUID notificationId);

    ResponsePojo changePassword(String oldPassword, String newPassword);

    ResponsePojo restorePassword(String userLogin);

    List<CourseTrainerPojo> getCourseTrainers(UUID courseId) throws SQLException;

    List<LearningFeedbackQuestion> loadFeedbackData(UUID feedbackTemplateId);

    void finishFeedback(AnsweredFeedback answeredFeedback);

    CourseSection loadCourseSectionData(UUID enrollmentId, UUID courseSectionId);

    List<BookPojo> loadBooks();
}