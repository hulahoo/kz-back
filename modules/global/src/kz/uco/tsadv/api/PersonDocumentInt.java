package kz.uco.tsadv.api;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import java.util.Date;

@MetaClass(name = "tsadv$PersonDocumentInt")
public class PersonDocumentInt extends AbstractEntityInt {
    private static final long serialVersionUID = -6279565645543074220L;

    @MetaProperty
    protected String issueDate;

    @MetaProperty
    protected String expiredDate;

    @MetaProperty
    protected String issuedBy;

    @MetaProperty
    protected String description;

    @MetaProperty
    protected String documentType;

    @MetaProperty
    protected String personLegacyId;

    @MetaProperty
    protected String documentNumber;

    @MetaProperty
    protected String status;

    @MetaProperty
    protected String file;

    public String getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(String issueDate) {
        this.issueDate = issueDate;
    }


    public String getExpiredDate() {
        return expiredDate;
    }

    public void setExpiredDate(String expiredDate) {
        this.expiredDate = expiredDate;
    }


    public void setIssuedBy(String issuedBy) {
        this.issuedBy = issuedBy;
    }

    public String getIssuedBy() {
        return issuedBy;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setPersonLegacyId(String personLegacyId) {
        this.personLegacyId = personLegacyId;
    }

    public String getPersonLegacyId() {
        return personLegacyId;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getFile() {
        return file;
    }



}