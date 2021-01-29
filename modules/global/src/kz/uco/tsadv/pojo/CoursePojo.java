package kz.uco.tsadv.pojo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CoursePojo implements Serializable {
    private String name;
    private Double avgRate;
    private String preRequisitions;
    private List<PairPojo<UUID, String>> trainers;
    private Date startDate;
    private Date endDate;
    private Integer finished;
    private Boolean isIssuedCertificate;
    private List<PairPojo<UUID, String>> sections;
    private String logo;
    private List<CommentPojo> comments;
    private List<PairPojo<Double, Long>> rating;
    private Integer rateReviewCount;
    private String description;
    private Long educationDuration;
    private Long educationPeriod;

    public List<CommentPojo> getComments() {
        return comments;
    }

    public void setComments(List<CommentPojo> comments) {
        this.comments = comments;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getAvgRate() {
        return avgRate;
    }

    public void setAvgRate(Double avgRate) {
        this.avgRate = avgRate;
    }

    public String getPreRequisitions() {
        return preRequisitions;
    }

    public void setPreRequisitions(String preRequisitions) {
        this.preRequisitions = preRequisitions;
    }

    public List<PairPojo<UUID, String>> getTrainers() {
        return trainers;
    }

    public void setTrainers(List<PairPojo<UUID, String>> trainers) {
        this.trainers = trainers;
    }

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

    public Integer getFinished() {
        return finished;
    }

    public void setFinished(Integer finished) {
        this.finished = finished;
    }

    public List<PairPojo<UUID, String>> getSections() {
        return sections;
    }

    public void setSections(List<PairPojo<UUID, String>> sections) {
        this.sections = sections;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public Boolean getIssuedCertificate() {
        return isIssuedCertificate;
    }

    public void setIssuedCertificate(Boolean issuedCertificate) {
        isIssuedCertificate = issuedCertificate;
    }

    public Integer getRateReviewCount() {
        return rateReviewCount;
    }

    public void setRateReviewCount(Integer rateReviewCount) {
        this.rateReviewCount = rateReviewCount;
    }

    public List<PairPojo<Double, Long>> getRating() {
        return rating;
    }

    public void setRating(List<PairPojo<Double, Long>> rating) {
        this.rating = rating;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getEducationDuration() {
        return educationDuration;
    }

    public void setEducationDuration(Long educationDuration) {
        this.educationDuration = educationDuration;
    }

    public Long getEducationPeriod() {
        return educationPeriod;
    }

    public void setEducationPeriod(Long educationPeriod) {
        this.educationPeriod = educationPeriod;
    }
}
