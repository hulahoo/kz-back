package kz.uco.tsadv.modules.learning.model;

import kz.uco.base.entity.abstraction.AbstractParentEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Table(name = "TSADV_SCORM_QUESTION_MAPPING")
@Entity(name = "tsadv_ScormQuestionMapping")
public class ScormQuestionMapping extends AbstractParentEntity {
    private static final long serialVersionUID = -7900353713627031691L;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "LEARNING_OBJECT_ID")
    protected LearningObject learningObject;

    @NotNull
    @Column(name = "CODE", nullable = false)
    protected String code;

    @NotNull
    @Column(name = "QUESTION", nullable = false)
    protected String question;

    @Column(name = "SERIAL_NUMBER")
    protected Integer serialNumber;

    public Integer getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(Integer serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public LearningObject getLearningObject() {
        return learningObject;
    }

    public void setLearningObject(LearningObject learningObject) {
        this.learningObject = learningObject;
    }
}