package kz.uco.tsadv.modules.recognition.pojo;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import kz.uco.tsadv.global.entity.PersonPojo;

import java.util.UUID;

@MetaClass(name = "tsadv$RecognitionCommentPojo")
public class RecognitionCommentPojo extends BaseUuidEntity {
    private static final long serialVersionUID = -6484061675400478760L;

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
    protected UUID recognitionId;

    @MetaProperty
    protected UUID parentCommentId;

    @MetaProperty
    protected PersonPojo parentCommentAuthor;

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

    public UUID getRecognitionId() {
        return recognitionId;
    }

    public void setRecognitionId(UUID recognitionId) {
        this.recognitionId = recognitionId;
    }

    public UUID getParentCommentId() {
        return parentCommentId;
    }

    public void setParentCommentId(UUID parentCommentId) {
        this.parentCommentId = parentCommentId;
    }

    public PersonPojo getParentCommentAuthor() {
        return parentCommentAuthor;
    }

    public void setParentCommentAuthor(PersonPojo parentCommentAuthor) {
        this.parentCommentAuthor = parentCommentAuthor;
    }
}