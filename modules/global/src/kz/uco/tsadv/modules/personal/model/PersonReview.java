package kz.uco.tsadv.modules.personal.model;

import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.persistence.*;
import java.util.Date;

@Table(name = "TSADV_PERSON_REVIEW")
@Entity(name = "tsadv$PersonReview")
public class PersonReview extends AbstractParentEntity {
    private static final long serialVersionUID = -6363398035240283640L;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "AUTHOR_ID")
    protected PersonGroupExt author;

    @Lob
    @Column(name = "TEXT")
    protected String text;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DATE_TIME", nullable = false)
    protected Date dateTime;


    @Column(name = "LIKING")
    protected String liking;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PERSON_ID")
    protected PersonGroupExt person;

    public String getLiking() {
        return liking;
    }

    public void setLiking(String liking) {
        this.liking = liking;
    }


    public void setPerson(PersonGroupExt person) {
        this.person = person;
    }

    public PersonGroupExt getPerson() {
        return person;
    }


    public void setAuthor(PersonGroupExt author) {
        this.author = author;
    }

    public PersonGroupExt getAuthor() {
        return author;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public Date getDateTime() {
        return dateTime;
    }


}