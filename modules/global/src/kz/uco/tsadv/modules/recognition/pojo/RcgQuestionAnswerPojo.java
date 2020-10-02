package kz.uco.tsadv.modules.recognition.pojo;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.entity.BaseUuidEntity;

import java.util.UUID;

@MetaClass(name = "tsadv$RcgQuestionAnswerPojo")
public class RcgQuestionAnswerPojo extends BaseUuidEntity {
    private static final long serialVersionUID = -4411964966403078925L;

    @MetaProperty
    protected String stringId;

    @MetaProperty
    protected String text;

    @MetaProperty
    protected String image;

    public RcgQuestionAnswerPojo() {
        super();
        stringId = id.toString();
    }

    @Override
    public void setId(UUID id) {
        super.setId(id);
        stringId = id.toString();
    }

    public String getStringId() {
        return stringId;
    }

    public void setStringId(String stringId) {
        this.stringId = stringId;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage() {
        return image;
    }


}