package kz.uco.tsadv.entity.tb;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;

import java.util.List;
import javax.persistence.OneToMany;

@Table(name = "TSADV_HARMFULL_FACTORS")
@Entity(name = "tsadv$HarmfullFactors")
public class HarmfullFactors extends AbstractParentEntity {
    private static final long serialVersionUID = -9070094103257363228L;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ORGANIZATION_ID")
    protected OrganizationGroupExt organization;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "harmfullFactors")
    protected List<NotAllowedPerson> notAllowedPerson;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "harmfullFactors")
    protected List<PreAndPostShiftInspection> preAndPost;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "harmfullFactors")
    protected List<Attachment> attachment;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "harmfullFactors")
    protected List<HarmfulFactorsDetail> details;

    @Temporal(TemporalType.DATE)
    @Column(name = "ENTRY_DATE", nullable = false)
    protected Date entryDate;

    @Column(name = "LABORATORY", nullable = false)
    protected String laboratory;

    public void setNotAllowedPerson(List<NotAllowedPerson> notAllowedPerson) {
        this.notAllowedPerson = notAllowedPerson;
    }

    public List<NotAllowedPerson> getNotAllowedPerson() {
        return notAllowedPerson;
    }


    public void setPreAndPost(List<PreAndPostShiftInspection> preAndPost) {
        this.preAndPost = preAndPost;
    }

    public List<PreAndPostShiftInspection> getPreAndPost() {
        return preAndPost;
    }


    public void setAttachment(List<Attachment> attachment) {
        this.attachment = attachment;
    }

    public List<Attachment> getAttachment() {
        return attachment;
    }


    public void setDetails(List<HarmfulFactorsDetail> details) {
        this.details = details;
    }

    public List<HarmfulFactorsDetail> getDetails() {
        return details;
    }


    public void setOrganization(OrganizationGroupExt organization) {
        this.organization = organization;
    }

    public OrganizationGroupExt getOrganization() {
        return organization;
    }

    public void setEntryDate(Date entryDate) {
        this.entryDate = entryDate;
    }

    public Date getEntryDate() {
        return entryDate;
    }

    public void setLaboratory(String laboratory) {
        this.laboratory = laboratory;
    }

    public String getLaboratory() {
        return laboratory;
    }


}