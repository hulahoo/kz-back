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
import com.haulmont.cuba.core.entity.StandardEntity;

import kz.uco.tsadv.modules.learning.dictionary.DicInternshipType;
import kz.uco.tsadv.modules.learning.dictionary.DicInternshipRating;
import kz.uco.tsadv.modules.personal.group.OrganizationGroupExt;
import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import java.util.List;
import javax.persistence.OneToMany;

@Table(name = "TSADV_INTERNSHIP")
@Entity(name = "tsadv$Internship")
public class Internship extends StandardEntity {
    private static final long serialVersionUID = -6834387965723383636L;


    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PERSON_GROUP_ID")
    protected PersonGroupExt personGroup;

    @Column(name = "COMMENT_", length = 1000)
    protected String comment;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "INTERNSHIP_TYPE_ID")
    protected DicInternshipType internshipType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SCHOOL_ID")
    protected PartyExt school;

    @Column(name = "SPECIALIZATION")
    protected String specialization;

    @Column(name = "REASON")
    protected String reason;

    @Column(name = "AGREEMENT_NUMBER")
    protected String agreementNumber;

    @Temporal(TemporalType.DATE)
    @Column(name = "AGREEMENT_DATE")
    protected Date agreementDate;

    @Temporal(TemporalType.DATE)
    @NotNull
    @Column(name = "START_DATE", nullable = false)
    protected Date startDate;

    @Temporal(TemporalType.DATE)
    @NotNull
    @Column(name = "END_DATE", nullable = false)
    protected Date endDate;

    @NotNull
    @Column(name = "PAYABLE", nullable = false)
    protected Boolean payable = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MAIN_MENTOR_ID")
    protected PersonGroupExt mainMentor;

    @Column(name = "MAIN_MENTOR_REASON")
    protected String mainMentorReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INTERNSHIP_RATING_ID")
    protected DicInternshipRating internshipRating;

    @Column(name = "INTERNSHIP_REASON", length = 1000)
    protected String internshipReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORGANIZATION_GROUP_ID")
    protected OrganizationGroupExt organizationGroup;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "internship")
    protected List<InternshipExpenses> internshipExpenses;

    @Temporal(TemporalType.DATE)
    @Column(name = "ORDER_DATE")
    protected Date orderDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "MENTOR_ORDER_DATE")
    protected Date mentorOrderDate;

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

    public void setMentorOrderDate(Date mentorOrderDate) {
        this.mentorOrderDate = mentorOrderDate;
    }

    public Date getMentorOrderDate() {
        return mentorOrderDate;
    }


    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }


    public void setInternshipReason(String internshipReason) {
        this.internshipReason = internshipReason;
    }

    public String getInternshipReason() {
        return internshipReason;
    }


    public void setInternshipExpenses(List<InternshipExpenses> internshipExpenses) {
        this.internshipExpenses = internshipExpenses;
    }

    public List<InternshipExpenses> getInternshipExpenses() {
        return internshipExpenses;
    }


    public void setInternshipRating(DicInternshipRating internshipRating) {
        this.internshipRating = internshipRating;
    }

    public DicInternshipRating getInternshipRating() {
        return internshipRating;
    }

    public void setOrganizationGroup(OrganizationGroupExt organizationGroup) {
        this.organizationGroup = organizationGroup;
    }

    public OrganizationGroupExt getOrganizationGroup() {
        return organizationGroup;
    }


    public DicInternshipType getInternshipType() {
        return internshipType;
    }

    public void setInternshipType(DicInternshipType internshipType) {
        this.internshipType = internshipType;
    }


    public void setSchool(PartyExt school) {
        this.school = school;
    }

    public PartyExt getSchool() {
        return school;
    }





    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public void setAgreementNumber(String agreementNumber) {
        this.agreementNumber = agreementNumber;
    }

    public String getAgreementNumber() {
        return agreementNumber;
    }

    public void setAgreementDate(Date agreementDate) {
        this.agreementDate = agreementDate;
    }

    public Date getAgreementDate() {
        return agreementDate;
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

    public void setPayable(Boolean payable) {
        this.payable = payable;
    }

    public Boolean getPayable() {
        return payable;
    }

    public void setMainMentor(PersonGroupExt mainMentor) {
        this.mainMentor = mainMentor;
    }

    public PersonGroupExt getMainMentor() {
        return mainMentor;
    }

    public void setMainMentorReason(String mainMentorReason) {
        this.mainMentorReason = mainMentorReason;
    }

    public String getMainMentorReason() {
        return mainMentorReason;
    }


}