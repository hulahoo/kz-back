package kz.uco.tsadv.modules.recognition.pojo;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.entity.BaseUuidEntity;

@MetaClass(name = "tsadv$PersonAwardPojo")
public class PersonAwardPojo extends BaseUuidEntity {
    private static final long serialVersionUID = -8592727224601356536L;

    @MetaProperty
    protected String authorEmployeeNumber;

    @MetaProperty
    protected String receiverEmployeeNumber;

    @MetaProperty
    protected String history;

    @MetaProperty
    protected String why;

    @MetaProperty
    protected String status;

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

    public void setHistory(String history) {
        this.history = history;
    }

    public String getHistory() {
        return history;
    }

    public void setWhy(String why) {
        this.why = why;
    }

    public String getWhy() {
        return why;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }


}