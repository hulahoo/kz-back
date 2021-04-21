package kz.uco.tsadv.beans.validation.tdc.enrollment;

import kz.uco.tsadv.modules.learning.model.Enrollment;

import javax.validation.ValidationException;

public interface EnrollmentValidation {
    void validate(Enrollment enrollment) throws ValidationException;
}
