package kz.uco.tsadv.modules.recognition.feedback;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.base.entity.abstraction.AbstractParentEntity;

@Table(name = "TSADV_RCG_FEEDBACK_COMMENT")
@Entity(name = "tsadv$RcgFeedbackComment")
public class RcgFeedbackComment extends AbstractParentEntity {
    private static final long serialVersionUID = 2884649500377997064L;

    @NotNull
    @Column(name = "TEXT", nullable = false, length = 2000)
    protected String text;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_COMMENT_ID")
    protected RcgFeedbackComment parentComment;

    @NotNull
    @Column(name = "TEXT_EN", nullable = false, length = 2000)
    protected String textEn;

    @NotNull
    @Column(name = "TEXT_RU", nullable = false, length = 2000)
    protected String textRu;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "AUTHOR_ID")
    protected PersonGroupExt author;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "RCG_FEEDBACK_ID")
    protected RcgFeedback rcgFeedback;

    public void setParentComment(RcgFeedbackComment parentComment) {
        this.parentComment = parentComment;
    }

    public RcgFeedbackComment getParentComment() {
        return parentComment;
    }


    public void setRcgFeedback(RcgFeedback rcgFeedback) {
        this.rcgFeedback = rcgFeedback;
    }

    public RcgFeedback getRcgFeedback() {
        return rcgFeedback;
    }


    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

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

    public void setAuthor(PersonGroupExt author) {
        this.author = author;
    }

    public PersonGroupExt getAuthor() {
        return author;
    }


}