package kz.uco.tsadv.modules.learning.model;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import kz.uco.tsadv.modules.learning.enums.EnrollmentStatus;

import javax.validation.constraints.NotNull;
import java.util.Date;

@MetaClass(name = "tsadv$TemplateEnrollment")
public class TemplateEnrollment extends BaseUuidEntity {
    private static final long serialVersionUID = -6184153756895592040L;

    @NotNull
    @MetaProperty(mandatory = true)
    protected Integer status;

    @NotNull
    @MetaProperty(mandatory = true)
    protected Date date;

    @MetaProperty
    protected String reason;

    public void setStatus(EnrollmentStatus status) {
        this.status = status == null ? null : status.getId();
    }


    public void setDate(Date date) {
        this.date = date;
    }


    public Date getDate() {
        return date;
    }


    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }


    public EnrollmentStatus getStatus() {
        return status == null ? null : EnrollmentStatus.fromId(status);
    }


}