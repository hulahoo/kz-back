package kz.uco.tsadv.entity.tb;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.UUID;
import javax.persistence.Column;
import kz.uco.base.entity.abstraction.AbstractParentEntity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import kz.uco.tsadv.entity.tb.dictionary.HarmfulFactorType;

@Table(name = "TSADV_HARMFUL_FACTORS_DETAIL")
@Entity(name = "tsadv$HarmfulFactorsDetail")
public class HarmfulFactorsDetail extends AbstractParentEntity {
    private static final long serialVersionUID = 6780532760489018840L;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "HARMFUL_FACTOR_TYPE_ID")
    protected HarmfulFactorType harmfulFactorType;

    @Column(name = "TOTAL_SAMPLES")
    protected Long totalSamples;

    @Column(name = "MAX_PERMIS_CONCENTRATION")
    protected Long maxPermisConcentration;

    @Column(name = "MAX_SINGLE_CONCENTRATION")
    protected Long maxSingleConcentration;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "HARMFULL_FACTORS_ID")
    protected HarmfullFactors harmfullFactors;

    public void setHarmfullFactors(HarmfullFactors harmfullFactors) {
        this.harmfullFactors = harmfullFactors;
    }

    public HarmfullFactors getHarmfullFactors() {
        return harmfullFactors;
    }


    public HarmfulFactorType getHarmfulFactorType() {
        return harmfulFactorType;
    }

    public void setHarmfulFactorType(HarmfulFactorType harmfulFactorType) {
        this.harmfulFactorType = harmfulFactorType;
    }


    public void setTotalSamples(Long totalSamples) {
        this.totalSamples = totalSamples;
    }

    public Long getTotalSamples() {
        return totalSamples;
    }

    public void setMaxPermisConcentration(Long maxPermisConcentration) {
        this.maxPermisConcentration = maxPermisConcentration;
    }

    public Long getMaxPermisConcentration() {
        return maxPermisConcentration;
    }

    public void setMaxSingleConcentration(Long maxSingleConcentration) {
        this.maxSingleConcentration = maxSingleConcentration;
    }

    public Long getMaxSingleConcentration() {
        return maxSingleConcentration;
    }


}