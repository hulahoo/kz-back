package kz.uco.tsadv.entity.dbview;

import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.DesignSupport;
import com.haulmont.cuba.core.global.Metadata;
import kz.uco.base.common.MultiLanguageUtils;
import kz.uco.tsadv.entity.bproc.AbstractBprocRequest;
import kz.uco.tsadv.modules.administration.TsadvUser;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@NamePattern("%s %s|process,requestNumber")
@DesignSupport("{'dbView':true,'generateDdl':false}")
@Table(name = "tsadv_bpm_proc_instance_vw")
@Entity(name = "tsadv$ProcInstanceV")
public class ProcInstanceV extends AbstractBprocRequest {
    private static final long serialVersionUID = -8264485652552694037L;

    @Transient
    @MetaProperty(related = {"processEn", "processKz", "processRu"})
    protected String process;

    @Column(name = "PROCESS_RU")
    protected String processRu;

    @Column(name = "PROCESS_KZ")
    protected String processKz;

    @Column(name = "PROCESS_EN")
    protected String processEn;

    @Column(name = "ENTITY_NAME")
    protected String entityName;

    @Column(name = "business_key_")
    protected String businessKey;

    @Column(name = "ENTITY_ID")
    protected UUID entityId;

    @Column(name = "ACTIVE")
    protected Boolean active;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "start_user_id")
    protected TsadvUser startUser;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "start_time")
    protected Date startTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "end_time")
    protected Date endTime;

    @Transient
    @MetaProperty(related = {"entityName", "entityId"})
    protected AbstractBprocRequest entity;

    public String getBusinessKey() {
        return businessKey;
    }

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }

    public void setEntity(AbstractBprocRequest entity) {
        this.entity = entity;
    }

    public AbstractBprocRequest getEntity() {
        if (entity == null) {
            DataManager dataManager = AppBeans.get(DataManager.class);
            Metadata metadata = AppBeans.get(Metadata.class);
            entity = dataManager.getReference(metadata.getClassNN(entityName).getJavaClass(), entityId);
        }
        return entity;
    }

    @Override
    public String getProcessDefinitionKey() {
        return getEntity().getProcessDefinitionKey();
    }

    @Override
    public String getProcessInstanceBusinessKey() {
        return this.businessKey;
    }

    public void setProcess(String process) {
        this.process = process;
    }

    public String getProcessRu() {
        return processRu;
    }

    public void setProcessRu(String processRu) {
        this.processRu = processRu;
    }

    public String getProcessKz() {
        return processKz;
    }

    public void setProcessKz(String processKz) {
        this.processKz = processKz;
    }

    public String getProcessEn() {
        return processEn;
    }

    public void setProcessEn(String processEn) {
        this.processEn = processEn;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public UUID getEntityId() {
        return entityId;
    }

    public void setEntityId(UUID entityId) {
        this.entityId = entityId;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public TsadvUser getStartUser() {
        return startUser;
    }

    public void setStartUser(TsadvUser startUser) {
        this.startUser = startUser;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getProcess() {
        if (process == null) {
            this.process = MultiLanguageUtils.getCurrentLanguageValue(processRu, processKz, processEn);
        }
        return process;
    }

}