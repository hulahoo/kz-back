package kz.uco.tsadv.api;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;
import kz.uco.tsadv.api.AbstractEntityInt;
import java.util.UUID;

@NamePattern("%s|id")
@MetaClass(name = "tsadv$OfferInt")
public class OfferInt extends AbstractEntityInt {
    private static final long serialVersionUID = -6315866846540493813L;

    @MetaProperty
    protected String requisition;

    @MetaProperty
    protected String requisitionCode;

    @MetaProperty
    protected String jobName;

    @MetaProperty
    protected String proposedSalary;

    @MetaProperty
    protected String expireDate;

    @MetaProperty
    protected String file;

    @MetaProperty
    protected String fileName;

    @MetaProperty
    protected String proposedStartDate;

    @MetaProperty
    protected String status;

    @MetaProperty
    protected String comment;

    @MetaProperty
    protected UUID offerId;

    public UUID getOfferId() {
        return offerId;
    }

    public void setOfferId(UUID offerId) {
        this.offerId = offerId;
    }



    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }


    public String getRequisition() {
        return requisition;
    }

    public void setRequisition(String requisition) {
        this.requisition = requisition;
    }

    public String getRequisitionCode() {
        return requisitionCode;
    }

    public void setRequisitionCode(String requisitionCode) {
        this.requisitionCode = requisitionCode;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getProposedSalary() {
        return proposedSalary;
    }

    public void setProposedSalary(String proposedSalary) {
        this.proposedSalary = proposedSalary;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getProposedStartDate() {
        return proposedStartDate;
    }

    public void setProposedStartDate(String proposedStartDate) {
        this.proposedStartDate = proposedStartDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
