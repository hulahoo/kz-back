package kz.uco.tsadv.modules.personal.model;

import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.entity.annotation.Listeners;
import com.haulmont.cuba.core.entity.annotation.Lookup;
import com.haulmont.cuba.core.entity.annotation.LookupType;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import kz.uco.base.entity.abstraction.AbstractParentCategorizedEntity;
import kz.uco.tsadv.modules.personal.dictionary.DicAbsenceStatus;
import kz.uco.tsadv.modules.personal.dictionary.DicAbsenceType;
import kz.uco.tsadv.modules.personal.group.OrderGroup;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Listeners("tsadv_AbsenceListener")
@NamePattern("%s %s  %s %s %s %s %s|absenceDays,absenceStatus,dateFrom,dateTo,notificationDate,order,type")
@Table(name = "TSADV_ABSENCE")
@Entity(name = "tsadv$Absence")
public class Absence extends AbstractParentCategorizedEntity {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
    private static final long serialVersionUID = 2614557804382950349L;

    @Temporal(TemporalType.DATE)
    @Column(name = "NOTIFICATION_DATE")
    protected Date notificationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FILE_ID")
    protected FileDescriptor file;

    @Transient
    @MetaProperty(related = {"dateFrom", "dateTo", "type"})
    protected String typeAndDate;

    @Transient
    @MetaProperty
    protected String numberAndTypeAndDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_ABSENCE_ID")
    protected Absence parentAbsence;

    @Column(name = "ORDER_NUM")
    protected String orderNum;

    @Temporal(TemporalType.DATE)
    @Column(name = "ORDER_DATE")
    protected Date orderDate;

    @OneToMany(mappedBy = "parentAbsence")
    protected List<Absence> vacationLink;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATE_FROM", nullable = false)
    protected Date dateFrom;

    @Temporal(TemporalType.DATE)
    @Column(name = "DATE_TO", nullable = false)
    protected Date dateTo;

    @Column(name = "ABSENCE_DAYS", nullable = false)
    protected Integer absenceDays;

    @Column(name = "ADDITIONAL_DAYS")
    protected Integer additionalDays;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORDER_ID")
    protected OrderGroup order;

    @NotNull
    @Lookup(type = LookupType.DROPDOWN)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "TYPE_ID")
    protected DicAbsenceType type;

    @Lookup(type = LookupType.SCREEN, actions = {"lookup", "open", "clear"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ABSENCE_STATUS_ID")
    protected DicAbsenceStatus absenceStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORD_ASSIGNMENT_ID")
    protected OrdAssignment ordAssignment;

    @Lookup(type = LookupType.DROPDOWN)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ABSENCE_REQUEST_ID")
    protected AbsenceRequest absenceRequest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERSON_GROUP_ID")
    protected PersonGroupExt personGroup;


    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "absence")
    protected List<AbsenceToAbsenceBalance> absenceToAbsenceBalance;

    @Column(name = "LEGACY_ID")
    protected String legacyId;


    @Temporal(TemporalType.DATE)
    @Column(name = "PERIOD_START")
    protected Date periodStart;

    @Temporal(TemporalType.DATE)
    @Column(name = "PERIOD_END")
    protected Date periodEnd;

    @Column(name = "USE_IN_BALANCE")
    protected Boolean useInBalance;

    public void setUseInBalance(Boolean useInBalance) {
        this.useInBalance = useInBalance;
    }

    public Boolean getUseInBalance() {
        return useInBalance;
    }


    public void setPeriodStart(Date periodStart) {
        this.periodStart = periodStart;
    }

    public Date getPeriodStart() {
        return periodStart;
    }

    public void setPeriodEnd(Date periodEnd) {
        this.periodEnd = periodEnd;
    }

    public Date getPeriodEnd() {
        return periodEnd;
    }


    public void setLegacyId(String legacyId) {
        this.legacyId = legacyId;
    }

    public String getLegacyId() {
        return legacyId;
    }


    public void setFile(FileDescriptor file) {
        this.file = file;
    }

    public FileDescriptor getFile() {
        return file;
    }


    public void setAdditionalDays(Integer additionalDays) {
        this.additionalDays = additionalDays;
    }

    public Integer getAdditionalDays() {
        return additionalDays;
    }

    public Absence getParentAbsence() {
        return parentAbsence;
    }

    public void setParentAbsence(Absence parentAbsence) {
        this.parentAbsence = parentAbsence;
    }


    public List<Absence> getVacationLink() {
        return vacationLink;
    }

    public void setVacationLink(List<Absence> vacationLink) {
        this.vacationLink = vacationLink;
    }

    public OrdAssignment getOrdAssignment() {
        return ordAssignment;
    }

    public void setOrdAssignment(OrdAssignment ordAssignment) {
        this.ordAssignment = ordAssignment;
    }


    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setTypeAndDate(String typeAndDate) {
        this.typeAndDate = typeAndDate;
    }

    public String getTypeAndDate() {

        typeAndDate = type == null ? "" : type.getLangValue() + " " + dateFormat.format(dateFrom) + " - " + dateFormat.format(dateTo);
        return typeAndDate;
    }

    public String getNumberAndTypeAndDate() {
        numberAndTypeAndDate = (orderNum != null ? (orderNum + " ") : "");
        if (type != null) numberAndTypeAndDate += type.getLangValue() + "";
        if(dateFrom != null) numberAndTypeAndDate += dateFormat.format(dateFrom) + "";
        if(dateTo != null) numberAndTypeAndDate += dateFormat.format(dateTo) + "";
        return numberAndTypeAndDate;
    }

    public void setNumberAndTypeAndDate(String numberAndTypeAndDate) {
        this.numberAndTypeAndDate = numberAndTypeAndDate;
    }

    public void setAbsenceToAbsenceBalance(List<AbsenceToAbsenceBalance> absenceToAbsenceBalance) {
        this.absenceToAbsenceBalance = absenceToAbsenceBalance;
    }

    public List<AbsenceToAbsenceBalance> getAbsenceToAbsenceBalance() {
        return absenceToAbsenceBalance;
    }


    public void setPersonGroup(PersonGroupExt personGroup) {
        this.personGroup = personGroup;
    }

    public PersonGroupExt getPersonGroup() {
        return personGroup;
    }

    public void setAbsenceRequest(AbsenceRequest absenceRequest) {
        this.absenceRequest = absenceRequest;
    }

    public AbsenceRequest getAbsenceRequest() {
        return absenceRequest;
    }

    public OrderGroup getOrder() {
        return order;
    }

    public void setOrder(OrderGroup order) {
        this.order = order;
    }

    public void setNotificationDate(Date notificationDate) {
        this.notificationDate = notificationDate;
    }

    public Date getNotificationDate() {
        return notificationDate;
    }

    public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }

    public Date getDateFrom() {
        return dateFrom;
    }

    public void setDateTo(Date dateTo) {
        this.dateTo = dateTo;
    }

    public Date getDateTo() {
        return dateTo;
    }

    public void setAbsenceDays(Integer absenceDays) {
        this.absenceDays = absenceDays;
    }

    public Integer getAbsenceDays() {
        return absenceDays;
    }

    public void setAbsenceStatus(DicAbsenceStatus absenceStatus) {
        this.absenceStatus = absenceStatus;
    }

    public DicAbsenceStatus getAbsenceStatus() {
        return absenceStatus;
    }

    public DicAbsenceType getType() {
        return type;
    }

    public void setType(DicAbsenceType type) {
        this.type = type;
    }


}