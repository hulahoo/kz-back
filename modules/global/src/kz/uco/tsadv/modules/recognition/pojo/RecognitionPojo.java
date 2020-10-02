package kz.uco.tsadv.modules.recognition.pojo;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import kz.uco.tsadv.global.entity.PersonPojo;

import java.util.List;

@MetaClass(name = "tsadv$RecognitionPojo")
public class RecognitionPojo extends BaseUuidEntity {
    private static final long serialVersionUID = 5619280529973268667L;

    @MetaProperty
    protected PersonPojo sender;

    @MetaProperty
    protected List<RecognitionCommentPojo> lastComments;

    @MetaProperty
    protected String text;

    @MetaProperty
    protected String reverseText;

    @MetaProperty
    protected PersonPojo receiver;

    @MetaProperty
    protected String createDate;

    @MetaProperty
    protected String say;

    @MetaProperty
    protected Long likeCount;

    @MetaProperty
    protected Long commentCount;

    @MetaProperty
    protected Long commentPages;

    @MetaProperty
    protected RecognitionTypePojo type;

    @MetaProperty
    protected Integer currentLike = 0;

    @MetaProperty
    protected Integer translated = 0;

    @MetaProperty
    protected List<QualityPojo> qualities;

    @MetaProperty
    protected PersonPojo teamLiker;

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

    public PersonPojo getTeamLiker() {
        return teamLiker;
    }

    public void setTeamLiker(PersonPojo teamLiker) {
        this.teamLiker = teamLiker;
    }

    public Integer getCurrentLike() {
        return currentLike;
    }

    public void setCurrentLike(Integer currentLike) {
        this.currentLike = currentLike;
    }

    public List<QualityPojo> getQualities() {
        return qualities;
    }

    public void setQualities(List<QualityPojo> qualities) {
        this.qualities = qualities;
    }

    public PersonPojo getSender() {
        return sender;
    }

    public void setSender(PersonPojo sender) {
        this.sender = sender;
    }

    public List<RecognitionCommentPojo> getLastComments() {
        return lastComments;
    }

    public void setLastComments(List<RecognitionCommentPojo> lastComments) {
        this.lastComments = lastComments;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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

    public String getSay() {
        return say;
    }

    public void setSay(String say) {
        this.say = say;
    }

    public Long getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Long likeCount) {
        this.likeCount = likeCount;
    }

    public Long getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Long commentCount) {
        this.commentCount = commentCount;
    }

    public Long getCommentPages() {
        return commentPages;
    }

    public void setCommentPages(Long commentPages) {
        this.commentPages = commentPages;
    }

    public RecognitionTypePojo getType() {
        return type;
    }

    public void setType(RecognitionTypePojo type) {
        this.type = type;
    }
}