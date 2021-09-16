package kz.uco.tsadv.modules.personal.dictionary;

import kz.uco.base.entity.abstraction.AbstractDictionary;
import kz.uco.tsadv.modules.personal.model.DicIncentiveIndicatorScoreSetting;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Table(name = "TSADV_DIC_INCENTIVE_INDICATORS")
@Entity(name = "tsadv_DicIncentiveIndicators")
public class DicIncentiveIndicators extends AbstractDictionary {
    private static final long serialVersionUID = -6323124983682771731L;

    @NotNull
    @Column(name = "GENERAL", nullable = false)
    private Boolean general = false;

    @JoinColumn(name = "TYPE_ID")
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private DicIncentiveIndicatorType type;

    @OneToMany(mappedBy = "indicator")
    private List<DicIncentiveIndicatorScoreSetting> scoreSettings;

    public List<DicIncentiveIndicatorScoreSetting> getScoreSettings() {
        return scoreSettings;
    }

    public void setScoreSettings(List<DicIncentiveIndicatorScoreSetting> scoreSettings) {
        this.scoreSettings = scoreSettings;
    }

    public Boolean getGeneral() {
        return general;
    }

    public void setGeneral(Boolean general) {
        this.general = general;
    }

    public DicIncentiveIndicatorType getType() {
        return type;
    }

    public void setType(DicIncentiveIndicatorType type) {
        this.type = type;
    }
}