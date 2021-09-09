package kz.uco.tsadv.modules.learning.model;

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.learning.enums.QuestionType;

import javax.persistence.*;
import java.util.List;

@Table(name = "TSADV_QUESTION")
@Entity(name = "tsadv$Question")
public class Question extends AbstractParentEntity {
    private static final long serialVersionUID = -4594952210203072649L;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "BANK_ID")
    protected kz.uco.tsadv.modules.learning.model.QuestionBank bank;

    @Column(name = "TEXT", length = 2000)
    protected String text;

    @Column(name = "TYPE_", nullable = false)
    protected Integer type;

    @Column(name = "SCORE", nullable = false)
    protected Integer score;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "question")
    protected List<Answer> answers;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IMAGE_ID")
    private FileDescriptor image;

    public FileDescriptor getImage() {
        return image;
    }

    public void setImage(FileDescriptor image) {
        this.image = image;
    }

    public QuestionType getType() {
        return type == null ? null : QuestionType.fromId(type);
    }

    public void setType(QuestionType type) {
        this.type = type == null ? null : type.getId();
    }


    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setBank(kz.uco.tsadv.modules.learning.model.QuestionBank bank) {
        this.bank = bank;
    }

    public QuestionBank getBank() {
        return bank;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Integer getScore() {
        return score;
    }


}