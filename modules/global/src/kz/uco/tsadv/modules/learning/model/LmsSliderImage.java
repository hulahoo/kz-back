package kz.uco.tsadv.modules.learning.model;

import com.haulmont.cuba.core.entity.FileDescriptor;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;

import javax.persistence.*;

@Table(name = "TSADV_LMS_SLIDER_IMAGE")
@Entity(name = "tsadv$LmsSliderImage")
public class LmsSliderImage extends StandardEntity {
    private static final long serialVersionUID = 7313155703161788604L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IMAGE_ID")
    protected FileDescriptor image;

    @Lob
    @Column(name = "URL")
    protected String url;

    @Column(name = "ORDER_")
    protected Integer order;

    @OnDelete(DeletePolicy.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SLIDER_ID")
    protected LmsSlider slider;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

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