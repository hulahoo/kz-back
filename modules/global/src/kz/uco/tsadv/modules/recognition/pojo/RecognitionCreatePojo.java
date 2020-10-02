package kz.uco.tsadv.modules.recognition.pojo;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import java.util.UUID;
import javax.validation.constraints.NotNull;
import com.haulmont.cuba.core.entity.BaseUuidEntity;
import java.util.List;

@MetaClass(name = "tsadv$RecognitionCreatePojo")
public class RecognitionCreatePojo extends BaseUuidEntity {
    private static final long serialVersionUID = -5904598783767768649L;

    @MetaProperty
    protected String authorEmployeeNumber;

    @MetaProperty
    protected List<QualityPojo> qualities;

    @MetaProperty
    protected String receiverEmployeeNumber;

    @MetaProperty
    protected UUID recognitionTypeId;

    @NotNull
    @MetaProperty(mandatory = true)
    protected Boolean notifyManager = false;

    @MetaProperty
    protected String comment;

    public void setQualities(List<QualityPojo> qualities) {
        this.qualities = qualities;
    }

    public List<QualityPojo> getQualities() {
        return qualities;
    }


    public void setAuthorEmployeeNumber(String authorEmployeeNumber) {
        this.authorEmployeeNumber = authorEmployeeNumber;
    }

    public String getAuthorEmployeeNumber() {
        return authorEmployeeNumber;
    }

    public void setReceiverEmployeeNumber(String receiverEmployeeNumber) {
        this.receiverEmployeeNumber = receiverEmployeeNumber;
    }

    public String getReceiverEmployeeNumber() {
        return receiverEmployeeNumber;
    }

    public void setRecognitionTypeId(UUID recognitionTypeId) {
        this.recognitionTypeId = recognitionTypeId;
    }

    public UUID getRecognitionTypeId() {
        return recognitionTypeId;
    }

    public void setNotifyManager(Boolean notifyManager) {
        this.notifyManager = notifyManager;
    }

    public Boolean getNotifyManager() {
        return notifyManager;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }


}