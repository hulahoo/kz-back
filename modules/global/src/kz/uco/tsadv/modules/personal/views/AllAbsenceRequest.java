package kz.uco.tsadv.modules.personal.views;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.DbView;
import com.haulmont.cuba.core.global.Metadata;
import kz.uco.tsadv.entity.bproc.AbstractBprocRequest;
import kz.uco.tsadv.modules.personal.dictionary.DicAbsenceType;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import org.springframework.util.Assert;

import javax.persistence.*;
import java.util.Date;

@DbView
@NamePattern("%s (%s)|requestNumber,requestDate")
@Table(name = "TSADV_ALL_ABSENCE_REQUEST")
@Entity(name = "tsadv_AllAbsenceRequest")
public class AllAbsenceRequest extends AbstractBprocRequest {
    private static final long serialVersionUID = -6579268376026005800L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TYPE_ID")
    protected DicAbsenceType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERSON_GROUP_ID")
    protected PersonGroupExt personGroup;

    @Column(name = "ENTITY_NAME")
    private String entityName;

    @Temporal(TemporalType.DATE)
    @Column(name = "start_date")
    protected Date startDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "end_date")
    protected Date endDate;

    @Column(name = "ABSENCE_DAYS")
    protected Integer absenceDays;

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Integer getAbsenceDays() {
        return absenceDays;
    }

    public void setAbsenceDays(Integer absenceDays) {
        this.absenceDays = absenceDays;
    }

    @Override
    public String getProcessDefinitionKey() {
        Metadata metadata = AppBeans.get(Metadata.class);
        @SuppressWarnings("rawtypes") com.haulmont.cuba.core.entity.Entity entity = metadata.create(metadata.getClassNN(this.entityName));
        Assert.isTrue(entity instanceof AbstractBprocRequest, "entityName is wrong!");
        AbstractBprocRequest abstractBprocRequest = (AbstractBprocRequest) entity;
        return abstractBprocRequest.getProcessDefinitionKey();
    }

    public DicAbsenceType getType() {
        return type;
    }

    public void setType(DicAbsenceType type) {
        this.type = type;
    }

    public PersonGroupExt getPersonGroup() {
        return personGroup;
    }

    public void setPersonGroup(PersonGroupExt personGroup) {
        this.personGroup = personGroup;
    }
}