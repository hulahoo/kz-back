package kz.uco.tsadv.pojo;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

public class OrgRequestSaveModel implements Serializable {

    private UUID id;
    private UUID company;
    private UUID department;
    private UUID author;
    private Date modifyDate;
    private Date requestDate;
    private UUID requestStatus;
    private String comment;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getCompany() {
        return company;
    }

    public void setCompany(UUID company) {
        this.company = company;
    }

    public UUID getDepartment() {
        return department;
    }

    public void setDepartment(UUID department) {
        this.department = department;
    }

    public UUID getAuthor() {
        return author;
    }

    public void setAuthor(UUID author) {
        this.author = author;
    }

    public Date getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(Date modifyDate) {
        this.modifyDate = modifyDate;
    }

    public Date getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }

    public UUID getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(UUID requestStatus) {
        this.requestStatus = requestStatus;
    }
}
