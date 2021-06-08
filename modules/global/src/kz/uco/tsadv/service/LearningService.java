package kz.uco.tsadv.service;

import com.haulmont.cuba.core.entity.FileDescriptor;
import kz.uco.tsadv.modules.learning.model.CourseSection;
import kz.uco.tsadv.modules.learning.model.Enrollment;
import kz.uco.tsadv.modules.learning.model.Homework;
import kz.uco.tsadv.modules.learning.model.feedback.CourseFeedbackTemplate;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import java.util.List;

public interface LearningService {
    String NAME = "tsadv_LearningService";

    void importQuestionBank(FileDescriptor fileDescriptor) throws Exception;

    void importTests(FileDescriptor fileDescriptor) throws Exception;

    String unzipPackage(String packageName, FileDescriptor fileDescriptor);

    boolean deletePackage(String dirName);

    boolean allCourseSectionPassed(List<CourseSection> courseSectionList, Enrollment enrollment);

    boolean allHomeworkPassed(List<Homework> homeworkList, PersonGroupExt personGroup);

    boolean haveAFeedbackQuestion(List<CourseFeedbackTemplate> feedbackTemplateList, PersonGroupExt personGroup);

    void noDataForFeedbackAnswerMore7Days();

    void noAttemptLast7Days();
}