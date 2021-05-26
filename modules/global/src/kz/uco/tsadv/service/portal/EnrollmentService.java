package kz.uco.tsadv.service.portal;

import kz.uco.tsadv.pojo.CategoryCoursePojo;

import java.util.List;
import java.util.UUID;

public interface EnrollmentService {
    String NAME = "tsadv_EnrollmentService";

    List<CategoryCoursePojo> searchEnrollment();

    List<CategoryCoursePojo> searchEnrollment(String courseName);
}