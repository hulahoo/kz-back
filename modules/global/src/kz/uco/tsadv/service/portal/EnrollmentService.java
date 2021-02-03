package kz.uco.tsadv.service.portal;

import kz.uco.tsadv.modules.learning.dictionary.DicCategory;

import java.util.List;
import java.util.UUID;

public interface EnrollmentService {
    String NAME = "tsadv_EnrollmentService";

    List<DicCategory> searchEnrollment(UUID userId);

    List<DicCategory> searchEnrollment(String courseName, UUID userId);
}