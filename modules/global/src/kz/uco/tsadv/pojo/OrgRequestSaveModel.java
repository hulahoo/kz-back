package kz.uco.tsadv.pojo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class OrgRequestSaveModel implements Serializable {

    public static class FileModel implements Serializable {
        private UUID id;

        public UUID getId() {
            return id;
        }

        public void setId(UUID id) {
            this.id = id;
        }
    }

    private UUID id;
    private UUID company;
    private UUID department;
    private UUID author;
    private Date modifyDate;
    private Date requestDate;
    private UUID status;
    private String comment;

    private List<FileModel> file;

    public List<FileModel> getFile() {
        return file;
    }

    public void setFile(List<FileModel> file) {
        this.file = file;
    }

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

    public UUID getStatus() {
        return status;
    }

    public void setStatus(UUID status) {
        this.status = status;
    }
}
