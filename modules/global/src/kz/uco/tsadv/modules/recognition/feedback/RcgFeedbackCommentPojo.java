package kz.uco.tsadv.modules.recognition.feedback;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import kz.uco.tsadv.global.entity.PersonPojo;

import java.util.UUID;

@MetaClass(name = "tsadv$RcgFeedbackCommentPojo")
public class RcgFeedbackCommentPojo extends BaseUuidEntity {
    private static final long serialVersionUID = -1376094398045593760L;

    @MetaProperty
    protected PersonPojo author;

    @MetaProperty
    protected String createDate;

    @MetaProperty
    protected String text;

    @MetaProperty
    protected String reverseText;

    @MetaProperty
    protected Integer translated = 0;

    @MetaProperty
    protected UUID feedbackId;

    @MetaProperty
    protected UUID parentCommentId;

    @MetaProperty
    protected PersonPojo parentCommentAuthor;

    public PersonPojo getParentCommentAuthor() {
        return parentCommentAuthor;
    }

    public void setParentCommentAuthor(PersonPojo parentCommentAuthor) {
        this.parentCommentAuthor = parentCommentAuthor;
    }

    public UUID getParentCommentId() {
        return parentCommentId;
    }

    public void setParentCommentId(UUID parentCommentId) {
        this.parentCommentId = parentCommentId;
    }

    public PersonPojo getAuthor() {
        return author;
    }

    public void setAuthor(PersonPojo author) {
        this.author = author;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getReverseText() {
        return reverseText;
    }

    public void setReverseText(String reverseText) {
        this.reverseText = reverseText;
    }

    public Integer getTranslated() {
        return translated;
    }

    public void setTranslated(Integer translated) {
        this.translated = translated;
    }

    public UUID getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(UUID feedbackId) {
        this.feedbackId = feedbackId;
    }
}