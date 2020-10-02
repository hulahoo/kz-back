package kz.uco.tsadv.modules.recognition;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import kz.uco.tsadv.modules.recruitment.enums.HS_Periods;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import java.util.List;
import javax.persistence.OneToMany;

@NamePattern("%s|name")
@Table(name = "TSADV_COIN_DISTRIBUTION_RULE")
@Entity(name = "tsadv$CoinDistributionRule")
public class CoinDistributionRule extends StandardEntity {
    private static final long serialVersionUID = 6697023116836852364L;

    @NotNull
    @Column(name = "NAME", nullable = false)
    protected String name;

    @OneToMany(mappedBy = "coinDistributionRule")
    protected List<CoinDistributionPerson> persons;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "coinDistributionRule")
    protected List<CoinDistributionOrganization> organizations;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "coinDistributionRule")
    protected List<CoinDistributionPosition> positions;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "coinDistributionRule")
    protected List<CoinDistributionJob> jobs;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "coinDistributionRule")
    protected List<CoinDistributionGrade> grades;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "coinDistributionRule")
    protected List<CoinDistributionCostCenter> costCenters;

    @Column(name = "DESCRIPTION", length = 2000)
    protected String description;

    @NotNull
    @Column(name = "DISTRIBUTION_FREQUENCY", nullable = false)
    protected Integer distributionFrequency;

    @NotNull
    @Column(name = "ZERO_FREQUENCY", nullable = false)
    protected Integer zeroFrequency;

    @NotNull
    @Column(name = "ACTIVE", nullable = false)
    protected Boolean active = false;

    @NotNull
    @Column(name = "COINS", nullable = false)
    protected Long coins;

    public void setPersons(List<CoinDistributionPerson> persons) {
        this.persons = persons;
    }

    public List<CoinDistributionPerson> getPersons() {
        return persons;
    }


    public void setCoins(Long coins) {
        this.coins = coins;
    }

    public Long getCoins() {
        return coins;
    }


    public void setOrganizations(List<CoinDistributionOrganization> organizations) {
        this.organizations = organizations;
    }

    public List<CoinDistributionOrganization> getOrganizations() {
        return organizations;
    }

    public void setPositions(List<CoinDistributionPosition> positions) {
        this.positions = positions;
    }

    public List<CoinDistributionPosition> getPositions() {
        return positions;
    }

    public void setJobs(List<CoinDistributionJob> jobs) {
        this.jobs = jobs;
    }

    public List<CoinDistributionJob> getJobs() {
        return jobs;
    }

    public void setGrades(List<CoinDistributionGrade> grades) {
        this.grades = grades;
    }

    public List<CoinDistributionGrade> getGrades() {
        return grades;
    }

    public void setCostCenters(List<CoinDistributionCostCenter> costCenters) {
        this.costCenters = costCenters;
    }

    public List<CoinDistributionCostCenter> getCostCenters() {
        return costCenters;
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDistributionFrequency(HS_Periods distributionFrequency) {
        this.distributionFrequency = distributionFrequency == null ? null : distributionFrequency.getId();
    }

    public HS_Periods getDistributionFrequency() {
        return distributionFrequency == null ? null : HS_Periods.fromId(distributionFrequency);
    }

    public void setZeroFrequency(HS_Periods zeroFrequency) {
        this.zeroFrequency = zeroFrequency == null ? null : zeroFrequency.getId();
    }

    public HS_Periods getZeroFrequency() {
        return zeroFrequency == null ? null : HS_Periods.fromId(zeroFrequency);
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getActive() {
        return active;
    }


}