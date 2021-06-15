package kz.uco.tsadv.pojo;

import com.haulmont.cuba.core.entity.BaseGenericIdEntity;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class CategoryCoursePojo implements Serializable {
    private UUID id;
    private String langValue;
    private List<CoursePojo> courses;

    public CategoryCoursePojo() {
    }

    public CategoryCoursePojo(UUID id) {
        this.id = id;
    }

    public CategoryCoursePojo(UUID id, String langValue) {
        this.id = id;
        this.langValue = langValue;
    }

    public CategoryCoursePojo(UUID id, String langValue, List<CoursePojo> courses) {
        this.id = id;
        this.langValue = langValue;
        this.courses = courses;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getLangValue() {
        return langValue;
    }

    public void setLangValue(String langValue) {
        this.langValue = langValue;
    }

    public List<CoursePojo> getCourses() {
        return courses;
    }

    public void setCourses(List<CoursePojo> courses) {
        this.courses = courses;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;

        if (other == null || getClass() != other.getClass())
            return false;

        return Objects.equals(getId(), ((CategoryCoursePojo) other).getId());
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }
}
