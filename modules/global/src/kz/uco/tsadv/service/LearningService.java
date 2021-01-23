package kz.uco.tsadv.service;

import com.haulmont.cuba.core.entity.FileDescriptor;
import kz.uco.tsadv.modules.learning.model.Course;

import java.util.List;
import java.util.UUID;

public interface LearningService {
    String NAME = "tsadv_LearningService";


    void importQuestionBank(FileDescriptor fileDescriptor) throws Exception;

    void importTests(FileDescriptor fileDescriptor) throws Exception;

    String unzipPackage(String packageName,FileDescriptor fileDescriptor);

    boolean deletePackage(String dirName);

    List<Course> learningHistory(UUID personGroupId);
}