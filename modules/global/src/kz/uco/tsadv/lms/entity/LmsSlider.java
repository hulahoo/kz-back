package kz.uco.tsadv.lms.entity;

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import kz.uco.tsadv.lms.enums.LmsSliderPosition;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.List;

@Table(name = "TSADV_LMS_SLIDER")
@Entity(name = "tsadv$LmsSlider")
public class LmsSlider extends StandardEntity {
    private static final long serialVersionUID = -379325387167965328L;

    @NotNull
    @Column(name = "POSITION_", nullable = false)
    protected String position;

    @Column(name = "URL", length = 1000)
    protected String url;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "slider")
    protected List<LmsSliderImage> images;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setImages(List<LmsSliderImage> images) {
        this.images = images;
    }

    public List<LmsSliderImage> getImages() {
        return images;
    }


    public void setPosition(LmsSliderPosition position) {
        this.position = position == null ? null : position.getId();
    }

    public LmsSliderPosition getPosition() {
        return position == null ? null : LmsSliderPosition.fromId(position);
    }



}