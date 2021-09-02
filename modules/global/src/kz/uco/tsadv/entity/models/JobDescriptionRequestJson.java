package kz.uco.tsadv.entity.models;

import com.haulmont.chile.core.annotations.MetaClass;
import com.haulmont.chile.core.annotations.MetaProperty;
import com.haulmont.cuba.core.entity.BaseUuidEntity;

import java.util.Date;

@MetaClass(name = "tsadv_JobDescriptionRequestJson")
public class JobDescriptionRequestJson extends BaseUuidEntity {
    private static final long serialVersionUID = -6338440001613757929L;

    @MetaProperty
    protected String requestNumber;

    @MetaProperty
    protected String requestDate;

    @MetaProperty
    protected String initiator;

    @MetaProperty
    protected String requestStatus;

    @MetaProperty
    private String statusCode;

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(String requestStatus) {
        this.requestStatus = requestStatus;
    }

    public String getInitiator() {
        return initiator;
    }

    public void setInitiator(String initiator) {
        this.initiator = initiator;
    }

    public String getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(String requestDate) {
        this.requestDate = requestDate;
    }

    public String getRequestNumber() {
        return requestNumber;
    }

    public void setRequestNumber(String requestNumber) {
        this.requestNumber = requestNumber;
    }
}