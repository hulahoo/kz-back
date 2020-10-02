package kz.uco.tsadv.modules.recognition;

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.annotation.Listeners;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.tsadv.modules.recognition.dictionary.DicRecognitionType;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Listeners("tsadv_RecognitionListener")
@NamePattern("%s|recognitionType")
@Table(name = "TSADV_RECOGNITION")
@Entity(name = "tsadv$Recognition")
public class Recognition extends AbstractParentEntity {
    private static final long serialVersionUID = -8652520447659082675L;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "RECOGNITION_TYPE_ID")
    protected DicRecognitionType recognitionType;

    @NotNull
    @Column(name = "NOTIFY_MANAGER", nullable = false)
    protected Boolean notifyManager = false;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "recognition")
    protected List<RecognitionComment> comments;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "recognition")
    protected List<RecognitionLike> likes;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "recognition")
    protected List<RecognitionQuality> recognitionQualities;

    @Temporal(TemporalType.DATE)
    @NotNull
    @Column(name = "RECOGNITION_DATE", nullable = false)
    protected Date recognitionDate;

    @Length(min = 100, max = 2000, message = "{msg://Recognition.comment.min.length}")
    @NotNull
    @Column(name = "COMMENT_", nullable = false, length = 2000)
    protected String comment;

    @NotNull
    @Column(name = "COMMENT_EN", nullable = false, length = 2000)
    protected String commentEn;

    @NotNull
    @Column(name = "COMMENT_RU", nullable = false, length = 2000)
    protected String commentRu;

    @Column(name = "COINS")
    protected Long coins;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "AUTHOR_ID")
    protected PersonGroupExt author;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "RECEIVER_ID")
    protected PersonGroupExt receiver;

    public void setNotifyManager(Boolean notifyManager) {
        this.notifyManager = notifyManager;
    }

    public Boolean getNotifyManager() {
        return notifyManager;
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


    public List<RecognitionQuality> getRecognitionQualities() {
        return recognitionQualities;
    }

    public void setRecognitionQualities(List<RecognitionQuality> recognitionQualities) {
        this.recognitionQualities = recognitionQualities;
    }


    public PersonGroupExt getReceiver() {
        return receiver;
    }

    public void setReceiver(PersonGroupExt receiver) {
        this.receiver = receiver;
    }


    public PersonGroupExt getAuthor() {
        return author;
    }

    public void setAuthor(PersonGroupExt author) {
        this.author = author;
    }


    public void setComments(List<RecognitionComment> comments) {
        this.comments = comments;
    }

    public List<RecognitionComment> getComments() {
        return comments;
    }

    public void setLikes(List<RecognitionLike> likes) {
        this.likes = likes;
    }

    public List<RecognitionLike> getLikes() {
        return likes;
    }


    public void setRecognitionType(DicRecognitionType recognitionType) {
        this.recognitionType = recognitionType;
    }

    public DicRecognitionType getRecognitionType() {
        return recognitionType;
    }

    public void setRecognitionDate(Date recognitionDate) {
        this.recognitionDate = recognitionDate;
    }

    public Date getRecognitionDate() {
        return recognitionDate;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    public void setCoins(Long coins) {
        this.coins = coins;
    }

    public Long getCoins() {
        return coins;
    }


}