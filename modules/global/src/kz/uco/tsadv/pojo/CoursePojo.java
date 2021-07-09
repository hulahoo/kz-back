package kz.uco.tsadv.pojo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CoursePojo implements Serializable {
    private UUID id;
    private String name;
    private Double avgRate;
    private String preRequisitions;
    private List<PairPojo<UUID, String>> trainers;
    private Date startDate;
    private Date endDate;
    private Integer finished;
    private Boolean isIssuedCertificate;
    private String learningProof;
    private List<PairPojo<UUID, String>> sections;
    private UUID logo;
    private List<CommentPojo> comments;
    private List<PairPojo<Double, Long>> rating;
    private Integer rateReviewCount;
    private String description;
    private Long educationDuration;
    private Long educationPeriod;
    private String enrollmentId;
    private Boolean selfEnrollment;
    private String enrollmentStatus;
    private CategoryCoursePojo category;
    private Boolean isOnline;

    public String getLearningProof() {
        return learningProof;
    }

    public void setLearningProof(String learningProof) {
        this.learningProof = learningProof;
    }

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

    public UUID getLogo() {
        return logo;
    }

    public void setLogo(UUID logo) {
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

    public Boolean getSelfEnrollment() {
        return selfEnrollment;
    }

    public void setSelfEnrollment(Boolean selfEnrollment) {
        this.selfEnrollment = selfEnrollment;
    }

    public String getEnrollmentId() {
        return enrollmentId;
    }

    public void setEnrollmentId(String enrollmentId) {
        this.enrollmentId = enrollmentId;
    }

    public String getEnrollmentStatus() {
        return enrollmentStatus;
    }

    public void setEnrollmentStatus(String enrollmentStatus) {
        this.enrollmentStatus = enrollmentStatus;
    }

    public CategoryCoursePojo getCategory() {
        return category;
    }

    public void setCategory(CategoryCoursePojo category) {
        this.category = category;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Boolean getOnline() {
        return isOnline;
    }

    public void setOnline(Boolean online) {
        isOnline = online;
    }
}
