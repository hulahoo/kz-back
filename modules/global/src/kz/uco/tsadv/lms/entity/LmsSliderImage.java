package kz.uco.tsadv.lms.entity;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import com.haulmont.cuba.core.entity.StandardEntity;
import javax.persistence.Column;

@Table(name = "TSADV_LMS_SLIDER_IMAGE")
@Entity(name = "tsadv$LmsSliderImage")
public class LmsSliderImage extends StandardEntity {
    private static final long serialVersionUID = 7313155703161788604L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IMAGE_ID")
    protected FileDescriptor image;

    @Column(name = "ORDER_")
    protected Integer order;

    @OnDelete(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SLIDER_ID")
    protected LmsSlider slider;

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Integer getOrder() {
        return order;
    }


    public void setImage(FileDescriptor image) {
        this.image = image;
    }

    public FileDescriptor getImage() {
        return image;
    }

    public void setSlider(LmsSlider slider) {
        this.slider = slider;
    }

    public LmsSlider getSlider() {
        return slider;
    }


}