package kz.uco.tsadv.service;

import com.haulmont.cuba.core.entity.FileDescriptor;

public interface LearningService {
    String NAME = "tsadv_LearningService";


    void importQuestionBank(FileDescriptor fileDescriptor) throws Exception;

    void importTests(FileDescriptor fileDescriptor) throws Exception;

    String unzipPackage(String packageName,FileDescriptor fileDescriptor);

    boolean deletePackage(String dirName);

}