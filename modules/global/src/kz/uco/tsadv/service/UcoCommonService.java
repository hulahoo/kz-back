package kz.uco.tsadv.service;


import com.haulmont.cuba.core.entity.FileDescriptor;
import kz.uco.tsadv.modules.personal.dictionary.DicOperatorCode;

import java.util.List;
import java.util.UUID;

public interface UcoCommonService {
    String NAME = "tsadv_UcoCommonService";

    List<DicOperatorCode> fillPhoneCodes();

    FileDescriptor getFileDescriptor(UUID fileDescriptorId);
}