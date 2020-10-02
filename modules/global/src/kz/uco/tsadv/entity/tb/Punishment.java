package kz.uco.tsadv.entity.tb;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import com.haulmont.chile.core.annotations.NamePattern;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import kz.uco.tsadv.modules.personal.model.PersonExt;
import kz.uco.tsadv.entity.tb.dictionary.PunishmentType;

@NamePattern("%s|description")
@Table(name = "TSADV_PUNISHMENT_TB")
@Entity(name = "tsadv$PunishmentTb")
public class Punishment extends AbstractParentEntity {
    private static final long serialVersionUID = -5496087523993942808L;

    @Temporal(TemporalType.DATE)
    @Column(name = "ORDER_DATE")
    protected Date orderDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PUNISHMENT_TYPE_ID")
    protected PunishmentType punishmentType;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PERSON_ID")
    protected PersonExt person;

    @Column(name = "ORDER_NUMBER")
    protected Long orderNumber;

    @Temporal(TemporalType.DATE)
    @Column(name = "PUNISHMENT_DATE", nullable = false)
    protected Date punishmentDate;

    @Column(name = "DESCRIPTION")
    protected String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACCIDENTS_ID")
    protected Accidents accidents;


    public void setAccidents(Accidents accidents) {
        this.accidents = accidents;
    }

    public Accidents getAccidents() {
        return accidents;
    }

    public void setPerson(PersonExt person) {
        this.person = person;
    }

    public PersonExt getPerson() {
        return person;
    }

    public PunishmentType getPunishmentType() {
        return punishmentType;
    }

    public void setPunishmentType(PunishmentType punishmentType) {
        this.punishmentType = punishmentType;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderNumber(Long orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Long getOrderNumber() {
        return orderNumber;
    }

    public void setPunishmentDate(Date punishmentDate) {
        this.punishmentDate = punishmentDate;
    }

    public Date getPunishmentDate() {
        return punishmentDate;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }



}