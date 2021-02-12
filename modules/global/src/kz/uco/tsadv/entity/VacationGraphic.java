package kz.uco.tsadv.entity;

import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Table(name = "TSADV_VACATION_GRAPHIC")
@Entity(name = "tsadv_VacationGraphic")
@NamePattern("%s|name")
public class VacationGraphic extends StandardEntity {
    private static final long serialVersionUID = -1727374443355254589L;

    @Column(name = "REQUEST_NUMBER")
    private Long requestNumber;

    @NotNull
    @Column(name = "NAME", nullable = false)
    private String name;

    @NotNull
    @Column(name = "SURNAME", nullable = false)
    private String surname;

    @NotNull
    @Column(name = "MIDDLENAME", nullable = false)
    private String middlename;

    @NotNull
    @Column(name = "DIVISION", nullable = false)
    private String division;

    @NotNull
    @Column(name = "DUTY", nullable = false)
    private String duty;

    @Temporal(TemporalType.DATE)
    @NotNull
    @Column(name = "START_DATE", nullable = false)
    private Date startDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "END_DATE")
    private Date endDate;

    @Column(name = "COMMENTS")
    private String comments;

    @Column(name = "IS_SEND_TO_ORACLE")
    private Boolean isSendToOracle;

    @Transient
    @MetaProperty
    protected String nameSurnameMiddlename;

    public String getNameSurnameMiddlename() {
        return name + " "+ surname + " " +middlename;
    }

    public void setRequestNumber(Long requestNumber) {
        this.requestNumber = requestNumber;
    }

    public Long getRequestNumber() {
        return requestNumber;
    }

    public Boolean getIsSendToOracle() {
        return isSendToOracle;
    }

    public void setIsSendToOracle(Boolean isSendToOracle) {
        this.isSendToOracle = isSendToOracle;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public String getDuty() {
        return duty;
    }

    public void setDuty(String duty) {
        this.duty = duty;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public String getMiddlename() {
        return middlename;
    }

    public void setMiddlename(String middlename) {
        this.middlename = middlename;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}