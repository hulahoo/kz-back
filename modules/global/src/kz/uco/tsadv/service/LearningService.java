package kz.uco.tsadv.service;

import com.haulmont.cuba.core.entity.FileDescriptor;
import kz.uco.tsadv.lms.pojo.LearningHistoryPojo;
import kz.uco.tsadv.modules.learning.model.Course;
import kz.uco.tsadv.modules.learning.model.CourseSection;
import kz.uco.tsadv.modules.learning.model.Enrollment;
import kz.uco.tsadv.modules.learning.model.Homework;
import kz.uco.tsadv.modules.learning.model.feedback.CourseFeedbackTemplate;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public interface LearningService {
    String NAME = "tsadv_LearningService";


    void importQuestionBank(FileDescriptor fileDescriptor) throws Exception;

    void importTests(FileDescriptor fileDescriptor) throws Exception;

    String unzipPackage(String packageName, FileDescriptor fileDescriptor);

    boolean deletePackage(String dirName);

    boolean allCourseSectionPassed(List<CourseSection> courseSectionList);

    boolean allHomeworkPassed(List<Homework> homeworkList, PersonGroupExt personGroup);

    boolean haveAFeedbackQuestion(List<CourseFeedbackTemplate> feedbackTemplateList, PersonGroupExt personGroup);
}