package kz.uco.tsadv.modules.personal.model;

import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.OnDeleteInverse;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DeletePolicy;
import com.haulmont.cuba.core.global.Messages;
import kz.uco.tsadv.entity.bproc.AbstractBprocRequest;
import kz.uco.tsadv.modules.personal.dictionary.DicAbsenceType;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.personal.dictionary.DicRequestStatus;
import kz.uco.tsadv.modules.personal.group.AssignmentGroupExt;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.haulmont.cuba.core.entity.FileDescriptor;
import javax.validation.constraints.NotNull;

@NamePattern("%s (%s)|id,requestDate")
@Table(name = "TSADV_ABSENCE_REQUEST")
@Entity(name = "tsadv$AbsenceRequest")
public class AbsenceRequest extends AbstractBprocRequest {
    private static final long serialVersionUID = 5087051995273747332L;

    @OnDeleteInverse(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ASSIGNMENT_GROUP_ID")
    protected AssignmentGroupExt assignmentGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ATTACHMENT_ID")
    protected FileDescriptor attachment;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATE_FROM")
    protected Date dateFrom;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATE_TO")
    protected Date dateTo;

    @Column(name = "ABSENCE_DAYS")
    protected Integer absenceDays;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TYPE_ID")
    protected DicAbsenceType type;

    @Column(name = "COMMENT_", length = 3000)
    protected String comment;

    @NotNull
    @Column(name = "DISTANCE_WORKING_CONFIRM", nullable = false)
    protected Boolean distanceWorkingConfirm = false;

    public void setDistanceWorkingConfirm(Boolean distanceWorkingConfirm) {
        this.distanceWorkingConfirm = distanceWorkingConfirm;
    }

    public Boolean getDistanceWorkingConfirm() {
        return distanceWorkingConfirm;
    }


    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }

    public Date getDateFrom() {
        return dateFrom;
    }

    public void setDateTo(Date dateTo) {
        this.dateTo = dateTo;
    }

    public Date getDateTo() {
        return dateTo;
    }

    public void setAbsenceDays(Integer absenceDays) {
        this.absenceDays = absenceDays;
    }

    public Integer getAbsenceDays() {
        return absenceDays;
    }

    public void setType(DicAbsenceType type) {
        this.type = type;
    }

    public DicAbsenceType getType() {
        return type;
    }

    public void setAssignmentGroup(AssignmentGroupExt assignmentGroup) {
        this.assignmentGroup = assignmentGroup;
    }

    public AssignmentGroupExt getAssignmentGroup() {
        return assignmentGroup;
    }

    public void setAttachment(FileDescriptor attachment) {
        this.attachment = attachment;
    }

    public FileDescriptor getAttachment() {
        return attachment;
    }
}