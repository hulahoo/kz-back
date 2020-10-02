package kz.uco.tsadv.modules.learning.model;

import kz.uco.tsadv.modules.learning.dictionary.DicLearningObjectType;
import kz.uco.tsadv.modules.learning.model.LearningObject;
import kz.uco.tsadv.modules.learning.model.Test;
import kz.uco.base.entity.abstraction.AbstractParentEntity;

import javax.persistence.*;

@Table(name = "tsadv_course_section_object")
@Entity(name = "tsadv$CourseSectionObject")
public class CourseSectionObject extends AbstractParentEntity {
    private static final long serialVersionUID = 5800314004649781801L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OBJECT_TYPE_ID")
    protected DicLearningObjectType objectType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CONTENT_ID")
    protected LearningObject content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TEST_ID")
    protected Test test;

    public LearningObject getContent() {
        return content;
    }

    public void setContent(LearningObject content) {
        this.content = content;
    }


    public DicLearningObjectType getObjectType() {
        return objectType;
    }

    public void setObjectType(DicLearningObjectType objectType) {
        this.objectType = objectType;
    }


    public void setTest(Test test) {
        this.test = test;
    }

    public Test getTest() {
        return test;
    }


}