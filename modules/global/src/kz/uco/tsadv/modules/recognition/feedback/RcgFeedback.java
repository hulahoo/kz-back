package kz.uco.tsadv.modules.recognition.feedback;

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.cuba.core.entity.annotation.Listeners;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import org.hibernate.validator.constraints.Length;

@Listeners("tsadv_RcgFeedbackListener")
@Table(name = "TSADV_RCG_FEEDBACK")
@Entity(name = "tsadv$RcgFeedback")
public class RcgFeedback extends AbstractParentEntity {
    private static final long serialVersionUID = 3161663603962150411L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TYPE_ID")
    protected DicRcgFeedbackType type;

    @NotNull
    @Column(name = "DIRECTION", nullable = false)
    protected String direction;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "AUTHOR_ID")
    protected PersonGroupExt author;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "RECEIVER_ID")
    protected PersonGroupExt receiver;

    @Length(min = 50, max = 2000)
    @NotNull
    @Column(name = "COMMENT_", nullable = false, length = 2000)
    protected String comment;

    @NotNull
    @Column(name = "COMMENT_EN", nullable = false, length = 2000)
    protected String commentEn;

    @NotNull
    @Column(name = "COMMENT_RU", nullable = false, length = 2000)
    protected String commentRu;

    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    @Column(name = "FEEDBACK_DATE", nullable = false)
    protected Date feedbackDate;

    @NotNull
    @Column(name = "THEME", nullable = false, length = 2000)
    protected String theme;

    @NotNull
    @Column(name = "THEME_RU", nullable = false, length = 2000)
    protected String themeRu;

    @NotNull
    @Column(name = "THEME_EN", nullable = false, length = 2000)
    protected String themeEn;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "rcgFeedback")
    protected List<RcgFeedbackAttachment> attachments;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "rcgFeedback")
    protected List<RcgFeedbackComment> comments;

    public void setDirection(RcgFeedbackDirection direction) {
        this.direction = direction == null ? null : direction.getId();
    }

    public RcgFeedbackDirection getDirection() {
        return direction == null ? null : RcgFeedbackDirection.fromId(direction);
    }


    public void setComments(List<RcgFeedbackComment> comments) {
        this.comments = comments;
    }

    public List<RcgFeedbackComment> getComments() {
        return comments;
    }


    public void setAttachments(List<RcgFeedbackAttachment> attachments) {
        this.attachments = attachments;
    }

    public List<RcgFeedbackAttachment> getAttachments() {
        return attachments;
    }


    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    public void setCommentEn(String commentEn) {
        this.commentEn = commentEn;
    }

    public String getCommentEn() {
        return commentEn;
    }

    public void setCommentRu(String commentRu) {
        this.commentRu = commentRu;
    }

    public String getCommentRu() {
        return commentRu;
    }

    public void setType(DicRcgFeedbackType type) {
        this.type = type;
    }

    public DicRcgFeedbackType getType() {
        return type;
    }

    public void setFeedbackDate(Date feedbackDate) {
        this.feedbackDate = feedbackDate;
    }

    public Date getFeedbackDate() {
        return feedbackDate;
    }


    public void setAuthor(PersonGroupExt author) {
        this.author = author;
    }

    public PersonGroupExt getAuthor() {
        return author;
    }

    public void setReceiver(PersonGroupExt receiver) {
        this.receiver = receiver;
    }

    public PersonGroupExt getReceiver() {
        return receiver;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getTheme() {
        return theme;
    }

    public void setThemeRu(String themeRu) {
        this.themeRu = themeRu;
    }

    public String getThemeRu() {
        return themeRu;
    }

    public void setThemeEn(String themeEn) {
        this.themeEn = themeEn;
    }

    public String getThemeEn() {
        return themeEn;
    }


}