package kz.uco.tsadv.modules.personal.model;

//import com.haulmont.bpm.entity.ProcInstance;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.entity.annotation.Listeners;
import kz.uco.tsadv.modules.administration.TsadvUser;
import kz.uco.uactivity.entity.Activity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.UUID;

@Table(name = "TSADV_BPM_REQUEST_MESSAGE")
@Listeners("tsadv_BpmRequestMessageListener")
@Entity(name = "tsadv$BpmRequestMessage")
public class BpmRequestMessage extends StandardEntity {
    private static final long serialVersionUID = 1512411063628632361L;

    @NotNull
    @Column(name = "ENTITY_NAME", nullable = false)
    protected String entityName;

    @NotNull
    @Column(name = "ENTITY_ID", nullable = false)
    protected UUID entityId;

    @Column(name = "ENTITY_REQUEST_NUMBER")
    protected Long entityRequestNumber;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "SEND_DATE")
    protected Date sendDate;

    @Column(name = "MESSAGE", length = 3000)
    protected String message;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ASSIGNED_USER_ID")
    protected TsadvUser assignedUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ASSIGNED_BY_ID")
    protected TsadvUser assignedBy;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_ID")
    protected BpmRequestMessage parent;

    @Column(name = "LVL")
    protected Integer lvl;

    @Column(name = "SCREEN_NAME")
    protected String screenName;

    /*@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROC_INSTANCE_ID")
    protected ProcInstance procInstance;*/

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACTIVITY_ID")
    protected Activity activity;

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public Activity getActivity() {
        return activity;
    }

    public TsadvUser getAssignedUser() {
        return assignedUser;
    }

    public void setAssignedUser(TsadvUser assignedUser) {
        this.assignedUser = assignedUser;
    }

    public TsadvUser getAssignedBy() {
        return assignedBy;
    }

    public void setAssignedBy(TsadvUser assignedBy) {
        this.assignedBy = assignedBy;
    }

    /*public void setProcInstance(ProcInstance procInstance) {
        this.procInstance = procInstance;
    }

    public ProcInstance getProcInstance() {
        return procInstance;
    }*/


    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setLvl(Integer lvl) {
        this.lvl = lvl;
    }

    public Integer getLvl() {
        return lvl;
    }

    public void setParent(BpmRequestMessage parent) {
        this.parent = parent;
    }

    public BpmRequestMessage getParent() {
        return parent;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityId(UUID entityId) {
        this.entityId = entityId;
    }

    public UUID getEntityId() {
        return entityId;
    }

    public void setEntityRequestNumber(Long entityRequestNumber) {
        this.entityRequestNumber = entityRequestNumber;
    }

    public Long getEntityRequestNumber() {
        return entityRequestNumber;
    }

    public void setSendDate(Date sendDate) {
        this.sendDate = sendDate;
    }

    public Date getSendDate() {
        return sendDate;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}