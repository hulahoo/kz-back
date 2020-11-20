package kz.uco.tsadv.entity.dbview;

import com.haulmont.bpm.entity.ProcDefinition;
import com.haulmont.bpm.entity.ProcInstance;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DesignSupport;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.UserSessionSource;
import kz.uco.tsadv.modules.administration.UserExt;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@NamePattern("%s %s|requestType,requestNumber")
@DesignSupport("{'dbView':true,'generateDdl':false}")
@Table(name = "tsadv_bpm_proc_instance_vw")
@Entity(name = "tsadv$ProcInstanceV")
public class ProcInstanceV extends StandardEntity {
    private static final long serialVersionUID = -8264485652552694037L;

    @Transient
    @MetaProperty(related = {"processEn", "processRu"})
    protected String requestType;

    @Column(name = "PROCESS_RU")
    protected String processRu;

    @Column(name = "PROCESS_EN")
    protected String processEn;

    @Column(name = "ENTITY_NAME")
    protected String entityName;

    @Column(name = "ENTITY_ID")
    protected UUID entityId;

    @Column(name = "REQUEST_NUMBER")
    protected String requestNumber;

    @Column(name = "ACTIVE")
    protected Boolean active;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STARTED_BY_ID")
    protected UserExt startedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STARTED_BY_PERSON_GROUP_ID")
    protected PersonGroupExt startedByPersonGroup;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "START_DATE")
    protected Date startDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "END_DATE")
    protected Date endDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "EFFECTIVE_DATE")
    protected Date effectiveDate;

    @Column(name = "CANCELLED")
    protected Boolean cancelled;

    @Transient
    @MetaProperty(related = {"entityName", "entityId"})
    protected BaseUuidEntity entity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERSON_GROUP_ID")
    protected PersonGroupExt personGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CURRENT_APPROVER_ID")
    protected UserExt currentApprover;

    @Column(name = "DETAIL_RU")
    protected String detailRu;

    @Column(name = "DETAIL_EN")
    protected String detailEn;

    @Transient
    @MetaProperty(related = {"detailEn", "detailRu"})
    protected String detail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PROC_DEFINITION_ID")
    protected ProcDefinition procDefinition;

    public void setStartedByPersonGroup(PersonGroupExt startedByPersonGroup) {
        this.startedByPersonGroup = startedByPersonGroup;
    }

    public PersonGroupExt getStartedByPersonGroup() {
        return startedByPersonGroup;
    }


    @MetaProperty(related = "id")
    public ProcInstance getProcInstance() {
        ProcInstance procInstance = AppBeans.get(Metadata.class).create(ProcInstance.class);
        procInstance.setId(this.id);
        return procInstance;
    }

    public void setCancelled(Boolean cancelled) {
        this.cancelled = cancelled;
    }

    public Boolean getCancelled() {
        return cancelled;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setProcDefinition(ProcDefinition procDefinition) {
        this.procDefinition = procDefinition;
    }

    public ProcDefinition getProcDefinition() {
        return procDefinition;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public Date getEffectiveDate() {
        return effectiveDate;
    }

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

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getDetail() {
        if (detail == null) {
            detail = "en".equals(AppBeans.get(UserSessionSource.class).getLocale().getLanguage())
                    ? detailEn
                    : detailRu;
        }
        return detail;
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

    public void setCurrentApprover(UserExt currentApprover) {
        this.currentApprover = currentApprover;
    }

    public UserExt getCurrentApprover() {
        return currentApprover;
    }

    public void setPersonGroup(PersonGroupExt personGroup) {
        this.personGroup = personGroup;
    }

    public PersonGroupExt getPersonGroup() {
        return personGroup;
    }

    public BaseUuidEntity getEntity() {
        if (entity == null) {
            entity = (BaseUuidEntity) AppBeans.get(Metadata.class).create(entityName);
            entity.setId(entityId);
        }
        return entity;
    }

    public void setEntity(BaseUuidEntity entity) {
        this.entity = entity;
    }

    public void setRequestNumber(String requestNumber) {
        this.requestNumber = requestNumber;
    }

    public String getRequestNumber() {
        return requestNumber;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getRequestType() {
        if (requestType == null) {
            requestType = "en".equals(AppBeans.get(UserSessionSource.class).getLocale().getLanguage())
                    ? processEn
                    : processRu;
        }
        return requestType;
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

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getActive() {
        return active;
    }

    public void setStartedBy(UserExt startedBy) {
        this.startedBy = startedBy;
    }

    public UserExt getStartedBy() {
        return startedBy;
    }
}