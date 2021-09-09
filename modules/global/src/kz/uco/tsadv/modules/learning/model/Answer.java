package kz.uco.tsadv.modules.learning.model;

import com.haulmont.cuba.core.entity.FileDescriptor;
import kz.uco.base.entity.abstraction.AbstractParentEntity;

import javax.persistence.*;

@Table(name = "TSADV_ANSWER")
@Entity(name = "tsadv$Answer")
public class Answer extends AbstractParentEntity {
    private static final long serialVersionUID = 311427660371982364L;

    @Column(name = "ANSWER", length = 2000)
    protected String answer;

    @Column(name = "CORRECT", nullable = false)
    protected Boolean correct = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "QUESTION_ID")
    protected kz.uco.tsadv.modules.learning.model.Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IMAGE_ID")
    private FileDescriptor image;

    public FileDescriptor getImage() {
        return image;
    }

    public void setImage(FileDescriptor image) {
        this.image = image;
    }

    public void setQuestion(kz.uco.tsadv.modules.learning.model.Question question) {
        this.question = question;
    }

    public Question getQuestion() {
        return question;
    }


    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getAnswer() {
        return answer;
    }

    public void setCorrect(Boolean correct) {
        this.correct = correct;
    }

    public Boolean getCorrect() {
        return correct;
    }


}