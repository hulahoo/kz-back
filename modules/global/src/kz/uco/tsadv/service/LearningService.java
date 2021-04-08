package kz.uco.tsadv.service;

import com.haulmont.cuba.core.entity.FileDescriptor;
import kz.uco.tsadv.lms.pojo.LearningHistoryPojo;
import kz.uco.tsadv.modules.learning.model.Course;
import kz.uco.tsadv.modules.learning.model.Enrollment;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public interface LearningService {
    String NAME = "tsadv_LearningService";


    void importQuestionBank(FileDescriptor fileDescriptor) throws Exception;

    void importTests(FileDescriptor fileDescriptor) throws Exception;

    String unzipPackage(String packageName, FileDescriptor fileDescriptor);

    boolean deletePackage(String dirName);
}