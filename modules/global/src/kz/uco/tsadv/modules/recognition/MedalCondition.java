package kz.uco.tsadv.modules.recognition;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import kz.uco.tsadv.modules.recognition.dictionary.DicQuality;
import com.haulmont.cuba.core.entity.StandardEntity;

@Table(name = "TSADV_MEDAL_CONDITION")
@Entity(name = "tsadv$MedalCondition")
public class MedalCondition extends StandardEntity {
    private static final long serialVersionUID = 7643213367419664509L;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "MEDAL_ID")
    protected Medal medal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "QUALITY_ID")
    protected DicQuality quality;

    @Column(name = "QUALITY_QUANTITY")
    protected Long qualityQuantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CHILD_MEDAL_ID")
    protected Medal childMedal;

    @Column(name = "MEDAL_QUANTITY")
    protected Long medalQuantity;

    public void setMedal(Medal medal) {
        this.medal = medal;
    }

    public Medal getMedal() {
        return medal;
    }

    public void setQuality(DicQuality quality) {
        this.quality = quality;
    }

    public DicQuality getQuality() {
        return quality;
    }

    public void setQualityQuantity(Long qualityQuantity) {
        this.qualityQuantity = qualityQuantity;
    }

    public Long getQualityQuantity() {
        return qualityQuantity;
    }

    public void setChildMedal(Medal childMedal) {
        this.childMedal = childMedal;
    }

    public Medal getChildMedal() {
        return childMedal;
    }

    public void setMedalQuantity(Long medalQuantity) {
        this.medalQuantity = medalQuantity;
    }

    public Long getMedalQuantity() {
        return medalQuantity;
    }


}