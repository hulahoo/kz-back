package kz.uco.tsadv.modules.learning.model;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import com.haulmont.chile.core.annotations.NamePattern;
import kz.uco.tsadv.modules.learning.enums.IdpStatus;
import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import java.util.List;
import javax.persistence.OneToMany;

@NamePattern("%s|planName")
@Table(name = "TSADV_INDIVIDUAL_DEVELOPMENT_PLAN")
@Entity(name = "tsadv$IndividualDevelopmentPlan")
public class IndividualDevelopmentPlan extends AbstractParentEntity {
    private static final long serialVersionUID = 9123323813633138489L;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PERSON_GROUP_ID")
    protected PersonGroupExt personGroup;

    @NotNull
    @Column(name = "PLAN_NAME", nullable = false)
    protected String planName;

    @NotNull
    @Column(name = "STATUS", nullable = false)
    protected String status;

    @Temporal(TemporalType.DATE)
    @NotNull
    @Column(name = "START_DATE", nullable = false)
    protected Date startDate;

    @Temporal(TemporalType.DATE)
    @NotNull
    @Column(name = "END_DATE", nullable = false)
    protected Date endDate;
    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "idp")
    protected List<IdpDetail> idpDetail;

    public void setIdpDetail(List<IdpDetail> idpDetail) {
        this.idpDetail = idpDetail;
    }

    public List<IdpDetail> getIdpDetail() {
        return idpDetail;
    }


    public IdpStatus getStatus() {
        return status == null ? null : IdpStatus.fromId(status);
    }

    public void setStatus(IdpStatus status) {
        this.status = status == null ? null : status.getId();
    }



    public void setPersonGroup(PersonGroupExt personGroup) {
        this.personGroup = personGroup;
    }

    public PersonGroupExt getPersonGroup() {
        return personGroup;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public String getPlanName() {
        return planName;
    }



    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getEndDate() {
        return endDate;
    }


}