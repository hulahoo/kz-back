package kz.uco.tsadv.entity.dbview;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.global.DesignSupport;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.uactivity.entity.Activity;

@NamePattern("%s|orderCode")
@DesignSupport("{'dbView':true,'generateDdl':false}")
@Table(name = "TSADV_ACTIVITY_TASK_VIEW")
@Entity(name = "tsadv$ActivityTask")
public class ActivityTask extends BaseUuidEntity {
    private static final long serialVersionUID = 8491218128136470781L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACTIVITY_ID")
    protected Activity activity;

    @Column(name = "ORDER_CODE")
    protected String orderCode;

    @Column(name = "PROCESS_EN")
    protected String processEn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERSON_GROUP_ID")
    protected PersonGroupExt personGroup;

    @Temporal(TemporalType.DATE)
    @Column(name = "ORDER_DATE")
    protected Date orderDate;

    @Column(name = "STATUS")
    protected Integer status;

    @Column(name = "PROCESS_RU")
    protected String processRu;

    @Temporal(TemporalType.DATE)
    @Column(name = "START_DATE")
    protected Date startDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "EXPIRY_DATE")
    protected Date expiryDate;

    @Column(name = "IS_EXPIRED_TASK")
    protected Boolean isExpiredTask;

    @Column(name = "DETAIL_RU")
    protected String detailRu;

    @Column(name = "DETAIL_EN")
    protected String detailEn;

    public void setDetailRu(String detailRu) {
        this.detailRu = detailRu;
    }

    public String getDetailRu() {
        return detailRu;
    }

    public void setDetailEn(String detailEn) {
        this.detailEn = detailEn;
    }

    public String getDetailEn() {
        return detailEn;
    }


    public void setIsExpiredTask(Boolean isExpiredTask) {
        this.isExpiredTask = isExpiredTask;
    }

    public Boolean getIsExpiredTask() {
        return isExpiredTask;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setProcessRu(String processRu) {
        this.processRu = processRu;
    }

    public String getProcessRu() {
        return processRu;
    }

    public void setProcessEn(String processEn) {
        this.processEn = processEn;
    }

    public String getProcessEn() {
        return processEn;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setPersonGroup(PersonGroupExt personGroup) {
        this.personGroup = personGroup;
    }

    public PersonGroupExt getPersonGroup() {
        return personGroup;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public Date getOrderDate() {
        return orderDate;
    }

}