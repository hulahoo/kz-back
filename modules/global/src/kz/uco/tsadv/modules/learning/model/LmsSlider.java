package kz.uco.tsadv.modules.learning.model;

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import kz.uco.tsadv.modules.learning.dictionary.DicLmsSliderPosition;

import javax.persistence.*;
import java.util.List;

@NamePattern("%s|position")
@Table(name = "TSADV_LMS_SLIDER")
@Entity(name = "tsadv$LmsSlider")
public class LmsSlider extends StandardEntity {
    private static final long serialVersionUID = -379325387167965328L;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POSITION_ID")
    protected DicLmsSliderPosition position;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "slider")
    protected List<LmsSliderImage> images;

    public void setPosition(DicLmsSliderPosition position) {
        this.position = position;
    }

    public DicLmsSliderPosition getPosition() {
        return position;
    }

    public void setImages(List<LmsSliderImage> images) {
        this.images = images;
    }

    public List<LmsSliderImage> getImages() {
        return images;
    }


}