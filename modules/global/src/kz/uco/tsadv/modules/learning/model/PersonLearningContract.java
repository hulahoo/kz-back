package kz.uco.tsadv.modules.learning.model;

import kz.uco.base.entity.abstraction.AbstractParentEntity;
import kz.uco.tsadv.modules.personal.group.PersonGroupExt;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Table(name = "TSADV_PERSON_LEARNING_CONTRACT")
@Entity(name = "tsadv$PersonLearningContract")
public class PersonLearningContract extends AbstractParentEntity {
    private static final long serialVersionUID = -5552341321368691979L;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PERSON_GROUP_ID")
    protected PersonGroupExt personGroup;

    @OneToMany(mappedBy = "personLearningContract")
    protected List<LearningExpense> learningExpense;

    @Column(name = "CONTRACT_NUMBER")
    protected String contractNumber;

    @Temporal(TemporalType.DATE)
    @Column(name = "CONTRACT_DATE")
    protected Date contractDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "TERM_OF_SERVICE")
    protected Date termOfService;

    @Lob
    @Column(name = "OTHER")
    protected String other;

    public void setLearningExpense(List<LearningExpense> learningExpense) {
        this.learningExpense = learningExpense;
    }

    public List<LearningExpense> getLearningExpense() {
        return learningExpense;
    }


    public void setPersonGroup(PersonGroupExt personGroup) {
        this.personGroup = personGroup;
    }

    public PersonGroupExt getPersonGroup() {
        return personGroup;
    }

    public void setContractNumber(String contractNumber) {
        this.contractNumber = contractNumber;
    }

    public String getContractNumber() {
        return contractNumber;
    }

    public void setContractDate(Date contractDate) {
        this.contractDate = contractDate;
    }

    public Date getContractDate() {
        return contractDate;
    }


    public void setTermOfService(Date termOfService) {
        this.termOfService = termOfService;
    }

    public Date getTermOfService() {
        return termOfService;
    }

    public void setOther(String other) {
        this.other = other;
    }

    public String getOther() {
        return other;
    }


}