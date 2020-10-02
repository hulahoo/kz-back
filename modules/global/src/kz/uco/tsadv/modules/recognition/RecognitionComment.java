package kz.uco.tsadv.modules.recognition;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import com.haulmont.cuba.core.entity.StandardEntity;

@Table(name = "TSADV_RECOGNITION_COMMENT")
@Entity(name = "tsadv$RecognitionComment")
public class RecognitionComment extends StandardEntity {
    private static final long serialVersionUID = -6010057777211969745L;

    @NotNull
    @Column(name = "TEXT", nullable = false, length = 2000)
    protected String text;

    @NotNull
    @Column(name = "TEXT_EN", nullable = false, length = 2000)
    protected String textEn;

    @NotNull
    @Column(name = "TEXT_RU", nullable = false, length = 2000)
    protected String textRu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_COMMENT_ID")
    protected RecognitionComment parentComment;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "AUTHOR_ID")
    protected PersonGroupExt author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RECOGNITION_ID")
    protected Recognition recognition;

    public void setTextEn(String textEn) {
        this.textEn = textEn;
    }

    public String getTextEn() {
        return textEn;
    }

    public void setTextRu(String textRu) {
        this.textRu = textRu;
    }

    public String getTextRu() {
        return textRu;
    }


    public void setRecognition(Recognition recognition) {
        this.recognition = recognition;
    }

    public Recognition getRecognition() {
        return recognition;
    }


    public void setParentComment(RecognitionComment parentComment) {
        this.parentComment = parentComment;
    }

    public RecognitionComment getParentComment() {
        return parentComment;
    }


    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setAuthor(PersonGroupExt author) {
        this.author = author;
    }

    public PersonGroupExt getAuthor() {
        return author;
    }


}