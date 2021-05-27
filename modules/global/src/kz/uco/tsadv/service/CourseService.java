package kz.uco.tsadv.service;


import kz.uco.tsadv.modules.administration.TsadvUser;
import kz.uco.tsadv.modules.learning.model.*;
import kz.uco.tsadv.pojo.CategoryCoursePojo;
import kz.uco.tsadv.pojo.CoursePojo;
import kz.uco.tsadv.pojo.PairPojo;
import kz.uco.tsadv.pojo.ScormInputData;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

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

    List<CategoryCoursePojo> searchCourses(UUID personGroupId, String courseName);

    List<CategoryCoursePojo> allCourses(UUID personGroupId);

    Enrollment courseEnrollmentInfo(UUID enrollmentId);

    CourseSection courseSectionWithEnrollmentAttempts(UUID courseSectionId, UUID enrollmentId);

    void createScormAttempt(UUID courseSectionId, UUID enrollmentId, List<ScormInputData> inputData, Boolean success);

    CourseSectionAttempt createTestScormAttempt(@NotNull UUID courseSectionId, @NotNull UUID enrollmentId, @NotNull BigDecimal score, @NotNull BigDecimal minScore, @NotNull BigDecimal maxScore);

    PairPojo<Boolean, String> validateEnroll(UUID courseId, UUID personGroupId, String locale);

    List<CategoryCoursePojo> mapCoursesToCategory(Stream<Course> courseStream, UUID personGroupID);
}