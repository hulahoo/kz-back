package kz.uco.tsadv.service;


import kz.uco.tsadv.modules.administration.TsadvUser;
import kz.uco.tsadv.modules.learning.dictionary.DicCategory;
import kz.uco.tsadv.modules.learning.model.*;
import kz.uco.tsadv.pojo.CoursePojo;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface CourseService {
    String NAME = "tsadv_CourseService";

    void updateCourseSectionAttempt(CourseSectionAttempt courseSectionAttempt);

    void addCourseReview(CourseReview courseReview);

    void addLearningPathReview(LearningPathReview learningPathReview);

    List getCategoryHierarchy(String categoryId);

    int addFavorite(UUID learningPathId, UUID personGroupId);

    int deleteFavorite(UUID learningPathId, UUID personGroupId);

    void deleteAllAttempt(Enrollment enrollment);

    void sendParametrizedNotification(String notificationCode, TsadvUser user, Map<String, Object> params);

    void updateEnrollmentStatus(CourseSectionAttempt courseSectionAttempt);

    void insertPersonAnswers(List<PersonAnswer> personAnswers);

    float completedSectionsPercent(UUID enrollmentId);

    List<AssignedTestPojo> loadAssignedTest(int firstResult, int maxResults, boolean forRowCount, int paramsForOrderBy);

    List<AssignedTestPojo> loadAssignedTest(int firstResult, int maxResults, boolean forRowCount, int paramsForOrderBy, List<Map<String, String>> filter, String lang);

    List<AssignedTestPojo> loadAssignedTest(int firstResult, int maxResults, boolean forRowCount);

    List<AssignedTestPojo> loadAssignedTest(int firstResult, int maxResults, boolean forRowCount, int paramsForOrderBy, Map<String, Object> param);

    List<AssignedTestPojo> loadAssignedTest(int firstResult, int maxResults, boolean forRowCount, int paramsForOrderBy, List<Map<String, String>> filter, String lang, Map<String, Object> param);

    List<AssignedTestPojo> loadAssignedTest(int firstResult, int maxResults, boolean forRowCount, Map<String, Object> param);

    void addEnrollment(AssignedTestPojo assignedTestPojo);

    void removeEnrollment(UUID enrollmentId);

    CoursePojo courseInfo(UUID courseId, UUID personGroupId);

    Map<String, Object> courseTrainerInfo(UUID trainerId);

    List<DicCategory> searchCourses(String courseName);
}