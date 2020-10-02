package kz.uco.tsadv.modules.recognition.feedback;

import javax.persistence.Entity;
import javax.persistence.Table;
import com.haulmont.cuba.core.entity.FileDescriptor;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import kz.uco.base.entity.abstraction.AbstractDictionary;
import javax.persistence.Column;

@Table(name = "TSADV_DIC_RCG_FEEDBACK_TYPE")
@Entity(name = "tsadv$DicRcgFeedbackType")
public class DicRcgFeedbackType extends AbstractDictionary {
    private static final long serialVersionUID = -4520087646464203145L;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "IMAGE_ID")
    protected FileDescriptor image;

    @Column(name = "COLOR")
    protected String color;

    public void setColor(String color) {
        this.color = color;
    }

    public String getColor() {
        return color;
    }


    public void setImage(FileDescriptor image) {
        this.image = image;
    }

    public FileDescriptor getImage() {
        return image;
    }


}