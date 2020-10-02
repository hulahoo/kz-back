package kz.uco.tsadv.lms.pojo;

import java.io.Serializable;

public class CourseSectionPojo implements Serializable {
    protected String id;
    protected String sectionName;
    protected Integer order;
    protected Boolean isPassed;
    protected String langValue1;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Boolean getPassed() {
        return isPassed;
    }

    public void setPassed(Boolean passed) {
        isPassed = passed;
    }

    public String getLangValue1() {
        return langValue1;
    }

    public void setLangValue1(String langValue1) {
        this.langValue1 = langValue1;
    }
}
