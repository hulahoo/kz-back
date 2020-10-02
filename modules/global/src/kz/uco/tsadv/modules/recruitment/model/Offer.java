package kz.uco.tsadv.modules.recruitment.model;

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.entity.annotation.Listeners;
import com.haulmont.cuba.core.entity.annotation.Lookup;
import com.haulmont.cuba.core.entity.annotation.LookupType;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import kz.uco.base.entity.dictionary.DicCurrency;
import kz.uco.tsadv.modules.recruitment.enums.OfferStatus;
import kz.uco.tsadv.modules.recruitment.model.*;
import kz.uco.tsadv.modules.recruitment.model.JobRequest;
import kz.uco.tsadv.modules.recruitment.model.OfferTemplate;
import kz.uco.base.entity.abstraction.AbstractParentEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Listeners("tsadv_OfferListener")
@NamePattern("%s|jobRequest")
@Table(name = "TSADV_OFFER")
@Entity(name = "tsadv$Offer")
public class Offer extends AbstractParentEntity {
    private static final long serialVersionUID = 4530256457945750529L;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "JOB_REQUEST_ID")
    protected JobRequest jobRequest;

    @Column(name = "CANDIDATE_COMMENTARY", length = 1000)
    protected String candidateCommentary;

    @Column(name = "PROPOSED_SALARY")
    protected Double proposedSalary;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CURRENCY_ID")
    protected DicCurrency currency;

    @Temporal(TemporalType.DATE)
    @Column(name = "EXPIRE_DATE")
    protected Date expireDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "PROPOSED_START_DATE")
    protected Date proposedStartDate;

    @Column(name = "STATUS")
    protected Integer status;

    @Lookup(type = LookupType.DROPDOWN)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OFFER_TEMPLATE_ID")
    protected OfferTemplate offerTemplate;

    @OnDelete(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FILE_ID")
    protected FileDescriptor file;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "offer")
    protected List<OfferHistory> history;

    @NotNull
    @Column(name = "NEED_BUISNESS_PARTNER_APPROVE", nullable = false)
    protected Boolean needBuisnessPartnerApprove = false;
    public void setCandidateCommentary(String candidateCommentary) {
        this.candidateCommentary = candidateCommentary;
    }

    public String getCandidateCommentary() {
        return candidateCommentary;
    }


    public OfferTemplate getOfferTemplate() {
        return offerTemplate;
    }

    public void setOfferTemplate(OfferTemplate offerTemplate) {
        this.offerTemplate = offerTemplate;
    }



    public void setNeedBuisnessPartnerApprove(Boolean needBuisnessPartnerApprove) {
        this.needBuisnessPartnerApprove = needBuisnessPartnerApprove;
    }

    public Boolean getNeedBuisnessPartnerApprove() {
        return needBuisnessPartnerApprove;
    }


    public FileDescriptor getFile() {
        return file;
    }

    public void setFile(FileDescriptor file) {
        this.file = file;
    }


    public void setHistory(List<OfferHistory> history) {
        this.history = history;
    }

    public List<OfferHistory> getHistory() {
        return history;
    }


    public Date getProposedStartDate() {
        return proposedStartDate;
    }

    public void setProposedStartDate(Date proposedStartDate) {
        this.proposedStartDate = proposedStartDate;
    }





    public void setProposedSalary(Double proposedSalary) {
        this.proposedSalary = proposedSalary;
    }

    public Double getProposedSalary() {
        return proposedSalary;
    }

    public void setStatus(OfferStatus status) {
        this.status = status == null ? null : status.getId();
    }

    public OfferStatus getStatus() {
        return status == null ? null : OfferStatus.fromId(status);
    }


    public void setJobRequest(JobRequest jobRequest) {
        this.jobRequest = jobRequest;
    }

    public JobRequest getJobRequest() {
        return jobRequest;
    }

    public void setCurrency(DicCurrency currency) {
        this.currency = currency;
    }

    public DicCurrency getCurrency() {
        return currency;
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
    }

    public Date getExpireDate() {
        return expireDate;
    }


}