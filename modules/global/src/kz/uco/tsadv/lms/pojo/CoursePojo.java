package kz.uco.tsadv.lms.pojo;

import java.io.Serializable;
import java.util.List;

public class CoursePojo implements Serializable {
    protected String id;
    protected String name;
    protected String description;
    protected String logo;
    protected String enrollmentId;
    protected Boolean selfEnrollment;

    protected List<CourseSectionPojo> sections;
    protected List<SimplePojo> courseFeedbacks;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public List<CourseSectionPojo> getSections() {
        return sections;
    }

    public void setSections(List<CourseSectionPojo> sections) {
        this.sections = sections;
    }

    public String getEnrollmentId() {
        return enrollmentId;
    }

    public void setEnrollmentId(String enrollmentId) {
        this.enrollmentId = enrollmentId;
    }

    public List<SimplePojo> getCourseFeedbacks() {
        return courseFeedbacks;
    }

    public void setCourseFeedbacks(List<SimplePojo> courseFeedbacks) {
        this.courseFeedbacks = courseFeedbacks;
    }

    public Boolean isSelfEnrollment() {
        return selfEnrollment;
    }

    public void setSelfEnrollment(Boolean selfEnrollment) {
        this.selfEnrollment = selfEnrollment;
    }
}