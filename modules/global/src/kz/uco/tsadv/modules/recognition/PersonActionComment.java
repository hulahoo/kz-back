package kz.uco.tsadv.modules.recognition;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import kz.uco.base.entity.shared.Person;
import kz.uco.tsadv.modules.recognition.dictionary.DicActionLikeType;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import javax.persistence.Column;
import kz.uco.base.entity.shared.PersonGroup;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

@Table(name = "TSADV_PERSON_ACTION_COMMENT")
@Entity(name = "tsadv$PersonActionComment")
public class PersonActionComment extends AbstractParentEntity {
    private static final long serialVersionUID = -6174779005846473937L;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ACTION_ID")
    protected PersonAction action;

    @Column(name = "COMMENT_", length = 2000)
    protected String comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_COMMENT_ID")
    protected PersonActionComment parentComment;


    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PERSON_GROUP_ID")
    protected PersonGroupExt personGroup;

    public PersonGroupExt getPersonGroup() {
        return personGroup;
    }

    public void setPersonGroup(PersonGroupExt personGroup) {
        this.personGroup = personGroup;
    }



    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }


    public void setParentComment(PersonActionComment parentComment) {
        this.parentComment = parentComment;
    }

    public PersonActionComment getParentComment() {
        return parentComment;
    }


    public void setAction(PersonAction action) {
        this.action = action;
    }

    public PersonAction getAction() {
        return action;
    }




}