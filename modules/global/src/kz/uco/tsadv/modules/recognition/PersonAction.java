package kz.uco.tsadv.modules.recognition;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import com.haulmont.cuba.core.entity.StandardEntity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import kz.uco.tsadv.modules.recognition.dictionary.DicPersonActionType;
import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import java.util.List;
import javax.persistence.OneToMany;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

@Table(name = "TSADV_PERSON_ACTION")
@Entity(name = "tsadv$PersonAction")
public class PersonAction extends StandardEntity {
    private static final long serialVersionUID = -3346312180782528921L;

    @Column(name = "ACTION_LANG1", length = 60)
    protected String actionLang1;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "AUTHOR_ID")
    protected PersonGroupExt author;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "RECEIVER_ID")
    protected PersonGroupExt receiver;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ACTION_TYPE_ID")
    protected DicPersonActionType actionType;

    @Column(name = "ACTION_LANG2", length = 60)
    protected String actionLang2;

    @Column(name = "ACTION_LANG3", length = 60)
    protected String actionLang3;

    @Column(name = "ACTION_LANG4", length = 60)
    protected String actionLang4;

    @Column(name = "ACTION_LANG5", length = 60)
    protected String actionLang5;

    @NotNull
    @Column(name = "ACTOR_FULLNAME_LANG1", nullable = false, length = 1000)
    protected String actorFullnameLang1;

    @Column(name = "ACTOR_FULLNAME_LANG2", length = 1000)
    protected String actorFullnameLang2;

    @Column(name = "ACTOR_FULLNAME_LANG3", length = 1000)
    protected String actorFullnameLang3;

    @Column(name = "ACTOR_FULLNAME_LANG4", length = 1000)
    protected String actorFullnameLang4;

    @Column(name = "ACTOR_FULLNAME_LANG5", length = 1000)
    protected String actorFullnameLang5;

    @Column(name = "TO_FULLNAME_LANG1", length = 1000)
    protected String toFullnameLang1;

    @Column(name = "TO_FULLNAME_LANG2", length = 1000)
    protected String toFullnameLang2;

    @Column(name = "TO_FULLNAME_LANG3", length = 1000)
    protected String toFullnameLang3;

    @Column(name = "TO_FULLNAME_LANG4", length = 1000)
    protected String toFullnameLang4;

    @Column(name = "TO_FULLNAME_LANG5", length = 1000)
    protected String toFullnameLang5;

    @Temporal(TemporalType.DATE)
    @NotNull
    @Column(name = "ACTION_DATE", nullable = false)
    protected Date actionDate;

    @NotNull
    @Column(name = "COMMENT_", nullable = false, length = 4000)
    protected String comment;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "action")
    protected List<PersonActionObject> personActionObject;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "action")
    protected List<PersonActionLike> personActionLike;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "action")
    protected List<PersonActionComment> personActionComment;

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


    public void setPersonActionObject(List<PersonActionObject> personActionObject) {
        this.personActionObject = personActionObject;
    }

    public List<PersonActionObject> getPersonActionObject() {
        return personActionObject;
    }

    public void setPersonActionLike(List<PersonActionLike> personActionLike) {
        this.personActionLike = personActionLike;
    }

    public List<PersonActionLike> getPersonActionLike() {
        return personActionLike;
    }

    public void setPersonActionComment(List<PersonActionComment> personActionComment) {
        this.personActionComment = personActionComment;
    }

    public List<PersonActionComment> getPersonActionComment() {
        return personActionComment;
    }


    public void setActionType(DicPersonActionType actionType) {
        this.actionType = actionType;
    }

    public DicPersonActionType getActionType() {
        return actionType;
    }


    public void setActionLang1(String actionLang1) {
        this.actionLang1 = actionLang1;
    }

    public String getActionLang1() {
        return actionLang1;
    }

    public void setActionLang2(String actionLang2) {
        this.actionLang2 = actionLang2;
    }

    public String getActionLang2() {
        return actionLang2;
    }

    public void setActionLang3(String actionLang3) {
        this.actionLang3 = actionLang3;
    }

    public String getActionLang3() {
        return actionLang3;
    }

    public void setActionLang4(String actionLang4) {
        this.actionLang4 = actionLang4;
    }

    public String getActionLang4() {
        return actionLang4;
    }

    public void setActionLang5(String actionLang5) {
        this.actionLang5 = actionLang5;
    }

    public String getActionLang5() {
        return actionLang5;
    }

    public void setActorFullnameLang1(String actorFullnameLang1) {
        this.actorFullnameLang1 = actorFullnameLang1;
    }

    public String getActorFullnameLang1() {
        return actorFullnameLang1;
    }

    public void setActorFullnameLang2(String actorFullnameLang2) {
        this.actorFullnameLang2 = actorFullnameLang2;
    }

    public String getActorFullnameLang2() {
        return actorFullnameLang2;
    }

    public void setActorFullnameLang3(String actorFullnameLang3) {
        this.actorFullnameLang3 = actorFullnameLang3;
    }

    public String getActorFullnameLang3() {
        return actorFullnameLang3;
    }

    public void setActorFullnameLang4(String actorFullnameLang4) {
        this.actorFullnameLang4 = actorFullnameLang4;
    }

    public String getActorFullnameLang4() {
        return actorFullnameLang4;
    }

    public void setActorFullnameLang5(String actorFullnameLang5) {
        this.actorFullnameLang5 = actorFullnameLang5;
    }

    public String getActorFullnameLang5() {
        return actorFullnameLang5;
    }

    public void setToFullnameLang1(String toFullnameLang1) {
        this.toFullnameLang1 = toFullnameLang1;
    }

    public String getToFullnameLang1() {
        return toFullnameLang1;
    }

    public void setToFullnameLang2(String toFullnameLang2) {
        this.toFullnameLang2 = toFullnameLang2;
    }

    public String getToFullnameLang2() {
        return toFullnameLang2;
    }

    public void setToFullnameLang3(String toFullnameLang3) {
        this.toFullnameLang3 = toFullnameLang3;
    }

    public String getToFullnameLang3() {
        return toFullnameLang3;
    }

    public void setToFullnameLang4(String toFullnameLang4) {
        this.toFullnameLang4 = toFullnameLang4;
    }

    public String getToFullnameLang4() {
        return toFullnameLang4;
    }

    public void setToFullnameLang5(String toFullnameLang5) {
        this.toFullnameLang5 = toFullnameLang5;
    }

    public String getToFullnameLang5() {
        return toFullnameLang5;
    }

    public void setActionDate(Date actionDate) {
        this.actionDate = actionDate;
    }

    public Date getActionDate() {
        return actionDate;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }


}