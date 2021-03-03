package kz.uco.tsadv.modules.learning.model;

import com.haulmont.cuba.core.entity.FileDescriptor;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Table(name = "TSADV_STUDENT_HOMEWORK")
@Entity(name = "tsadv_StudentHomework")
public class StudentHomework extends AbstractParentEntity {
    private static final long serialVersionUID = 6625282045265986670L;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "HOMEWORK_ID")
    protected Homework homework;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PERSON_GROUP_ID")
    protected PersonGroupExt personGroup;

    @Lob
    @Column(name = "ANSWER")
    protected String answer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ANSWER_FILE_ID")
    protected FileDescriptor answerFile;

    @NotNull
    @Column(name = "IS_DONE", nullable = false)
    protected Boolean isDone = false;

    @Lob
    @Column(name = "TRAINER_COMMENT")
    protected String trainerComment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TRAINER_ID")
    protected PersonGroupExt trainer;

    public PersonGroupExt getTrainer() {
        return trainer;
    }

    public void setTrainer(PersonGroupExt trainer) {
        this.trainer = trainer;
    }

    public String getTrainerComment() {
        return trainerComment;
    }

    public void setTrainerComment(String trainerComment) {
        this.trainerComment = trainerComment;
    }

    public Boolean getIsDone() {
        return isDone;
    }

    public void setIsDone(Boolean isDone) {
        this.isDone = isDone;
    }

    public FileDescriptor getAnswerFile() {
        return answerFile;
    }

    public void setAnswerFile(FileDescriptor answerFile) {
        this.answerFile = answerFile;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public PersonGroupExt getPersonGroup() {
        return personGroup;
    }

    public void setPersonGroup(PersonGroupExt personGroup) {
        this.personGroup = personGroup;
    }

    public Homework getHomework() {
        return homework;
    }

    public void setHomework(Homework homework) {
        this.homework = homework;
    }
}