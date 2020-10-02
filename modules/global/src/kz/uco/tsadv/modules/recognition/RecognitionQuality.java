package kz.uco.tsadv.modules.recognition;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.haulmont.cuba.core.entity.StandardEntity;
import kz.uco.tsadv.modules.recognition.dictionary.DicQuality;
import com.haulmont.cuba.core.entity.annotation.Listeners;

@Listeners("tsadv_RecognitionQualityListener")
@Table(name = "TSADV_RECOGNITION_QUALITY")
@Entity(name = "tsadv$RecognitionQuality")
public class RecognitionQuality extends StandardEntity {
    private static final long serialVersionUID = -1340299216471702471L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RECOGNITION_ID")
    protected Recognition recognition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "QUALITY_ID")
    protected DicQuality quality;

    public DicQuality getQuality() {
        return quality;
    }

    public void setQuality(DicQuality quality) {
        this.quality = quality;
    }


    public void setRecognition(Recognition recognition) {
        this.recognition = recognition;
    }

    public Recognition getRecognition() {
        return recognition;
    }


}