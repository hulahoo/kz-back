package kz.uco.tsadv.modules.recognition.feedback;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.BaseUuidEntity;

@NamePattern("%s|name")
@MetaClass(name = "tsadv$DicRcgFeedbackTypePojo")
public class DicRcgFeedbackTypePojo extends BaseUuidEntity {
    private static final long serialVersionUID = -7431808332035716790L;

    @MetaProperty
    protected String name;

    @MetaProperty
    protected String imageId;

    @MetaProperty
    protected String image;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
        image = String.format("./dispatch/rf_type_image/%s", imageId);
    }

    public String getImage() {

        return image;
    }
}