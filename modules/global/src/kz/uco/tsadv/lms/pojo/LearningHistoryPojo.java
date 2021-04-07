package kz.uco.tsadv.lms.pojo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.stream.Collectors;

public class LearningHistoryPojo implements Serializable {
    private Date startDate;
    private Date endDate;
    private String course;
    private String trainer;
    private BigDecimal result;
    private String certificate;
    private String enrollmentStatus;

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getTrainer() {
        return trainer;
    }

    public void setTrainer(String trainer) {
        this.trainer = trainer;
    }

    public BigDecimal getResult() {
        return result;
    }

    public void setResult(BigDecimal result) {
        this.result = result;
    }

    public String getCertificate() {
        return certificate;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    public String getEnrollmentStatus() {
        return enrollmentStatus;
    }

    public void setEnrollmentStatus(String enrollmentStatus) {
        this.enrollmentStatus = enrollmentStatus;
    }

    public static final class Builder {
        private LearningHistoryPojo learningHistoryPojo;

        public Builder() {
            learningHistoryPojo = new LearningHistoryPojo();
        }

        public static Builder aLearningHistoryPojo() {
            return new Builder();
        }

        public Builder startDate(Date startDate) {
            learningHistoryPojo.setStartDate(startDate);
            return this;
        }

        public Builder endDate(Date endDate) {
            learningHistoryPojo.setEndDate(endDate);
            return this;
        }

        public Builder course(String course) {
            learningHistoryPojo.setCourse(course);
            return this;
        }

        public Builder trainer(String trainer) {
            learningHistoryPojo.setTrainer(trainer);
            return this;
        }

        public Builder result(BigDecimal result) {
            learningHistoryPojo.setResult(result);
            return this;
        }

        public Builder certificate(String certificate) {
            learningHistoryPojo.setCertificate(certificate);
            return this;
        }

        public Builder enrollmentStatus(String enrollmentStatus) {
            learningHistoryPojo.setEnrollmentStatus(enrollmentStatus);
            return this;
        }

        public LearningHistoryPojo build() {
            return learningHistoryPojo;
        }
    }
}
