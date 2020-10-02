package kz.uco.tsadv.modules.recognition.feedback;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.entity.BaseUuidEntity;

import java.util.UUID;

@MetaClass(name = "tsadv$RcgFeedbackAttachmentPojo")
public class RcgFeedbackAttachmentPojo extends BaseUuidEntity {
    private static final long serialVersionUID = -3261424703820346134L;

    @MetaProperty
    protected String name;

    @MetaProperty
    protected String type;

    @MetaProperty
    protected String url;

    @Override
    public void setId(UUID id) {
        super.setId(id);
        this.url = String.format("./dispatch/rf_attachment/%s", id.toString());
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}