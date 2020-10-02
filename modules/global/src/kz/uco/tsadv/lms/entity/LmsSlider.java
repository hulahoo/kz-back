package kz.uco.tsadv.lms.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.haulmont.cuba.core.entity.FileDescriptor;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import com.haulmont.cuba.core.entity.StandardEntity;
import javax.persistence.Column;
import kz.uco.tsadv.lms.enums.LmsSliderPosition;
import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import java.util.List;
import javax.persistence.OneToMany;

@Table(name = "TSADV_LMS_SLIDER")
@Entity(name = "tsadv$LmsSlider")
public class LmsSlider extends StandardEntity {
    private static final long serialVersionUID = -379325387167965328L;

    @NotNull
    @Column(name = "POSITION_", nullable = false)
    protected String position;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "slider")
    protected List<LmsSliderImage> images;

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