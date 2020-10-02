package kz.uco.tsadv.modules.learning.model;

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import kz.uco.tsadv.modules.learning.model.*;
import kz.uco.tsadv.modules.learning.model.LearningPathCourse;
import kz.uco.base.entity.abstraction.AbstractTimeBasedEntity;
import kz.uco.tsadv.modules.learning.dictionary.DicCategory;

import javax.persistence.*;
import java.util.List;

@NamePattern("%s|name")
@Table(name = "TSADV_LEARNING_PATH")
@Entity(name = "tsadv$LearningPath")
public class LearningPath extends AbstractTimeBasedEntity {
    private static final long serialVersionUID = 2771131343274670626L;

    @Column(name = "NAME", nullable = false, length = 1000)
    protected String name;

    @OnDelete(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CATEGORY_ID")
    protected DicCategory category;

    @Lob
    @Column(name = "DESCRIPTION", nullable = false)
    protected String description;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "learningPath")
    protected List<kz.uco.tsadv.modules.learning.model.LearningPathCourse> courses;

    @Transient
    @MetaProperty
    protected Double avgRate;

    @Transient
    @MetaProperty
    protected Long reviewCount;

    @Transient
    @MetaProperty
    protected Long courseCount;

    public void setCourseCount(Long courseCount) {
        this.courseCount = courseCount;
    }

    public Long getCourseCount() {
        return courseCount;
    }


    public void setReviewCount(Long reviewCount) {
        this.reviewCount = reviewCount;
    }

    public Long getReviewCount() {
        return reviewCount;
    }


    public void setAvgRate(Double avgRate) {
        this.avgRate = avgRate;
    }

    public Double getAvgRate() {
        return avgRate;
    }


    public List<kz.uco.tsadv.modules.learning.model.LearningPathCourse> getCourses() {
        return courses;
    }

    public void setCourses(List<LearningPathCourse> courses) {
        this.courses = courses;
    }


    public void setCategory(DicCategory category) {
        this.category = category;
    }

    public DicCategory getCategory() {
        return category;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


}