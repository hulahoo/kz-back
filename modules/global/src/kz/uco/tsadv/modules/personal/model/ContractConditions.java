package kz.uco.tsadv.modules.personal.model;

import com.haulmont.cuba.core.entity.StandardEntity;
import kz.uco.tsadv.modules.personal.dictionary.DicRelationshipType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Table(name = "TSADV_CONTRACT_CONDITIONS")
@Entity(name = "tsadv$ContractConditions")
public class ContractConditions extends StandardEntity {
    private static final long serialVersionUID = -3559965550974523753L;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "RELATIONSHIP_TYPE_ID")
    private DicRelationshipType relationshipType;

    @NotNull
    @Column(name = "AGE_MIN", nullable = false)
    private Integer ageMin;

    @NotNull
    @Column(name = "AGE_MAX", nullable = false)
    private String ageMax;

    @NotNull
    @Column(name = "IS_FREE", nullable = false)
    private Boolean isFree = false;

    @NotNull
    @Column(name = "COST_IN_KZT", nullable = false)
    private Double costInKzt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INSURANCE_CONTRACT_ID")
    private InsuranceContract insuranceContract;

    public InsuranceContract getInsuranceContract() {
        return insuranceContract;
    }

    public void setInsuranceContract(InsuranceContract insuranceContract) {
        this.insuranceContract = insuranceContract;
    }

    public Double getCostInKzt() {
        return costInKzt;
    }

    public void setCostInKzt(Double costInKzt) {
        this.costInKzt = costInKzt;
    }

    public Boolean getIsFree() {
        return isFree;
    }

    public void setIsFree(Boolean isFree) {
        this.isFree = isFree;
    }

    public String getAgeMax() {
        return ageMax;
    }

    public void setAgeMax(String ageMax) {
        this.ageMax = ageMax;
    }

    public Integer getAgeMin() {
        return ageMin;
    }

    public void setAgeMin(Integer ageMin) {
        this.ageMin = ageMin;
    }

    public DicRelationshipType getRelationshipType() {
        return relationshipType;
    }

    public void setRelationshipType(DicRelationshipType relationshipType) {
        this.relationshipType = relationshipType;
    }
}