package kz.uco.tsadv.modules.recognition.feedback;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import kz.uco.tsadv.global.entity.PersonPojo;

import java.util.List;

@MetaClass(name = "tsadv$RcgFeedbackPojo")
public class RcgFeedbackPojo extends BaseUuidEntity {
    private static final long serialVersionUID = 1531727101375356443L;

    @MetaProperty
    protected String theme;

    @MetaProperty
    protected String reverseTheme;

    @MetaProperty
    protected String comment;

    @MetaProperty
    protected String reverseComment;

    @MetaProperty
    protected PersonPojo sender;

    @MetaProperty
    protected PersonPojo receiver;

    @MetaProperty
    protected String createDate;

    @MetaProperty
    protected DicRcgFeedbackTypePojo type;

    @MetaProperty
    protected List<RcgFeedbackAttachmentPojo> attachments;

    @MetaProperty
    protected Integer translated = 0;

    @MetaProperty
    protected List<RcgFeedbackCommentPojo> lastComments;

    @MetaProperty
    protected Long commentCount = 0L;

    @MetaProperty
    protected Long commentPages = 0L;

    @MetaProperty
    protected String say;

    @MetaProperty
    protected Boolean forMe;

    @MetaProperty
    protected Boolean attachmentChanged;

    @MetaProperty
    protected Boolean sendFeedbackToAuthor;

    public void setSendFeedbackToAuthor(Boolean sendFeedbackToAuthor) {
        this.sendFeedbackToAuthor = sendFeedbackToAuthor;
    }

    public Boolean getSendFeedbackToAuthor() {
        return sendFeedbackToAuthor;
    }

    public Boolean getAttachmentChanged() {
        return attachmentChanged;
    }

    public void setAttachmentChanged(Boolean attachmentChanged) {
        this.attachmentChanged = attachmentChanged;
    }

    public Long getCommentPages() {
        return commentPages;
    }

    public void setCommentPages(Long commentPages) {
        this.commentPages = commentPages;
    }

    public List<RcgFeedbackAttachmentPojo> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<RcgFeedbackAttachmentPojo> attachments) {
        this.attachments = attachments;
    }

    public Boolean getForMe() {
        return forMe;
    }

    public void setForMe(Boolean forMe) {
        this.forMe = forMe;
    }

    public String getSay() {
        return say;
    }

    public void setSay(String say) {
        this.say = say;
    }

    public Long getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Long commentCount) {
        this.commentCount = commentCount;
    }

    public List<RcgFeedbackCommentPojo> getLastComments() {
        return lastComments;
    }

    public void setLastComments(List<RcgFeedbackCommentPojo> lastComments) {
        this.lastComments = lastComments;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getReverseTheme() {
        return reverseTheme;
    }

    public void setReverseTheme(String reverseTheme) {
        this.reverseTheme = reverseTheme;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getReverseComment() {
        return reverseComment;
    }

    public void setReverseComment(String reverseComment) {
        this.reverseComment = reverseComment;
    }

    public PersonPojo getSender() {
        return sender;
    }

    public void setSender(PersonPojo sender) {
        this.sender = sender;
    }

    public PersonPojo getReceiver() {
        return receiver;
    }

    public void setReceiver(PersonPojo receiver) {
        this.receiver = receiver;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public DicRcgFeedbackTypePojo getType() {
        return type;
    }

    public void setType(DicRcgFeedbackTypePojo type) {
        this.type = type;
    }

    public Integer getTranslated() {
        return translated;
    }

    public void setTranslated(Integer translated) {
        this.translated = translated;
    }
}